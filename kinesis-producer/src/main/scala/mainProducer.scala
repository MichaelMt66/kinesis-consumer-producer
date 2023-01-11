import java.util.concurrent.{Executors, TimeUnit}

import com.amazonaws.services.kinesis.producer.{KinesisProducer}
import org.slf4j.LoggerFactory

object mainProducer {


  def main(args: Array[String]): Unit = {

    val logger = LoggerFactory.getLogger(mainProducer.getClass)
    val producer = new KinesisProducer(KinesisSetting.config)
    val service = Executors.newFixedThreadPool(1)

    service.submit(new SenderAsync(producer))

    //shut down good practice
    Runtime.getRuntime().addShutdownHook(new Thread(
      new Runnable {
        override def run(): Unit = {
          service.shutdown()
          try {
            service.awaitTermination(200, TimeUnit.SECONDS)
            logger.info("Flushing remaining records.")
            producer.flushSync()
            logger.info("All records flushed.");
            producer.destroy()
            logger.info("Producer destroyed")

          }
          catch {
            case e: InterruptedException => logger.warn("shutting down", e)
          }
        }
      }))
  }
}

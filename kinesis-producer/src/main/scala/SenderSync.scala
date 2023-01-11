import com.amazonaws.services.kinesis.producer.KinesisProducer
import org.slf4j.{Logger, LoggerFactory}
import utils.sourceFiles

class SenderSync(
                   val producer: KinesisProducer,
                   val logger: Logger = LoggerFactory.getLogger("SenderSync")
                 ) extends Runnable {
  val STREAM_NAME = "home-kinesis"
  val PARTITION_KEY = "001"

  def run {
    while (true) {
      val record = sourceFiles.getByteBufferRecord
      logger.info("sending " + record)
      val result = producer.addUserRecord(STREAM_NAME, PARTITION_KEY, record).get()
      if (result.isSuccessful)
        logger.info("Put record into shard " + result.getShardId)
      else {
        logger.info("Error putting records" + result.getShardId)
      }
      Thread.sleep(1000)
    }
  }
}

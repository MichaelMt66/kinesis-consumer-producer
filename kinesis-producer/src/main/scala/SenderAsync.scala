import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import com.amazonaws.services.kinesis.producer.{KinesisProducer, UserRecordFailedException, UserRecordResult}
import com.google.common.util.concurrent.{FutureCallback, Futures}
import org.slf4j.{Logger, LoggerFactory}
import utils.sourceFiles

class SenderAsync(
                   val producer: KinesisProducer,
                   val logger: Logger = LoggerFactory.getLogger("SenderAync")
                 ) extends Runnable {

  val STREAM_NAME = "home-kinesis"
  val callback = new FutureCallback[UserRecordResult] {

    def onFailure(t: Throwable): Unit = { // If we see any failures, we will log them.
      val attempts = t.asInstanceOf[UserRecordFailedException].getResult.getAttempts.size - 1
      if (t.isInstanceOf[UserRecordFailedException]) {
        val last = t.asInstanceOf[UserRecordFailedException].getResult.getAttempts.get(attempts)
        if (attempts > 1) {
          val previous = t.asInstanceOf[UserRecordFailedException].getResult.getAttempts.get(attempts - 1)
          logger.error(String.format("Record failed to put - %s : %s. Previous failure - %s : %s", last.getErrorCode, last.getErrorMessage, previous.getErrorCode, previous.getErrorMessage))
        }
        else logger.error(String.format("Record failed to put - %s : %s.", last.getErrorCode, last.getErrorMessage))
      }
    }

    def onSuccess(result: UserRecordResult): Unit = {
      logger.info("Record sent successfully")
    }

  }

  def time = {
    new SimpleDateFormat("YYYY/MM/dd HH:mm:ss").format(new Date)
  }

  def run {
    while (true) {
      val timestamp = time
      val record = sourceFiles.getRecord() + s",${timestamp}"
      logger.info("Sending Record " + record)
      val partitionKey = record.split(",")(0)
      logger.info("Parition key : " + partitionKey)
      val byteBufferRecord = ByteBuffer.wrap(record.getBytes("UTF-8"))
      val f = producer.addUserRecord(STREAM_NAME, partitionKey, byteBufferRecord)
      Futures.addCallback(f, callback)
      //producer.flush()
      Thread.sleep(1000)
    }
  }
}
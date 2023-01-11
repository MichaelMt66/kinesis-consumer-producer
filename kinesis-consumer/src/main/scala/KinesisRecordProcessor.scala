import java.nio.charset.Charset
import org.slf4j.LoggerFactory
import software.amazon.kinesis.lifecycle.events.{InitializationInput, ProcessRecordsInput, ShardEndedInput, ShutdownRequestedInput}
import software.amazon.kinesis.processor.{ShardRecordProcessor, ShardRecordProcessorFactory}
import software.amazon.kinesis.retrieval.KinesisClientRecord
import software.amazon.kinesis.exceptions.InvalidStateException
import software.amazon.kinesis.exceptions.ShutdownException
import scala.collection.JavaConverters._

class SampleRecordProcessorFactory extends ShardRecordProcessorFactory {
  override def shardRecordProcessor(): ShardRecordProcessor = new SampleRecordProcessor()
}


class SampleRecordProcessor extends ShardRecordProcessor {
  private val decoder = Charset.forName("UTF-8").newDecoder()
  private val logger = LoggerFactory.getLogger(this.getClass)
  private var Shardxd: String = _

  override def initialize(initializationInput: InitializationInput): Unit = {

    Shardxd = initializationInput.shardId()
    try {
      logger.info("Initializing + " + initializationInput.extendedSequenceNumber())
    }
  }

  override def processRecords(processRecordsInput: ProcessRecordsInput): Unit = {

    try {
      processRecordsInput.records().asScala.foreach(record => {
        logger.info(s"Processing record: Partition key = ${record.partitionKey()}, Data = ${record.data()}")
        processRecord(record)
      }
      )
      //doing checkpoint after read record
      processRecordsInput.checkpointer().checkpoint()
    } catch {
      case t: Throwable => logger.error(s"Caught error while processing records.  Aborting !!!: ${t.getMessage}")
    }
  }

  def processRecord(record: KinesisClientRecord): Unit = {

    try {
      val data = decoder.decode(record.data()).toString
      logger.info(data)
    }
  }

  override def shutdownRequested(shutdownRequestedInput: ShutdownRequestedInput): Unit = {

    import software.amazon.kinesis.exceptions.ShutdownException
    try {
      logger.info("Scheduler is shutting down, checkpointing.")
      shutdownRequestedInput.checkpointer.checkpoint
    } catch {
      case s: ShutdownException => logger.error("Exception while checkpointing at requested shutdown. Do something please!!!.", s)
      case i: InvalidStateException => logger.error("Exception while checkpointing at requested shutdown. Do something please!!!.", i)
    }
  }

  import software.amazon.kinesis.lifecycle.events.LeaseLostInput

  def leaseLost(leaseLostInput: LeaseLostInput): Unit = {
    logger.error("leaseLostInput ", leaseLostInput.toString)
  }


  def shardEnded(shardEndedInput: ShardEndedInput): Unit = {

    try {
      logger.info("Reached shard end checkpointing.")
      shardEndedInput.checkpointer.checkpoint
    } catch {
      case s: ShutdownException => logger.error("Exception while checkpointing at shard end. Do something please!!.", s)
      case i: InvalidStateException => logger.error("Exception while checkpointing at shard end. Do something please!.", i)
    }

  }

}
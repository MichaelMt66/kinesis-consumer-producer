import org.slf4j.LoggerFactory
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient
import software.amazon.kinesis.common.{ConfigsBuilder, InitialPositionInStream, InitialPositionInStreamExtended}
import software.amazon.kinesis.coordinator.Scheduler
import java.util.UUID


class Consumer extends Runnable {
  private val logger = LoggerFactory.getLogger(this.getClass)
  private val REGION = Region.US_EAST_1
  private val STREAM_NAME = "home-kinesis"
  private val APPLICATION_NAME = "dummy-kinesis-consumer"
  private val threadName = Thread.currentThread().getName()
  private val kinesisClient = KinesisAsyncClient.builder().credentialsProvider(EnvironmentVariableCredentialsProvider.create()).region(REGION).build()
  private val dynamoClient = DynamoDbAsyncClient.builder().credentialsProvider(EnvironmentVariableCredentialsProvider.create()).region(REGION).build()
  private val cloudWatchClient = CloudWatchAsyncClient.builder().credentialsProvider(EnvironmentVariableCredentialsProvider.create()).region(REGION).build()
  private val configsBuilder = new ConfigsBuilder(
    STREAM_NAME,
    APPLICATION_NAME,
    kinesisClient,
    dynamoClient,
    cloudWatchClient,
    UUID.randomUUID().toString(),
    new SampleRecordProcessorFactory()
  )

  override def run(): Unit = {
    getSchedular.run()
  }

  def getSchedular = new Scheduler(
    configsBuilder.checkpointConfig,
    configsBuilder.coordinatorConfig,
    configsBuilder.leaseManagementConfig,
    configsBuilder.lifecycleConfig,
    configsBuilder.metricsConfig,
    configsBuilder.processorConfig,
    configsBuilder.retrievalConfig().initialPositionInStreamExtended(
      InitialPositionInStreamExtended.newInitialPosition(InitialPositionInStream.LATEST)
    ))


}


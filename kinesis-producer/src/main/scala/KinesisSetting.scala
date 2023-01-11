import com.amazonaws.auth.DefaultAWSCredentialsProviderChain
import com.amazonaws.services.kinesis.producer.KinesisProducerConfiguration

object KinesisSetting {

  //https://docs.aws.amazon.com/sdk-for-java/v1/developer-guide/credentials.html

  val config = new KinesisProducerConfiguration()
    .setCredentialsProvider(new DefaultAWSCredentialsProviderChain())
    .setRegion("us-east-1")
    .setRecordMaxBufferedTime(20000) //Maximum amount of time (milliseconds) a record may spend being buffered being sent
    .setRequestTimeout(600) //The maximum total time (milliseconds) elapsed between when we begin a HTTP request and receiving all of the response. If it goes over, the request  will be timed-out
    .setAggregationMaxSize(5120) //Maximum number of bytes to pack into an aggregated Kinesis record.
    .setAggregationEnabled(true)

}

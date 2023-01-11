lazy val root = (project in file(".")).
  settings(
    name := "dummyKinesisProducer",
    version := "2.0",
    scalaVersion := "2.12.7",
    resourceDirectory in assembly := file("."),
    assemblyJarName in assembly := "dummyKinesisProducer.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )

libraryDependencies ++= Seq(
  "com.amazonaws" % "amazon-kinesis-producer" % "0.12.11",
  "com.amazonaws" % "aws-java-sdk-dynamodb" % "1.11.443",
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "com.typesafe" % "config" % "1.3.3"
)
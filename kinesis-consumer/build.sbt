lazy val root = (project in file(".")).
  settings(
    name := "dummyKinesisConsumer",
    version := "2.0",
    scalaVersion := "2.12.7",
    resourceDirectory in assembly := file("."),
    assemblyJarName in assembly := "dummyKinesisConsumer.jar",
    assemblyMergeStrategy in assembly := {
      case PathList("META-INF", xs@_*) => MergeStrategy.discard
      case x => MergeStrategy.first
    }
  )


libraryDependencies ++= Seq(
  "software.amazon.kinesis" % "amazon-kinesis-client" % "2.2.7",
  "org.slf4j" % "slf4j-simple" % "1.7.30",
  "com.typesafe" % "config" % "1.3.3"
)

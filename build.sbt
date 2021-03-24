name := "dedupe_spark_sample"

version := "0.1"

scalaVersion := "2.12.5"

val sparkVersion = "3.0.2"
val scalaTestVersion = "3.0.5"

libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-streaming" % sparkVersion,
  "org.apache.spark" %% "spark-sql" % sparkVersion,
  "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion,
  "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
)
import TestUtility.{InMemoryStoreWriter, testEvenTransform}
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.execution.streaming.MemoryStream
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpec}

class MemoryExampleSpec extends WordSpec with Matchers with BeforeAndAfterAll{


  "MemoryExample " should {

    "remove duplicates for a ArrayLevelData having duplicate records " in {
      val sparkSession = SparkSession.builder().master("local[*]").getOrCreate()
      sparkSession.sparkContext.setLogLevel("ERROR")
      import sparkSession.implicits._

      val inputData = Seq(Person(1, 4),Person(2,8),Person(3,7),Person(4,89))
      val expectedData = Seq(Person(2,8),Person(4,89))

      val testKey = "person-test-1"
      val inputStream: MemoryStream[Person] = new MemoryStream[Person](1, sparkSession.sqlContext)
      inputStream.addData(inputData)

      testEvenTransform(inputStream.toDS())
        .writeStream
        .outputMode("append")
        .foreach {
          new InMemoryStoreWriter[Person](testKey, data => data)
        }.start()
        .awaitTermination(15000)

      val transformedData = InMemoryStoreWriter.getValues(testKey)
      sparkSession.close()

      transformedData.sortBy(_.id) shouldBe expectedData.sortBy(_.id)
    }
  }
}

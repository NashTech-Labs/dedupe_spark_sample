import org.apache.spark.sql.{Dataset, ForeachWriter}

import scala.collection.mutable
import scala.collection.mutable.ListBuffer

object TestUtility {

  def testEvenTransform(ds:Dataset[Person]): Dataset[Person] ={
    ds.filter(_.id % 2 == 0)
  }

  class InMemoryStoreWriter[T](
                                key: String,
                                rowExtractor: T => Person) extends ForeachWriter[T] {

    override def open(partitionId: Long, version: Long): Boolean = true

    override def process(row: T): Unit = {
      InMemoryStoreWriter.addValue(key, rowExtractor(row))
    }

    override def close(errorOrNull: Throwable): Unit = {}
  }

  object InMemoryStoreWriter {
    private val data = new mutable.HashMap[String, mutable.ListBuffer[Person]]()

    def addValue(key: String, value: Person): Option[ListBuffer[Person]] = {
      data.synchronized {
        val values = data.getOrElse(key, new mutable.ListBuffer[Person]())
        values.append(value)
        data.put(key, values)
      }
    }

    def getValues(key: String): mutable.ListBuffer[Person] = data.getOrElse(key, ListBuffer.empty)
  }

}

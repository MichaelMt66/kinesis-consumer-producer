package utils

import java.nio.ByteBuffer
import scala.io.Source
import scala.util.Random

object sourceFiles {

  def jsonData(): Seq[String] = {
    val path = "/sampleData/transaction.csv"
    val filename = getClass.getResource(path)
    val dataLines = Source.fromURL(filename).getLines()
    dataLines.toSeq
  }

  def getByteBufferRecord():ByteBuffer = {
    ByteBuffer.wrap(getRandomRecord(jsonData()).getBytes("UTF-8"))
  }

  def bufferByteData(data: Seq[String]): Seq[ByteBuffer] = {
    data.map(line => ByteBuffer.wrap(line.getBytes("UTF-8")))
  }

  def getRandomData(arr: Seq[String]): Seq[String] = {
    val random = new Random
    for (i <- 1 to 10) yield arr(random.nextInt(arr.length))
  }

  def getRandomRecord(arr: Seq[String]): String = {
    val random = new Random
    arr(random.nextInt(arr.length))
  }

  def getData(): Seq[String] = {
    getRandomData(jsonData)
  }

  def getRecord(): String = {
    getRandomRecord(jsonData())
  }
}

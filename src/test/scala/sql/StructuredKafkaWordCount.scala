package sql

import org.apache.spark.sql.SparkSession

/**
  * Created by Administrator on 2016/11/29.
  */
object StructuredKafkaWordCount {

    def main(args: Array[String]) {
      val host = "localhost"
      val port = 9092
      val spark = SparkSession
        .builder
        .appName("StructuredNetworkWordCount")
        .master("local[2]")
        .getOrCreate()
      import spark.implicits._
      // Create DataFrame representing the stream of input lines from connection to host:port
      val lines = spark.readStream
        .format("socket")
        .option("host", host)
        .option("port", port)
        .load().as[String]

      // Split the lines into words
      val words = lines.flatMap(_.split(" "))

      // Generate running word count
      val wordCounts = words.groupBy("value").count()

      // Start running the query that prints the running counts to the console
      val query = wordCounts.writeStream
        .outputMode("complete")
        .format("console")
        .start()

      query.awaitTermination()
    }
}

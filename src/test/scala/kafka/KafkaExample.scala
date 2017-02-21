package kafka

/**
  * Created by Administrator on 2016/11/2.
  */

import _root_.kafka.serializer.StringDecoder

import org.apache.spark.SparkConf
import org.apache.spark.streaming._
import org.apache.spark.streaming.kafka.KafkaUtils

/**
  * Consumes messages from one or more topics in Kafka and does wordcount.
  * Usage: KafkaWordCount <zkQuorum> <group> <topics> <numThreads>
  *   <zkQuorum> is a list of one or more zookeeper servers that make quorum
  *   <group> is the name of kafka consumer group
  *   <topics> is a list of one or more kafka topics to consume from
  *   <numThreads> is the number of threads the kafka consumer should use
  *
  * Example:
  *    `$ bin/run-example \
  *      org.apache.spark.examples.streaming.KafkaWordCount zoo01,zoo02,zoo03 \
  *      my-consumer-group topic1,topic2 1`
  */
object KafkaWordCount {
  def main(args: Array[String]) {

//    val Array(brokers, topics) = args
    val brokers = "121.42.141.232:9092,115.28.156.126:9092,121.42.60.39:9092"
    val topics = "test2"
    StreamingExamples.setStreamingLogLevels()
    val sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local")
    val ssc = new StreamingContext(sparkConf, Seconds(2))
    ssc.checkpoint("checkpoint")

    // Create direct kafka stream with brokers and topics
    val topicsSet = topics.split(",").toSet
    val kafkaParams = Map[String, String]("metadata.broker.list" -> brokers)

    val messages = KafkaUtils.createDirectStream[String, String,StringDecoder,StringDecoder](ssc, kafkaParams, topicsSet)

    // Get the lines, split them into words, count the words and print
    val lines = messages.map(_._2)
    val words = lines.flatMap(_.split(" "))
    println(words.count())
//    val wordCounts = words.map(x => (x, 1L)).reduceByKey(_ + _)
//    wordCounts.print()



    ssc.start()
    ssc.awaitTermination()
  }
}

package kafka

import kafka.serializer.StringDecoder
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Minutes, Seconds, StreamingContext}

/**
  * Created by Administrator on 2016/11/2.
  */


object WordCount {
  def main(args: Array[String]) {
    val zk = "127.0.0.1:2181"
//    val zk = "121.42.141.232:2181,115.28.156.126:2181,121.42.60.39:2181"
    val gp = "group1"
    val top = "test"
    val num = "4"
    val Array(zkQuorum, group, topics, numThreads) = Array(zk,gp,top,num);
    val sparkConf = new SparkConf().setAppName("KafkaWordCount").setMaster("local")
    sparkConf.set("es.index.auto.create", "true")
    val ssc = new StreamingContext(sparkConf, Seconds(2))




/*    val topics = Set("test")
//    val brokers = "121.42.141.232:9092,115.28.156.126:9092,121.42.60.39:9092"
    val borkers = "localhost:2181"
    val kafkaParams = Map[String, String](

    "metadata.broker.list" -> brokers, "serializer.class" -> "kafka.serializer.StringEncoder")*/
    // Create a direct stream
//    val lines = KafkaUtils.createDirectStream[String, String, StringDecoder, StringDecoder](ssc, kafkaParams, topics)

    ssc.checkpoint("checkpoint")
    /*val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)
    val userClicks = lines.flatMap(x => {println(x._2);Some(x)})
    userClicks.foreachRDD(rdd=>{rdd.foreachPartition(partition=>partition.foreach(pair=> {
      println(pair)
      println("111111111111111111111111111111111111111111111111")
    }
    ) )
    })*/



   /* val topicMap =  Map[String, Int]("TEST-TOPIC"->4)
    val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
    val words = lines.flatMap(_.split(" "))
    words.foreachRDD(x=>println(x.collect().toString))
    val wordCounts = words.map(x => (x, 1L))
      .reduceByKeyAndWindow(_ + _, _ - _, Seconds(2), Seconds(2), 2)
    wordCounts.saveAsTextFiles("/out")
    wordCounts.print()*/

      val topicMap = topics.split(",").map((_, numThreads.toInt)).toMap
      val lines = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap).map(_._2)
      val words = lines.flatMap(_.split(" "))
      val wordCounts = words.map(x => (x, 1L))
        .reduceByKeyAndWindow(_ + _, _ - _, Seconds(2), Seconds(2), 2)

      wordCounts.print()


    /*val kafkaStream = KafkaUtils.createStream(ssc, zkQuorum, group, topicMap)
    val events = kafkaStream.flatMap(line => {
      val data = JSONObject.fromObject(line._2)
      Some(data)
    })

    // Compute user click times
    val userClicks = events.map(x => (x.getString("uid"), x.getInt("click_count"))).reduceByKey(_ + _)
    userClicks.foreachRDD(rdd => {
      rdd.foreachPartition(partitionOfRecords => {
        partitionOfRecords.foreach(pair => {
          val uid = pair._1
          val clickCount = pair._2
          val jedis = RedisClient.pool.getResource
          jedis.select(dbIndex)
          jedis.hincrBy(clickHashKey, uid, clickCount)
          RedisClient.pool.returnResource(jedis)
        })
      })
    })*/
    ssc.start()
    ssc.awaitTermination()
  }
}

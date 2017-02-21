/**
  * Created by Administrator on 2016/11/3.
  */
import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.HashPartitioner
import org.apache.spark.streaming.Duration

object WebPagePopularityValueCalculator {
  private val checkpointDir = "popularity-data-checkpoint"
  private val msgConsumerGroup = "user-behavior-topic-message-consumer-group"

  def main(args: Array[String]) {
    val zkServers = "121.42.141.232:2181,115.28.156.126:2181,121.42.60.39:2181"
    val processingInterval = 2
    val conf = new SparkConf().setAppName("Web Page Popularity Value Calculator").setMaster("spark://127.42.141.232:7077")
    val ssc = new StreamingContext(conf, Seconds(processingInterval.toInt))
    //using updateStateByKey asks for enabling checkpoint
    ssc.checkpoint(checkpointDir)
    val kafkaStream = KafkaUtils.createStream(
      //Spark streaming context
      ssc,
      //zookeeper quorum. e.g zkserver1:2181,zkserver2:2181,...
      zkServers,
      //kafka message consumer group ID
      msgConsumerGroup,
      //Map of (topic_name -> numPartitions) to consume. Each partition is consumed in its own thread
      Map("TEST-TOPIC" -> 3))
    val msgDataRDD = kafkaStream.map(_._2)
    val popularityData = msgDataRDD.map { msgLine =>
    {
      val dataArr: Array[String] = msgLine.split("\\|")
      val pageID = dataArr(0)
      //calculate the popularity value
      val popValue: Double = dataArr(1).toFloat * 0.8 + dataArr(2).toFloat * 0.8 + dataArr(3).toFloat * 1
      (pageID, popValue)
    }
    }
    //sum the previous popularity value and current value
    val updatePopularityValue = (iterator: Iterator[(String, Seq[Double], Option[Double])]) => {
      iterator.flatMap(t => {
        val newValue:Double = t._2.sum
        val stateValue:Double = t._3.getOrElse(0);
        Some(newValue + stateValue)
      }.map(sumedValue => (t._1, sumedValue)))
    }
    val initialRDD = ssc.sparkContext.parallelize(List(("page1", 0.00)))
    val stateDstream = popularityData.updateStateByKey[Double](updatePopularityValue,
      new HashPartitioner(ssc.sparkContext.defaultParallelism), true, initialRDD)
    //set the checkpoint interval to avoid too frequently data checkpoint which may
    //may significantly reduce operation throughput
    stateDstream.checkpoint(Duration(8*processingInterval.toInt*1000))
    //after calculation, we need to sort the result and only show the top 10 hot pages
    stateDstream.foreachRDD { rdd => {
      val sortedData = rdd.map{ case (k,v) => (v,k) }.sortByKey(false)
      val topKData = sortedData.take(10).map{ case (v,k) => (k,v) }
      topKData.foreach(x => {
        println(x)
      })
    }
    }
    ssc.start()
    ssc.awaitTermination()
  }
}

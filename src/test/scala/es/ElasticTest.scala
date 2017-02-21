package es

/**
  * Created by lsy on 2016/8/26.
  */

import org.apache.spark.{rdd, SparkContext}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{SparkSession, SQLContext}
import org.elasticsearch.spark.rdd.EsSpark
import org.apache.spark.SparkContext._
import org.elasticsearch.spark.sql._
import org.elasticsearch.spark._
case class Person(id: String, name: String, age: Int)
class ElasticTest {


  def test(): Unit ={
    val userClicks = List[Tuple3[String,String,Int]](Tuple3("lsy","apple",1))


    val sc = new SparkContext("local", "Simple App")
    val sqlContext = new SQLContext(sc)
    val people = sqlContext.esDF("data/table")
    // check the associated schema
    println(people.schema.treeString)


    val options = Map("pushdown" -> "true", "es.nodes" -> "localhost", "es.port" -> "9200")
    val spark14DF = sqlContext.read.format("org.elasticsearch.spark.sql").options(options).load("test/test")
    def addIndex(person:Person): Unit = {
      val rdd = sc.makeRDD(Seq(person))
      EsSpark.saveToEs(rdd ,"data/people")
    }
  }


  //注册为tempTable
    //      logDataFrame.registerTempTable("daplog")
    //      //查询该批次的pv,ip数,uv
    //      val logCountsDataFrame =
    //        sqlContext.sql("select date_format(current_timestamp(),'yyyy-MM-dd HH:mm:ss') as time,count(1) as pv,count(distinct ip) as ips,count(distinct cookieid) as uv from daplog")
    //      //打印查询结果
    //      logCountsDataFrame.show()

}

object ElasticTest{


  def main(args: Array[String]) {
    val sc = new SparkContext("local", "Simple App")
//    val sc = SparkSession
//      .builder()
//      .appName("Spark SQL basic example")
//      .config("spark.some.config.option", "some-value")
//      .master("local[2]")
//      .getOrCreate()

    val numbers = Map("one" -> 1, "two" -> 2, "three" -> 3)
    val airports = Map("arrival" -> "Otopeni", "SFO" -> "San Fran")

    sc.makeRDD(Seq(numbers, airports)).saveToEs("spark/docs")
    //注册为tempTable
//          logDataFrame.registerTempTable("daplog")
//          //查询该批次的pv,ip数,uv
//          val logCountsDataFrame =
//            sqlContext.sql("select date_format(current_timestamp(),'yyyy-MM-dd HH:mm:ss') as time,count(1) as pv,count(distinct ip) as ips,count(distinct cookieid) as uv from daplog")
//          //打印查询结果
//          logCountsDataFrame.show()
  }


}

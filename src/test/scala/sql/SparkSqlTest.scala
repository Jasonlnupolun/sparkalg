package sql

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.SparkSession._

/**
  * Created by Administrator on 2016/11/29.
  */
class SparkSqlTest {

  def getSparkSession():SparkSession={
   val spark = SparkSession.builder().appName("sqltest")
    .config("","")
    .master("local[2]")
    .getOrCreate()
  spark
  }
}

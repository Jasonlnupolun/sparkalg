package kafka

/**
  * Created by Administrator on 2016/11/2.
  */

import org.apache.log4j.{Level, Logger}

import org.apache.spark.internal.Logging

/** Utility functions for Spark Streaming examples. */
object StreamingExamples  {

  /** Set reasonable logging levels for streaming if the user has not configured log4j. */
  def setStreamingLogLevels() {
    val log4jInitialized = Logger.getRootLogger.getAllAppenders.hasMoreElements
    if (!log4jInitialized) {
      // We first log something to initialize Spark's default logging, then we override the
      // logging level.
//      Logger.getRootLogger.setLevel(Level.WARN)
    }
  }
}
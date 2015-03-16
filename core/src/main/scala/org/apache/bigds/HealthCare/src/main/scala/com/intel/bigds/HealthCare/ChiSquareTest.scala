package com.intel.bigds.HealthCare.stat

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.mllib.stat.test.PatchedChiSq
import org.apache.spark.rdd.RDD
import org.apache.spark.{SparkContext, SparkConf}
import org.apache.spark.mllib.stat._
import com.intel.bigds.HealthCare.preprocessing._
import org.apache.spark.mllib.linalg.{Matrices, Matrix, Vector, Vectors}

object ChisqwithData {
  def main(args:Array[String]): Unit ={
    if (args.length != 2){
      System.err.println("parameter required: <spark master address> <file address> ")
      System.exit(1)
    }

    val conf = new SparkConf()
      .setMaster(args(0))
      .setAppName("Chisquare test with Synthesized Data")

    val sc = new SparkContext(conf)

    val file_address = args(1)
    val data = sc.textFile(file_address).map(i => i.split(",")).zipWithIndex().filter(_._2 > 0).map(_._1)

    //In this case, Chi-square two-sample test is conducted on column(1,7), (2, 7), (3, 7), (5, 7)
    val test_data = DataContainer.To_LabeledData(data, Array(1,2,3,5), 7)

    println("=============printed test data============")
    test_data.foreach{case LabeledPoint(label, features) => println(label + " || " + features.toArray.mkString(" ")) }
    val independenceResult = PatchedChiSq.chiSquaredFeatures(test_data)

    println("=============ChiSquare independence test result=============")
    independenceResult.foreach(i => {
      println(i.toString())
    })

    val example_data = "1,2,3,1,2,3,3,3,2,1,3 \n" +
                       "2,1,2,1,1,1,1,2,3,2,2 \n" +
                       "1,2,1,2,3,2,3,1,2,1,3 \n" +
                       "3,3,2,3,2,1,3,1,3,3,1 \n" +
                       "1,2,3,1,1,1,1,2,2,2,3 \n" +
                       "1,2,1,2,1,2,3,2,1,2,1"

    val input_data:RDD[Array[String]] = sc.parallelize(example_data.split("\n").map(i => i.trim.split(",")))

    val aggregated_data = DataContainer.DataAggregate(input_data) //Array[Array[Int]]
    println("==============data aggregation in batch result=============")
    println(aggregated_data._1.mkString(","))
    println(aggregated_data._2.map(i => i.mkString(",")).mkString("\n"))



    val DataToUse = aggregated_data._2.zipWithIndex
    val num_attributes = aggregated_data._2.length
    val br_num_attributes = sc.broadcast(num_attributes)

//broadcast all data. compute each pairs containing a specific feature seperately. This part can be extracted into a PairScan function in DataContainer
//is it meaningful to conduct PairScan for independent tests?
    val br_DataToUse = sc.broadcast(DataToUse)
    val pValue_results = sc.parallelize(DataToUse).flatMap { case (data, index) => {
       val data1 = Vectors.dense(data)
       val index1 = index
       for (i <- index + 1 until num_attributes) yield {
         val data2_row = br_DataToUse.value(i)
         val data2 = Vectors.dense(data2_row._1)
         val pValue = PatchedChiSq.chiSquared(data1, data2).pValue
         (pValue,(index1, i))
       }
      }
    }
    println("===========================pvalues top 10 with pair numbers===========================")
    pValue_results.sortBy(_._1,false).take(10).foreach(println _)


  }
}
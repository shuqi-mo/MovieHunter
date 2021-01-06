package offline

import org.apache.spark.mllib.feature.Word2VecModel
import org.apache.spark.mllib.feature.Word2Vec
import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.expressions.UserDefinedFunction
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Row
import org.apache.spark.sql.functions._
import scala.collection.mutable.ArrayBuffer
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter

object Embedding {
  // 电影序列
  def processItemSequence(sparkSession: SparkSession, rawSampleDataPath: String) : RDD[Seq[String]] = {
    val ratingsResourcesPath = this.getClass.getResource(rawSampleDataPath)
    val ratingSamples = sparkSession.read.format("csv").option("header", "true").load(ratingsResourcesPath.getPath)
    val sortUdf: UserDefinedFunction = udf((rows: Seq[Row]) => {
      rows.map{ case Row(movieId: String, timestamp: String) => (movieId, timestamp) }
        .sortBy{ case (_, timestamp) => timestamp }
        .map{ case (movieId, _) => movieId }
    })
    ratingSamples.printSchema();
    // 用户评分序列
    val userSeq = ratingSamples
      .where(col("rating") >= 3.5)
      .groupBy("userId")
      .agg(sortUdf(collect_list(struct("movieId", "timestamp"))) as "movieIds")
      .withColumn("movieIdStr", array_join(col("movieIds"), " "))
    userSeq.select("userId", "movieIdStr").show(10, truncate = false)
    userSeq.select("movieIdStr").rdd.map(r => r.getAs[String]("movieIdStr").split(" ").toSeq)
  }

  def trainItem2vec(sparksession: SparkSession, samples: RDD[Seq[String]], embLength: Int, embOutputFilename: String): Word2VecModel = {
    val word2Vec = new Word2Vec()
      .setVectorSize(embLength)
      .setWindowSize(5)
    val model = word2Vec.fit(samples)
    val embFolderPath = this.getClass.getResource("/webroot/modeldata/")
    val file = new File(embFolderPath.getPath + embOutputFilename)
    val bw = new BufferedWriter(new FileWriter(file))
    for(movieId <- model.getVectors.keys)
      bw.write(movieId + ":" + model.getVectors(movieId).mkString(" ") + "\n")
    bw.close()
    model
  }

  def generateUserEmb(sparkSession: SparkSession, rawSampleDataPath: String, word2VecModel: Word2VecModel, embLength:Int, embOutputFilename:String): Unit = {
    val ratingsResourcesPath = this.getClass.getResource(rawSampleDataPath)
    val ratingSamples = sparkSession.read.format("csv").option("header", "true").load(ratingsResourcesPath.getPath)
    ratingSamples.show(10, false);
    val userEmbeddings = new ArrayBuffer[(String, Array[Float])]()
    ratingSamples.collect().groupBy(_.getAs[String]("userId"))
      .foreach(user => {
        val userId = user._1
        var userEmb = new Array[Float](embLength)
        userEmb = user._2.foldRight[Array[Float]](userEmb)((row, newEmb) => {
          val movieId = row.getAs[String]("movieId")
          val movieEmb = word2VecModel.getVectors.get(movieId)
          if(movieEmb.isDefined)
            newEmb.zip(movieEmb.get).map { case(x, y) => x + y}
          else
            newEmb
        })
        userEmbeddings.append((userId, userEmb))
      })
    val embFolderPath = this.getClass.getResource("/webroot/modeldata/")
    val file = new File(embFolderPath.getPath + embOutputFilename)
    val bw = new BufferedWriter(new FileWriter(file))
    for(userEmb <- userEmbeddings)
      bw.write(userEmb._1 + ":" + userEmb._2.mkString(" ") + "\n")
    bw.close()
  }

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName("ctrModel")
      .set("spark.submit.deployMode", "client")
    val spark = SparkSession.builder.config(conf).getOrCreate()
    val rawSampleDataPath = "/webroot/sampledata/ratings.csv"
    val embLength = 10
    val samples = processItemSequence(spark, rawSampleDataPath)
    val model = trainItem2vec(spark, samples, embLength, "item2vecEmb.csv")
    generateUserEmb(spark, rawSampleDataPath, model, embLength, "userEmb.csv")
  }
}

package training.erik121212.nl.naivebayes

import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD

object RunJob {

  def main(args: Array[String]): Unit = {

    import org.apache.spark.{SparkConf, SparkContext}

    val conf = new SparkConf().setMaster("local").setAppName("Naive Bayes")
    val sc = new SparkContext(conf)

    import org.apache.spark.mllib.classification.{NaiveBayes, NaiveBayesModel}
    import org.apache.spark.mllib.util.MLUtils


    // Split data into training (60%) and test (40%).
    //val Array(training, test) = data.randomSplit(Array(0.6, 0.4))
    //val training = MLUtils.loadLibSVMFile(sc, "file:///tmp/training.txt")
    val training = MLUtils.loadLibSVMFile(sc, "spark-core\\naive-bayes\\src\\main\\resources\\sparkdata\\training.txt")
    val test: RDD[LabeledPoint] = MLUtils.loadLibSVMFile(sc, "spark-core\\naive-bayes\\src\\main\\resources\\sparkdata\\test.txt")



    val model: NaiveBayesModel = NaiveBayes.train(training, lambda = 1.0, modelType = "multinomial")

    val prediction = test.map {
      p => (model.predict(p.features))

    }
    prediction.foreach({
      prediction => System.out.println(s"""Prediction: ${prediction}   """)
    })



    val predictionAndLabel: RDD[(Double, Double)] = test.map {

      p => (model.predict(p.features), p.label)
    }


    predictionAndLabel.foreach({
      pAndL => System.out.println(s"""Feature: ${pAndL._1}   Label: ${pAndL._2}""")
    })

    val accuracy = 1.0 * predictionAndLabel.filter(x => x._1 == x._2).count() / test.count()
    System.out.println(s"""Accuracy: ${accuracy}""")

        // Save and load model
    //model.save(sc, "file:///tmp/myNaiveBayesModel2")
    //   model.save(sc, "file:///tmp/myNaiveBayesModel")
    //   val sameModel = NaiveBayesModel.load(sc, "target/tmp/myNaiveBayesModel")

  }


}


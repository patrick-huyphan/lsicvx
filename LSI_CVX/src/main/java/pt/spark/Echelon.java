/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.util.Arrays;
import java.util.LinkedList;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

/**
 *
 * @author patrick_huy
 * Use to reduce matrix size, get the core of matrix, remove independent feature vector
 * 
 */
public class Echelon {
  private static final Logger LOGGER = LoggerFactory.getLogger(Echelon.class);

  /**
   * The task body
   */
  public void run(String master, String inputFilePath, String outFilePath) {
    /*
     * This is the address of the Spark cluster. We will call the task from WordCountTest and we
     * use a local standalone cluster. [*] means use all the cores available.
     * See {@see http://spark.apache.org/docs/latest/submitting-applications.html#master-urls}.
     */
//    String master = "local[*]";
    /*
     * Initialises a Spark context.
     */
    SparkConf conf = new SparkConf()
        .setAppName(Echelon.class.getName())
        .setMaster(master);
    JavaSparkContext context = new JavaSparkContext(conf);

    /*
     * Performs a work count sequence of tasks and prints the output with a logger.
     */
    context.textFile(inputFilePath)
        .flatMap(text -> Arrays.asList(text.split(" ")).iterator())
        .mapToPair(word -> new Tuple2<>(word, 1))
        .reduceByKey((a, b) -> a + b)
        .foreach(result -> LOGGER.info(
            String.format("Word [%s] count [%d].", result._1(), result._2)));
    
        double[][] array = {{1.12, 2.05, 3.12}, {5.56, 6.28, 8.94}, {10.2, 8.0, 20.5}};
  LinkedList<Vector> rowsList = new LinkedList<>();
  for (int i = 0; i < array.length; i++) {
    Vector currentRow = Vectors.dense(array[i]);
    rowsList.add(currentRow);
  }
  JavaRDD<Vector> rows = context.parallelize(rowsList);

  // Create a RowMatrix from JavaRDD<Vector>.
  RowMatrix mat = new RowMatrix(rows.rdd());
  
  
  context.stop();
  }    
}

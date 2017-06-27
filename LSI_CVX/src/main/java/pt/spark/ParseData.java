package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import scala.collection.Map;


/**
 * ParseData class, we will call this class to:
 * - read text data which already prepared( tokenize and remove stop work, reduce same meaning work).
 * - build VSM
 */
public class ParseData {
  /**
   * We use a logger to print the output. Sl4j is a common library which works with log4j, the
   * logging system used by Apache Spark.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ParseData.class);

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
        .setAppName(ParseData.class.getName())
        .setMaster(master);
    JavaSparkContext context = new JavaSparkContext(conf);

    /*
     * Performs a work count sequence of tasks and prints the output with a logger.
     */
    JavaRDD<String> row =  context.textFile(inputFilePath).flatMap(text -> Arrays.asList(text.split(" ")).iterator());
    
    JavaPairRDD<String, Integer > kv = row.mapToPair(word -> new Tuple2<>(word, 1)).reduceByKey((a, b) -> a + b);
    
    kv.foreach(result -> LOGGER.info(String.format("Word [%s] count [%d].", result._1(), result._2)));
   
    
    context.stop();
  }
}

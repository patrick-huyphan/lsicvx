package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * ADMM class, we will call this class with the test WordCountTest.
 */
public class ADMM {
  /**
   * We use a logger to print the output. Sl4j is a common library which works with log4j, the
   * logging system used by Apache Spark.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(ADMM.class);

  /**
   * This is the entry point when the task is called from command line with spark-submit.sh.
   * See {@see http://spark.apache.org/docs/latest/submitting-applications.html}
   */
  public static void main(String[] args) {
    checkArgument(args.length > 0, "Please provide the path of input file as first parameter.");
    new ADMM().run(args[0], args[1]);
  }

  /**
   * The task body
   */
  public void run(String inputFilePath, String outFilePath) {
    /*
     * This is the address of the Spark cluster. We will call the task from WordCountTest and we
     * use a local standalone cluster. [*] means use all the cores available.
     * See {@see http://spark.apache.org/docs/latest/submitting-applications.html#master-urls}.
     */
    /*
    TODO: update to load config from file
    */
    String master = "local[*]";

    /*
     * Initialises a Spark context.
     */
    SparkConf conf = new SparkConf()
        .setAppName(ADMM.class.getName())
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
    
    /*
    Flow process:
    - read matrix data D and B
    - proces ADMM
    */
  }
}

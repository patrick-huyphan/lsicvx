package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class main {

  /**
   * This is the entry point when the task is called from command line with spark-submit.sh.
   * See {@see http://spark.apache.org/docs/latest/submitting-applications.html}
   * args[0]: input doc
   * args[1]: output
   * args[2]: input query
   */
  public static void main(String[] args) {
    checkArgument(args.length > 0, "Please provide the path of input file as first parameter.");
    
    String master = "local[*]";
    // currently, not support: matrix data should be prepared before
    new ParseData().run(master, args[0],args[1]);
    
    // read output from parse data
    new Echelon().run(master, args[0],args[1]);
    
    // read output from echelon: 
    new SCC().run(master, args[0],args[1]);
    
    // read outpur from parse data and echelon and SCC: Ax-B
    new ADMM().run(master, args[0],args[1]);
    
    // read output from parse+ ADMM 
    new Query().run(master, args[0],args[1]);
  }
 
}

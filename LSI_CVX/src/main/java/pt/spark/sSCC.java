package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.LinkedList;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import breeze.linalg.*;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import org.apache.spark.broadcast.Broadcast;


/**
 * sSCC class, we will call this class to clustering data, return the row in same cluster
 - map data to master, calculate edge weigh
 - each executer calculate row data by: read Matrix, update D,X,V,U
  D,V,U related to edge
 */
public class sSCC {
  /**
   * We use a logger to print the output. Sl4j is a common library which works with log4j, the
   * logging system used by Apache Spark.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(sSCC.class);
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
//    /*
//     * Initialises a Spark context.
//     */
    SparkConf conf = new SparkConf()
        .setAppName(sSCC.class.getName())
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
      
  // Create a RowMatrix from JavaRDD<Vector>.
  RowMatrix mat = sCommonFunc.readMatrixToRowMatrix(context, array);

  
  // Compute the top 3 singular values and corresponding singular vectors.
//  SingularValueDecomposition<RowMatrix, Matrix> svd = mat.computeSVD(3, true, 1.0E-9d);
//  RowMatrix U = svd.U();
//  Vector s = svd.s();
//  Matrix V = svd.V();



  double rho = 0.8;
  double lamda = 0.6;
  double lamda2 = 0.6;
  double eps_abs= 1e-6;
  double eps_rel= 1e-6;
  
  context.broadcast(rho);
  
  Broadcast<RowMatrix> t = context.broadcast(mat);
  
  // each solveADMM process for 1 column of input matrix
    solveADMM();
            
    context.stop();
  }
  
  private void solveADMM()
  {
      boolean stop = false;
      while(stop)
      {
          calD();
          updateX();
          updateZ();
          updateU();
          
          stop = checkStop();
      }
  }


  private boolean checkStop()
  {
      updateRho();
      return false;
  }
  
  private void calD()
  {}
  
  
  private void updateX()
  {}
  
    private void updateU()
  {}
    
    private void updateZ()
  {}
    
  private void updateRho()
  {}     

      private void eps_primal()
  {}
    
    private void eps_dual()
  {}
}

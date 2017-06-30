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
import java.util.ArrayList;
import java.util.List;
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
   JavaRDD<Vector> matI = sCommonFunc.array2RDDVector(context, array);

  
  // Compute the top 3 singular values and corresponding singular vectors.
//  SingularValueDecomposition<RowMatrix, Matrix> svd = mat.computeSVD(3, true, 1.0E-9d);
//  RowMatrix U = svd.U();
//  Vector s = svd.s();
//  Matrix V = svd.V();


  /*
TODO: 
    - init E set
    - init U,V,D,X0
  */

  double rho = 0.8;
//  double lamda = 0.6;
//  double lamda2 = 0.6;
//  double eps_abs= 1e-6;
//  double eps_rel= 1e-6;
  
  List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = new ArrayList<>();  
//  context.broadcast(rho);
  Broadcast<Double>  lamda = context.broadcast(0.6);
  Broadcast<Double>  lamda2 = context.broadcast(0.01);
  Broadcast<Double>  eps_abs = context.broadcast(1e-6);
  Broadcast<Double>  eps_rel = context.broadcast(1e-6);
  Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>>  E = context.broadcast(eSet);
//  sCommonFunc.matrixToRDD(context, m)
  Broadcast<JavaRDD<Vector>> mat = context.broadcast(matI);
  
  

  // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
    solveADMM(context, mat, E, lamda, lamda2, eps_abs, eps_rel);
            
    context.stop();
  }
  
  private void solveADMM( JavaSparkContext sc, 
          Broadcast<JavaRDD<Vector>> mat,
          Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e,
          Broadcast<Double>  lamda,
          Broadcast<Double>  lamda2,
          Broadcast<Double>  eps_abs,
          Broadcast<Double>  eps_rel)
  {
      List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> D = null;
      List<Double> x = null;
      List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> z = null;
      List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> u = null; 
      boolean stop = false;
      
      while(stop)
      {
          D = calD(e, z, u);
          x = updateX(mat, e,D);
          z = updateZ(e,x,u,z);
          u = updateU(e,x,u,z);
          
          stop = checkStop();
      }
  }


  private boolean checkStop()
  {
      updateRho();
      return false;
  }
  
  private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> calD(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> U, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> V)
  {
      List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
      return ret;
  }
  
  
  private List<Double> updateX(Broadcast<JavaRDD<Vector>> mat, Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>>D)
  {
      List<Double> ret = null;
      return ret;
  }
  
    private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> updateU(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e, List<Double>x, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> U, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> V)
  {
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
      return ret;
  }
    
  private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> updateZ(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e, List<Double>x, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> U, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>>V)
  {
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
      return ret;
  }
    
  private void updateRho()
  {
      
  }     

    private void eps_primal()
  {
      
  }
    
    private void eps_dual()
  {
      
  }
}

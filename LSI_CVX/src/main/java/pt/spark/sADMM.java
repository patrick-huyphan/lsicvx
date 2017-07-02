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
import static com.google.common.base.Preconditions.checkArgument;
import org.apache.spark.broadcast.Broadcast;
//import org.apache.spark.mllib.optimization.tfocs.SolverL1RLS


/**
 * sADMM class, we will call this class to find projection matrix, user to convert matrix to latent space
 - map data to master
 - each executer call column matrix by: read matrix, update x,u,v
 * 
 */
public class sADMM {
  /**
   * We use a logger to print the output. Sl4j is a common library which works with log4j, the
   * logging system used by Apache Spark.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(sADMM.class);

  /**
   * The task body
   */
  public static LinkedList<Tuple2<Integer,Vector>> run(String master, 
          LinkedList<Tuple2<Integer,Vector>> rowsListDocTermD, 
          LinkedList<Tuple2<Integer,Vector>> rowsListDocTermB, 
          String inputFilePath, 
          String outFilePath) {
    /*
     * This is the address of the Spark cluster. We will call the task from WordCountTest and we
     * use a local standalone cluster. [*] means use all the cores available.
     * See {@see http://spark.apache.org/docs/latest/submitting-applications.html#master-urls}.
     */
//    String master = "local[*]";
    /**
     * TODO
     * Initialises a Spark context.
     */
        int n = rowsListDocTermD.size();
        int m = rowsListDocTermD.get(0)._2.size();
        int k = rowsListDocTermB.get(0)._2.size();
        
    double D[][] = new double[n][m];
    double B[][] = new double[n][k];
    for(int i = 0; i< n; i++)
        {
//            System.out.println("pt.spark.sSCC.run() driver "+mat.get(i)._1+"\t "+ mat.get(i)._2.toString());
//            System.arraycopy(mat.get(i)._2.toArray(), 0, _A[mat.get(i)._1], mat.get(i)._1*mat.get(i)._2.size(), mat.get(i)._2.size());
            for(int j = 0; j< m; j++)
            {
                D[i][j] = rowsListDocTermD.get(i)._2.apply(j);
            }
            for(int j = 0; j< k; j++)
            {
                B[i][j] = rowsListDocTermB.get(i)._2.apply(j);
            }
        }
     

        double[][] Bt = LocalVector2D.Transpose(B); //[nk]->[kn]
        double[][] BtB = LocalVector2D.IMtx(k);//Matrix.mul(Bt, B); //[kn]*[nk]=[kk]
        double[][] Am = LocalVector2D.Transpose(BtB);
        double[][] Bm = LocalVector2D.scale(Am, -1);
        double[][] AtB = LocalVector2D.mul(Am, Bm);
    
    SparkConf conf = new SparkConf()
        .setAppName(sADMM.class.getName())
        .setMaster(master);
    JavaSparkContext sc = new JavaSparkContext(conf);

    /*
     * Performs a work count sequence of tasks and prints the output with a logger.
     */
    //read B matrix from scc
//    sc.textFile(inputFilePath)
//        .flatMap(text -> Arrays.asList(text.split(" ")).iterator())
//        .mapToPair(word -> new Tuple2<>(word, 1))
//        .reduceByKey((a, b) -> a + b)
//        .foreach(result -> LOGGER.info(
//            String.format("Word [%s] count [%d].", result._1(), result._2)));
    
//        double[][] array = {{1.12, 2.05, 3.12}, {5.56, 6.28, 8.94}, {10.2, 8.0, 20.5}};

//  LinkedList<Vector> rowsList = new LinkedList<>();
//  for (int i = 0; i < array.length; i++) {
//    Vector currentRow = Vectors.dense(array[i]);
//    rowsList.add(currentRow);
//  }
//  JavaRDD<Tuple2<Integer,Vector>> rows0 = sc.parallelize(rowsListDocTerm0);
//  JavaRDD<Tuple2<Integer,Vector>> rows1 = sc.parallelize(rowsListDocTerm1);

  // Create a RowMatrix from JavaRDD<Vector>.
//  RowMatrix mat0 = new RowMatrix(rows0.rdd());
//  RowMatrix mat1 = new RowMatrix(rows1.rdd());
  
//  
//  double rho = 0.8;
//  double lamda = 0.6;
//  double eps_abs= 1e-6;
//  double eps_rel= 1e-6;
  // each solveADMM process for 1 column of input matrix
//  Broadcast<Double> rhob= sc.broadcast(rho);
//    Broadcast<RowMatrix> t0 = sc.broadcast(mat0);
//    Broadcast<RowMatrix> t1 = sc.broadcast(mat1);
  
        Broadcast<Double> rho0 = sc.broadcast(0.8);
        Broadcast<Double> lamda = sc.broadcast(0.6);
        Broadcast<Double> eps_abs = sc.broadcast(1e-6);
        Broadcast<Double> eps_rel = sc.broadcast(1e-6);

JavaRDD<Tuple2<Integer, Vector>> matI = sc.parallelize(rowsListDocTermD);

matI.mapToPair((Tuple2<Integer, Vector> t) -> {

            System.out.println("pt.spark.sSCC.run() driver "+t._1+"\t "+ t._2.toString());
            return  new Tuple2<>(t._1, 
                solveADMM(
                        t._1//, 
//                        mat.value(),
//                        _numberOfVertices.value(),
//                        _numOfFeature.value(),
//                        E.value(), 
//                        _X.value(),
//                        _ni.value(),
//                        _xAvr.value(),
//                        _u.value(),
//                        rho0.value(), 
//                        lamda.value(), 
//                        lamda2.value(), 
//                        eps_abs.value(), 
//                        eps_rel.value()
                ));//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
);
  
  sc.stop();
  return null;
  }

  
  private static Vector solveADMM(int id)
  {

              NodeADMM xNode = null;
//                      new NodeADMM(0, _Ddata, _Bdata, Bt, BtB, Am, Bm, AtB, 0, 0, 0, 0);
//                      curruntI._1, 
//                _A, 
//                _X[curruntI._1],
//                _edges,
//                ni, 
//                xAvr,
//                lamda,lamda2, rho0, 
//                eps_abs, eps_rel);
        return Vectors.dense(xNode.X[0]);
  }

}

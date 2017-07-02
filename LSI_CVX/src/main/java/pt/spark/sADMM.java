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
          double[][] D, 
          double[][]  B,//rowsListDocTermB, 
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
        int n = D.length;
        int m = D[0].length;
        int k = B[0].length;
        
//    double D[][] = new double[n][m];
//        double B[][] = new double[n][k];
    
        LinkedList<Tuple2<Integer,Vector>> rowsListDocTermD = new LinkedList<>();
        for (int i = 0; i < D.length; i++) {
            Vector row = Vectors.dense(D[i]);
            rowsListDocTermD.add(new Tuple2<>(i,row));
        }
        
        LinkedList<Tuple2<Integer,Vector>> rowsListDocTermB = new LinkedList<>();
        for (int i = 0; i < B.length; i++) {
            Vector row = Vectors.dense(B[i]);
            rowsListDocTermB.add(new Tuple2<>(i,row));
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
        Broadcast<Integer> _n = sc.broadcast(n);
        Broadcast<Integer> _m = sc.broadcast(m);
        Broadcast<Integer> _k = sc.broadcast(k);
        Broadcast<double[][]> _D = sc.broadcast(D);
        Broadcast<double[][]> _B = sc.broadcast(B);

        Broadcast<double[][]> _Bt = sc.broadcast(Bt);
        Broadcast<double[][]> _BtB = sc.broadcast(BtB);
        Broadcast<double[][]> _AtB = sc.broadcast(AtB);
    JavaRDD<Tuple2<Integer, Vector>> matI = sc.parallelize(rowsListDocTermD);

    matI.mapToPair((Tuple2<Integer, Vector> t) -> {
            System.out.println("pt.spark.sSCC.run() driver "+t._1+"\t "+ t._2.toString());
            return  new Tuple2<>(t._1, 
                solveADMM(t._1, 
                        _D.value(),
                        _B.value(),
                        _Bt.value(),
                        _BtB.value(),
                        _AtB.value(),
                        rho0.value(),
                        lamda.value(),
                        eps_abs.value(),
                        eps_rel.value())
                );//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
    );
  
  sc.stop();
  return null;
  }

  
  private static Vector solveADMM(int id,
          double[][] _Ddata, 
          double[][]_Bdata, 
          double[][]Bt, 
          double[][]BtB, 
//          double[][]Am, 
//          double[][]Bm, 
          double[][]AtB, 
          double _lamda,double _rho,double e1 ,double e2)
  {

              NodeADMM xNode = new NodeADMM(id, _Ddata, _Bdata, Bt, BtB, AtB, _lamda, _rho, e1, e2);
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

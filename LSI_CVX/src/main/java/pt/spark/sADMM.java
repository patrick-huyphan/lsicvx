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
import java.util.ArrayList;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.DenseMatrix;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
//import org.apache.spark.mllib.optimization.tfocs.SolverL1RLS

/**
 * sADMM class, we will call this class to find projection matrix, user to
 * convert matrix to latent space - map data to master - each executer call
 * column matrix by: read matrix, update x,u,v
 *
 */
public class sADMM {

    /**
     * We use a logger to print the output. Sl4j is a common library which works
     * with log4j, the logging system used by Apache Spark.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(sADMM.class);

    /**
     *
     * @param sc
     * @param D: n*m
     * @param B: k*n
     * @param inputFilePath
     * @param outFilePath
     * @return X= k*m
     */
    public static List<Tuple2<Integer, Vector>> run(JavaSparkContext sc,
            double[][] D,
            double[][] B,//rowsListDocTermB, 
//            String inputFilePath,
            String outFilePath) {
        /**
         * TODO
         */
        int n = D.length;
        int m = D[0].length;
        int k = B[0].length;

        // convert to colum data
        double D2[][] = LocalMatrix.Transpose(D);
        LinkedList<Tuple2<Integer, Vector>> rowsListDocTermD = new LinkedList<>();
        for (int i = 0; i < D2.length; i++) {
            Vector row = Vectors.dense(D2[i]);
            rowsListDocTermD.add(new Tuple2<>(i, row));
        }

/**
 * TODO: use spark suport matrix to process those array
 */
        B = LocalMatrix.orthonormal(B);
        double[][] Bt = LocalMatrix.Transpose(B); //[nk]->[kn]
        double[][] BtB = LocalMatrix.IMtx(k);//Matrix.mul(Bt, B); //[kn]*[nk]=[kk]
        double[][] Am = LocalMatrix.Transpose(BtB);
        double[][] Bm = LocalMatrix.scale(Am, -1);
        double[][] AtB = LocalMatrix.mul(Am, Bm);


        Broadcast<Double> rho0 = sc.broadcast(0.8);
        Broadcast<Double> lamda = sc.broadcast(0.6);
        Broadcast<Double> eps_abs = sc.broadcast(1e-6);
        Broadcast<Double> eps_rel = sc.broadcast(1e-6);
        Broadcast<Integer> _n = sc.broadcast(n);
        Broadcast<Integer> _m = sc.broadcast(m);
        Broadcast<Integer> _k = sc.broadcast(k);
        Broadcast<double[][]> _Bt = sc.broadcast(Bt);
        Broadcast<double[][]> _BtB = sc.broadcast(BtB);
        Broadcast<double[][]> _AtB = sc.broadcast(AtB);
        
        System.out.println("pt.spark.sADMM.run()");
        JavaRDD<Tuple2<Integer, Vector>> matI = sc.parallelize(rowsListDocTermD);
        JavaPairRDD<Integer, Vector> retPair = matI.mapToPair((Tuple2<Integer, Vector> t) -> {
//            System.out.println("pt.spark.sADMM.run() driver " + t._1 + "\t " + t._2.toString());
            return new Tuple2<Integer, Vector>(t._1,
                    solveADMM(t,
                            _n.value(),
                            _m.value(),
                            _k.value(),
                            _Bt.value(),
                            _BtB.value(),
                            _AtB.value(),
                            rho0.value(),
                            lamda.value(),
                            eps_abs.value(),
                            eps_rel.value())
            );
        }
        );
        retPair.cache();
//        System.out.println("pt.spark.sADMM.run() end "+ retPair.count());
        List<Tuple2<Integer, Vector>> retList= retPair.collect();
        retPair.saveAsTextFile(outFilePath + "/ADMM");
        System.out.println("pt.spark.sADMM.run() detroy and return "+retList.size());
        
        rho0.destroy();
        lamda.destroy();
        eps_abs.destroy();
        eps_rel.destroy();
        _n.destroy();
        _m.destroy();
        _k.destroy();
        _Bt.destroy();
        _BtB.destroy();
        _AtB.destroy();
         
        return retList;
    }
    
    private static Vector solveADMM(
            Tuple2<Integer, Vector> _Ddata,
            int _n, int _m, int _k,
            double[][] Bt,
            double[][] BtB,
            double[][] AtB,
            double _lamda, double _rho,
            double e1, double e2) {
        
        NodeADMM xNode = new NodeADMM(
                _Ddata,
                _n, _m, _k,
                Bt,
                BtB,
                AtB,
                _lamda, _rho,
                e1, e2);
        return Vectors.dense(xNode.X);
    }
    
}

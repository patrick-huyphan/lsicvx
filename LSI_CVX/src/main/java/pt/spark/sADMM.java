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
import java.io.FileWriter;
//import org.apache.spark.mllib.optimization.tfocs.SolverL1RLS

/**
 * sADMM class, we will call this class to find projection matrix, user to
 * convert matrix to latent space - map data to master - each executer call
 * column matrix by: read matrix, update x,u,v
 * return list of column of projection matrix
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
     * @param orthonormal
     * @param loop
     * @param orthonormal
     * @param outFilePath
     * @return X= k*m
     */
    public List<Tuple2<Integer, Vector>> retMat;
    public sADMM(JavaSparkContext sc,
            double[][] D,
            double[][] B,//rowsListDocTermB, 
//            String inputFilePath,
            boolean orthonormal,
            int loop,
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
        if(orthonormal ==true)
        {
            System.out.println("pt.spark.sADMM.run() with orthonormal");
            B = LocalMatrix.orthonormal(B);
        }
        double[][] Bt = LocalMatrix.Transpose(B); //[nk]->[kn]
        double[][] BtB = LocalMatrix.IMtx(k);//Matrix.mul(Bt, B); //[kn]*[nk]=[kk]
        double[][] Am = LocalMatrix.Transpose(BtB);
        double[][] Bm = LocalMatrix.scale(Am, -1);
        double[][] AtB = LocalMatrix.mul(Am, Bm);
        double[][] BtD= LocalMatrix.mul(Bt, D); //[nk]*[n] = k
        
        //LocalMatrix.printMat(Bt, false,"Bt");
        //LocalMatrix.printMat(BtB, "BtB");
        //LocalMatrix.printMat(Am, "At");
        //LocalMatrix.printMat(Bm, "Bt");
        //LocalMatrix.printMat(AtB, "AtB");    

        Broadcast<Integer> _n = sc.broadcast(n);
        Broadcast<Integer> _m = sc.broadcast(m);
        Broadcast<Integer> _k = sc.broadcast(k);
//        Broadcast<double[][]> _Bt = sc.broadcast(Bt);
        Broadcast<double[][]> _BtB = sc.broadcast(BtB);
        Broadcast<double[][]> _AtB = sc.broadcast(AtB);
        Broadcast<double[][]> _BtD = sc.broadcast(BtD);
        Broadcast<Integer> loopb = sc.broadcast(loop);
        System.out.println("pt.spark.sADMM.run()");
        JavaRDD<Tuple2<Integer, Vector>> matI = sc.parallelize(rowsListDocTermD);
        JavaPairRDD<Integer, Vector> retPair = matI.mapToPair((Tuple2<Integer, Vector> t) -> {
//            System.out.println("pt.spark.sADMM.run() driver " + t._1 + "\t " + t._2.toString());
            return new Tuple2<Integer, Vector>(t._1,
                    solveADMM(t._1,
                            _n.value(), _m.value(), _k.value(),
                            _BtB.value(),
                            _AtB.value(),
                            _BtD.value(),
                            loopb.value())
            );
        }
        );
        retPair.cache();
//        System.out.println("pt.spark.sADMM.run() end "+ retPair.count());
//        List<Tuple2<Integer, Vector>> retList= retPair.collect();
        retMat = retPair.collect();    
        retPair.saveAsTextFile(outFilePath + "/ADMM");
        System.out.println("pt.spark.sADMM.run() detroy and return "+retMat.size());
        
        
        for (Tuple2<Integer, Vector> r: retMat) {
            double[] tmp = r._2.toArray();
            LocalVector. printV(tmp, " "+r._1, true);
        }
            
        _n.destroy();
        _m.destroy();
        _k.destroy();
//        _Bt.destroy();
        _BtB.destroy();
        _AtB.destroy();
        _BtD.destroy();
        loopb.destroy();
         
/*
        double[][] B3 = new double[m][k];

        for(Tuple2<Integer, Vector> r : retList)
        {
            B3[r._1] = r._2.toArray();            
        }

        LocalMatrix.printMat(B3, "B3"); 
*/
//        retMat = retList;
//        return retList;
    }
    
    private static Vector solveADMM(
            int id,
            int _n, int _m, int _k,
//            double[][] Bt,
            double[][] BtB,
            double[][] AtB,
            double[][] BtD,
            int loop) {
        
        // lsi = new ADMM(D, B, 0.04, 0.8, 0.005, 0.0001);
        double _rho = 0.04;
        double _lamda = 0.8;
        double e1 = 0.005; 
        double e2 = 0.0001; 
        double [] Btd = LocalMatrix.getCol(BtD, id);
        NodeADMM xNode = new NodeADMM(
                id,
                _n, _m, _k,
                Btd,
                BtB,
                AtB,
                 _rho, _lamda,
                e1, e2, loop);
//        LocalVector.printV(xNode.X, " solve "+id, true);
        return Vectors.dense(xNode.X);
    }
    
}

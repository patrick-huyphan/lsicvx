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
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import scala.Function1;

/**
 * sSCC class, we will call this class to clustering data, return the row in
 * same cluster - map data to master, calculate edge weigh - each executer
 * calculate row data by: read Matrix, update D,X,V,U D,V,U related to edge
 */
public class sSCC {

    /**
     * We use a logger to print the output. Sl4j is a common library which works
     * with log4j, the logging system used by Apache Spark.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(sSCC.class);

    /**
     *
     * @param context
     * @param A: matrix term doc
     * @param inputFilePath
     * @param outFilePath
     * @return
     */
    public static List<Tuple2<Integer, Vector>> run(JavaSparkContext context,
            double[][] A,// term-doc
//            String inputFilePath,
            String outFilePath) {

        double _rho0 = 0.8;
        double _lamda = 0.6;
        double _lamda2 = 0.01;
        double _eps_abs = 1e-6;
        double _eps_rel = 1e-6;
        int numberOfVertices = A.length;
        int numOfFeature = A[0].length;

        LinkedList<Tuple2<Integer, Vector>> rowsListTermDoc = new LinkedList<>();
        for (int i = 0; i < A.length; i++) {
            Vector row = Vectors.dense(A[i]);
            rowsListTermDoc.add(new Tuple2<>(i, row));
        }

        List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = buildE(rowsListTermDoc);
////TODO: init global data : X, u, xAvr        
        int[] ni = retSize(numberOfVertices, eSet);
////        Matrix.printMat(A, "centered");
//        //Init
        double[][] X = new double[numberOfVertices][numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
            double x = 1 - ((_lamda2) / LocalVector1D.norm(LocalVector2D.getCol(A, i)));
            x = (x > 0) ? x : 0;
            //update by column i
            for (int j = 0; j < numberOfVertices; j++) {
                X[j][i] = x * A[j][i];
            }
        }
//        
        double[] u = new double[numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
            u[i] = LocalVector1D.norm(LocalVector2D.getCol(X, i));
        }
        u = LocalVector1D.scale(u, _lamda2);
        double[] xAvr = new double[numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
            double x = 1 - (u[i] / LocalVector1D.norm(LocalVector2D.getCol(A, i)));
            x = (x > 0) ? x : 0;
            for (int j = 0; j < numberOfVertices; j++) {
                X[j][i] = x * A[j][i];
            }
            xAvr[i] = LocalVector1D.avr(LocalVector2D.getCol(A, i));
        }

//        for(Tuple2<Integer, Vector> mi: rowsList)
//        {
//            System.out.println("pt.spark.sSCC.run() "+mi._1+"\t "+ mi._2.toString());
//        }
//    String master = "local[*]";
//    /*
//     * Initialises a Spark context.
//     */
        System.out.println("pt.spark.sSCC.run()");
//        CoordinateMatrix Cm = null;

        Broadcast<Double> rho0 = context.broadcast(0.8);
        Broadcast<Double> lamda = context.broadcast(0.6);
//        Broadcast<Double> lamda2 = context.broadcast(0.01);
        Broadcast<Double> eps_abs = context.broadcast(1e-6);
        Broadcast<Double> eps_rel = context.broadcast(1e-6);
        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);

//        Broadcast<double[]> _u = context.broadcast(u);
        Broadcast<double[]> _xAvr = context.broadcast(xAvr);
        Broadcast<int[]> _ni = context.broadcast(ni);
        Broadcast<double[][]> _X = context.broadcast(X);
        Broadcast<Integer> _numOfFeature = context.broadcast(numOfFeature);
        Broadcast<Integer> _numberOfVertices = context.broadcast(numberOfVertices);
        Broadcast<double[][]> mat = context.broadcast(A);
        JavaRDD<Tuple2<Integer, Vector>> matI = context.parallelize(rowsListTermDoc);

        LocalVector2D.printMat(X, "x init");
        System.out.println("pt.spark.sSCC.run() 2 start map scc local");
        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        JavaPairRDD<Integer, Vector> ret = matI.mapToPair((Tuple2<Integer, Vector> t1)
                -> {
//            System.out.println("pt.spark.sSCC.run() driver "+t1._1+"\t "+ t1._2.toString());
            return new Tuple2<>(t1._1,
                    solveADMM(t1,
                            mat.value(),
                            _numberOfVertices.value(),
                            _numOfFeature.value(),
                            E.value(),
                            _X.value()[t1._1],
                            _ni.value(),
                            _xAvr.value(),
                            //                        _u.value(),
                            rho0.value(),
                            lamda.value(),
                            //                        lamda2.value(), 
                            eps_abs.value(),
                            eps_rel.value()));
        }
        );

//        double[][] retArray = new double[numOfFeature][numOfFeature];
        List<Tuple2<Integer, Vector>> retList = ret.collect();

        ret.saveAsObjectFile(outFilePath + "/scc");
        System.out.println("pt.spark.sSCC.run() end");
        return retList;
    }

    private static Vector solveADMM(Tuple2<Integer, Vector> curruntI,
            double[][] _A,
            int numberOfVertices,
            int numOfFeature,
            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
            double[] _X,
            int[] ni,
            double[] xAvr,
            //            double [] ui,
            Double rho0,
            Double lamda,
            //            Double lamda2,
            Double eps_abs,
            Double eps_rel) throws IOException {
        /*
TODO: 
        - init U,V,D,X0
      (<r,c>,v[])
         */
        System.out.println("pt.spark.sSCC.solveADMM() " + curruntI._1 + " " + numberOfVertices + "-" + numOfFeature);

        List<LocalEdge> _edges = new ArrayList(); //rebuild from e
        for (int i = 0; i < e.size(); i++) {
            _edges.add(new LocalEdge(e.get(i)._1._1, e.get(i)._1._2, e.get(i)._2));
        }

        NodeSCC xNode = new NodeSCC(curruntI._1,
                _A,
                _X,
                _edges,
                ni,
                xAvr,
                lamda,
                //                lamda2, 
                rho0,
                eps_abs, eps_rel);
        return Vectors.dense(xNode.X);
    }

    private static List<Tuple2<Tuple2<Integer, Integer>, Double>> buildE(LinkedList<Tuple2<Integer, Vector>> rowsList) {
        List<Tuple2<Tuple2<Integer, Integer>, Double>> ret = new ArrayList<>();
        for (int i = 0; i < rowsList.size(); i++) {
            for (int j = i + 1; j < rowsList.size(); j++) {
                double value = LocalVector1D.cosSim(rowsList.get(i)._2.toArray(), rowsList.get(j)._2.toArray());
                ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(i)._1, rowsList.get(j)._1), value));
                ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(j)._1, rowsList.get(i)._1), value));
            }
        }
        return ret;
    }

    private static int[] retSize(int numberOfVertices, List<Tuple2<Tuple2<Integer, Integer>, Double>> edges) {
        int[] ret = new int[numberOfVertices];
        for (Tuple2<Tuple2<Integer, Integer>, Double> e : edges) {
            ret[e._1._1] = ret[e._1._1] + 1;
            ret[e._1._2] = ret[e._1._2] + 1;
        }
        return ret;
    }

    public static double[][] getPresentMat(List<Tuple2<Integer, Vector>> scc, double[][] A) {
        List<List<Integer>> cluster = getCluster(scc);
        double[][] presentMat = new double[A.length][cluster.size()];//[numberOfVertices];
//        double[][] ret = new double[data[0].length][index.size()];
//        System.out.println("paper.MSTClustering.getPresentMath()");
        for (int j = 0; j < cluster.size(); j++) {
            List<Integer> edgesL = cluster.get(j);
            if (edgesL.isEmpty()) {
//                System.out.println("paper.Clustering.getPresentMat() empty "+j);
                continue;
            }
            int shotestCol = edgesL.get(0);
            double min = 100;
            for (Integer node : edgesL) {
                double norm = LocalVector1D.norm(A[node - 1]);
                if (norm < min) {
                    min = norm;
                    shotestCol = node - 1;
                }

            }
//            System.out.println("\npaper.Paper.getPresentMath() "+j+" "+ shotestCol);

            for (int i = 0; i < A.length; i++) {
                presentMat[i][j] = A[i][shotestCol]; //new Random().nextDouble(); // 
            }
        }
        return presentMat;
    }

    private static List<List<Integer>> getCluster(List<Tuple2<Integer, Vector>> scc) {
//        int  ret[] = new int[scc.size()];
        HashMap<Integer, Integer> xxx = new HashMap<>();
        List<List<Integer>> cluster = new ArrayList<>();
//        List<Integer>  intdex = new ArrayList<Integer>();
        for (int i = 0; i < scc.size(); i++) {
            if (!xxx.containsKey(i)) {
                xxx.put(i, i);
                for (int j = i + 1; j < scc.size(); j++) {
                    if (!xxx.containsKey(j)) {
                        if ((LocalVector1D.isSameVec(scc.get(i)._2.toArray(), scc.get(j)._2.toArray()))) //                        if((Vector.isDepen(Matrix.getRow(X, i),Matrix.getRow(X, j))))    
                        {
//                                System.out.println("same: "+i+"-"+j);
                            xxx.put(j, i);
                        }
                    }
                }
            }
        }
//    	count--;
//    	System.out.println("Num of sub mat: "+intdex.size());

        for (int i = 0; i < scc.size(); i++) {
            List<Integer> sub = new ArrayList<>();
            for (int j = i; j < scc.size(); j++) {
                if (xxx.get(j) == i) {
                    sub.add(j + 1);
                }
            }
            if (!sub.isEmpty()) {
                cluster.add(sub);
            }
        }
//    	cluster.add(intdex);
//Test    
        System.out.println("paper.SCC.getCluster() " + cluster.size());
        for (int i = 0; i < cluster.size(); i++) {
            List<Integer> sub = cluster.get(i);
            System.out.print("Cluster " + i + ":\t");
            for (int j = 0; j < sub.size(); j++) {
                System.out.print(sub.get(j) + "\t");
            }
            System.out.println("");
        }
        return cluster;
    }
}

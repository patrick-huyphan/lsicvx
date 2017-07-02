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
     * The task body
     */
    public static JavaPairRDD<Integer, Vector> run(String master, 
            LinkedList<Tuple2<Integer, Vector>> rowsList, 
            String inputFilePath, 
            String outFilePath) {
        

        int numberOfVertices = (int)rowsList.size();
        int numOfFeature = rowsList.get(0)._2.size();
////TODO: init global data : X, u, xAvr        
        int [] ni = retSize(numberOfVertices);
////        Matrix.printMat(A, "centered");
//        //Init
        double [][] X = new double[numberOfVertices][numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            double x = 1- ((lambda2)/pt.paper.LocalVector1D.norm(pt.paper.LocalVector2D.getCol(A, i)));
//            x = (x>0)? x:0;
//            //update by column i
//            for(int j = 0 ; j< numberOfVertices; j++)
//            {
//                X[j][i] = x*A[j][i];
//            }
//        }
//        
        double[] u = new double[numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            u[i] = pt.paper.LocalVector1D.norm(pt.paper.LocalVector2D.getCol(X, i));
//        }
//        u = pt.paper.LocalVector1D.scale(u, lambda2);        
        double[] xAvr = new double[numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            double x = 1- (u[i]/pt.paper.LocalVector1D.norm(pt.paper.LocalVector2D.getCol(A, i)));
//            x = (x>0)? x:0;
//            for(int j = 0 ; j< numberOfVertices; j++)
//            {
//                X[j][i] = x*A[j][i];
//            }
//            xAvr[i] = pt.paper.LocalVector1D.avr(pt.paper.LocalVector2D.getCol(A, i));
//        }


//        for(Tuple2<Integer, Vector> mi: rowsList)
//        {
//            System.out.println("pt.spark.sSCC.run() "+mi._1+"\t "+ mi._2.toString());
//        }
        List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = buildE(rowsList);
        
//    String master = "local[*]";
//    /*
//     * Initialises a Spark context.
//     */
        SparkConf conf = new SparkConf()
                .setAppName(sSCC.class.getName())
                .setMaster(master);
        JavaSparkContext context = new JavaSparkContext(conf);


        

        
        System.out.println("pt.spark.sSCC.run()");
//        CoordinateMatrix Cm = null;
        
//  double rho0 = 0.8;
//  double lamda = 0.6;
//  double lamda2 = 0.6;
//  double eps_abs= 1e-6;
//  double eps_rel= 1e-6;
        // (<r,c>,w)


        Broadcast<Double> rho0 = context.broadcast(0.8);
        Broadcast<Double> lamda = context.broadcast(0.6);
        Broadcast<Double> lamda2 = context.broadcast(0.01);
        Broadcast<Double> eps_abs = context.broadcast(1e-6);
        Broadcast<Double> eps_rel = context.broadcast(1e-6);
        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
        


        Broadcast<double[]> _u = context.broadcast(u);
        Broadcast<double[]> _xAvr = context.broadcast(xAvr);
        Broadcast<int[]> _ni = context.broadcast(ni);
        Broadcast<double[][]> _X = context.broadcast(X);
        Broadcast<Integer> _numOfFeature = context.broadcast(numOfFeature);
        Broadcast<Integer> _numberOfVertices = context.broadcast(numberOfVertices);
        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        
        Broadcast<LinkedList<Tuple2<Integer, Vector>>> mat = context.broadcast(rowsList);
        JavaRDD<Tuple2<Integer, Vector>> matI = context.parallelize(rowsList);
//        Broadcast<JavaRDD<Tuple2<Integer, Vector>>> mat = context.broadcast(matI);
//        matI.cache();

        System.out.println("pt.spark.sSCC.run() 2 start map scc local");
        JavaPairRDD<Integer, Vector> ret = matI.mapToPair((Tuple2<Integer, Vector> t1) ->
        {
            System.out.println("pt.spark.sSCC.run() driver "+t1._1+"\t "+ t1._2.toString());
            return  new Tuple2<>(t1._1, 
                solveADMM(t1, 
                        mat.value(),
                        _numberOfVertices.value(),
                        _numOfFeature.value(),
                        E.value(), 
                        _X.value(),
                        _ni.value(),
                        _xAvr.value(),
                        _u.value(),
                        rho0.value(), 
                        lamda.value(), 
                        lamda2.value(), 
                        eps_abs.value(), 
                        eps_rel.value()));//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                    }
        );

//        Cm.toRowMatrix().rows().map(new Function1<Vector, U>, ct);
        
        context.stop();
        System.out.println("pt.spark.sSCC.run() end");
        return ret;
    }

    private static Vector solveADMM(Tuple2<Integer, Vector> curruntI,
            LinkedList<Tuple2<Integer, Vector>> mat,
            int numberOfVertices,
            int numOfFeature,
            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
            double[][] _X,
            int[]   ni,
            double [] xAvr,
            double [] ui,
            Double rho0,
            Double lamda,
            Double lamda2,
            Double eps_abs,
            Double eps_rel) throws IOException {
        /*
TODO: 
        - init U,V,D,X0
      (<r,c>,v[])
         */
        System.out.println("pt.spark.sSCC.solveADMM() "+curruntI._1 +" "+numberOfVertices+"-"+numOfFeature);
        double[][] _A = new double[numberOfVertices][numOfFeature];// rebuild from mat
        
//        List<Tuple2<Integer, Vector>> matT = mat.collect();
        for(int i = 0; i< mat.size(); i++)
        {
//            System.out.println("pt.spark.sSCC.run() driver "+mat.get(i)._1+"\t "+ mat.get(i)._2.toString());
//            System.arraycopy(mat.get(i)._2.toArray(), 0, _A[mat.get(i)._1], mat.get(i)._1*mat.get(i)._2.size(), mat.get(i)._2.size());
            for(int j = 0; j< mat.get(i)._2.size(); j++)
            {
                _A[i][j] = mat.get(i)._2.apply(j);
            }
        }
        
        List<LocalEdge> _edges = new ArrayList(); //rebuild from e
        for(int i =0; i< e.size(); i++)
        {
            _edges.add(new LocalEdge(e.get(i)._1._1, e.get(i)._1._2, e.get(i)._2));
        }
       
        NodeSCC xNode = new NodeSCC(curruntI._1, 
                _A, 
                _X[curruntI._1],
                _edges,
                ni, 
                xAvr,
                lamda,lamda2, rho0, 
                eps_abs, eps_rel);
        return Vectors.dense(xNode.X);
    }

    private static List<Tuple2<Tuple2<Integer, Integer>, Double>> buildE(LinkedList<Tuple2<Integer, Vector>> rowsList) {
        List<Tuple2<Tuple2<Integer, Integer>, Double>> ret = new ArrayList<>();
        for(int i = 0; i< rowsList.size(); i++)
        {
            for(int j = i+1; j< rowsList.size(); j++)
            {
                double value =  LocalVector1D.cosSim(rowsList.get(i)._2.toArray(), rowsList.get(j)._2.toArray());
                ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(i)._1,rowsList.get(j)._1),value));
                ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(j)._1,rowsList.get(i)._1),value));
            }
        }
        return ret;
    }
    private static int[] retSize(int numberOfVertices)
    {
        int [] ret = new int[numberOfVertices];
//        for(pt.paper.LocalEdge e: edges)
//        {
//            ret[e.sourcevertex] = ret[e.sourcevertex]+1;
//            ret[e.destinationvertex] = ret[e.destinationvertex]+1;
//        }
        return ret;
    }
}

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
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;

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
    public void run(String master, LinkedList<Vector> rowsList, String inputFilePath, String outFilePath) {
//    String master = "local[*]";
//    /*
//     * Initialises a Spark context.
//     */
        SparkConf conf = new SparkConf()
                .setAppName(sSCC.class.getName())
                .setMaster(master);
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<Vector> matI = context.parallelize(rowsList);

//  double rho0 = 0.8;
//  double lamda = 0.6;
//  double lamda2 = 0.6;
//  double eps_abs= 1e-6;
//  double eps_rel= 1e-6;
        // (<r,c>,w)
        List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = buildE(rowsList);

        Broadcast<Double> rho0 = context.broadcast(0.8);
        Broadcast<Double> lamda = context.broadcast(0.6);
        Broadcast<Double> lamda2 = context.broadcast(0.01);
        Broadcast<Double> eps_abs = context.broadcast(1e-6);
        Broadcast<Double> eps_rel = context.broadcast(1e-6);
        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
//  sCommonFunc.matrixToRDD(context, m)
        Broadcast<JavaRDD<Vector>> mat = context.broadcast(matI);

        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        matI.map(new Function<Vector, Vector>() {
            @Override
            public Vector call(Vector t1) throws Exception {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                return solveADMM(context, t1, mat, E, rho0, lamda, lamda2, eps_abs, eps_rel);
            }
        });

        context.stop();
    }

    private Vector solveADMM(JavaSparkContext sc,
            Vector curruntI,
            Broadcast<JavaRDD<Vector>> mat,
            Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e,
            Broadcast<Double> rho0,
            Broadcast<Double> lamda,
            Broadcast<Double> lamda2,
            Broadcast<Double> eps_abs,
            Broadcast<Double> eps_rel) {
        /*
TODO: 
        - init U,V,D,X0
      (<r,c>,v[])
         */
        double rho = rho0.value();
        List<Double> x0 = null;
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> z0 = null;
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> u0 = null;

        List<Double> x = null;
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> z = initZ(e);
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> u = initU(e);
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> D = null;
        boolean stop = false;

        int loop = 0;
        while (loop < 100) {
            D = calD(e, z, u);
            x = updateX(mat, e, D);
            z = updateZ(e, x, u, z);
            u = updateU(e, x, u, z);
            if (loop > 1) {
                stop = checkStop(x, rho, z0, u0);
            }
            if (loop > 1 && stop) {
                break;
            }

            loop++;
            x = x0;
            z = z0;
            u = u0;
        }

//        double[] tmp = new double[x.size()];
//        int j = 0;
//        for(double i:x)
//        {
//            tmp[j] = i;
//            j++;
//        }
//        Double[] array = x.stream().toArray(Double[]::new);
        return Vectors.dense(ArrayUtils.toPrimitive(x.stream().toArray(Double[]::new)));
    }

    private boolean checkStop(List<Double> x0,
            double cRho,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> z,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> u) {
        double r = eps_dual();
        double s = eps_pri();

        cRho = updateRho(cRho, r, s);

        return false;
    }

    private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> calD(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> U,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> V) {
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
        return ret;
    }

    private List<Double> updateX(Broadcast<JavaRDD<Vector>> mat, Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> D) {
        List<Double> ret = null;
        return ret;
    }

    private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> updateU(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e,
            List<Double> x, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> U,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> V) {
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
        return ret;
    }

    private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> initU(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e) {
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
        return ret;
    }

    private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> updateZ(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e,
            List<Double> x, List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> U,
            List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> V) {
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
        return ret;
    }

    private List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> initZ(Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> e) {
        List<Tuple2<Tuple2<Integer, Integer>, List<Double>>> ret = null;
        return ret;
    }

    private double updateRho(double rho, double r, double s) {
        if (r > 8 * s) {
            rho = rho * 0.5;//(r/s);//2*rho;
        }
        if (s > 8 * r) {
            rho = rho * 2;//(r/s);//rho/2;
        }
        return rho;
    }

    private double eps_dual() {
        return 0;
    }

    private double eps_pri() {
        return 0;
    }

    private List<Tuple2<Tuple2<Integer, Integer>, Double>> buildE(LinkedList<Vector> rowsList) {
        List<Tuple2<Tuple2<Integer, Integer>, Double>> ret = new ArrayList<>();
        Vector[]tmp = rowsList.toArray(new Vector[0]);
        for(int i = 0; i< tmp.length; i++)
        {
            for(int j = i+1; j< tmp.length; j++)
            {
                double value =  pt.paper.LocalVector.cosSim(tmp[i].toArray(), tmp[j].toArray());
                ret.add(new Tuple2<>(new Tuple2<>(i,j),value));
                ret.add(new Tuple2<>(new Tuple2<>(j,i),value));
            }
        }
        return ret;
    }
}

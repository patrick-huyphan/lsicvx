package pt.spark;

import breeze.linalg.DenseMatrix;
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
import static javafx.scene.input.KeyCode.R;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.rdd.RDD;
import scala.Function1;
import scala.Function2;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
//import scala.concurrent.Channel.LinkedList;

/**
 * sQuery class, we will call this class to calculate the cosine similarity of
 * source and query in latent space. - just mul 2 matrix source and projection
 * to get latent space - and calculate cosine in latent space
 */
public class sQuery {

    /**
     * We use a logger to print the output. Sl4j is a common library which works
     * with log4j, the logging system used by Apache Spark.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(sQuery.class);

    /**
     * The task body
     */
    public List<List<Tuple2<Integer,Tuple2<Integer, Double>>>> run(JavaSparkContext sc,
            double[][] D,   
            List<Tuple2<Integer, Vector>> B,//m*k
            double[][] Q,
//            String inputFilePath,
            String outFilePath) {

        /**
         * Input: D n*m X k*m Q i*m TODO: D' = D*Xt -> (n*m x m*k)n*k Q' = Q*Xt
         * -> i*k sim(D',Q')
         */
        System.out.println("pt.spark.sQuery.run()");
        RowMatrix rD = sCommonFunc.loadRowM(sc, D); //n,m
        RowMatrix rQ = sCommonFunc.loadRowM(sc, Q); //t,m 

        Matrix mX = sCommonFunc.loadDenseMatrix(B).transpose(); //m,k

        List<Vector> D2 = rD.multiply(mX).rows().toJavaRDD().collect();

        Broadcast<List<Vector>> _D2 = sc.broadcast(D2);

        JavaRDD<List<Tuple2<Integer,Tuple2<Integer, Double>>>> abc = rQ.multiply(mX).rows().toJavaRDD().zipWithIndex().map((Tuple2<Vector, Long> v1) -> {
            List<Tuple2<Integer,Tuple2<Integer, Double>>> ret = new ArrayList<>();
            List<Vector> D2L = _D2.value();
            for (Vector v : D2L) {
                double value = LocalVector.cosSim(v.toArray(), v1._1.toArray());
                ret.add(new Tuple2<>(v1._2.intValue(), new Tuple2<>(D2L.indexOf(v), value)));
            }
            return ret;
        });
        
        abc.saveAsTextFile(outFilePath + "/queryRes");
        _D2.destroy();
        return abc.collect();
    }
}

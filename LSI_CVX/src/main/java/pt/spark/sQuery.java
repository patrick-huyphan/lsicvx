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
import java.util.ArrayList;
import java.util.List;
import static javafx.scene.input.KeyCode.R;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.Matrix;
import org.apache.spark.rdd.RDD;
import scala.Function1;
import scala.Function2;
import java.util.Comparator;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.VoidFunction;
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

    public List<Tuple2<Integer,List<Tuple2<Integer, Double>>>> run(JavaSparkContext sc,
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

        JavaPairRDD<Vector, Long> D2 = rD.multiply(mX).rows().toJavaRDD().zipWithIndex();
        Broadcast<List<Tuple2<Vector,Long>>> _D2 = sc.broadcast(D2.collect());
        
        JavaPairRDD<Vector, Long> Q2 = rQ.multiply(mX).rows().toJavaRDD().zipWithIndex();
        Broadcast<List<Tuple2<Vector,Long>>> _Q2 = sc.broadcast(Q2.collect());
        

        JavaRDD<Tuple2<Integer,List<Tuple2<Integer, Double>>>> abc = Q2.map(new Function<Tuple2<Vector, Long>, Tuple2<Integer, List<Tuple2<Integer, Double>>>>() {
            @Override
            public Tuple2<Integer, List<Tuple2<Integer, Double>>> call(Tuple2<Vector, Long> v1) throws Exception {
                List<Tuple2<Integer, Double>> ret = new ArrayList<>();
                List<Tuple2<Vector,Long>> D2L = _D2.value();
                for (Tuple2<Vector,Long> v : D2L) {
                    double value = LocalVector.cosSim(v._1.toArray(), v1._1.toArray());
                    ret.add(new Tuple2<>(D2L.indexOf(v), value));
                }
//                ret.sort(new Comparator<Tuple2<Integer, Double>> () {
//                    @Override
//                    public int compare(Tuple2<Integer, Double> o1, Tuple2<Integer, Double> o2) {
//                        return (o1._2>o2._2)? 1: (o1._2<o2._2)? -1:0;
//                    }
//                });
//            List<Tuple2<Integer,Tuple2<Integer, Double>>> retTop20 = new ArrayList<>();
//            for(int i = 0; i<20; i++)
//            {
//                retTop20.add(ret.get(i));
//            }
//            return retTop20;
                return new Tuple2<>(v1._2.intValue(), ret);
            }
        });
               
        abc.cache();
        abc.saveAsTextFile(outFilePath + "/queryRes");
        
//        abc.foreach( new VoidFunction<List<Tuple2<Integer, Tuple2<Integer, Double>>>>() {
//            @Override
//            public void call(List<Tuple2<Integer, Tuple2<Integer, Double>>> t) throws Exception {
//                for(Tuple2<Integer, Tuple2<Integer, Double>> s : t)
//                {
//                    System.out.println("top qeury() "+ s._1+" "+ s._2._1+": "+s._2._2);
//                }
//            }
//        });

        JavaRDD<Tuple2<Integer,List<Tuple2<Integer, Double>>>> bcd = D2.map((Tuple2<Vector, Long> d1) -> {
            List<Tuple2<Integer, Double>> ret = new ArrayList<>();
            List<Tuple2<Vector,Long>> Q2L = _Q2.value();
            for (Tuple2<Vector,Long> v : Q2L) {
                double value = LocalVector.cosSim(v._1.toArray(), d1._1.toArray());
                ret.add(new Tuple2<>(Q2L.indexOf(v), value));//new Tuple2<>(d1._2.intValue(), 
            }
            return new Tuple2<>(d1._2.intValue(), ret);
        }); 

        bcd.cache();
        bcd.saveAsTextFile(outFilePath + "/queryRes2");
        
        List<Tuple2<Integer,List<Tuple2<Integer, Double>>>> t = abc.collect();
        List<Tuple2<Integer,List<Tuple2<Integer, Double>>>> t2 = bcd.collect();
        _D2.unpersist();
        _Q2.unpersist();

        for(Tuple2<Integer,List<Tuple2<Integer, Double>>> r:t2)
        {
            for(Tuple2<Integer, Double> a: r._2())
            {
                 System.out.println("top qeury() "+ r._1+" "+ a._1+": "+a._2);
            }
        }
        return t;
    }
}

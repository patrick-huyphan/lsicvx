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
            double[][] D,   //n*m
            double[][] Q,   //t*m
            List<Tuple2<Integer, Vector>> B,//m*k
            String outFilePath) {

        /**
         * Input: D n*m X k*m Q i*m TODO: D' = D*Xt -> (n*m x m*k)n*k Q' = Q*Xt
         * -> i*k sim(D',Q')
         */
        System.out.println("pt.spark.sQuery.run()");
        Matrix mX = sCommonFunc.loadDenseMatrix(B).transpose(); //m,k
                
        double [][] B2 = new double[mX.numRows()][mX.numCols()];
        for(int i =0; i< mX.numRows(); i++)
        {
            for(int j =0; j< mX.numCols(); j++)
                B2[i][j] = mX.apply(i, j);
        }

        //double [][] B3 = LocalMatrix.Transpose(B2);
        double[][] DM2 = LocalMatrix.mul(D,B2);
        double[][] QM2 = LocalMatrix.mul(Q,B2);
        
        double[][] ret2 = LocalMatrix.Transpose(LocalMatrix.sim(DM2, QM2));

        List<List<LocalEdge>> res = new ArrayList<>();
        
        for(int i = 0; i< ret2[0].length; i++)
        {
            List<LocalEdge> e = new ArrayList<>();
            for(int j = 0; j< ret2.length; j++)
            {
                e.add(new LocalEdge(i,j, ret2[j][i]));
            }
//            Collections.sort(e);
            e.sort(new Comparator<LocalEdge>() {
                @Override
                public int compare(LocalEdge o1, LocalEdge o2) {
                       if(o1.weight>o2.weight) return -1;
                       else if(o1.weight<o2.weight) return 1;
                       else return 0;
                }
            });
            List<LocalEdge> e2 = new ArrayList<>();
            for(int k = 0; k<30; k++)
            {
                e2.add(e.get(k));
            }
            res.add(e2);
        }
        
        for(List<LocalEdge> e : res)
        {
            System.out.println("pt.paper.Paper.PaperRuner()");
            for(LocalEdge i: e)
            {
                System.out.println(i.sourcevertex+" "+i.destinationvertex+":\t"+i.weight);
            }
        }
        

        
        RowMatrix rD = sCommonFunc.loadRowM(sc, D); //n,m
        RowMatrix rQ = sCommonFunc.loadRowM(sc, Q); //t,m 


        
        List<Vector> D2 = rD.multiply(mX).rows().toJavaRDD().collect(); //n*k
        
        Broadcast<List<Vector>> _D2 = sc.broadcast(D2);
        JavaPairRDD<Vector, Long> Q2 =  rQ.multiply(mX).rows().toJavaRDD().zipWithIndex(); //t*k
        
        JavaRDD<Tuple2<Integer,List<Tuple2<Integer, Double>>>> abc = Q2.map((Tuple2<Vector, Long> v1) -> {
            List<Tuple2<Integer, Double>> ret = new ArrayList<>();
            List<Vector> D2L = _D2.value();
//            for (Vector v : D2L) 
            for(int i = 0; i< D2L.size(); i++)
            {
                double value = LocalVector.cosSim(D2L.get(i).toArray(), v1._1.toArray());
                ret.add(new Tuple2<>(i, value));
//                System.out.println("pt.spark.sQuery.abc() "+ v1._2+" "+ i +": "+value);
            }
            return new Tuple2<>(v1._2.intValue(), ret);
        });
        abc.cache();
        abc.saveAsTextFile(outFilePath + "/queryRes");
        
        
        _D2.unpersist();
        
        List<Tuple2<Integer,List<Tuple2<Integer, Double>>>> t = abc.collect();
        
        List<Tuple2<Integer,List<Tuple2<Integer, Double>>>> ret = new ArrayList<>();
        
        for(Tuple2<Integer,List<Tuple2<Integer, Double>>> r:t)
        {
//            for(Tuple2<Integer, Double> i :r._2())
//                System.out.println("pt.spark.sQuery.run() "+ r._1+" "+ i._1 +": "+i._2);
            r._2.sort(new Comparator<Tuple2<Integer, Double>>() {
                @Override
                public int compare(Tuple2<Integer, Double> o1, Tuple2<Integer, Double> o2) {
                    if(o1._2>o2._2) return -1;
                    else if (o1._2<o2._2) return 1;
                    else return 0;
                }
            });
//            for(Tuple2<Integer, Double> i :r._2())
//                System.out.println("pt.spark.sQuery.run() "+ r._1+" "+ i._1 +": "+i._2);
            
            List<Tuple2<Integer, Double>> rl = new ArrayList<>();
//            for(int i = 0; i<30; i++)
            int tmp =0;
            for(Tuple2<Integer, Double> i :r._2())    
            {
                if(tmp>30)
                    break;
                rl.add(i);
                tmp++;
            }
            ret.add(new Tuple2<>(r._1,rl));
        }

        for(Tuple2<Integer,List<Tuple2<Integer, Double>>> r:ret)
        {
        //    System.out.println("top 30 query() "+ r._1);
            for(Tuple2<Integer, Double> a: r._2())
            {
        //         System.out.println(a._1+": "+a._2);
            }
        }
        return ret;
    }
}

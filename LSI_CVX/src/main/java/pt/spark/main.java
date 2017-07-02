package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;


import java.util.LinkedList;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;


public class main {

    /**
     * This is the entry point when the task is called from command line with
     * spark-submit.sh. See {
     *
     * @see http://spark.apache.org/docs/latest/submitting-applications.html}
     * args[0]: input doc args[1]: output args[2]: input query
     */
    public static void main(String[] args) {
        checkArgument(args.length > 0, "Please provide the path of input file as first parameter.");

        String master = "local[*]";
        // currently, not support: matrix data should be prepared before
        // read output from parse data
        double[][] docTermData = pt.paper.CSVFile.readMatrixData(args[0]);
//        double[][] docTermData = pt.paper.CSVFile.readMatrixData("../data/data.csv");
        //TODO: parallel echelon 
        double[][] echelon = LocalVector2D.echelon(docTermData);//
        
        double[][] termDocData = LocalVector2D.Transpose(echelon);
      
        
        // read output from echelon:         
        SparkConf conf = new SparkConf()
                .setAppName(sSCC.class.getName())
                .setMaster(master);
        JavaSparkContext context = new JavaSparkContext(conf);
        
        List<Tuple2<Integer,Vector>> scc = new sSCC().run(context, termDocData, args[0], args[1]);

        double[][] rowsListDocTermRd = getPresentMat(scc, docTermData);//new double[docTermData.length][docTermData[0].length];
        // read outpur from parse data and echelon and sSCC: Ax-B
        List<Tuple2<Integer,Vector>> pMatrix = new sADMM().run(context, docTermData, rowsListDocTermRd, args[0], args[1]);

        // read output from parse+ sADMM 
        new sQuery().run(context, args[0], args[1]);
        
        context.close();
    }
    
    private static double[][] getPresentMat( List<Tuple2<Integer,Vector>> scc, double[][] rowsListDocTerm )
    {
//        List<Tuple2<Integer,Vector>> sccL = scc.collect();
        // reduce same term
        
        //add term to new list
        double[][] ret = null;
        
        return ret;
    }
}

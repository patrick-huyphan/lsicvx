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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
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
        String ouputdir = args[1]+"/"+System.currentTimeMillis();
        // currently, not support: matrix data should be prepared before
        // read output from parse data
        
// testing data
//        double[][] DQ = pt.paper.CSVFile.readMatrixData(args[0]);
        
//        double[][] docTermData = LocalVector2D.subMat(DQ, 0, 26, 0, DQ[0].length);
//        double[][] query = LocalVector2D.subMat(DQ, 26, 3, 0, DQ[0].length);
        
//        double[][] docTermData = pt.paper.CSVFile.readMatrixData(args[0]);
//        double[][] query = new double[10][docTermData[0].length];

        double[][] DQ = pt.paper.CSVFile.readMatrixData(args[0]);
        int numofQ = Integer.parseInt(args[2]);
        
        double[][] docTermData = LocalVector2D.subMat(DQ, 0, DQ.length-numofQ, 0, DQ[0].length);
        double[][] query = LocalVector2D.subMat(DQ, DQ.length-numofQ, numofQ, 0, DQ[0].length);

//        double[][] docTermData = pt.paper.CSVFile.readMatrixData("../data/data.csv");
        //TODO: parallel echelon 
        double[][] echelon = LocalVector2D.echelon(docTermData);//
        
        double[][] termDocData = LocalVector2D.Transpose(echelon); 
      
        
        
        // read output from echelon:         
        SparkConf conf = new SparkConf()
                .setAppName(sSCC.class.getName())
                .setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        List<Tuple2<Integer,Vector>> scc = new sSCC().run(sc, termDocData,
//                args[0], 
                ouputdir);

        double[][] rowsListDocTermRd = sSCC.getPresentMat(scc, docTermData);//new double[docTermData.length][docTermData[0].length];
        // read outpur from parse data and echelon and sSCC: Ax-B
        List<Tuple2<Integer,Vector>> pMatrix = new sADMM().run(sc, docTermData, rowsListDocTermRd, 
//                args[0], 
                ouputdir);

        // read output from parse+ sADMM 
        new sQuery().run(sc, 
                docTermData, 
                pMatrix, 
                query, 
//                args[0], 
                ouputdir);
        
        sc.close();
    }
    
    
}

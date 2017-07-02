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
//    new sParseData().run(master, args[0],args[1]);

        // read output from parse data
//    new sEchelon().run(master, args[0],args[1]);
        double[][] docTermData = pt.paper.CSVFile.readMatrixData(args[0]);
        
        //TODO: parallel echelon 
        double[][] echelon = LocalVector2D.echelon(docTermData);//
        
        double[][] termDocData = LocalVector2D.Transpose(echelon);

//        sCommonFunc.loadDenseMatrix(termDocData);
//        sCommonFunc.loadDenseMatrix(docTermData);
        
        LinkedList<Tuple2<Integer,Vector>> rowsListTermDoc = new LinkedList<>();
        for (int i = 0; i < termDocData.length; i++) {
            Vector currentRow = Vectors.dense(termDocData[i]);
            rowsListTermDoc.add( new Tuple2<>(i,currentRow));
        }

        
        // read output from echelon: 
        JavaRDD<Tuple2<Integer, Vector>> scc = new sSCC().run(master, rowsListTermDoc, args[0], args[1]);

        
        
//        LinkedList<Tuple2<Integer,Vector>> rowsListDocTermRd = new LinkedList<>();
//        for (int i = 0; i < docTermReduce.length; i++) {
//            Vector currentRow = Vectors.dense(docTermReduce[i]);
//            rowsListDocTermRd.add(new Tuple2<>(i,currentRow));
//        }
        
        LinkedList<Tuple2<Integer,Vector>> rowsListDocTerm = new LinkedList<>();
        for (int i = 0; i < docTermData.length; i++) {
            Vector currentRow = Vectors.dense(docTermData[i]);
            rowsListDocTerm.add(new Tuple2<>(i,currentRow));
        }

        LinkedList<Tuple2<Integer,Vector>> rowsListDocTermRd = getPresentMat(scc, rowsListDocTerm);//new double[docTermData.length][docTermData[0].length];
        // read outpur from parse data and echelon and sSCC: Ax-B
        LinkedList<Tuple2<Integer,Vector>> pMatrix = new sADMM().run(master, rowsListDocTerm, rowsListDocTermRd, args[0], args[1]);

        // read output from parse+ sADMM 
        new sQuery().run(master, args[0], args[1]);
    }
    
    private static LinkedList<Tuple2<Integer,Vector>> getPresentMat( JavaRDD<Tuple2<Integer, Vector>> scc, LinkedList<Tuple2<Integer,Vector>> rowsListDocTerm )
    {
        List<Tuple2<Integer,Vector>> sccL = scc.collect();
        // reduce same term
        
        //add term to new list
        LinkedList<Tuple2<Integer,Vector>> ret = null;
        
        return ret;
    }
}

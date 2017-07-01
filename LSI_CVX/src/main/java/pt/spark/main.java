package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.LinkedList;
import org.apache.spark.mllib.linalg.Vectors;

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
        double[][] termDocData = pt.paper.Matrix.Transpose(docTermData);

        LinkedList<org.apache.spark.mllib.linalg.Vector> rowsListTermDoc = new LinkedList<>();
        for (int i = 0; i < termDocData.length; i++) {
            org.apache.spark.mllib.linalg.Vector currentRow = Vectors.dense(termDocData[i]);
            rowsListTermDoc.add(currentRow);
        }
        // read output from echelon: 
        new sSCC().run(master, rowsListTermDoc, args[0], args[1]);

        LinkedList<org.apache.spark.mllib.linalg.Vector> rowsListDocTerm = new LinkedList<>();
        for (int i = 0; i < docTermData.length; i++) {
            org.apache.spark.mllib.linalg.Vector currentRow = Vectors.dense(docTermData[i]);
            rowsListDocTerm.add(currentRow);
        }

        double[][] docTermReduce = getPresentMat();//new double[docTermData.length][docTermData[0].length];
        LinkedList<org.apache.spark.mllib.linalg.Vector> rowsListDocTermRd = new LinkedList<>();
        for (int i = 0; i < docTermReduce.length; i++) {
            org.apache.spark.mllib.linalg.Vector currentRow = Vectors.dense(docTermReduce[i]);
            rowsListDocTermRd.add(currentRow);
        }
        // read outpur from parse data and echelon and sSCC: Ax-B
        new sADMM().run(master, rowsListDocTerm, rowsListDocTermRd, args[0], args[1]);

        // read output from parse+ sADMM 
        new sQuery().run(master, args[0], args[1]);
    }
    
    private static double[][] getPresentMat()
    {
        double[][] ret = null;
        return ret;
    }
}

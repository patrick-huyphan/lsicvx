/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.mllib.linalg.distributed.MatrixEntry;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import org.apache.spark.rdd.*;
import org.apache.spark.mllib.linalg.*;
import scala.Tuple2;

/**
 *
 * @author patrick_huy
 */
public class sCommonFunc {
    
    public static JavaRDD<Vector>  matrixToRDD( JavaSparkContext sc, Matrix m)
    {
        List<Vector> V = null;
        
//        val columns = m.toArray.grouped(m.numRows);
        
        return sc.parallelize( V);//m.rowIter().toList());
    }
    
    public static JavaPairRDD<String, Integer>  readMatrixToRDD( JavaSparkContext sc, String input)
    {  
        return sc.textFile(input)
             .flatMap(text -> Arrays.asList(text.split(" ")).iterator())
             .mapToPair(word -> new Tuple2<>(word, 1))
             .reduceByKey((a, b) -> a + b);
    }
    
    public static JavaRDD<Vector>  array2RDDVector( JavaSparkContext sc, double[][] input)
    {
        LinkedList<Vector> rowsList = new LinkedList<>();
        for (int i = 0; i < input.length; i++) {
          Vector currentRow = Vectors.dense(input[i]);
          rowsList.add(currentRow);
        }
        return sc.parallelize(rowsList);
    }
        
    public static RowMatrix  readMatrixToRowMatrix( JavaSparkContext sc, double[][] input)
    {
        JavaRDD<Vector> rows = array2RDDVector(sc, input);

//        rows.cache();
        
        // Create a RowMatrix from JavaRDD<Vector>.
        RowMatrix mat = new RowMatrix(rows.rdd());
        return mat;
    }
    public static RowMatrix  readMatrixToRowMatrix( JavaSparkContext sc, String input)
    {
        int numR = 10;
        LinkedList<Vector> rowsList = new LinkedList<>();
        List<Tuple2<Integer, Double>> itrbl = new ArrayList<>();
        for (int i = 0; i < numR; i++) {
          Vector currentRow = Vectors.sparse(i, itrbl);
          rowsList.add(currentRow);
        }
        JavaRDD<Vector> rows = sc.parallelize(rowsList);

        // Create a RowMatrix from JavaRDD<Vector>.
        RowMatrix mat = new RowMatrix(rows.rdd());
        return mat;
    }
    public static JavaRDD<List<Double>>  readMatrixToRDD( JavaSparkContext sc, double[][]m)
    {
        
        List<List<Double>> x = new ArrayList<List<Double>>();
        for(double[] i: m)
        {
            List<Double> tmp = Arrays.stream(i).boxed().collect(Collectors.toList());
            x.add(tmp);
        }
        
        return sc.parallelize(x);
    }

    public static JavaPairRDD<Tuple2<Integer,Integer>,Double>  readCSVMatrixToRDD( JavaSparkContext sc, String fileName)
    {
        return sc.textFile(fileName).
                flatMapToPair(text -> Arrays.asList(new Tuple2<>(new Tuple2<>(Integer.parseInt(text.split(" ")[0]),Integer.parseInt(text.split(" ")[1])),Double.parseDouble(text.split(" ")[2]))).iterator());
    }
        
    public static int  sampleRDD( JavaSparkContext sc, Matrix m)
    {
        //Create Java RDD of type integer with list of integers
        final JavaRDD<Integer> intRDD = sc.parallelize(Arrays.asList(1, 2, 3, 4, 50, 61, 72, 8, 9, 19, 31, 42, 53, 6, 7, 23));
        // Create a new Java RDD by removing numbers greater than 10 from integer RDD
        final JavaRDD<Integer> filteredRDD = intRDD.filter((x) -> (x > 10 ? false : true));
        // Create a new transformed RDD by transforming the numbers to their squares
        final JavaRDD<Integer> transformedRDD = filteredRDD.map((x) -> (x * x) );
        // Calculate the sum of all transformed integers. Since reduce is a value function, it will trigger actual execution
        final int sumTransformed = transformedRDD.reduce( (x, y) -> (x + y) );

        System.out.println(sumTransformed);
        
        return sumTransformed;
    }
        
    class GetLength implements Function<String, Integer> {
    public Integer call(String s) { return s.length(); }
    }
    class Sum implements Function2<Integer, Integer, Integer> {
      public Integer call(Integer a, Integer b) { return a + b; }
    }
}

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
public class commonFunc {
    
    public static JavaRDD<Vector>  matrixToRDD( JavaSparkContext context, Matrix m)
    {
//        List<Vector> V = 
        m.rowIter();
        int[] row; //= m.rowIter().toList();
        int[] colum;
        List<Vector> V = null;
        
//        val columns = m.toArray.grouped(m.numRows);
        
        return context.parallelize( V);//m.rowIter().toList());
    }
    
    public static JavaPairRDD<String, Integer>  readMatrixToRDD( JavaSparkContext context, String input)
    {  
        return context.textFile(input)
             .flatMap(text -> Arrays.asList(text.split(" ")).iterator())
             .mapToPair(word -> new Tuple2<>(word, 1))
             .reduceByKey((a, b) -> a + b);
    }
    
    public static RowMatrix  readMatrixToRowMatrix( JavaSparkContext context, double[][] input)
    {
        LinkedList<Vector> rowsList = new LinkedList<>();
        for (int i = 0; i < input.length; i++) {
          Vector currentRow = Vectors.dense(input[i]);
          rowsList.add(currentRow);
        }
        JavaRDD<Vector> rows = context.parallelize(rowsList);

        // Create a RowMatrix from JavaRDD<Vector>.
        RowMatrix mat = new RowMatrix(rows.rdd());
        return mat;
    }
    public static JavaRDD<List<Double>>  readMatrixToRDD( JavaSparkContext context, double[][]m)
    {
        
        List<List<Double>> x = new ArrayList<List<Double>>();
        for(double[] i: m)
        {
            List<Double> tmp = Arrays.stream(i).boxed().collect(Collectors.toList());
            x.add(tmp);
        }
        
        return context.parallelize(x);
    }
        
    public static int  sampleRDD( JavaSparkContext context, Matrix m)
    {
        //Create Java RDD of type integer with list of integers
        final JavaRDD<Integer> intRDD = context.parallelize(Arrays.asList(1, 2, 3, 4, 50, 61, 72, 8, 9, 19, 31, 42, 53, 6, 7, 23));
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

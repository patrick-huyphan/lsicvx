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
import org.apache.spark.mllib.linalg.distributed.IndexedRowMatrix;
import scala.Tuple2;

/**
 *
 * @author patrick_huy
 */
public class sCommonFunc {
    
    public static JavaRDD<Vector>  matrixToRDD( JavaSparkContext sc, Matrix m)
    {
        // Create a dense matrix ((1.0, 2.0), (3.0, 4.0), (5.0, 6.0))
        Matrix dm = Matrices.dense(3, 2, // row, column
                new double[] {1.0, 3.0, 5.0, 2.0, 4.0, 6.0}); // value

        // Create a sparse matrix ((9.0, 0.0), (0.0, 8.0), (0.0, 6.0))
        Matrix sm = Matrices.sparse(3, 2,   // num of row, column
                new int[] {0, 1, 3},        // index of colPtrs:    the index corresponding to the start of a new column 
                new int[] {0, 2, 1},        // index of rowIndices:   the row index of the entry. They must be in strictly increasing order for each column 
                new double[] {9, 6, 8});    // non-zero value

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
    /*
    TODO:
    convert betwen 2Darray and:
    - rowmatrix
    - rowindex matrix
    - coodiantematrix
    */
    public static SparseMatrix loadSparseMatrix(double[][] array)
    {
        return loadDenseMatrix(array).toSparse();
    }
    
    public static SparseMatrix loadSparseMatrix(String inputFile)
    {
        return loadDenseMatrix(inputFile).toSparse();
    }
    public static DenseMatrix loadDenseMatrix(double[][] array)
    {
        int length = array[0].length;
        double[] array1D = new double[array.length* length];
        // load to 1d array of column matrix
        for(int i = 0; i< length; i++)
            for(int j = 0; j< array.length; j++)
            {
                //TODO: check
                array1D[i+ j*array.length] = array[j][i];
            }
        return new DenseMatrix(array.length, length, array1D, true) ;
    }
    
    public static DenseMatrix loadDenseMatrix(String inputFile)
    {
        double[][] array = null;
        return loadDenseMatrix(array);
    }        
    public static CoordinateMatrix loadCM(JavaSparkContext sc, double[][] array)
    {
        List<MatrixEntry> listEntry = new ArrayList<>();
        for(int i = 0; i< array.length; i++)
        {
            for(int j =0; j< array[0].length; j++)
                listEntry.add(new MatrixEntry(i, j, array[i][j]));
        }
        return new CoordinateMatrix(sc.parallelize(listEntry).rdd());
    }
       
    public static CoordinateMatrix loadCM(JavaSparkContext sc, DenseMatrix denseMatrix)
    {
        List<MatrixEntry> listEntry = new ArrayList<>();
        for(int i = 0; i< denseMatrix.numRows(); i++)
        {
            for(int j =0; j< denseMatrix.numCols(); j++)
                listEntry.add(new MatrixEntry(i, j, denseMatrix.apply(i, j)));
        }
        return new CoordinateMatrix(sc.parallelize(listEntry).rdd());
    }
        
    public static  IndexedRowMatrix loadIndexRM(JavaSparkContext sc, double[][] array)
    {
        return loadCM(sc, array).toIndexedRowMatrix();
    }

    public static RowMatrix loadRowM(JavaSparkContext sc, double[][] array)
    {
        return loadIndexRM(sc, array).toRowMatrix();
    }
    
    public static CoordinateMatrix  loadCM( JavaSparkContext sc, String input)
    {
        // read file to array or read file and add to list
        double[][] array= null;
        return loadCM(sc, array);

//        List<MatrixEntry> listEntry = new ArrayList<>();
//        for(int i = 0; i< array.length; i++)
//        {
//            for(int j =0; j< array[0].length; j++)
//                listEntry.add(new MatrixEntry(i, j, array[i][j]));
//        }
//        return new CoordinateMatrix(sc.parallelize(listEntry).rdd());
    }
    public static  IndexedRowMatrix loadIndexRM(JavaSparkContext sc, String input)
    {
        return loadCM(sc, input).toIndexedRowMatrix();
    }
    public static RowMatrix  loadRowM( JavaSparkContext sc, String input)
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

    /*
    TODO
    */
        
    public static DenseMatrix toDenseMatrix(CoordinateMatrix CM)
    {
        int m = (int)CM.numCols();
        int n = (int)CM.numRows();
        RDD<MatrixEntry> matEntry = CM.entries();
        matEntry.collect();
        double[][] ret = new double[n][m];
        for(int i = 0; i<n; i++)
        {
            for(int j = 0; j<m; j++)
            {
                ret[i][j] = 0;
                
            }
        }
//        DenseMatrix ret = null;
        return null;
    }
    
    public static double[][] to2DArray(CoordinateMatrix CM)
    {
        int m = (int)CM.numCols();
        int n = (int)CM.numRows();
        double[][] ret = new double[n][m];
        for(int i = 0; i<n; i++)
        {
            for(int j = 0; j<m; j++)
            {
                ret[i][j] = 0;
                CM.entries();
            }
        }
        return ret;
    }

    
    public static double[][] to2DArray(DenseMatrix dM)
    {
        int m = dM.numCols();
        int n = dM.numRows();
        double[][] ret = new double[n][m];
        for(int i = 0; i<n; i++)
        {
            for(int j = 0; j<m; j++)
                ret[i][j] = dM.apply(i, j);
        }
        return ret;
    }
    public static double[][] to2DArray(SparseMatrix sM)
    {
        return to2DArray(sM.toDense());
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

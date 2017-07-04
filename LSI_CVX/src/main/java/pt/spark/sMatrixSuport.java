/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import org.apache.spark.api.java.JavaSparkContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
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
 * TODO: parallel matrix support funtion such as :
 *  - echelon 
 *  - orthonormal
 *  - inverse
 */
public class sMatrixSuport extends CoordinateMatrix{
    public sMatrixSuport(RDD<MatrixEntry> entries, long nRows, long nCols) {
        super(entries, nRows, nCols);
    }
    public void echelon()
    {
        
    }
    
    public void orthonormal()
    {
        
    }
    
    public void inverse()
    {
        
    }


}

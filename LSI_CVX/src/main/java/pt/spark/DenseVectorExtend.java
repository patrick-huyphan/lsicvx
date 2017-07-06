/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import org.apache.spark.mllib.linalg.DenseVector;

/**
 *
 * @author patrick_huy
 */
public class DenseVectorExtend extends DenseVector{
    
    public DenseVectorExtend(double[] values) {
        super(values);
    }
    
}

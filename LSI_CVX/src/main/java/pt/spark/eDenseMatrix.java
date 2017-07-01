/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import org.apache.spark.mllib.linalg.DenseMatrix;

/**
 *
 * @author patrick_huy
 */
public class eDenseMatrix extends DenseMatrix{
    
    public eDenseMatrix(int i, int i1, double[] doubles, boolean bln) {
        super(i, i1, doubles, bln);
    }
    
    
}

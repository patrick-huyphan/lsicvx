/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

//import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

/**
 * @author patrick_huy
 * D=BX =>X=BtD
 * X(k-m) latent topic space
 * B(n-k) projection, orthogonal matrix
 * D(n-m) term doc matrix
 */
public class LSI {
    double [][] D;
    double [][] B;
    double [][] X;
    double lambda;
    static final int MAX_LOOP = 100;
    int k;// k row in A
    int m;// m column in A
    int n;// n row in D
    
    public LSI(double _Ddata[][], double _Bdata[][], double _lambda)
    {
        n = _Ddata.length;// n row in D
        m = _Ddata[0].length;// m column in A
        k = _Bdata[0].length;// k row in A
        lambda = _lambda;
        
        D = _Ddata;
        B = _Bdata;
        X = new double[k][m];
    }
}

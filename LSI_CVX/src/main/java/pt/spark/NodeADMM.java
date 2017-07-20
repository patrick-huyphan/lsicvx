/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.text.DecimalFormat;
import java.util.List;
import org.apache.spark.mllib.linalg.Vector;
import pt.spark.LocalVector;
import pt.spark.LocalMatrix;
import scala.Tuple2;
/**
 *
 * @author patrick_huy
 * D=BX =>X=BtD
 * X(k-m) latent topic space
 * B(n-k) projection, orthogonal matrix
 * D(n-m) term doc matrix
 */
public class NodeADMM {
    double []X;
    double lambda;
    static final int MAX_LOOP = 100;
    int k;// k row in A
    int m;// m column in A
    int n;// n row in D
    double rho;
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    
    /**
     * 
     * @param _Ddata
     * @param _n
     * @param _m
     * @param _k
     * @param Bt
     * @param BtB
     * @param AtB
     * @param _rho
     * @param _lambda
     * @param e1
     * @param e2 
     */

    public NodeADMM(Tuple2<Integer, Vector> _Ddata, 
            int _n,
            int _m,
            int _k,
            double[][] Bt,
            double[][] BtB,
            double[][] AtB,
            double _rho,double _lambda, 
            double e1, double e2, int loopt) {

        n = _n;// n row in D
        m = _m;// m column in A
        k = _k;// k row in A
        lambda = _lambda;
        
        X = new double[k];//[m];
     
//        double[][] BD = Matrix.mul(Bt,D );
//        Matrix.printMat(AtB, "AtB");    
//        Matrix.printMat(Am, "At");
//        Matrix.printMat(_Bdata, "B");
//        Matrix.printMat(B, "B2");
//        Matrix.printMat(D, "D");
//        Matrix.printMat(BD, "B orthonormal");
//        Matrix.printMat(Bt, "Bt");
//        Matrix.printMat(BtB, "BtB");
//        Matrix.printMat(BtB_rho_Im, "BtB_rho_Im");
//        Matrix.printMat(Matrix.IMtx(k), "Matrix.IMtx(k)");
//        Matrix.printMat(IMtxRho, "IMtxRho");
//        System.out.println("paper.NodeADMM.<init>() rho "+ _rho);
        
        {
            rho = _rho;
            boolean stop = false;
            
            //init x, u ,v
//            double[] x = new double[k]; //Matrix.getCol(BD, i);//new double[k]; //
            double[] z= LocalVector.rVector(k, 0.4);
            double[] u = LocalVector.scale(z, -0.05);//new double[k];//Vector.scale(z, -0.5);   // [k]; new double[k];
            double[] d=  _Ddata._2.toArray(); //LocalVector2D.getCol(D, id); //[n]*m
            double[] Btd= LocalMatrix.mul(Bt, d); //[nk]*[n] = k
            
//            Vector.printV(z, "init z "+i, true);
//            Vector.printV(u, "init u "+i, true);
//            Vector.printV(x, "init x "+i, true);
//            Vector.printV(d, "di", true);
//            Vector.printV(Btd, "Btd "+i, true);
            
            
            double[] x0 = new double[k];
            double[] z0 = new double[k];//Matrix.getCol(BtB, i%k);  // [k]; new double[k];
            double[] u0 = new double[k];//Vector.scale(z, 0.5);   // [k]; new double[k];
            
            int loop = 0;
            while(loop<loopt)//1489) 143 // long = short+1
            {
                double[][] IMtxRho = LocalMatrix.scale(BtB, rho);
                double[][] iBtB_rho_Im = LocalMatrix.invert(LocalMatrix.plus(BtB, IMtxRho)); //[kk]
//                System.out.print(".");
                X= updateX(u, z,iBtB_rho_Im,Btd);
//                double lamPRho = ;
                z= updateZ(X, u, lambda/rho);                
                u= updateU(X, u, z);
                
//            Vector.printV(z, "z:"+ i+"-"+loop, true);
//            Vector.printV(u, "u:"+ i+"-"+loop, true);
//            Vector.printV(x, "x:"+ i+"-"+loop, true);

                if(loop>1)
                    stop = checkStop(z, x0, u0, z0,e1,e2,k,m, AtB, loop);
                x0=LocalVector.copy(X);
                u0=LocalVector.copy(u);
                z0=LocalVector.copy(z);
//                if(loop>1450)
//                    Vector.printV(x, "x:"+ i+"-"+loop +" rho:"+rho, true);
                if(stop)// && loop>1)
                {
                    System.err.println(_Ddata._1+" Stop at "+loop);
                    break;
                }
                loop++;
            }
            System.out.println(_Ddata._1 + "\t"+loop);
        }
    }
    
    //x^{k+1} = 2(A^TA + \rho I_m)^-1 [ A^Tb - \rho (z^k - u^k)]
    private double[] updateX(double[] u, double[] z,double[][] BtB_rho_Im, double[] Btd)
    {
        //- rho (z^k - u^k)[k]-[k]
        double[] rho_zk_uk=  LocalVector.scale((LocalVector.plus(z, LocalVector.scale(u, -1))),rho*(-1.0));
        double[] ret = LocalMatrix.mul(BtB_rho_Im, LocalVector.plus(Btd, rho_zk_uk));
        
//        for(int d= 0; d< ret.length; d++)
//        {
//            if((1e-8>=(-1*ret[d])) && (ret[d]<1e-8))
//                ret[d] = 0;
//        }
        return LocalVector.scale(ret,2);
    }
    
    /*
     * u^{k+1} = u^k + x^{k+1} - z^{k+1}
     */    
    private double[] updateU(double[] x, double[] u, double[] z)
    {
        return LocalVector.plus(u, LocalVector.plus(x, LocalVector.scale(z, -1)));
    }

    /*
     * z^{k+1} = S_{\frag{\lambda}{\rho}}(x^{k+1} + u^k)
     * = x^{k+1} +u^k - frag{\lambda}{\rho} if >
     * = 0 if || \leq
     * = x^{k+1} +u^k + frag{\lambda}{\rho} if < 
     */    
    private double[] updateZ(double[] x, double[] u, double lamPRho)
    {
//        double lamPRho = lambda/rho;
        return LocalVector.proxN1(LocalVector.plus(x, u), lamPRho);        
    }
    
    private double updateRho(double r, double s)
    {
        if(r>10*s)
            rho =  Double.valueOf(twoDForm.format(rho* 1.5));//(r/s);//2*rho;
        if(s>10*r)
        {
//            System.err.println("s>r");
            rho =  Double.valueOf(twoDForm.format(rho* 0.75));//(r/s);//rho/2;
        }
        System.err.println("update rho "+rho);
        return rho;
    }
    
    private double dualResidual(double[] Zp, double[] Z)
    {
        return LocalVector.norm(LocalVector.scale(LocalVector.plus(Zp, LocalVector.scale(Z, -1)),rho));
    }
    
    private double primalResidual(double[] X0, double[] Z0)
    {
        return LocalVector.norm(LocalVector.plus(X0, LocalVector.scale(Z0,-1)));
    }
    
    
    private boolean checkStop(double[] z, double[] x0, double[] u0, double[] z0, double epsilonA, double epsilonR, int n, int m, double [][] A,int time)
    {
//        if(this.rho ==0)
//            return true;
        double r = primalResidual(x0, z0);
        double s = dualResidual(z0, z);

        double eP = epsilonA * Math.sqrt(n) + epsilonR*((LocalVector.norm(x0)>LocalVector.norm(z0))?LocalVector.norm(x0): LocalVector.norm(z0));
        double eD = epsilonA * Math.sqrt(m) + epsilonR*(LocalVector.norm(LocalMatrix.mul(A,LocalVector.scale(u0,rho))));

//        System.err.println("new rho "+rho+": \t"+r+" - "+s +"\t"+eP+":"+eD);
        if((r<= eP) && (s<=eD))
        {
//            System.err.println("new rho "+rho+": \t"+r+" - "+s +"\t"+eP+":"+eD);
        		return true;
        }

        updateRho(r, s);
        return false;
    }
}

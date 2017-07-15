/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * @author patrick_huy
 * D=BX =>X=BtD
 * X(k-m) latent topic space
 * B(n-k) projection, orthogonal matrix
 * D(n-m) term doc matrix
 */
public class ADMM extends LSI{
//    double [][] D;
//    double [][] B;
//    double [][] X;

    /**
     *
     * @param _Ddata
     * @param _Bdata
     * @param rho
     * @param lambda
     * @param e1
     * @param e2
     */
    
    double rho;
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    
    public ADMM(double [][] _Ddata, double [][] _Bdata, double _rho,double _lambda, double e1, double e2) {
        super(_Ddata, _Bdata, _lambda);
        B = Matrix.orthonormal( _Bdata);//Matrix.Transpose(_Bdata);

        double[][] Bt = Matrix.Transpose(B); //[nk]->[kn]
        double[][] BtB = Matrix.IMtx(k);//Matrix.mul(Bt, B); //[kn]*[nk]=[kk]
        double[][] Am = Matrix.Transpose(BtB);
        double[][] Bm = Matrix.scale(Am, -1);
        double[][] AtB = Matrix.mul(Am, Bm);
        
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
//        System.out.println("paper.ADMM.<init>() rho "+ _rho);
        
        for(int i = 0;i<m; i++)
        {
            rho = _rho;
            boolean stop = false;
            
            //init x, u ,v
            double[] x = new double[k]; //Matrix.getCol(BD, i);//new double[k]; //
            double[] z= Vector.rVector(k, 0.4);
            double[] u = Vector.scale(z, -0.05);//new double[k];//Vector.scale(z, -0.5);   // [k]; new double[k];
            double[] d=  Matrix.getCol(D, i); //[n]*m
            double[] Btd= Matrix.mul(Bt, d); //[nk]*[n] = k
            
//            Vector.printV(z, "init z "+i, true);
//            Vector.printV(u, "init u "+i, true);
//            Vector.printV(x, "init x "+i, true);
//            Vector.printV(d, "di", true);
//            Vector.printV(Btd, "Btd "+i, true);
            
            
            double[] x0 = new double[k];
            double[] z0 = new double[k];//Matrix.getCol(BtB, i%k);  // [k]; new double[k];
            double[] u0 = new double[k];//Vector.scale(z, 0.5);   // [k]; new double[k];
            
            int loop = 0;
            while(loop<146)//1489) 143 // long = short+1
            {
                double[][] IMtxRho = Matrix.scale(BtB, rho);
                double[][] iBtB_rho_Im = Matrix.invert(Matrix.plus(BtB, IMtxRho)); //[kk]
        
                x= updateX(u, z,iBtB_rho_Im,Btd);
//                double lamPRho = ;
                z= updateZ(x, u, lambda/rho);                
                u= updateU(x, u, z);
                
//            Vector.printV(z, "z:"+ i+"-"+loop, true);
//            Vector.printV(u, "u:"+ i+"-"+loop, true);
//            Vector.printV(x, "x:"+ i+"-"+loop, true);

                if(loop>1)
                    stop = checkStop(z, x0, u0, z0,e1,e2,k,m, AtB, loop);
                x0=Vector.copy(x);
                u0=Vector.copy(u);
                z0=Vector.copy(z);
//                if(loop>1450)
//                    Vector.printV(x, "x:"+ i+"-"+loop +" rho:"+rho, true);
                if(stop)// && loop>1)
                {
//                    System.err.println(i+" Stop at "+loop);
                    break;
                }
                loop++;
            }
            System.out.println(".");
//            Vector.printV(x, "x_"+ i, true);
            X = Matrix.updateCol(X, x,i);
        }
        Matrix.printMat(X, "return");
    }
    
    //x^{k+1} = 2(A^TA + \rho I_m)^-1 [ A^Tb - \rho (z^k - u^k)]
    private double[] updateX(double[] u, double[] z,double[][] BtB_rho_Im, double[] Btd)
    {
        //- rho (z^k - u^k)[k]-[k]
        double[] rho_zk_uk=  Vector.scale((Vector.plus(z, Vector.scale(u, -1))),rho*(-1.0));
        double[] ret = Matrix.mul(BtB_rho_Im, Vector.plus(Btd, rho_zk_uk));
        
//        for(int d= 0; d< ret.length; d++)
//        {
//            if((1e-8>=(-1*ret[d])) && (ret[d]<1e-8))
//                ret[d] = 0;
//        }
        return Vector.scale(ret,2);
    }
    
    /*
     * u^{k+1} = u^k + x^{k+1} - z^{k+1}
     */    
    private double[] updateU(double[] x, double[] u, double[] z)
    {
        return Vector.plus(u, Vector.plus(x, Vector.scale(z, -1)));
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
        return Vector.proxN1(Vector.plus(x, u), lamPRho);        
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
        return rho;
    }
    
    private double dualResidual(double[] Zp, double[] Z)
    {
        return Vector.norm(Vector.scale(Vector.plus(Zp, Vector.scale(Z, -1)),rho));
    }
    
    private double primalResidual(double[] X0, double[] Z0)
    {
        return Vector.norm(Vector.plus(X0, Vector.scale(Z0,-1)));
    }
    
    
    private boolean checkStop(double[] z, double[] x0, double[] u0, double[] z0, double epsilonA, double epsilonR, int n, int m, double [][] A,int time)
    {
//        if(this.rho ==0)
//            return true;
        double r = primalResidual(x0, z0);
        double s = dualResidual(z0, z);

        double eP = epsilonA * Math.sqrt(n) + epsilonR*((Vector.norm(x0)>Vector.norm(z0))?Vector.norm(x0): Vector.norm(z0));
        double eD = epsilonA * Math.sqrt(m) + epsilonR*(Vector.norm(Matrix.mul(A,Vector.scale(u0,rho))));

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

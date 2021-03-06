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
//            lsi = new ADMM(D, B, 0.04, 0.8, 0.005, 0.0001);
    public ADMM(double [][] _Ddata, double [][] _Bdata, double _rho,double _lambda, double e1, double e2, boolean isOrth) {
        super(_Ddata, _Bdata, _lambda);
        
        if(isOrth)
            B = Matrix.orthonormal( _Bdata);//Matrix.Transpose(_Bdata);
//        else
//            B = _Bdata;//Matrix.Transpose(_Bdata);
        
        double[][] Bt = Matrix.Transpose(B); //[nk]->[kn]
        double[][] BtB = Matrix.IMtx(k);//Matrix.mul(Bt, B); //[kn]*[nk]=[kk]
        double[][] Am = Matrix.Transpose(BtB);
        double[][] Bm = Matrix.scale(Am, -1);
        double[][] AtB = Matrix.mul(Am, Bm);
        double[][] BtD= Matrix.mul(Bt, D); //[nk]*[n] = k
//        double[][] BD = Matrix.mul(Bt,D );
//        Matrix.printMat(_Bdata, "B");
//        Matrix.printMat(B, "B2");
//        Matrix.printMat(D, "D");
//        Matrix.printMat(BD, "B orthonormal");
//        Matrix.printMat(Bt,false, "Bt");
//        Matrix.printMat(BtB, "BtB");
//        Matrix.printMat(Am, "At");
//        Matrix.printMat(Bm, "Bt");
//        Matrix.printMat(AtB, "AtB");
//        Matrix.printMat(BtB_rho_Im, "BtB_rho_Im");
//        Matrix.printMat(Matrix.IMtx(k), "Matrix.IMtx(k)");
//        Matrix.printMat(IMtxRho, "IMtxRho");
//        System.out.println("paper.ADMM.<init>() rho "+ _rho);
        
        for(int i = 0;i<m; i++)
        {           
            X = Matrix.updateCol(X, admmProcess(i, _rho,BtB, BtD, AtB, e1, e2),i);
        }
//        Matrix.printMat(X, "return");
    }
    
    private double[] admmProcess(int i, double _rho, double[][] BtB, double[][] BtD, double[][] AtB, double e1, double e2)
    {
            rho = _rho;
//            boolean stop = false;
            
            //init x, u ,v
            double[] x = new double[k]; //Matrix.getCol(BD, i);//new double[k]; //
            double[] z=  new double[k]; //Vector.rVector(k, 0.4);
            double[] u = new double[k]; //Vector.scale(z, -0.05);//new double[k];//Vector.scale(z, -0.5);   // [k]; new double[k];
            double[] Btd= Matrix.getCol(BtD, i);//Matrix.mul(Bt, d); //[nk]*[n] = k
            
//            Vector.printV(z, "init z "+i, true);
//            Vector.printV(u, "init u "+i, true);
//            Vector.printV(x, "init x "+i, true);
//            Vector.printV(d, "di", true);
//            Vector.printV(Btd, "Btd "+i, true);
            
            
            double[] x0 = new double[k];
            double[] z0 = new double[k];//Matrix.getCol(BtB, i%k);  // [k]; new double[k];
            double[] u0 = new double[k];//Vector.scale(z, 0.5);   // [k]; new double[k];
            
//            double s, r;
            
            int loop = 0;
            while(loop<MAX_LOOP)//1489) 143 // long = short+1
            {
                double[][] IMtxRho = Matrix.scale(BtB, rho);
                double[][] iBtB_rho_Im = Matrix.invert(Matrix.plus(BtB, IMtxRho)); //[kk]

//                if(i == 0 && loop<5)
//                {
//                    System.out.println(loop+":   "+Vector.norm(x)+" - "+Vector.norm(z)+" - "+Vector.norm(u) + "  -  "+rho);
//                }
//                
                x= updateX(u, z,iBtB_rho_Im,Btd);
                z= updateZ(x, u, lambda/rho);                
                u= updateU(x, u, z);
//            if(i==0)
//            {
//            Vector.printV(z0, "z:"+ i+"-"+loop, true);
//            Vector.printV(u, "u:"+ i+"-"+loop, true);
//            Vector.printV(x0, "x:"+ i+"-"+loop, true);
//            }
                if(checkStop(z, x0, u0, z0,e1,e2,k,m, AtB, loop, i) && loop>1){
//                    System.out.println(i+" pt.paper.ADMM.admmProcess() stop 1 at "+ loop);
                    break;
                }
//                s = Vector.norm(Vector.sub(x, z));
//                r = Vector.norm(Vector.sub(z, z0));
////                System.out.println(i+ " pt.paper.ADMM.admmProcess() "+loop+": "+ s+" - "+r);
//                if(r<0 && s <0)
//                {
//                    System.out.println(i+" pt.paper.ADMM.admmProcess() stop 2  at "+ loop);
//                    break;
//                }

//                if(i == 0 && loop<5)
//                {
//                    System.out.println(loop+":   "+Vector.norm(x)+" - "+Vector.norm(z)+" - "+Vector.norm(u));
//                }
                x0=Vector.copy(x);
                u0=Vector.copy(u);
                z0=Vector.copy(z);
//                if(loop>1450)
//                    Vector.printV(x, "x:"+ i+"-"+loop +" rho:"+rho, true);
                loop++;
            }
//            System.out.println(".");
//            Vector.printV(x, "x_"+ i, true);
            return x;
    }
    //x^{k+1} = 2(A^TA + \rho I_m)^-1 [ A^Tb - \rho (z^k - u^k)]
    // H= (AtA/n+I)^-1
    // X = HAtb +H(z-u) 
    //   = (AtA/n+I)^-1 Atb + (AtA/n+I)^-1(z-u)
    //   = (AtA/n+I)^-1 (Atb + z-u)
    private double[] updateX(double[] u, double[] z,double[][] AtA_rho_Im, double[] Atb)
    {
        //- rho (z^k - u^k)[k]-[k]
        double[] rho_zk_uk=  Vector.scale((Vector.sub(z, u)),rho*(-1.0));
        double[] ret = Matrix.mul(AtA_rho_Im, Vector.plus(Atb, rho_zk_uk));
        for(int i = 0; i< k; i++)
        {
            ret[i] = (ret[i]>0)?ret[i]:0; // sastify A>0
        }
        return ret;//Vector.scale(ret,2);
    }
    
    /*
     * u^{k+1} = u^k + x^{k+1} - z^{k+1}
     */    
    private double[] updateU(double[] x, double[] u, double[] z)
    {
        return Vector.plus(u, Vector.sub(x, z));
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
            rho =  Double.valueOf(twoDForm.format(rho* 2));//(r/s);//2*rho;
        if(s>10*r)
        {
//            System.err.println("s>r");
            rho =  Double.valueOf(twoDForm.format(rho* 0.5));//(r/s);//rho/2;
        }
        return rho;
    }
    
    private double dualResidual(double[] Zp, double[] Z)
    {
        return Vector.norm(Vector.scale(Vector.sub(Zp, Z),rho));
    }
    
    private double primalResidual(double[] X0, double[] Z0)
    {
        return Vector.norm(Vector.sub(X0, Z0));
    }
    
    
    private boolean checkStop(double[] z, double[] x0, double[] u0, double[] z0, double epsilonA, double epsilonR, int n, int m, double [][] A,int time, int id)
    {
//        if(this.rho ==0)
//            return true;
        double r = primalResidual(x0, z0);
        double s = dualResidual(z0, z);

        double eP = epsilonA * Math.sqrt(n) + epsilonR*((Vector.norm(x0)>Vector.norm(z0))?Vector.norm(x0): Vector.norm(z0));
        double eD = epsilonA * Math.sqrt(m) + epsilonR*(Vector.norm(Matrix.mul(A,Vector.scale(u0,rho))));


        if((r<= eP) && (s<=eD))
        {
//            System.err.println(" Stop at "+time+ " new rho "+rho+": \t"+r+" - "+s +"\t"+eP+":"+eD);
            return true;
        }

        updateRho(r, s);
//        if(time<5 && id ==0)
//            System.err.println(time+ "new rho "+rho+": \t"+r+" - "+s);
        return false;
    }
}

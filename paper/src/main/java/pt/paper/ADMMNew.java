/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.optimizers.LPOptimizationRequest;
import com.joptimizer.optimizers.LPPrimalDualMethod;
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
public class ADMMNew extends LSI{
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
    public ADMMNew(double [][] _Ddata, double [][] _Bdata, double _rho,double _lambda, double e1, double e2) throws JOptimizerException {
        super(_Ddata, _Bdata, _lambda);
        B = Matrix.orthonormal( _Bdata);//Matrix.Transpose(_Bdata);

        double[][] Bt = Matrix.Transpose(B); //[nk]->[kn]
        double[][] BtB = Matrix.IMtx(k);//Matrix.mul(Bt, B); //[kn]*[nk]=[kk]
        double[][] Am = Matrix.Transpose(BtB);
        double[][] Bm = Matrix.scale(Am, -1);
        double[][] AtB = Matrix.mul(Am, Bm);
//      double[] d=  Matrix.getCol(D, i); //[n]*m
        double[][] Btd= Matrix.mul(Bt, D); //[nk]*[n] = k         

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
        
//          double tmp [][] = LP(D, B);
//          Matrix.printMat(tmp, " LP");

        rho = _rho;
//            boolean stop = false;

        //init x, u ,v
//            double[][] x = new double[m][k]; //Matrix.getCol(BD, i);//new double[k]; //
        double[][] z= new double[m][k];
        for(int i = 0; i<m; i++)
            z[i] = Vector.rVector(k, 0.4);
        double[][] u = Matrix.scale(z, -0.05);//new double[k];//Vector.scale(z, -0.5);   // [k]; new double[k];
//            System.out.println("pt.paper.ADMMNew.<init>() "+u.length + " "+u[0].length +" - "+z.length + " "+z[0].length); 
//            double[] d=  Matrix.getCol(D, i); //[n]*m
//            double[] Btd= Matrix.mul(Bt, d); //[nk]*[n] = k

//            Vector.printV(z, "init z "+i, true);
//            Vector.printV(u, "init u "+i, true);
//            Vector.printV(x, "init x "+i, true);
//            Vector.printV(d, "di", true);
//            Vector.printV(Btd, "Btd "+i, true);


        double[][] x0 = new double[k][m];
        double[][] z0 = new double[m][k];//Matrix.getCol(BtB, i%k);  // [k]; new double[k];
        double[][] u0 = new double[m][k];//Vector.scale(z, 0.5);   // [k]; new double[k];

        int loop = 0;
        while(loop<146)//1489) 143 // long = short+1
        {
            for(int i = 0;i<m; i++)
            {
                double[][] IMtxRho = Matrix.scale(BtB, rho);
                double[][] iBtB_rho_Im = Matrix.invert(Matrix.plus(BtB, IMtxRho)); //[kk]
//                    System.out.println("pt.paper.ADMMNew.<init>() "+u.length + " "+u[i].length +" - "+z.length + " "+z[i].length);        
                Matrix.updateCol(X, updateX(i, u, z,iBtB_rho_Im, Matrix.getCol(Btd, i)), i) ;//[i]= updateX(i, u, z,iBtB_rho_Im, Matrix.getCol(Btd, i));
//                double lamPRho = ;
                z[i]= updateZ(i, u, lambda/rho);                
                u[i]= updateU(i, u, z);
            }
//            if(i==0)
//            {
//            Vector.printV(z0, "z:"+ i+"-"+loop, true);
//            Vector.printV(u, "u:"+ i+"-"+loop, true);
//            Vector.printV(x0, "x:"+ i+"-"+loop, true);
//            }
            if(loop>1 && checkStop(z, x0, u0, z0,e1,e2,k,m, AtB, loop)){
                break;
//                    System.err.println("update rho "+ loop+": "+rho);
            }
            x0=Matrix.Copy(X);
            u0=Matrix.Copy(u);
            z0=Matrix.Copy(z);
//                if(loop>1450)
//                    Vector.printV(x, "x:"+ i+"-"+loop +" rho:"+rho, true);
            loop++;
        }
//            System.out.println(".");
//            Vector.printV(x, "x_"+ i, true);
//            X = x;//Matrix.updateCol(X, x,0); //TODO: update

//        Matrix.printMat(X, "return");
    }
    
    private double[][] LP(double [][] D, double [][] B) throws JOptimizerException
    {
        double ret[][]= new double[k][m];
        
        // C=?
        // G= D
        // h = B.getcol(i)
        for(int i =0; i<m; i++)
        {
            double[] c = Matrix.getCol(B, i) ;//new double[]{2., 1.}; //???
            // D
//            double[][] G = 
//                new double[][]{
//                {-1., 1.},
//                {-1., -1.},
//                {0, -1.},
//                {1., -2.},
//            };

            double[] h = Matrix.getCol(B, i);//new double[]{1.,-2., 0, 4.};
            //Bounds on variables
    //        double[] lb = new double[]{0, 0};
    //        double[] ub = new double[]{10, 10};

            //optimization problem
            LPOptimizationRequest or = new LPOptimizationRequest();
            or.setC(c);
            or.setG(Matrix.Transpose(D));
            or.setH(h);
    //        or.setLb(lb);
    //        or.setUb(ub);
            or.setDumpProblem(true);

            //optimization
            LPPrimalDualMethod opt = new LPPrimalDualMethod();

            opt.setLPOptimizationRequest(or);
            opt.optimize();

            double[] sol = opt.getOptimizationResponse().getSolution();
//            for (double s : sol) {
//                System.out.println("pt.paper.main.jopE() LP :" + s);
//            }
            Matrix.updateCol(ret, sol, i);
        }
        
        return ret;
    }
    /**
     * TODO: min||||
     * x^{k+1} = 2(A^TA + \rho I_m)^-1 [ A^Tb - \rho (z^k - u^k)]
     * @param id
     * @param u
     * @param z
     * @param BtB_rho_Im
     * @param Btd
     * @return 
     */
    private double[] updateX(int id,double[][] u, double[][] z,double[][] BtB_rho_Im, double[] Btd) throws JOptimizerException
    {
        //- rho (z^k - u^k)[k]-[k]
        double[] rho_zk_uk=  Vector.scale((Vector.sub(z[id], u[id])),rho*(-1.0));
        double[] ret = Matrix.mul(BtB_rho_Im, Vector.plus(Btd, rho_zk_uk));
        return Vector.scale(ret,2);
    }
    
    /**
     * u^{k+1} = u^k + x^{k+1} - z^{k+1}
     * 
     * @param id
     * @param x
     * @param u
     * @param z
     * @return
     */    
    private double[] updateU(int id,double[][] u, double[][] z)
    {
        return Vector.plus(u[id], Vector.sub( Matrix.getCol(X, id),  z[id]));
    }

    /**
     * TODO: min ||||
     * z^{k+1} = S_{\frag{\lambda}{\rho}}(x^{k+1} + u^k)
     * = x^{k+1} +u^k - frag{\lambda}{\rho} if >
     * = 0 if || \leq
     * = x^{k+1} +u^k + frag{\lambda}{\rho} if < 
     * 
     * @param id
     * @param x
     * @param u
     * @param lamPRho
     * @return 
     */
    private double[] updateZ(int id, double[][] u, double lamPRho)
    {
//        double lamPRho = lambda/rho;
        return Vector.proxN1(Vector.plus(Matrix.getCol(X, id), u[id]), lamPRho);        
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
    
    private double dualResidual(double[][] Zp, double[][] Z)
    {
        double ret[] = new double[Zp.length];
        for(int i = 0; i< Zp.length; i++)
        {
            ret[i] = Vector.norm(Vector.scale(Vector.sub(Zp[i], Z[i]),rho));
        }
        return Vector.norm(ret);
    }
    
    private double primalResidual(double[][] X0, double[][] Z0)
    {
        double ret[] = new double[X0[0].length];
        for(int i = 0; i< X0[0].length; i++)
        {
            ret[i] = Vector.norm(Vector.sub(Matrix.getCol(X0, i), Z0[i]));
        }
        return Vector.norm(ret);
    }
    
    /**
     * TODO:
     * 
     * @param z
     * @param x0
     * @param u0
     * @param z0
     * @param epsilonA
     * @param epsilonR
     * @param n
     * @param m
     * @param A
     * @param time
     * @return 
     */
    private boolean checkStop(double[][] z, double[][] x0, double[][] u0, double[][] z0, double epsilonA, double epsilonR, int n, int m, double [][] A,int time)
    {
//        if(this.rho ==0)
//            return true;
        double r = primalResidual(x0, z0);
        double s = dualResidual(z0, z);

        double eP = epsilonA * Math.sqrt(n) + epsilonR*((Vector.norm(x0[0])>Vector.norm(z0[0]))?Vector.norm(x0[0]): Vector.norm(z0[0]));
        double eD = epsilonA * Math.sqrt(m) + epsilonR*(Vector.norm(Matrix.mul(A,Vector.scale(u0[0],rho))));

//        System.err.println(time+ "new rho "+rho+": \t"+r+" - "+s +"\t"+eP+":"+eD);
        if((r<= eP) && (s<=eD))
        {
//            System.err.println(" Stop at "+time+ " new rho "+rho+": \t"+r+" - "+s +"\t"+eP+":"+eD);
            return true;
        }

        updateRho(r, s);
        return false;
    }
}

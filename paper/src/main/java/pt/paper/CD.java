/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.util.Random;

/**
 *
 * @author patrick_huy
 */
public class CD extends LSI{
        /*
		BA=D
		A(k-m) latent
		B(n-k) projection
		D(n-m) term doc
     */

    /**
     *
     * @param D
     * @param B
     * @param lamda
     */

    
    public CD(double _D[][], double _B[][], double lamda) {
        super(_D, _B,lamda);
        System.out.println("CD() "+X.length+" "+X[0].length);
       
//        B = _B;//Matrix.orthonormal( _B);
//        X = Matrix.mul(Matrix.Transpose(B),D );
        // init k row-m column value of A
        for (int i = 0; i < k; i++) {
            for (int j = 0; j < m; j++) {
                X[i][j] = new Random().nextDouble();
            }
        }
        Matrix.printMat(X, "init X");
        //run m column of A
        for (int i = 0; i < m; i++) {
            boolean stop;// = false;
            //solve for colum i
            int loop =0;
            int indexS[] = new int[k];
            while (loop<MAX_LOOP) {
                stop = true;
                //calculate k value of Am
                
                for (int j = 0; j < k; j++) {
                    if(indexS[j] == 0)//if aj satisfy, not update aj
                    {
                        double b = 0;
                        double x = 0;
                        for (int l = 0; l < n ; l++) {
                            //cal beta:
                            double sum_bA = 0;
                            for (int t = 0; t < k ; t++) {
                                if ((t != j) && (B[l][t] != 0)) {
                                    sum_bA += B[l][t] * X[t][i];
    //                                if(X[t][i]!=0)
    //                                    System.out.println("X[t][i] "+t+"-"+i+":"+X[t][i]);
                                }
                            }
                            if (B[l][j] != 0) {
                                b += B[l][j] * (D[l][i] - sum_bA);
                                x += B[l][j] * B[l][j];
                            }
                        }

                        X[j][i] = (b > lamda) ? (b - lamda) / x 
//                                : ((b*(-1)) > lamda)?((b + lamda) / x)
                                :0;
                        //check stop: when all A[i][m]*x-b+lamda = 0->stop
                        double tmp = X[j][i] * x - b + lamda;
                        if(loop>1 && (tmp > -1e-9) && (tmp <1e-9))
                        {
                            tmp=0;
    //                        index++;
                            indexS[j]=1;
                            System.out.println(loop+"\t"+ i+"-"+j+":\ta: "+X[j][i]+ "\tb:"+b+"\tx:"+ x+ "\tstop:"+tmp+"\t");
                        }
                        else// (tmp != 0)
                            stop = false;
//                        System.out.println(loop+"\t"+ i+"-"+j+":\ta: "+X[j][i]+ "\tb:"+b+"\tx:"+ x+ "\tstop:"+tmp+"\t");
                    }
//                    System.out.println(j+":\ta"+i+":"+X[j][i]+ "\tb:"+b+"\tx:"+ x+ "\tstop:"+tmp+"\t");
                }

                if(stop) 
                {
                        System.out.println("coordinateDescent runing");
                        break;
                }
//                else
//                    stop = false;
                
                loop++;
            }
//            Vector.printV(Matrix.getCol(X, i), "X"+i, true);
        }
        Matrix.printMat(X, "ret");
//        return A;
    }

    /*
	 * TODO
		AX - B + lamda =0
     */
    private boolean checkStop(double A[][], double B[], double X[], int m) {
        boolean ret = true;
        for (int i = 0; i < B.length; i++) {
            double tmp = A[i][m] * X[i] - B[i] + lambda;
            System.out.println("paper.Paper.checkStop() " + i + " " + tmp);
            if (tmp != 0) {
                ret = false;
            }
        }
        return ret;
    }

}

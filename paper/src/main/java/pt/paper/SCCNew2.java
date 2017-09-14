/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import static pt.paper.Clustering.MAX_LOOP;

/**
 *
 * @author patrick_huy update in X update in rho theta
 * SCC is quadratic programming:
 * minimize->x: (1/2)x_TPx+qTx+r
 * Gx ≤ h 
 * Ax = b
 * where P ∈ S^+n, G ∈ R^mXn and A ∈ R^pXn
 * 
 * For convex clustering min fx + gx +h
 * fx: ||A - X||, cost of node, min of sum square b.w node X to control node A. A is inference centroid node. if Ai == Aj, Xi and Xj has same centroid and in same cluster.
 * gx: lambda wij||Xi - Xj||, cost of edge, the edge cost is a sum of norms of differences of the adjacent edge variables
 * h:  lambda2 u||X|| for sparse data
 * 
 */

public class SCCNew2 extends Clustering {

    double rho;
//    double rho2;
    double lambda2;
    double ea, er;
    public double[][] X;
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
//    boolean stop = false;

    double[] u;
    double[] xAvr;
    
    /**
     * with convex optimization, set start point and solve problem with linear
     * or quadratic programming:
     * min 1/2sum||A-X|| + lambda sum(rho||X-X||) + lambda_2 sum (u||X||) 
     * Lagrange = sum() + sum() 
     * X = min fx + sum (1/2||x-v+u||) 
     * V = min lambda *w ||v-v|| + rho/2(||x-z+u||+||x-z+u||) 
     * U = u + rho(x-z)
     *
     * @param _Matrix
     * @param _lambda: in range 1->2
     * @param _lambda2
     * @param _rho
     * @param _e1
     * @param _e2
     * @throws IOException
     */
    public SCCNew2(double[][] _Matrix, double _lambda, double _lambda2, double _rho, double _e1, double _e2) throws IOException, Exception {
        super(_Matrix, _lambda);
//        edges = updateEdge(); // for paper data
        lambda2 = _lambda2;
        rho = _rho;
        ea = _e1;
        er = _e2;

//        A = Matrix.centered(A);       
//        Matrix.printMat(A, "centered");
        //Init
        init();
//        Matrix.printMat(X, false,"SCC x0");

//        rho = rho2;
//            Matrix.printMat(X, "SCC x "+i);
        double[][] X0 = A;
        ListENode U = initU();
        ListENode V = initV();
        ListENode V0 = V;
        ListENode U0 = U;
        double B[][] = new double[numberOfVertices][numOfFeature];
        for(int i = 0; i< numberOfVertices; i++)
         B[i]= Vector.plus(A[i], Vector.scale(xAvr, numberOfVertices));
        FileWriter fw = null;
//            Vector.printV(X[i], "X "+i, stop);
        X=A;
        int loop = 0; 
        while (loop < 900) {

//                if(loop==4)
//                {
//                    double[] v = Matrix.getRow(X, i);
//                    Vector.printV(v, "v"+"-"+loop, stop);
//                }

            System.out.println("pt.paper.SCCNew.<loop>() " + loop +" lambda: "+lambda);
            for (int i = 0; i < numberOfVertices; i++) {             
                updateX(i, V, U, B[i]);
            }
            
            V = updateV(V, U); //
            U = updateU(U, V); //u-x+v
//            Matrix.printMat(X, "SCC x "+loop);
//            if(loop>20)  logData(loop, fw, U, V);
            
            if (checkStop(X0, U0, V0, V) && (loop > 1))// || (stop == true)))
            {
                System.out.println(" SCC 2 STOP at " + loop);
                break;
            }
            X0 = X;            
            V0 = V;
            U0 = U;
            lambda = lambda*1.005;
            loop++;
        }
//            Matrix.printMat(X, "SCC x "+i);
        for (int i = 0; i < numberOfVertices; i++) {
            Vector.formV(X[i], "0.000000000");
//            Vector.printV(X[i], "X[i] " + i, true);
        }
        logData(loop, fw, U, V);
//        CSVFile.saveMatrixData("SCC", X, "SCC");
        cluster = new ArrayList<>();

        fw = new FileWriter("tmp/SCC_Cluster.txt");
        getCluster(fw);
        fw.close();
        presentMat = new double[cluster.size()][A[0].length];
        getPresentMat();
    }

    void init() {
        X = new double[numberOfVertices][numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            double x = 1- ((lambda2)/Vector.norm(Matrix.getCol(A, i)));
//            x = (x>0)? x:0;
//            //update by column i
//            for(int j = 0 ; j< numberOfVertices; j++)
//            {
//                X[j][i] = x*A[j][i];
//            }
//        }

        u = new double[numOfFeature];
        xAvr = new double[numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
            u[i] = Vector.norm(Matrix.getCol(X, i));
            xAvr[i] = Vector.avr(Matrix.getCol(A, i));
        }
        u = Vector.scale(u, lambda2);
//        for (int i = 0; i < numOfFeature; i++) {
//            double x = 1- (u[i]/Vector.norm(Matrix.getCol(A, i))); //u[i]
//            x = (x>0)? x:0;
//            for (int j = 0; j < numberOfVertices; j++) {
//                X[j][i] =  xAvr[i];
//                X[j][i] =  x*A[j][i]; 
//                X[j][i] =  xAvr[i]*(1+A[j][i]);
//                X[j][i] = xAvr[i] * A[j][i];
//                X[j][i] = 1+ A[j][i];
//            }
//        }
//        for(int j = 0 ; j< numberOfVertices; j++)
//            Vector.printV(X[j],"init X "+j,true);
    }

    private void logData(int loop, FileWriter fw, ListENode U, ListENode V) throws IOException
    {
        fw = new FileWriter("tmp/"+loop+"_X_data.txt");

        for (int i = 0; i < numberOfVertices; i++) {
            for (int j = 0; j < numOfFeature; j++) {
                fw.append(X[i][j] + "\t");
            }
            fw.append("\n");
        }
        fw.close();

        fw = new FileWriter("tmp/"+loop+"_z_data.txt");
        for(Key k: V.E.keySet())
        {
            fw.append("\n" + k.src+"-"+k.dst+"\t");
            double [] tmp = V.get(k);
            for (int j = 0; j < numOfFeature; j++) {
                fw.append(tmp[j] + "\t");
            }
            fw.append("\n");
        }
        fw.close();

//        fw = new FileWriter("tmp/"+loop+"_u_data.txt");
//        for(Key k: U.E.keySet())
//        {
//            fw.append("\n" + k.src+"-"+k.dst+"\t");
//            double [] tmp = U.get(k);
//            for (int j = 0; j < numOfFeature; j++) {
//                fw.append(tmp[j] + "\t");
//            }
//            fw.append("\n");
//        }
        fw.close();
    }
    @Override
    public final void getCluster(FileWriter fw) {
//        int  ret[] = new int[numberOfVertices];
        HashMap<Integer, Integer> C = new HashMap<>();
//        List<Integer>  intdex = new ArrayList<Integer>();
        for (int i = 0; i < numberOfVertices; i++) {
            if (!C.containsKey(i)) {
                C.put(i, i);
                for (int j = i + 1; j < numberOfVertices; j++) {
                    if (!C.containsKey(j)) {
                        if ((Vector.isSameVec(Matrix.getRow(X, i), Matrix.getRow(X, j)))) {
                            C.put(j, i);
                        }
                    }
                }
            }
        }
//    	count--;
//    	System.out.println("Num of sub mat: "+intdex.size());

        for (int i = 0; i < numberOfVertices; i++) {
            List<Integer> sub = new LinkedList<>();
            for (int j = i; j < numberOfVertices; j++) {
                if (C.get(j) == i) {
                    sub.add(j);
                }
            }
            if (!sub.isEmpty()) {
                cluster.add(sub);
            }
        }
//    	cluster.add(intdex);
//Test    
        System.out.println("paper.SCC.getCluster() " + cluster.size());
        for (int i = 0; i < cluster.size(); i++) {
            List<Integer> sub = cluster.get(i);
            System.out.print("Cluster " + i + ":\t");
            for (int j = 0; j < sub.size(); j++) {
                System.out.print(sub.get(j) + "\t");
            }
            System.out.println("");
        }

    }


    /**
     * TODO: should update with optimize problem: 
     * min ||X-A|| + sum rho/2 ||x-z+u||
     * = (1/(1+n))(x + n*xAvr + (sum(j>i)(ui-zi)-sum(i>j)(uj-zj)))
     * @param i
     * @param D
     * @param n
     * @param xAvr
     * @return
     * -> follow Eric Chi paper
     */
    private void updateX(int i, ListENode V, ListENode U, double B[]) {
        double[] sumdi = new double[numOfFeature];
        double[] sumdj = new double[numOfFeature];
               
        // (sum(j>i)(ui-zi)-sum(i>j)(uj-zj))
        //TODO: review i>j and i>j???
        for(Key k: V.E.keySet())
        {
            if(i == k.src)
            {
                sumdi = Vector.plus(sumdi, Vector.plus(U.get(k), V.get(k)));
            }
            if(i == k.dst)
            {
                sumdj = Vector.plus(sumdj, Vector.plus(U.get(k), V.get(k)));
            }
        }
        double[] sumd = Vector.sub(sumdi, sumdj);
//        if(i==1)    Vector.printV(sumd,"X "+i,true);        
        //Vector.plus(A[i], Vector.scale(xAvr, numberOfVertices));
        X[i] = Vector.scale(Vector.plus(B,sumd), 1./(1+numberOfVertices));
    }
        
    private ListENode initU() {
         ListENode ret = new ListENode();
        
        for (Edge e : edges) {
                ret.put(e.scr, e.dst, new double[numOfFeature]);
                ret.put(e.dst, e.scr, new double[numOfFeature]);
        }
        return ret;
    }
    
    /**
     * Ue = Ue + (Ai - Ve)
     * U+ (v-  (X-X))
     * i = srs ? dst
     * @param U
     * @param V
     * @return
     * @throws Exception 
     */

    private ListENode updateU(ListENode U, ListENode V) throws Exception //C
    {
        ListENode ret = new ListENode();
        V.E.keySet().stream().forEach((v) -> {
//            if(v.scr == 50)  System.out.println("V D "+v.scr+" "+v.dst);
            double[] data = Vector.sub(V.get(v), Vector.sub(X[v.src], X[v.dst]));
            data = Vector.plus(U.get(v), data);
//            if(v.src == 5 && v.dst ==90)                Vector.printV(data, "U in "+v.src+" "+v.dst, true);

            ret.put(v.src, v.dst, data);//, Edge.getEdgeW(edges, v.src, v.dst)));
        });
        return ret;
    }
    
    private ListENode initV() {
        ListENode ret = new ListENode();
        for (Edge e : edges) {
            double[] src = Matrix.getRow(A, e.scr);//new double[dataLength];
            double[] dst = Matrix.getRow(A, e.dst);//new double[dataLength];
            ret.put(e.scr, e.dst, src);
            ret.put(e.dst, e.scr, dst);
        }
        return ret;
    }
    /*
    lamda - e.weigh
     */
    /**
     * TODO: review code, should update with optimize problem: Min
     * (lambda*w||v1-v2|| + rho/2(||x1-v1+u1||+||x2-v2+u2||))
     *
     * v1 = L() + (1-L)()
     * v2 = (1-L)() +L()
     * @param V
     * @param U
     * @return
     * 
     * proximal_2 (xi-xj- ui, lambda*w) -> follow Eric Chi paper
     */
    
    private ListENode updateV(ListENode V, ListENode U) throws Exception //B
    {
//        System.out.println("paper.AMA.updateV()");
        ListENode ret = new ListENode();

        V.E.keySet().stream().forEach((v) -> {
            double[] bbu = Vector.sub(Vector.sub(X[v.src], X[v.dst]), U.get(v));
            double w = Edge.getEdgeW(edges, v.src, v.dst);
            bbu = Vector.proxN2_2(bbu, lambda*w);
            ret.put(v.src, v.dst, bbu);
        });//
        return ret;
    }

    private double updateRho(double r, double s) {
        if (r > 10 * s) {
            rho = Double.valueOf(twoDForm.format(rho * 0.5));//(r/s);//2*rho;
        }
        if (s > 10 * r) {
            rho = Double.valueOf(twoDForm.format(rho * 2));//(r/s);//rho/2;
        }
        return rho;
    }

    /**
     * TODO 
     * 
     * From snapx: 
     * # r = Ax - z 
     * # s = rho * (A^T)(z - z_old) 
     * # e_pri = sqrt(p) * e_abs + e_rel * max(||Ax||, ||z||) 
     * # e_dual = sqrt(n) * e_abs + e_rel * ||rho * (A^T)u|| 
     * # Should stop if (||r|| <= e_pri) and (||s|| <= e_dual)
     * 
     * aram X0
     * @param U
     * @param V0
     * @param V
     * @return
     */
        private boolean checkStop(double[][] X0, ListENode U, ListENode V0, ListENode V) throws Exception {
        double r = primalResidual(X0, V0);
        double s = dualResidual(V0, V);
//        System.err.println("rho "+rho);

        double maxAB[] = new double[V.E.size()];
        int i = 0;
        for (Key b : V.E.keySet()) {
            double be = Vector.norm(V.get(b));
            double a = Vector.norm(A[b.src]);
            maxAB[i] = (a > be) ? a : be;
            i++;
//          maxAB   = (ab > maxAB) ? ab : maxAB;
        }

        double maxC[] = new double[U.E.size()];
        i=0;
        for (Key c : U.E.keySet()) {
            maxC[i] = Vector.norm(U.get(c));
            i++;
//            maxC = (value > maxC) ? value : maxC;
        }
//        double nz = Vector.norm(maxAB);
        double ed = ea + er * Vector.norm(maxC);//Cik?
        double ep = ea * Math.sqrt(numberOfVertices) + er * Vector.norm(maxAB); //Bik?

        updateRho(r, s);
        double nz[] = new double[V.E.size()];
        i= 0;
        for (Key b : V.E.keySet()) {
            nz[i] = Vector.norm(V.get(b));
        }
        
//        if (rho == 0) {
//            System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed+" ==== "+count);
//            return true;
//        }
//        double noZ = Vector.norm(nz);
        
//        System.out.println("pt.paper.SCCNew2.checkStop() "+r+" - "+s +" - "+noZ);
//        DecimalFormat twoDForml = new DecimalFormat("0.00000000");
//        noZ = (Double.isNaN(noZ))?0:Double.valueOf(twoDForml.format(noZ));
//        if(noZ == 0.)
//            return true;
        
        return (r <= ep) && (s <= ed);
    }
        
   
    private double primalResidual(double[][] X0, ListENode V0) {
//        double ret = 0;
        double []x = new double[V0.E.size()];
        int i =0;
        for (Key k : V0.E.keySet()) {
            x[i] = Vector.norm(Vector.sub(Matrix.getRow(X0, k.src), V0.get(k)));
            i++;
        }
        
        return Vector.norm(x);
    }

    
    private double dualResidual(ListENode Vp, ListENode V) throws Exception {
//        double ret = 0;
        double []x = new double[V.E.size()];
        int i =0;
        for (Key k : V.E.keySet()) {
            double[] bikp = Vp.get(k);// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = Vector.scale(Vector.sub(bikp, V.get(k)), rho);
            x[i] = Vector.norm(ai);
            i++;    
        }
        return Vector.norm(x);
    }
}

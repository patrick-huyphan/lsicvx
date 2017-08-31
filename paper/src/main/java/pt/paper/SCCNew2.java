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
    double rho2;
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
        rho = rho2 = _rho;
        ea = _e1;
        er = _e2;

//        A = Matrix.centered(A);       
//        Matrix.printMat(A, "centered");
        //Init
        init();
//        Matrix.printMat(X, false,"SCC x0");

//        rho = rho2;
//            Matrix.printMat(X, "SCC x "+i);
        double[][] X0;
        ListENode V0;
        ListENode U0;
        ListENode U = initU();
        ListENode V = initV();
        FileWriter fw = null;
//            Vector.printV(X[i], "X "+i, stop);
        int loop = 0; 
        while (loop < 40) {
            X0 = X;            
            V0 = V;
            U0 = U;
//                if(loop==4)
//                {
//                    double[] v = Matrix.getRow(X, i);
//                    Vector.printV(v, "v"+"-"+loop, stop);
//                }

            System.out.println("pt.paper.SCCNew.<loop>() " + loop);
            for (int i = 0; i < numberOfVertices; i++) {             
//                ListENode D = calcD(i, V, U); 
                updateX(i, V, U);
            }
            
            V = updateV(V, U); //
            U = updateU(U, V); //u-x+v
//            Matrix.printMat(X, "SCC x "+loop);

            if(loop>9)  logData(loop, fw, U, V);
            
            if (checkStop(X0, U0, V0, V) && (loop > 1))// || (stop == true)))
            {
                System.out.println(" SCC 2 STOP at " + loop);
                break;
            }
            loop++;
        }
//            Matrix.printMat(X, "SCC x "+i);
//            DecimalFormat twoDForml = new DecimalFormat("0.00000000");
//            for(int r = 0; r < numOfFeature; r++)
//            {
//                X[i][r] = (Double.isNaN(X[i][r]))?0:Double.valueOf(twoDForml.format(X[i][r]));
//            }
//            Vector.printV(X[i], "X "+i, stop);
        for (int i = 0; i < numberOfVertices; i++) {
            Vector.formV(X[i], "0.00000000");
//            Vector.printV(X[i], "X[i] " + i, true);
        }


        
//        getCluster(fw);
//        Matrix.printMat(X, "SCC x");
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
        for (int i = 0; i < numOfFeature; i++) {
//            double x = 1- (u[i]/Vector.norm(Matrix.getCol(A, i))); //u[i]
//            x = (x>0)? x:0;
            for (int j = 0; j < numberOfVertices; j++) {
//                X[j][i] =  xAvr[i];
//                X[j][i] =  x*A[j][i]; 
//                X[j][i] =  xAvr[i]*(1+A[j][i]);
//                X[j][i] = xAvr[i] * A[j][i];
//                X[j][i] = 1+ A[j][i];
            }
        }
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

        fw = new FileWriter("tmp/"+loop+"_u_data.txt");
        for(Key k: U.E.keySet())
        {
            fw.append("\n" + k.src+"-"+k.dst+"\t");
            double [] tmp = U.get(k);
            for (int j = 0; j < numOfFeature; j++) {
                fw.append(tmp[j] + "\t");
            }
            fw.append("\n");
        }
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

//    private int[] retSize() {
//        int[] ret = new int[numberOfVertices];
//        for (Edge e : edges) {
//            ret[e.scr] = ret[e.scr] + 1;
//            ret[e.dst] = ret[e.dst] + 1;
//        }
//        return ret;
//    }

    /**
     * TODO: should update with optimize problem: 
     * min ||X-A|| + sum rho/2 ||x-z+u||
     * = (1/(1+n))(x + n*xAvr + (sum(j>i)(ui-zi)-sum(i>j)(uj-zj)))
     * @param i
     * @param D
     * @param n
     * @param xAvr
     * @return
     */
    private void updateX(int i, ListENode V, ListENode U) {
        
        double[] sumdi = new double[numOfFeature];
        double[] sumdj = new double[numOfFeature];
        double[] sumd;
        double[] uj;
        double[] vj;
        double[] ui;
        double[] vi;
        
//        for (Key k : D.E.keySet()) {
//            sumd = Vector.scale(Vector.plus(sumd, D.get(k)), rho/2);
//        }
////        if(i==1)    Vector.printV(sumd," X "+i,true);
////        sumd = Vector.scale(sumd, 1. / (ni[i]));
//        X[i] = Vector.plus(xAvr, sumd);

        // (sum(j>i)(ui-zi)-sum(i>j)(uj-zj))
        //TODO: review i>j and i>j???
        for(Key k: V.E.keySet())
        {
            if(i == k.dst)
            {
                ui = U.get(k);
                vi = V.get(k);
                sumdi = Vector.plus(sumdi, Vector.sub(ui, vi));
            }
            if(i == k.src)
            {
                uj = U.get(k);
                vj = V.get(k);
                sumdj = Vector.plus(sumdj, Vector.sub(uj, vj));
            }
        }
        sumd = Vector.sub(sumdi, sumdj);
        
        X[i] = Vector.scale(Vector.plus(Vector.plus(X[i], Vector.scale(xAvr, numberOfVertices)),sumd), 1./(1+numberOfVertices));
//        if(i==1)    Vector.printV(X[i],"X "+i,true);
    }
    /*
     * De = A - Be + Ce
     */
    
//    private ListENode calcD(int i, ListENode V, ListENode U) throws Exception //(B-C)
//    {
//        ListENode D = new ListENode();
//
//        for (Key k : V.E.keySet()) {
//            if (k.src == i) {
////                if(i == 50) System.out.println("V D "+k.src+" "+k.dst);
//                double[] d = Vector.sub(Matrix.getRow(X, i), V.get(k));
//                d = Vector.plus(d, U.get(k));
//                D.put(k.src, k.dst, d);
//            }
//        }
//        return D;
//    }
        
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
     * i = srs ? dst
     * @param U
     * @param V
     * @return
     * @throws Exception 
     */

    private ListENode updateU(ListENode U, ListENode V) throws Exception //C
    {
        ListENode ret = new ListENode();
//        System.out.println("pt.paper.SCCNew.updateU()");
        for (Key v : V.E.keySet()) {
//            if(v.scr == 50)  System.out.println("V D "+v.scr+" "+v.dst);
//            EdgeNode u = EdgeNode.getEdgeNodeData(U, v.src, v.dst);// V.get(U.indexOf(e));
            //Ai - Be
            double[] data = Vector.sub(Matrix.getRow(X, v.src), V.get(v));
            //data = Vector.scale(data, rho);
            data = Vector.plus(U.get(v), data);
//            Vector.printV(data, "updateU:"+v.src+"-"+v.dst, true);
//            if(v.src == 5 && v.dst ==90)                Vector.printV(data, "U in "+v.src+" "+v.dst, true);

            ret.put(v.src, v.dst, data);
        }
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
     * proximal_2 (xi-xj- ui, lambda*w)
     */
    
    private ListENode updateV(ListENode V, ListENode U) throws Exception //B
    {
//        System.out.println("paper.AMA.updateV()");
        ListENode ret = new ListENode();

        V.E.keySet().stream().forEach((v) -> {
            double[] tmp = Vector.sub(Vector.sub(X[v.src], X[v.dst]), U.get(v));
            double w = Edge.getEdgeW(edges, v.src, v.dst);
            ret.put(v.src, v.dst, Vector.proxN2_2(tmp, lambda*w));
        });//
//        for (Key v : V.E.keySet()) {
//            double[] Ck = new double[numOfFeature];//uki
//
//            int count = 0;
//            for (Edge e : edges) {
//                if (v.src == e.scr) {
////                        if(i==50 ) System.out.println(i+ " U s "+u.scr+" "+u.dst);
//                    Ck = Vector.plus(Ck, Matrix.getRow(A, e.dst));
//                    count++;
//                }
//                if (v.src == e.dst) {
////                        if(i==50 ) System.out.println(i+ " U d "+u.scr+" "+u.dst);
//                    Ck = Vector.plus(Ck, Matrix.getRow(A, e.scr));
//                    count++;
//                }
//            }
//
//            double[] AkCk = Vector.scale(Ck, 1. / count);//new double[numOfFeature];
//
//
////                System.out.println("paper.SCC.updateV() "+ i+ ": "+e.scr+"-"+e.dst);
////                double[] Ai = Matrix.getRow(A,i); //n
////                double[] Ak =Matrix.getRow(A, (i==e.scr)?e.dst:e.scr);
//            double[] Vi = V.get(v);
//            double[] Ui = U.get(v);//C.relatedValue;
//            double[] AiCi = Vector.plus(Matrix.getRow(A, v.src), Vector.plus(Vi, Ui)); // u get ik As+Us
//
//            double weight = Edge.getEdgeW(edges, v.src, v.dst);
//            double a = (1 - weight) + lambda;// e.weight/2;
//            double b = weight / lambda;
//            double[] v_ik = Vector.plus(Vector.scale(AiCi, a), Vector.scale(AkCk, b));
////                if(i==90 ) System.out.println("V in "+v.scr+" "+v.dst);
////            if(v.src == 5 && v.dst ==90) Vector.printV(v_ik, "V in "+v.src+" "+v.dst, true);
//            ret.put(v.src, v.dst, Vector.scale(v_ik, weight));
//        }

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
        double ed = ea + er * Vector.norm(maxC);//Cik?
        double ep = ea * Math.sqrt(numberOfVertices) + er * Vector.norm(maxAB); //Bik?

        updateRho(r, s);
        if (rho == 0) {
            //System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed+" ==== "+count);
            return true;
        }
        
        System.out.println("pt.paper.SCCNew2.checkStop() "+r+" - "+s);
        return (r <= ep) && (s <= ed);
    }
        
   
    private double primalResidual(double[][] X0, ListENode V0) {
//        double ret = 0;
        double []x = new double[V0.E.size()];
        int i =0;
        for (Key k : V0.E.keySet()) {
//            double normR 
            x[i] = Vector.norm(Vector.plus(Matrix.getRow(X0, k.src), V0.get(k)));
//            ret = (ret > normR) ? ret : normR;
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
//            double normS = Vector.norm(ai);
//            ret = (ret > normS) ? ret : normS;
            x[i] = Vector.norm(ai);
            i++;    
        }
        return Vector.norm(x);
    }
}

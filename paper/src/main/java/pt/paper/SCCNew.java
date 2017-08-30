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

public class SCCNew extends Clustering {

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
    public SCCNew(double[][] _Matrix, double _lambda, double _lambda2, double _rho, double _e1, double _e2) throws IOException, Exception {
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

        rho = rho2;        
//            Matrix.printMat(X, "SCC x "+i);
        double[][] X0;
        
        List<EdgeNode> V0;
        List<EdgeNode> U0;
        List<EdgeNode> U = initU();
        List<EdgeNode> V = initV();        

//            Vector.printV(X[i], "X "+i, stop);
        int loop = 0;
//            if(i==1)
//            Vector.printV(X[i],"X "+i,true);
        while (loop < MAX_LOOP) {
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
                List<EdgeNode> D = calcD(i, V, U); //x-v+u
                updateX(i, D); //xAvr +sumD 
            }
            V = updateV(V, U); //
            U = updateU(U, V); //u-x+v
            
//            Matrix.printMat(X, "SCC x "+loop);
            
            if (checkStop(X0, U0, V0, V) && (loop > 1))// || (stop == true)))
            {
                System.out.println(" SCC STOP at " + loop);
                break;
            }
            loop++;
        }

        for (int i = 0; i < numberOfVertices; i++) {
            Vector.formV(X[i], "0.00000000");
//            Vector.printV(X[i], "X[i] " + i, true);
        }

        FileWriter fw = new FileWriter("X_data.txt");

        for (int i = 0; i < numberOfVertices; i++) {
            for (int j = 0; j < numOfFeature; j++) {
                fw.append(X[i][j] + "\t");
            }
            fw.append("\n");
        }

        cluster = new ArrayList<>();

        fw = new FileWriter("SCC_Cluster.txt");
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
     *
     * @param i
     * @param D
     * @param n
     * @param xAvr
     * @return
     */
    private void updateX(int i, List<EdgeNode> D) {
        double[] sumd = new double[numOfFeature];

        for (EdgeNode d : D) {
            sumd = Vector.scale(Vector.plus(sumd, d.relatedValue),rho/2);
        }
        sumd = Vector.scale(sumd, 1. / (ni[i]));
//        if(i==1)    Vector.printV(sumd," X "+i,true);
        X[i] = Vector.plus(xAvr, sumd);
//        if(i==1)    Vector.printV(X[i],"X "+i,true);
    }

    /*
     * De = A - Be + Ce
     */
    private List<EdgeNode> calcD(int i, List<EdgeNode> V, List<EdgeNode> U) throws Exception //(B-C)
    {
        List<EdgeNode> D = new LinkedList<>();

        for (EdgeNode v : V) {
            if (v.scr == i) {
//                if(i == 93) System.out.println("V D "+v.scr+" "+v.dst);
                double[] d = Vector.sub(Matrix.getRow(X, i), v.relatedValue);
                EdgeNode u = EdgeNode.getEdgeNodeData(U, v.scr, v.dst);// U.get(k);
                d = Vector.plus(d, u.relatedValue);
                D.add(new EdgeNode(v.scr, v.dst, d));
            }
        }
        return D;
    }

        
    private List<EdgeNode> initU() {
        List<EdgeNode> ret = new LinkedList<>();
      
        for (Edge e : edges) {
                ret.add(new EdgeNode(e.scr, e.dst, new double[numOfFeature]));
                ret.add(new EdgeNode(e.dst, e.scr, new double[numOfFeature]));
        }

//        System.out.println("pt.paper.SCCNew.initU() "+tmp.size());
//        for(EdgeNode e: ret)
//            System.out.println("paper.SCC.initU() "+e.scr+ " "+e.dst );
//        System.out.println("paper.SCC.initU() "+ret.size());
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
    private List<EdgeNode> updateU(List<EdgeNode> U, List<EdgeNode> V) throws Exception //C
    {
        List<EdgeNode> ret = new LinkedList<>();
//        System.out.println("pt.paper.SCCNew.updateU()");
        for (EdgeNode v : V) {
//            if(v.scr == 50)  System.out.println("V D "+v.scr+" "+v.dst);
            EdgeNode u = EdgeNode.getEdgeNodeData(U, v.scr, v.dst);// V.get(U.indexOf(e));
            //Ai - Be
            double[] data = Vector.sub(Matrix.getRow(X, u.scr), v.relatedValue);
            //data = Vector.scale(data, rho);
            data = Vector.plus(u.relatedValue, data);
//            Vector.printV(updateU.relatedValue, "updateU:"+e.scr+""+e.dst, true);
            ret.add(new EdgeNode(u.scr, u.dst, data));
        }
        return ret;
    }


    private List<EdgeNode> initV() {
        List<EdgeNode> ret = new LinkedList<>();
        for (Edge e : edges) {
            //ik
            double[] value1 = Matrix.getRow(X, e.scr);//new double[dataLength];
            ret.add(new EdgeNode(e.scr, e.dst, value1));
            ret.add(new EdgeNode(e.dst, e.scr, value1));
        }
//        for(EdgeNode e: ret)
//            System.out.println("paper.SCC.initV() "+e.scr+ " "+e.dst );
//        System.out.println("paper.SCC.initV() " +ret.size());
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
     */
    private List<EdgeNode> updateV(List<EdgeNode> V, List<EdgeNode> U) throws Exception //B
    {
//        System.out.println("paper.AMA.updateV()");
        List<EdgeNode> ret = new LinkedList<>();

        for (EdgeNode v : V) {
            double[] Ck = new double[numOfFeature];//uki
//                int get = 0;
//                int i=0 ;

            EdgeNode C = EdgeNode.getEdgeNodeData(U, v.scr, v.dst);
//            EdgeNode B = EdgeNode.getEdgeNodeData(V, v.scr, v.dst);
            double[] Vi = v.relatedValue;
            double[] Ui = C.relatedValue;
            int count = 0;
            for (Edge e : edges) {
//                    if(C.scr == i && ((C.dst==u.scr && u.dst != i) || (C.dst== u.dst && u.scr != i)))
                if (v.scr == e.scr) {
//                        if(i==50 ) System.out.println(i+ " U s "+u.scr+" "+u.dst);
//                        Ck= Vector.plus( Ck,  Matrix.getRow(A, (C.scr == i)?u.dst:u.scr));
                    Ck = Vector.plus(Ck, Matrix.getRow(A, e.dst));
                    count++;
                }
//                    if(C.dst == i && ((C.scr==u.scr && u.dst != i) || (C.scr== u.dst && u.scr != i)))
                if (v.scr == e.dst) {
//                        if(i==50 ) System.out.println(i+ " U d "+u.scr+" "+u.dst);
//                        Ck= Vector.plus( Ck,  Matrix.getRow(A, (C.dst == i)?u.scr:u.dst));
                    Ck = Vector.plus(Ck, Matrix.getRow(A, e.scr));
                    count++;
                }
            }

            double[] AkCk = Vector.scale(Ck, 1./ count);//new double[numOfFeature];
            if (count ==0) {
                System.out.println(" pt.paper.SCC.updateV() "+ v.scr +" "+v.dst);
            }

//                System.out.println("paper.SCC.updateV() "+ i+ ": "+e.scr+"-"+e.dst);
//                double[] Ai = Matrix.getRow(A,i); //n
//                double[] Ak =Matrix.getRow(A, (i==e.scr)?e.dst:e.scr);
            double[] AiCi = Vector.plus(Matrix.getRow(A, C.scr), Vector.plus(Vi, Ui)); // u get ik As+Us
//                double[] AkCk = Vector.plus(Ak,Ck); // u get ki Ad+Ud

//                double n = 1- ((lambda*e.weight)/(Vector.norm(Vector.plus(Vector.sub(Bi, Bk),Vector.sub(Ai, Ak)))/rho));
//                System.out.println("paper.SCC.updateV() "+ (1-n));
//                double theta = (0.5>n)? 0.5:n; //max

            double weight = Edge.getEdgeW(edges,v.scr, v.dst);
            double a = (1 - weight);// e.weight/2;
            double b = weight;
            double[] v_ik = Vector.plus(Vector.scale(AiCi, a), Vector.scale(AkCk, b));
//                if(i==90 ) System.out.println("V in "+e.scr+" "+e.dst);
//                double[] v_ki = Vector.plus(Vector.scale(AiCi, b), Vector.scale(AkCk, a));
//            ret.add(new EdgeNode(v.scr, v.dst, v_ik));
            ret.add(new EdgeNode(v.scr, v.dst, Vector.scale(v_ik, lambda)));
        }

        return ret;
    }

    private double updateRho(double r, double s) {
        if (r > 10 * s) {
            rho = Double.valueOf(twoDForm.format(rho * 0.745));//(r/s);//2*rho;
        }
        if (s > 10 * r) {
            rho = Double.valueOf(twoDForm.format(rho * 3));//(r/s);//rho/2;
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
    private boolean checkStop(double[][] X0, List<EdgeNode> U, List<EdgeNode> V0, List<EdgeNode> V) throws Exception {
        double r = primalResidual(X0, V0);
        double s = dualResidual(V0, V);
//        System.err.println("rho "+rho);

        double maxAB = 0;
        for (EdgeNode b : V) {
            double be = Vector.norm(b.relatedValue);
            double a = Vector.norm(A[b.scr]);
            double ab = (a > be) ? a : be;
            maxAB = (ab > maxAB) ? ab : maxAB;
        }

        double maxC = 0;
        for (EdgeNode c : U) {
            double value = Vector.norm(c.relatedValue);
            maxC = (value > maxC) ? value : maxC;
        }
        double ed = ea + er * maxC;//Cik?
        double ep = ea * Math.sqrt(numberOfVertices) + er * maxAB; //Bik?

        updateRho(r, s);
        if (rho == 0) {
            //System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed+" ==== "+count);
            return true;
        }
        return (r <= ep) && (s <= ed);
    }


        
    private double primalResidual(double[][] X0, List<EdgeNode> V0) {
//        double ret = 0;
        double []x = new double[V0.size()];
        int i =0;
        for (EdgeNode n : V0) {
//            double normR 
            x[i] = Vector.norm(Vector.plus(Matrix.getRow(X0, n.scr), n.relatedValue));
            i++;
        }
        
        return Vector.norm(x);
    }


    private double dualResidual(List<EdgeNode> Vp, List<EdgeNode> V) throws Exception {
//        double ret = 0;
        double []x = new double[V.size()];
        int i =0;
        for (EdgeNode n : V) {
            double[] bikp = EdgeNode.getEdgeNodeData(Vp, n.scr, n.dst).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = Vector.scale(Vector.sub(bikp, n.relatedValue), rho);
            x[i] = Vector.norm(ai);;
            i++;    
        }
        return Vector.norm(x);
    }
    
}

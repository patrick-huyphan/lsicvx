/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.JOptimizer;
import com.joptimizer.optimizers.OptimizationRequest;
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

public class SCC_cvx extends Clustering {

    double rho;
//    double rho2;
    double lambda2;
    double ea, er;
    public double[][] X;
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    boolean stop = false;

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
     * Solve 2 step
     * - min ||x-a|| +lamba sum ||x|| => X1
     * - min 1/2sum||A-X|| + lambda sum(rho||X-X||) -> A= X1
     * 
     * @param _Matrix
     * @param _lambda: in range 1->2
     * @param _lambda2
     * @param _rho
     * @param _e1
     * @param _e2
     * @throws IOException
     */
    public SCC_cvx(double[][] _Matrix, double _lambda, double _lambda2, double _rho, double _e1, double _e2) throws IOException, Exception {
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
        double[][] X0;
        List<EdgeNode> V0;
        List<EdgeNode> U0;
        List<EdgeNode> U = initU();
        List<EdgeNode> V = initV();

        int loop = 0;
        while (loop < MAX_LOOP) {
            X0 = X;
            V0 = V;
            U0 = U;
//                if(loop==4)
//                {
//                    double[] v = Matrix.getRow(X, i);
//                    Vector.printV(v, "v"+i+"-"+loop, stop);
//                }

            System.out.println("pt.paper.SCCNew.<loop>() " + loop);
            
            updateX(V, U); //xAvr +sumD                               
            V = updateV(V, U); //
            U = updateU(U, V); //u-x+v
//                Matrix.printMat(X, "SCC x "+loop +" "+i);
            if (checkStop(X0, U0, V0, V) && (loop > 1))// || (stop == true)))
            {
                System.out.println(" SCC STOP at " + loop);
                break;
            }
            loop++;
        }

//            Vector.printV(X[i], "X "+i, stop);
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
        cluster = new LinkedList<>();

        fw = new FileWriter("SCC_Cluster.txt");
        getCluster(fw);
        fw.close();
        presentMat = new double[cluster.size()][A[0].length];
        getPresentMat();
    }

    // call ADMM to solve X1???
    
    void init() {
        int uAdmm = 0;
        if(uAdmm==1)
        {
            ADMM X1= new ADMM(A, A, rho, lambda, ea, ea);
            X= X1.X;
        }
        else
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

    /**
     * TODO: should update with optimize problem: 
     * min 1/2||xi - a|| + sum rho/2 ||xi-zij+uij||
     * j is neighbours node of i 
     * gi   = 1/2||xi-a|| 
     *      = 1/2 (xi-a)^2
     *      = 1/2 (xxT - 2Atx +AtA)
     * hi   = sum rho/2 ||xi-(zij-uij)||  
     *      = sum rho/2 ||xi- zuij||
     *      = sum rho/2 ()
     * 
     * P = ATA
     * Q = ??
     * convert input to P  matrix, and q vector
     * solve QP
     * 
     * @param i
     * @param D
     * @param n
     * @param xAvr
     * @return
     */
    private double[][] updateX(List<EdgeNode> V, List<EdgeNode> U) throws JOptimizerException, Exception {
        double ret[] = new double[numOfFeature];
                
        for (int i = 0; i < numberOfVertices; i++) {
            double zu[][] = new double[ni[i]][numOfFeature];
            double x[] = Matrix.getCol(X, i);

            //h = sum rho/2 ||x-v+u||
            int cNeighbour = 0;
            for(Edge e:edges)
            {
                if(i== e.scr || i== e.dst)
                {
                    double ui[] = EdgeNode.getEdgeNodeData(U, e.scr, e.dst).relatedValue;
                    double vi[] = EdgeNode.getEdgeNodeData(V, e.scr, e.dst).relatedValue;
                    zu[cNeighbour] = Vector.sub(vi, ui);
                    cNeighbour++;
                }
            }
            
            /**
             * min g + h
             * g = 1/2 ||x-a|| -> minimum dist bw point a and x
             * h = sum rho/2 ||x-h|| -> minimum dist bw x and dummy neighbour point
             */
            double[][] P = new double[][]{{1., 0.4}, {0.4, 1.}}; //invertable matrix m*m
            double[][] A = new double[][]{{1, 1}}; 
            double[] b = new double[]{1};
            double[][] G = new double[][]{{-1, 0},{0, -1}}; 
            double[] c = new double[]{0,0};
            double[] initPoint = new double[]{0.1, 0.9};
            
            solveQP(P, c, A, b, G, c, initPoint);
            
            X[i] = ret;
        }
        return X;
    }

    /**
     * x= argmin gx+hx
     * g = 0/5 ||x-a||
     * h = sum rho/2||x-z+u|| -> book 8.7???
     * 
     * @param i
     * @param V
     * @param U
     * @return
     * @throws JOptimizerException
     * @throws Exception 
     */
    private double[] solveQP(double[][] P, double[] q, double[][] A, double[] b , double[][] G, double[] h, double[] initPoint) throws JOptimizerException, Exception
    {        
//        double[][] P = new double[][]{{1., 0.4}, {0.4, 1.}}; //invertable matrix m*m
        // min X^T P X + qT x + b
        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, q, 0);

        //equalities contrain Ax=b: x+y = 1 
//        double[][] A = new double[][]{{1, 1}}; 
//        double[] b = new double[]{1};

        //inequalities contrains Gx<h: 
//        List<ConvexMultivariateRealFunction>
        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[h.length];
        for(int i = 0; i< h.length; i++)
            inequalities[i] = new LinearMultivariateRealFunction(G[i], h[i]); // -x < 0
    //      inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1}, h[1]); // -y < 0

        //optimization problem
        OptimizationRequest or = new OptimizationRequest();
        or.setF0(objectiveFunction);
        or.setInitialPoint(initPoint);
//        or.setFi(inequalities); //if you want x>0 and y>0
        or.setA(A);
        or.setB(b);
        or.setToleranceFeas(1.E-12);
        or.setTolerance(1.E-12);

        //optimization
        JOptimizer opt = new JOptimizer();
        opt.setOptimizationRequest(or);
        opt.optimize();

        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE() QP :" + s);
//        }
        return sol;
    }
    /**
     * 
     * @return 
     */
    private List<EdgeNode> initU() {
        List<EdgeNode> ret = new LinkedList<>();
        ListENode tmp = new ListENode();
        
        for (Edge e : edges) {
                ret.add(new EdgeNode(e.scr, e.dst, new double[numOfFeature]));
                ret.add(new EdgeNode(e.dst, e.scr, new double[numOfFeature]));
                tmp.put(e.scr, e.dst, new double[numOfFeature]);
        }

//        System.out.println("pt.paper.SCC_cvx.initU() " +tmp.size());
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

    /**
     * @return 
     */
    private List<EdgeNode> initV() {
        List<EdgeNode> ret = new LinkedList<>();
               
        for (Edge e : edges) {
            //ik
            double[] src = Matrix.getRow(X, e.scr);//new double[dataLength];
            double[] dst = Matrix.getRow(X, e.dst);
            ret.add(new EdgeNode(e.scr, e.dst, src));
            ret.add(new EdgeNode(e.dst, e.scr, dst));
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
     * zij = theta(xi+uij) + (1-theta)(xj-uji)
     * 
     * theta= max(.5, 1-((lambda w)/(rho ||(xi+uij)- (xj+uji)||)))
     * 
     * 
     * @param V
     * @param U
     * @return
     */
    private List<EdgeNode> updateV(List<EdgeNode> V, List<EdgeNode> U) throws Exception //B
    {
//        System.out.println("paper.AMA.updateV()");
        List<EdgeNode> ret = new LinkedList<>();
        
        for (Edge e : edges) {
            double theta = 0;
            
            double xi[] = Matrix.getRow(X, e.scr);
            double xj[] = Matrix.getRow(X, e.dst);
            double uij[] = EdgeNode.getEdgeNodeData(U, e.scr, e.dst).relatedValue;
            double uji[] = EdgeNode.getEdgeNodeData(U, e.dst, e.scr).relatedValue;
            
            double tmp = Vector.norm(Vector.sub(Vector.plus(xi, uij), Vector.plus(xj, uji)));
            double w = Edge.getEdgeW(edges, e.scr, e.dst);
            tmp = 1-((lambda*w)/(rho *tmp));
            theta = (tmp>0.5)?tmp:0.5;
            
            //the(x-u) + (1-the)(x-u)
            double vij[]= Vector.plus(Vector.scale(Vector.plus(xi, uij), theta), Vector.scale(Vector.plus(xj, uji), 1-theta));
            double vji[]= Vector.plus(Vector.scale(Vector.plus(xj, uji), theta), Vector.scale(Vector.plus(xi, uij), 1-theta));
            
            ret.add(new EdgeNode(e.scr, e.dst, vij));
            ret.add(new EdgeNode(e.dst, e.scr, vji));            
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
//            ret = (ret > normR) ? ret : normR;
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
//            double normS = Vector.norm(ai);
//            ret = (ret > normS) ? ret : normS;
            x[i] = Vector.norm(ai);;
            i++;    
        }
        return Vector.norm(x);
    }
}

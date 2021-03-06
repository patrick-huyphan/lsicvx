/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
//import static pt.paper.Clustering.MAX_LOOP;

/**
 *
 * @author patrick_huy
 */
public class NodeSCC2{  //extends Clustering{
    double rho;
    double rho2;
    double lambda;
//    double lambda2;
    double ea, er;
    public double [][]X; //operate
    double [][] A; //original mat
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    boolean stop = false;
    int numberOfVertices;
    int numOfFeature;
    
    static final int MAX_LOOP = 50;
    List<LocalEdge> edges;
    
    
    public NodeSCC2(int id, 
            double[][] _A, double [][] _X, 
            List<LocalEdge> _edges, 
            int ni, double[] xAvr, 
            double _lambda, 
//            double _lambda2, 
            double _rho, 
            double _e1, double _e2) throws IOException
    {
        lambda =_lambda;
        rho = rho2 = _rho;
        ea = _e1;
        er = _e2;
        A = _A;
        edges = _edges;
        numberOfVertices = _A.length;
        numOfFeature = _A[0].length;

        
//        int [] ni = retSize();
////        Matrix.printMat(A, "centered");
//        //Init
        X = _X;//new double[numOfFeature];
//        System.out.println("pt.spark.NodeSCC.<init>() rho:  "+rho);
//        LocalVector.printV(X, "X start "+id, true);
        {
//            rho = rho2;
            double[][] X0;
            
            List<LocalEdgeNode> V0;
            List<LocalEdgeNode> U0;
            List<LocalEdgeNode> U = initU(id);
            List<LocalEdgeNode> V = initV(id);
            List<LocalEdgeNode> D;
            
            int loop = 0;
            stop = false;
            if(V.size()>0)
//            while(loop<MAX_LOOP)
            {
                X0 = X;
                V0 = V;
                U0 = U;
    //            if(loop>1)
//                D = calcD(X0,V,U,id);
                updateX(ni,id, xAvr);
//                Matrix.printMat(X, "NodeSCC x "+loop +" "+i);
/*
TODO: review to update related V and U, increase related node, expand cluster of each node.
*/
                V= updateV(V, U, id);
                U= updateU(U, V);
//                System.out.print("."+id);    
//                if((loop>1)&&(checkStop(X0, U0, V0, V)))// || (stop == true)))
//                {
                    //System.out.println(id+" NodeSCC STOP at "+loop);
//                    break;
//                }
                loop++;
            }
//            System.out.println();
            
//            X = LocalVector.formV(X, "0.0000000");
//            LocalVector.printV(X, "NodeSCC x "+id, true);
        }
//        LocalVector.printV(X, "SCC x", true);
    }
    
       
//    private int[] retSize()
//    {
//        int [] ret = new int[numberOfVertices];
//        for(LocalEdge e: edges)
//        {
//            ret[e.sourcevertex] = ret[e.sourcevertex]+1;
//            ret[e.destinationvertex] = ret[e.destinationvertex]+1;
//        }
//        return ret;
//    }
    private double[][] updateX(int n, int i, double xAvr[])
    {
  
        return X;
    }

   
    //Ue = Ue + (Ai - Ve)
    private List<LocalEdgeNode> updateU(List<LocalEdgeNode> U, List<LocalEdgeNode> V) //C
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
        for(LocalEdgeNode e: U)
        {
            LocalEdgeNode ve= getUVData(V, e.src, e.dst);// V.get(U.indexOf(e));
            if((e.src != ve.src) || (e.dst != ve.dst))
                System.out.println("paper.SCC.updateU()wrong ve "+e.src+ " "+e.dst+" "+ve.src+" "+ve.dst);
            //Ai - Be
            double[] data = LocalVector.plus(X[0],LocalVector.scale(ve.value, -1));
            //data = Vector.scale(data, rho);
            data = LocalVector.plus(e.value, data);
            LocalEdgeNode updateU = new LocalEdgeNode(e.src, e.dst, data);
//            Vector.printV(updateU.value, "updateU:"+e.src+""+e.dst, true);
            ret.add(updateU);
        }
        return ret;        
    }
      
    private List<LocalEdgeNode> initU(int i)
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
        for(LocalEdge e:edges)
        {
//            System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
            if(e.sourcevertex == i || e.destinationvertex == i)
            {
//                System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
                ret.add(new LocalEdgeNode(e.sourcevertex, e.destinationvertex, new double[numOfFeature]));
                ret.add(new LocalEdgeNode(e.destinationvertex, e.sourcevertex, new double[numOfFeature]));
            }
        }
//        for(LocalEdgeNode e: ret)
//            System.out.println("paper.NodeSCC.initU() "+e.src+ " "+e.dst );
//        System.out.println("paper.NodeSCC.initU() "+ret.size());
        return ret;        
    }
    

    private List<LocalEdgeNode> updateV(List<LocalEdgeNode> V, List<LocalEdgeNode> U, int i) //B
    {
//        System.out.println("paper.AMA.updateV()");
        List<LocalEdgeNode> ret = new ArrayList<>();
        for(LocalEdge e: edges)
        {
            if(e.sourcevertex == i || e.destinationvertex==i)
            {
                double[] Bi = null;//vik
                double[] Bk = null;//vki
                double[] Ci = null;//uik
                double[] Ck = null;//uki
                int get = 0;
    //            System.out.println(e.sourcevertex+" "+e.destinationvertex);
                for(LocalEdgeNode B:V)
                {
                    LocalEdgeNode C = getUVData(U, B.src, B.dst);// U.get(V.indexOf(B));
                    if((B.src != C.src) || (B.dst != C.dst))
                        System.out.println("paper.SCC.updateV()wrong ve "+B.src+ " "+B.dst+" "+C.src+" "+C.dst);

                    if(B.src == e.sourcevertex && B.dst == e.destinationvertex)
                    {
                        Bi = B.value;
                        Ci = C.value;
                        get++;
                    }
                    if(B.src == e.destinationvertex && B.dst == e.sourcevertex)
                    {
                        Bk = B.value; 
                        Ck = C.value; 
                        get++;
                    }

                    if(get==2)
                        break;
                }
    //                System.out.println("paper.NodeSCC.updateV() "+ i+ ": "+e.sourcevertex+"-"+e.destinationvertex);
                double[] Ai =LocalMatrix.getRow((i==e.sourcevertex)?A:A, (i==e.sourcevertex)?e.sourcevertex:e.destinationvertex); //n
                double[] Ak =LocalMatrix.getRow((i==e.sourcevertex)?A:A, (i==e.sourcevertex)?e.destinationvertex:e.sourcevertex);

                double[] AiCi = LocalVector.plus(Ai,Ci); // u get ik
                double[] AkCk = LocalVector.plus(Ak,Ck); // u get ki

                double n = 1- ((lambda*e.weight)/(LocalVector.norm(LocalVector.plus(LocalVector.plus(Bi, LocalVector.scale(Bk, -1)),LocalVector.plus(Ai, LocalVector.scale(Ak, -1))))/rho));
                double theta = 0.93;//(0.5>n)? 0.5:n; //max

    //            System.out.println("paper.NodeSCC.updateV() "+ theta);

                double[] b_ik = LocalVector.plus(LocalVector.scale(AiCi, theta), LocalVector.scale(AkCk, 1- theta));
                ret.add(new LocalEdgeNode(e.sourcevertex, e.destinationvertex, b_ik));

                double[] b_ki = LocalVector.plus(LocalVector.scale(AiCi, 1- theta), LocalVector.scale(AkCk, theta));
                ret.add(new LocalEdgeNode(e.destinationvertex, e.sourcevertex, b_ki));

//            Vector.printV(b_ik, "Bik", true);
//            Vector.printV(b_ki, "Bki", true);
            }
        }
        return ret;        
    }

    
    private List<LocalEdgeNode> initV(int i)
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
//        System.out.println("paper.NodeSCC.initV() "+(i+1));
        for(LocalEdge e:edges)
        {
//            System.out.println("paper.AMA.buildUV() "+e.sourcevertex+" "+e.destinationvertex);
            if(e.sourcevertex == i || e.destinationvertex == i)
            {
//                System.out.println((e.sourcevertex+1)+":"+(e.destinationvertex+1));
                //ik
                double[] value1 = LocalMatrix.getRow(A, e.sourcevertex) ;//new double[dataLength];
                ret.add(new LocalEdgeNode(e.sourcevertex, e.destinationvertex, value1));
                //ki
                double[] value2 = LocalMatrix.getRow(A, e.destinationvertex) ;//new double[dataLength];
                ret.add(new LocalEdgeNode(e.destinationvertex, e.sourcevertex, value2));
            }
        }
//        for(LocalEdgeNode e: ret)
//            System.out.println("paper.NodeSCC.initV() "+e.src+ " "+e.dst );
//        System.out.println("paper.NodeSCC.initV() " +ret.size());
        return ret;        
    }

    
    private double updateRho(double r, double s)
    {
        if(r>8*s)
            rho =  Double.valueOf(twoDForm.format(rho* 0.5));//(r/s);//2*rho;
        if(s>8*r)
            rho =  Double.valueOf(twoDForm.format(rho* 2));//(r/s);//rho/2;
        return rho;
    }
    
    private boolean checkStop(double[] X0, List<LocalEdgeNode> U, List<LocalEdgeNode> V0, List<LocalEdgeNode> V)
    {
        double r = primalResidual(X0,V0);
        double s = dualResidual(V0, V);
//        System.err.println("rho "+rho);
        updateRho(r, s);
        
        double maxAB= 0;
        for(LocalEdgeNode b:V)
        {
            double be = LocalVector.norm(b.value);
            double a = LocalVector.norm(A[b.src]);
            double ab = (a>be)? a:be;
            maxAB = (ab>maxAB)? ab:maxAB;
        }
        
        double maxC = 0;
        for(LocalEdgeNode c:U)
        {
            double value = LocalVector.norm(c.value);
            maxC = (value>maxC)? value:maxC;
        }
        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
        double ed = ea+er*maxC;//Cik?
        
        if(rho ==0)
            return true;
//        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
        return (r<=ep) && (s<=ed);
    }
    
     
    private double primalResidual(double[] X0, List<LocalEdgeNode> V0)
    {
        double ret = 0;
        for(LocalEdgeNode n: V0)
        {
            double normR = LocalVector.norm(LocalVector.plus(X0, n.value));
            ret = (ret>normR)?ret:normR;
        }
        return ret;
    }
    private double primalResidual(double[][] X0, List<LocalEdgeNode> V0, int i)
    {
        double ret = 0;
        double[] x= LocalMatrix.getRow(X0, i);
        for(LocalEdgeNode n: V0)
        {
            double normR = LocalVector.norm(LocalVector.plus(x, n.value));
            ret = (ret>normR)?ret:normR;
        }
        return ret;
    }
        
    private double dualResidual(List<LocalEdgeNode> Vp, List<LocalEdgeNode> V)
    {
        double ret = 0;
        for(LocalEdgeNode n: V)
        {
            double[] bikp = getUVData(V, n.src, n.dst).value;// Vp.get(V.indexOf(n)).value;
            double[] ai = LocalVector.scale(LocalVector.plus(bikp, LocalVector.scale(n.value, -1)),rho);
            double normS = LocalVector.norm(ai);
            ret = (ret>normS)?ret:normS;
        }
        return ret;
    }
    private double dualResidual(List<LocalEdgeNode> Vp, List<LocalEdgeNode> V, int i)
    {
        double ret = 0;
        
        for(LocalEdgeNode n: V)
        {
            double[] bikp = getUVData(V, n.src, n.dst).value;// Vp.get(V.indexOf(n)).value;
            double[] ai = LocalVector.scale(LocalVector.plus(bikp, LocalVector.scale(n.value, -1)),rho);
            double normS = LocalVector.norm(ai);
            ret = (ret>normS)?ret:normS;
        }
        return ret;
    }    
    
    private List<LocalEdge> updateEdge()
    {
        List<LocalEdge> ret = new ArrayList<>();
        
        ret.add(new LocalEdge(11,	17,	0.816496581));
        ret.add(new LocalEdge(11-1,	25-1,	0.666666667));
        ret.add(new LocalEdge(17-1,	23-1,	0.5));
        ret.add(new LocalEdge(23-1,	3-1,	0.5));
        ret.add(new LocalEdge(3-1,	15-1,	0.5));
        ret.add(new LocalEdge(15-1,	10-1,	0.5));
        ret.add(new LocalEdge(10-1,	24-1,	0.801783726));
        ret.add(new LocalEdge(24-1,	13-1,	0.43643578));
        ret.add(new LocalEdge(24-1,	26-1,	0.5));
        ret.add(new LocalEdge(26-1,	20-1,	0.666666667));
        ret.add(new LocalEdge(20-1,	9-1,	0.40824829));
        ret.add(new LocalEdge(9-1,	18-1,	0.577350269));
        ret.add(new LocalEdge(18-1,	21-1,	0.707106781));
        ret.add(new LocalEdge(18-1,	2-1,	0.288675135));
        ret.add(new LocalEdge(2-1,	8-1,	0.5));
        ret.add(new LocalEdge(8-1,	4-1,	0.5));
        ret.add(new LocalEdge(4-1,	22-1,	0.707106781));
        ret.add(new LocalEdge(4-1,	16-1,	0.40824829));
        ret.add(new LocalEdge(16-1,	1-1,	0.577350269));
        ret.add(new LocalEdge(1-1,	12-1,	0.707106781));
        ret.add(new LocalEdge(1-1,	19-1,	0.577350269));
        ret.add(new LocalEdge(19-1,	5-1,	0.816496581));
        ret.add(new LocalEdge(5-1,	14-1,	0.40824829));
        ret.add(new LocalEdge(16-1,	6-1,	0.40824829));
        ret.add(new LocalEdge(6-1,	7-1,	0.5));
        
        return ret;
    }
    
    private LocalEdgeNode getUVData(List<LocalEdgeNode> A, int s, int d)
    {
//        double[] ret = new double[numOfFeature];
        
        for(LocalEdgeNode e: A)
            if(e.src == s && e.dst ==d)
            {
                return e;//.value;
            }
//        System.out.println("paper.NodeSCC.getUVData() nul "+s+" "+d);
        return new LocalEdgeNode(s, d, new double[numOfFeature]);
    }
    
    public static void updateXNode()
    {
        
    }
    public static void updateVNode()
    {
        
    }
    public static void updateUNode()
    {
        
    }        
}


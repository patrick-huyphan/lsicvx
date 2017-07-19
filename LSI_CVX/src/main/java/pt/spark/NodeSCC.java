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
public class NodeSCC{  //extends Clustering{
    double rho;
    double rho2;
    double lambda;
//    double lambda2;
    double ea, er;
    public double []X; //operate
    double [][] A; //original mat
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    boolean stop = false;
    int numberOfVertices;
    int numOfFeature;
    
    static final int MAX_LOOP = 50;
    List<LocalEdge> edges;
    
    
    public NodeSCC(int id, 
            double[][] _A, double [] _X, 
            List<LocalEdge> _edges, 
            int [] ni, double[] xAvr, 
            double _lambda, 
//            double _lambda2, 
            double _rho, 
            double _e1, double _e2) throws IOException
    {
        lambda =_lambda;
//        lambda2 = _lambda2;
        rho = rho2 = _rho;
        ea = _e1;
        er = _e2;
        numOfFeature = _A[0].length;
        numberOfVertices = _A.length;
        A = _A;
        edges = _edges;
        
//        int [] ni = retSize();
////        Matrix.printMat(A, "centered");
//        //Init
        X = _X;//new double[numOfFeature];
//        System.out.println("pt.spark.NodeSCC.<init>() rho:  "+rho);
//        LocalVector.printV(X, "X start "+id, true);
//        for(int i = 0; i< numberOfVertices; i++)
        {
            rho = rho2;
            double[] X0;
            
            List<LocalEdgeNode> V0;
            List<LocalEdgeNode> U0;
            List<LocalEdgeNode> U = initU(id);
            List<LocalEdgeNode> V = initV(id);
            List<LocalEdgeNode> D;
            
            int loop = 0;
            stop = false;
            if(V.size()>0)
            while(loop<MAX_LOOP)
            {
                X0 = X;
                V0 = V;
                U0 = U;
    //            if(loop>1)
                D = calcD(X0,V,U,id);
                updateX(D, ni,id, xAvr);
//                Matrix.printMat(X, "NodeSCC x "+loop +" "+i);
/*
TODO: review to update related V and U, increase related node, expand cluster of each node.
*/
                V= updateV(V, U, id);
                U= updateU(U, V);
//                System.out.print("."+id);    
                if((loop>1)&&(checkStop(X0, U0, V0, V)))// || (stop == true)))
                {
                    System.out.println(id+" NodeSCC STOP at "+loop);
                    break;
                }
                loop++;
            }
//            System.out.println();
            
            X = LocalVector.formV(X, "0.0000000");
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
    private double[] updateX(List<LocalEdgeNode> D, int[] n, int i, double xAvr[])
    {
            double[] sumd = new double[numOfFeature];
            
            for(LocalEdgeNode d:D)
            {
                if(d.source == i)
                {
                    sumd= LocalVector.plus(sumd,d.relatedValue);
                }
            }
            sumd = LocalVector.scale(sumd, rho/n[i]);
//            Vector.printV(sumd, "sum "+i+" "+n[i], true);
//            X[i] = Vector.plus(X[i], sumd);
//            if(Vector.isZeroVector(sumd))
//                stop = true;
            
            for(int j = 0; j<numOfFeature; j++)
            {
//                double x=  ;
                X[j] = xAvr[j]+sumd[j];///(1+rho)); ///(rho*D.size()
                if(Double.isNaN(X[j]))
                    X[j]= 0;
//                X[i][j] = (x + (sumd[j]))/(1+(rho*n[i])); 
//                X[i][j] = X[i][j]+sumd[j];
//                X[i][j] = Double.valueOf(twoDForm.format(X[i][j]));
            } 
            
//        Vector.printV(X[i],"X"+i,true);    
        return X;
    }

    /*
     * De = A - Be + Ce
     */
    private List<LocalEdgeNode> calcD(double [] X0,List<LocalEdgeNode> V, List<LocalEdgeNode> U, int i) //(B-C)
    {
        List<LocalEdgeNode> D = new ArrayList<>();
//            System.err.println("u "+U.size()+" "+i);
//            System.err.println("v "+V.size()+" "+i);
//        for(LocalEdgeNode e: V)
        for(int k = 0; k< V.size(); k++)
        {
            LocalEdgeNode v = V.get(k);
            if(v.source == i)
            {
//            System.out.println("paper.NodeSCC.calcD()");
                LocalEdgeNode u = getUVData(U, v.source, v.dest);// U.get(k);

                double[] d = LocalVector.plus(X0, LocalVector.scale(v.relatedValue,-1));
                d =         LocalVector.plus(d, u.relatedValue);
    //            d = Vector.plus(Matrix.getRow(X0, e.source), Vector.scale(e.relatedValue,-1));
                D.add(new LocalEdgeNode(v.source, v.dest, d));
            }
        }
        return D;
    }
    
   
    //Ue = Ue + (Ai - Ve)
    private List<LocalEdgeNode> updateU(List<LocalEdgeNode> U, List<LocalEdgeNode> V) //C
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
        for(LocalEdgeNode e: U)
        {
            LocalEdgeNode ve= getUVData(V, e.source, e.dest);// V.get(U.indexOf(e));
            if((e.source != ve.source) || (e.dest != ve.dest))
                System.out.println("paper.SCC.updateU()wrong ve "+e.source+ " "+e.dest+" "+ve.source+" "+ve.dest);
            //Ai - Be
            double[] data = LocalVector.plus(X,LocalVector.scale(ve.relatedValue, -1));
            //data = Vector.scale(data, rho);
            data = LocalVector.plus(e.relatedValue, data);
            LocalEdgeNode updateU = new LocalEdgeNode(e.source, e.dest, data);
//            Vector.printV(updateU.relatedValue, "updateU:"+e.source+""+e.dest, true);
            ret.add(updateU);
        }
        return ret;        
    }
      
    private List<LocalEdgeNode> initU(int i)
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
//        HashMap<String, double[]> a = new HashMap<>();
//        if(null!= a.put( "s-d", 1.))
//                    System.out.println("paper.NodeSCC.initU() insert ok "+i);
        
        for(LocalEdge e:edges)
        {
//            System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
            if(e.sourcevertex == i || e.destinationvertex == i)
            {
//                System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
                //ik
                ret.add(new LocalEdgeNode(e.sourcevertex, e.destinationvertex, new double[numOfFeature]));
                //ki
                ret.add(new LocalEdgeNode(e.destinationvertex, e.sourcevertex, new double[numOfFeature]));
                
//                double[] v= new double[numOfFeature];
//                int [] k = new int[]{e.destinationvertex, e.sourcevertex};
//                if(a.put( k[0]+"-"+k[1], v)!= null)
//                    System.out.println("paper.NodeSCC.initU() insert ok "+i);
            }
        }
//        for(LocalEdgeNode e: ret)
//            System.out.println("paper.NodeSCC.initU() "+e.source+ " "+e.dest );
//        System.out.println("paper.NodeSCC.initU() "+ret.size());
        return ret;        
    }
    

    private List<LocalEdgeNode> updateV(List<LocalEdgeNode> V, List<LocalEdgeNode> U, int i) //B
    {
//        System.out.println("paper.AMA.updateV()");
        List<LocalEdgeNode> ret = new ArrayList<>();
        
//        for(LocalEdgeNode e: V)
//        {
//                double[] Bi = getUVData(V, e.source, e.dest).relatedValue;//vik
//                double[] Bk = getUVData(V, e.dest, e.source).relatedValue;//vki
//                double[] Ci = getUVData(U, e.source, e.dest).relatedValue;//uik
//                double[] Ck = getUVData(U, e.dest, e.source).relatedValue;//uki
//                
//                double[] Ai =Matrix.getCol((i==e.source)?A:A, (i==e.source)?e.source:e.dest); //n
//                double[] Ak =Matrix.getCol((i==e.source)?A:A, (i==e.source)?e.dest:e.source);
//
//                double[] AiCi = Vector.plus(Ai,Ci); // u get ik
//                double[] AkCk = Vector.plus(Ak,Ck); // u get ki
//
//                double n = 1- ((lambda)/(Vector.norm(Vector.plus(Vector.plus(Bi, Vector.scale(Bk, -1)),Vector.plus(Ai, Vector.scale(Ak, -1))))/rho));
//                double theta = (0.5>n)? 0.5:n; //max
//
//    //            System.out.println("paper.NodeSCC.updateV() "+ theta);
//
//                double[] b_ik = Vector.plus(Vector.scale(AiCi, theta), Vector.scale(AkCk, 1- theta));
//                ret.add(new LocalEdgeNode(e.source, e.dest, b_ik));
//
////                double[] b_ki = Vector.plus(Vector.scale(AiCi, 1- theta), Vector.scale(AkCk, theta));
////                ret.add(new LocalEdgeNode(e.dest, e.source, b_ki));
//        }
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
                    LocalEdgeNode C = getUVData(U, B.source, B.dest);// U.get(V.indexOf(B));
                    if((B.source != C.source) || (B.dest != C.dest))
                        System.out.println("paper.SCC.updateV()wrong ve "+B.source+ " "+B.dest+" "+C.source+" "+C.dest);

                    if(B.source == e.sourcevertex && B.dest == e.destinationvertex)
                    {
                        Bi = B.relatedValue;
                        Ci = C.relatedValue;
                        get++;
                    }
                    if(B.source == e.destinationvertex && B.dest == e.sourcevertex)
                    {
                        Bk = B.relatedValue; 
                        Ck = C.relatedValue; 
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
                double theta = (0.5>n)? 0.5:n; //max

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
//            System.out.println("paper.NodeSCC.initV() "+e.source+ " "+e.dest );
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
            double be = LocalVector.norm(b.relatedValue);
            double a = LocalVector.norm(A[b.source]);
            double ab = (a>be)? a:be;
            maxAB = (ab>maxAB)? ab:maxAB;
        }
        
        double maxC = 0;
        for(LocalEdgeNode c:U)
        {
            double value = LocalVector.norm(c.relatedValue);
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
            double normR = LocalVector.norm(LocalVector.plus(X0, n.relatedValue));
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
            double normR = LocalVector.norm(LocalVector.plus(x, n.relatedValue));
            ret = (ret>normR)?ret:normR;
        }
        return ret;
    }
        
    private double dualResidual(List<LocalEdgeNode> Vp, List<LocalEdgeNode> V)
    {
        double ret = 0;
        for(LocalEdgeNode n: V)
        {
            double[] bikp = getUVData(V, n.source, n.dest).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = LocalVector.scale(LocalVector.plus(bikp, LocalVector.scale(n.relatedValue, -1)),rho);
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
            double[] bikp = getUVData(V, n.source, n.dest).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = LocalVector.scale(LocalVector.plus(bikp, LocalVector.scale(n.relatedValue, -1)),rho);
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
            if(e.source == s && e.dest ==d)
            {
                return e;//.relatedValue;
            }
//        System.out.println("paper.NodeSCC.getUVData() nul "+s+" "+d);
        return new LocalEdgeNode(s, d, new double[numOfFeature]);
    }
}


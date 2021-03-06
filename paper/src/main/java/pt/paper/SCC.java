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
import java.util.List;
import static pt.paper.Clustering.MAX_LOOP;

/**
 *
 * @author patrick_huy
 * update in X
 * update in rho
 * theta
 * 
 */
public class SCC  extends Clustering{
    double rho;
    double rho2;
    double lambda2;
    double ea, er;
    public double [][] X;
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    boolean stop = false;
    
    double[] u ;
    double[] xAvr;
    /**
     * 
     * @param _Matrix
     * @param _lambda: in range 1->2
     * @param _lambda2
     * @param _rho
     * @param _e1
     * @param _e2
     * @throws IOException 
     */
    public SCC(double[][] _Matrix, double _lambda, double _lambda2,double _rho, double _e1, double _e2) throws IOException
    {
        super(_Matrix, _lambda);
//        edges = updateEdge(); // for paper data
        lambda2 = _lambda2;
        rho = rho2 = _rho;
        ea = _e1;
        er = _e2;

//        A = Matrix.centered(A);
        
//        presentMat = new double[numOfFeature][5];
//        int mat[]={1,4,5,12,22};
//        int mat[]={6,10,17,18,23};
//        for(int i = 0; i<numOfFeature; i++)
//        {
//            for(int j =0; j<5; j++)
//            presentMat[i][j]= _Matrix[i][mat[j]];
//        }
        
        int [] ni = retSize();
//        Matrix.printMat(A, "centered");
        //Init
        init();
//        Matrix.printMat(X, false,"SCC x0");
        
        for(int i = 0; i< numberOfVertices; i++)
        {
            rho = rho2;
            double[][] X0;
//            Matrix.printMat(X, "SCC x "+i);
            
//            ListENode V0;
//            ListENode U0;
//            ListENode V = initV2(i);
//            ListENode U = initU2(i);
//            ListENode D;
            
            List<EdgeNode> V0;
            List<EdgeNode> U0;
            List<EdgeNode> U = initU(i);
            List<EdgeNode> V = initV(i);
            List<EdgeNode> D;

//            Vector.printV(X[i], "X "+i, stop);

            int loop = 0;
            stop = false;
//            if(V.size()==0)
//                Vector.printV(X[i], "X "+i, false);

//            if(i==1)
//            Vector.printV(X[i],"X "+i,true);
            if(V.size()>0)
            while(loop<MAX_LOOP)
            {
                X0 = X;
                V0 = V;
                U0 = U;
//                if(loop==4)
//                {
//                    double[] v = Matrix.getRow(X, i);
//                    Vector.printV(v, "v"+i+"-"+loop, stop);
//                }

                D = calcD(V,U,i); //x-v+u
                updateX(D, ni[i],i, xAvr); //xAvr +sumD
                V= updateV(V, U, i); //
                U= updateU(U, V); //u-x+v
//                Matrix.printMat(X, "SCC x "+loop +" "+i);
                if(checkStop(X0, U0, V0, V) && (loop>1))// || (stop == true)))
                {
//                    System.out.println(i+" SCC STOP at "+loop);
                    break;
                }
                loop++;
            }
            else
                System.out.println("SCC v size =0 "+i);
//            Matrix.printMat(X, "SCC x "+i);
//            DecimalFormat twoDForml = new DecimalFormat("0.00000000");
//            for(int r = 0; r < numOfFeature; r++)
//            {
//                X[i][r] = (Double.isNaN(X[i][r]))?0:Double.valueOf(twoDForml.format(X[i][r]));
//            }
//            Vector.printV(X[i], "X "+i, stop);
            Vector.formV(X[i], "0.00000000");
        }
        FileWriter fw = new FileWriter("X_data.txt");
        
        for(int i = 0; i< numberOfVertices; i++)
        {
            for(int j = 0; j< numOfFeature; j++)
                fw.append(X[i][j]+"\t");
            fw.append("\n");
        }
//        getCluster(fw);
//        Matrix.printMat(X, "SCC x");
//        CSVFile.saveMatrixData("SCC", X, "SCC");
        cluster = new ArrayList<>();

        fw = new FileWriter("SCC_Cluster.txt");
        getCluster(fw);
        fw.close();
        presentMat= new double[cluster.size()][A[0].length];
        getPresentMat(true);
    }
    
    void init()
    {
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
        for(int i = 0; i< numOfFeature; i++)
        {
            u[i] = Vector.norm(Matrix.getCol(X, i));
            xAvr[i] = Vector.avr(Matrix.getCol(A, i));
        }
        u = Vector.scale(u, lambda2);              
        for(int i = 0; i< numOfFeature; i++)
        {            
//            double x = 1- (u[i]/Vector.norm(Matrix.getCol(A, i))); //u[i]
//            x = (x>0)? x:0;
            for(int j = 0 ; j< numberOfVertices; j++)
            {
//                X[j][i] =  xAvr[i];
//                X[j][i] =  x*A[j][i]; 
                X[j][i] =  xAvr[i]*A[j][i];
            }            
        }
    }

    @Override
    public final void getCluster(FileWriter fw)
    {
//        int  ret[] = new int[numberOfVertices];
        HashMap<Integer, Integer> xxx = new HashMap<>();
//        List<Integer>  intdex = new ArrayList<Integer>();
    	for(int i = 0; i<numberOfVertices; i++)
    	{
            if(!xxx.containsKey(i))
            {
                xxx.put(i, i);
                for(int j = i+1; j<numberOfVertices; j++)
                {
                    if(!xxx.containsKey(j))
                        if((Vector.isSameVec(Matrix.getRow(X, i),Matrix.getRow(X, j))))
                        {
                            xxx.put(j, i);
                        }
                }
            }
    	}
//    	count--;
//    	System.out.println("Num of sub mat: "+intdex.size());
    	
    	for(int i = 0; i<numberOfVertices; i++)
    	{
            List<Integer> sub = new ArrayList<>();
            for(int j = i; j<numberOfVertices; j++)
            {
                if(xxx.get(j)==i)
                { 
                    sub.add(j);
                }
            }
            if(!sub.isEmpty())
                cluster.add(sub);
    	}
//    	cluster.add(intdex);
//Test    
        System.out.println("paper.SCC.getCluster() "+cluster.size());
    	for(int i = 0; i<cluster.size(); i++)
    	{
            List<Integer> sub = cluster.get(i);
            System.out.print("Cluster "+ i+":\t");
            for(int j = 0; j<sub.size(); j++)
            {
                    System.out.print(sub.get(j) +"\t");
            }
            System.out.println("");
    	}

    }
       
    private int[] retSize()
    {
        int [] ret = new int[numberOfVertices];
        for(Edge e: edges)
        {
            ret[e.scr] = ret[e.scr]+1;
            ret[e.dst] = ret[e.dst]+1;
        }
        return ret;
    }
    private double[][] updateX(List<EdgeNode> D, int n, int i, double xAvr[])
    {
        double[] sumd = new double[numOfFeature];

        for(EdgeNode d:D)
        {
            sumd= Vector.plus(sumd,d.relatedValue);
//                if(i==50) System.out.println("pt.paper.SCC.updateX()"+ d.scr +" - " +d.dst);
        }
        sumd = Vector.scale(sumd, rho/(n*14));           
//        if(i==1)
//            Vector.printV(X[i],"X "+i,true);
        Matrix.updateRow(X, Vector.plus(xAvr, sumd), i); //
        return X;
    }

    private double[][] updateX(ListENode D, int[] n, int i)
    {
//        for(int i = 0; i<numberOfVertices; i++)
        {
            double[] sumd = new double[numOfFeature];
            
            for(Key d:D.E.keySet())
            {
//                if(d[0] == i)
//                {
////                    System.out.println("paper.SCC.updateX() "+i);
//                    sumd= Vector.scale(Vector.plus(sumd,D.get(d[0], d[1])), rho/2);
//                }
            }
//            sumd = Vector.scale(sumd, 1/n[i]);
//            Vector.printV(sumd, "sum "+i, true);
            X[i] = Vector.plus(X[i], Vector.scale(sumd, n[i]));
//            for(int j = 0; j<numOfFeature; j++)
//            {
////                X[i][j] = (A[i][j] + (rho*sumd[j]))/(1+(rho*n[i])); 
//                X[i][j] = X[i][j]+sumd[j];///(1+rho)); ///(rho*D.size()
//            }   
        }
        return X;
    }
    /*
     * De = A - Be + Ce
     */
    private List<EdgeNode> calcD(List<EdgeNode> V, List<EdgeNode> U, int i) //(B-C)
    {
        List<EdgeNode> D = new ArrayList<>();
//            System.err.println("u "+U.size()+" "+i);
//            System.err.println("v "+V.size()+" "+i);
//        for(EdgeNode e: V)
//        if(i == 50)
//        {
//            for(EdgeNode v:V)
//            {
//                System.out.println("V D "+v.scr+" "+v.dst);
//            }
//            System.out.println();
//        }
        for(EdgeNode v : V)
        {
            double[] d = Vector.sub(Matrix.getRow(X, i), v.relatedValue);
            EdgeNode u = getEdgeNodeData(U, v.scr, v.dst);// U.get(k);
            d = Vector.plus(d, u.relatedValue);
            D.add(new EdgeNode(v.scr, v.dst, d));            
        }
        return D;
    }
    
    //Ue = Ue + (Ai - Ve)
    private List<EdgeNode> updateU(List<EdgeNode> U, List<EdgeNode> V) //C
    {
        List<EdgeNode> ret = new ArrayList<>();
        for(EdgeNode v: V)
        {
            EdgeNode u= getEdgeNodeData(U, v.scr, v.dst);// V.get(U.indexOf(e));
            if((u.scr != v.scr) || (u.dst != v.dst))
                System.out.println("paper.SCC.updateU()wrong ve "+u.scr+ " "+u.dst+" "+v.scr+" "+v.dst);
            //Ai - Be
            double[] data = Vector.sub(Matrix.getRow(X, u.scr),v.relatedValue);
            //data = Vector.scale(data, rho);
            data = Vector.plus(u.relatedValue, data);
//            Vector.printV(updateU.relatedValue, "updateU:"+e.scr+""+e.dst, true);
            ret.add(new EdgeNode(u.scr, u.dst, data));
        }
        return ret;        
    }
    
    
    private List<EdgeNode> initU(int i)
    {
        List<EdgeNode> ret = new ArrayList<>();
        HashMap<String, double[]> a = new HashMap<>();
//        if(null!= a.put( "s-d", 1.))
//                    System.out.println("paper.SCC.initU() insert ok "+i);
        
        for(Edge e:edges)
        {
//            System.out.println("paper.SCC.initU() E "+e.scr+ " "+e.dst );
            if(e.scr == i || e.dst == i)
            {
                ret.add(new EdgeNode(e.scr, e.dst, new double[numOfFeature]));
            }
        }
//        for(EdgeNode e: ret)
//            System.out.println("paper.SCC.initU() "+e.scr+ " "+e.dst );
//        System.out.println("paper.SCC.initU() "+ret.size());
        return ret;        
    }
    

    /*
    lamda - e.weigh
    */
    private List<EdgeNode> updateV(List<EdgeNode> V, List<EdgeNode> U, int i) //B
    {
//        System.out.println("paper.AMA.updateV()");
        List<EdgeNode> ret = new ArrayList<>();

        for(Edge e: edges)
        {
            if(e.scr == i || e.dst==i)
            {
//                double[] Bi = null;//vik
//                double[] Bk = null;//vki
//                double[] Ci = null;//new double[numOfFeature];//uik
                double[] Ck = new double[numOfFeature];//uki
//                int get = 0;
                
                EdgeNode C = getEdgeNodeData(U, e.scr, e.dst);
                EdgeNode B = getEdgeNodeData(V, e.scr, e.dst);
                double[] Vi = B.relatedValue; 
                double[] Ui = C.relatedValue; 
                int count = 0;
                for(Edge u: edges)
                {     
                    if(C.scr == i && ((C.dst==u.scr && u.dst != i) || (C.dst== u.dst && u.scr != i)))
                    {
//                        if(i==50 ) System.out.println(i+ " U s "+u.scr+" "+u.dst);
                        Ck= Vector.plus( Ck,  Matrix.getRow(A, (C.scr == i)?u.dst:u.scr));
                        count++;
                    }
                    if(C.dst == i && ((C.scr==u.scr && u.dst != i) || (C.scr== u.dst && u.scr != i)))
                    {
//                        if(i==50 ) System.out.println(i+ " U d "+u.scr+" "+u.dst);
                        Ck= Vector.plus( Ck,  Matrix.getRow(A, (C.dst == i)?u.scr:u.dst));
                        count++;
                    }
                }
                
                double[] AkCk;// = new double[numOfFeature];
                if(count>0)
                {
                    AkCk = Vector.scale(Ck, 1./count);
//                    if(i==50)
//                        Vector.printV(Ck, " CK "+e.scr+" "+e.dst, false);
                }
                else
                {
//                    System.out.println(i+ " pt.paper.SCC.updateV() "+ e.scr +" "+e.dst);
                    AkCk = Matrix.getRow(A,i);
                }
                      
//                System.out.println("paper.SCC.updateV() "+ i+ ": "+e.scr+"-"+e.dst);
//                double[] Ai = Matrix.getRow(A,i); //n
//                double[] Ak =Matrix.getRow(A, (i==e.scr)?e.dst:e.scr);

                double[] AiCi = Vector.plus(Matrix.getRow(A,i), Vector.plus(Vi, Ui)); // u get ik As+Us
//                double[] AkCk = Vector.plus(Ak,Ck); // u get ki Ad+Ud

//                double n = 1- ((lambda*e.weight)/(Vector.norm(Vector.plus(Vector.sub(Bi, Bk),Vector.sub(Ai, Ak)))/rho));
//                System.out.println("paper.SCC.updateV() "+ (1-n));
//                double theta = (0.5>n)? 0.5:n; //max

//                double theta = 0.6;
//                System.out.println(i+ " paper.SCC.updateV() " + e.scr+"-"+e.dst+": "+ e.weight +"  "+lambda);
//                double thet = theta;
//                double thet = (1.5+lambda);    
                
                double a = (1 - e.weight)+lambda;// e.weight/2;
                double b = e.weight/lambda;
                double[] v_ik = Vector.plus(Vector.scale(AiCi, a), Vector.scale(AkCk, b));
//                ret.add(new EdgeNode(e.scr, e.dst, v_ik));
//                ret.add(new EdgeNode(e.dst, e.scr, Vector.scale(v_ik,lambda)));
//                if(i==90 ) System.out.println("V in "+e.scr+" "+e.dst);
//                double[] v_ki = Vector.plus(Vector.scale(AiCi, b), Vector.scale(AkCk, a));
                ret.add(new EdgeNode(e.scr, e.dst, Vector.scale(v_ik,e.weight)));

//            Vector.printV(b_ik, "Bik "+e.scr, true);
//            Vector.printV(b_ki, "Bki "+e.dst, true);
            }
        }
        return ret;        
    }
        
    private List<EdgeNode> initV(int i)
    {
        List<EdgeNode> ret = new ArrayList<>();
//        System.out.println("paper.SCC.initV() "+(i+1));
        for(Edge e:edges)
        {
//            System.out.println("paper.AMA.buildUV() "+e.scr+" "+e.dst);
            if(e.scr == i || e.dst == i)
            {
//                System.out.println((e.scr+1)+":"+(e.dst+1));
                //ik
                double[] value1 = Matrix.getRow(X, e.scr) ;//new double[dataLength];
                ret.add(new EdgeNode(e.scr, e.dst, value1));
            }
        }
//        for(EdgeNode e: ret)
//            System.out.println("paper.SCC.initV() "+e.scr+ " "+e.dst );
//        System.out.println("paper.SCC.initV() " +ret.size());
        return ret;        
    }
   
    private double updateRho(double r, double s)
    {
        if(r>10*s)
            rho =  Double.valueOf(twoDForm.format(rho* 0.745));//(r/s);//2*rho;
        if(s>10*r)
            rho =  Double.valueOf(twoDForm.format(rho* 3));//(r/s);//rho/2;
        return rho;
    }
    
    private boolean checkStop(double[][] X0, List<EdgeNode> U, List<EdgeNode> V0, List<EdgeNode> V)
    {
        double r = primalResidual(X0,V0);
        double s = dualResidual(V0, V);
//        System.err.println("rho "+rho);
                
        double maxAB= 0;
        for(EdgeNode b:V)
        {
            double be = Vector.norm(b.relatedValue);
            double a = Vector.norm(A[b.scr]);
            double ab = (a>be)? a:be;
            maxAB = (ab>maxAB)? ab:maxAB;
        }
        
        double maxC = 0;
        for(EdgeNode c:U)
        {
            double value = Vector.norm(c.relatedValue);
            maxC = (value>maxC)? value:maxC;
        }
        double ed = ea+er*maxC;//Cik?
        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
        
        updateRho(r, s);
        if(rho ==0)
        {
            //System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed+" ==== "+count);
            return true;
        }
        return (r<=ep) && (s<=ed);
    }
    
//    private boolean checkStop(double[][] X0, List<EdgeNode> U, List<EdgeNode> V0, List<EdgeNode> V, int i)
//    {
//        double r = primalResidual(X0,V0,i);
//        double s = dualResidual(V0, V,i);
////        System.err.println("rho "+rho);
//        updateRho(r, s);
//        
//        double maxAB= 0;
//        for(EdgeNode b:V)
//        {
//            double be = Vector.norm(b.relatedValue);
//            double a = Vector.norm(A[b.scr]);
//            double ab = (a>be)? a:be;
//            maxAB = (ab>maxAB)? ab:maxAB;
//        }
//        
//        double maxC = 0;
//        for(EdgeNode c:U)
//        {
//            double value = Vector.norm(c.relatedValue);
//            maxC = (value>maxC)? value:maxC;
//        }
//        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
//        double ed = ea+er*maxC;//Cik?
//        
////        if(rho ==0)
////            return true;
////        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
//        return (r<=ep) && (s<=ed);
//    }
//    private boolean checkStop(double[][] X0, ListENode U, ListENode V0, ListENode V)
//    {
//        double r = primalResidual(X0,V0);
//        double s = dualResidual(V0, V);
//        updateRho(r, s);
//        
//        double maxAB= 0;
//        for(int[] b:V.keySet())
//        {
//            double be = Vector.norm(V.get(b[0], b[1]));
//            double a = Vector.norm(A[b[0]]);
//            double ab = (a>be)? a:be;
//            maxAB = (ab>maxAB)? ab:maxAB;
//        }
//        
//        double maxC = 0;
//        for(int[] c:U.keySet())
//        {
//            double value = Vector.norm(U.get(c[0], c[1]));
//            maxC = (value>maxC)? value:maxC;
//        }
//        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
//        double ed = ea+er*maxC;//Cik?
//        
////        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
//        return (r<=ep) && (s<=ed);
//    }
    
    private double primalResidual(double[][] X0, List<EdgeNode> V0)
    {
        double ret = 0;
        for(EdgeNode n: V0)
        {
            double normR = Vector.norm(Vector.plus(Matrix.getRow(X0, n.scr), n.relatedValue));
            ret = (ret>normR)?ret:normR;
        }
        return ret;
    }
//    private double primalResidual(double[][] X0, List<EdgeNode> V0, int i)
//    {
//        double ret = 0;
//        double[] x= Matrix.getRow(X0, i);
//        for(EdgeNode n: V0)
//        {
//            double normR = Vector.norm(Vector.plus(x, n.relatedValue));
//            ret = (ret>normR)?ret:normR;
//        }
//        return ret;
//    }
//        
    private double dualResidual(List<EdgeNode> Vp, List<EdgeNode> V)
    {
        double ret = 0;
        for(EdgeNode n: V)
        {
            double[] bikp = getEdgeNodeData(V, n.scr, n.dst).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = Vector.scale(Vector.sub(bikp, n.relatedValue),rho);
            double normS = Vector.norm(ai);
            ret = (ret>normS)?ret:normS;
        }
        return ret;
    }
//    private double dualResidual(List<EdgeNode> Vp, List<EdgeNode> V, int i)
//    {
//        double ret = 0;
//        
//        for(EdgeNode n: V)
//        {
//            double[] bikp = getEdgeNodeData(V, n.scr, n.dst).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
//            double[] ai = Vector.scale(Vector.plus(bikp, Vector.scale(n.relatedValue, -1)),rho);
//            double normS = Vector.norm(ai);
//            ret = (ret>normS)?ret:normS;
//        }
//        return ret;
//    }    

    
    private List<Edge> updateEdge()
    {
        List<Edge> ret = new ArrayList<>();
        
        ret.add(new Edge(11,	17,	0.816496581));
        ret.add(new Edge(11-1,	25-1,	0.666666667));
        ret.add(new Edge(17-1,	23-1,	0.5));
        ret.add(new Edge(23-1,	3-1,	0.5));
        ret.add(new Edge(3-1,	15-1,	0.5));
        ret.add(new Edge(15-1,	10-1,	0.5));
        ret.add(new Edge(10-1,	24-1,	0.801783726));
        ret.add(new Edge(24-1,	13-1,	0.43643578));
        ret.add(new Edge(24-1,	26-1,	0.5));
        ret.add(new Edge(26-1,	20-1,	0.666666667));
        ret.add(new Edge(20-1,	9-1,	0.40824829));
        ret.add(new Edge(9-1,	18-1,	0.577350269));
        ret.add(new Edge(18-1,	21-1,	0.707106781));
        ret.add(new Edge(18-1,	2-1,	0.288675135));
        ret.add(new Edge(2-1,	8-1,	0.5));
        ret.add(new Edge(8-1,	4-1,	0.5));
        ret.add(new Edge(4-1,	22-1,	0.707106781));
        ret.add(new Edge(4-1,	16-1,	0.40824829));
        ret.add(new Edge(16-1,	1-1,	0.577350269));
        ret.add(new Edge(1-1,	12-1,	0.707106781));
        ret.add(new Edge(1-1,	19-1,	0.577350269));
        ret.add(new Edge(19-1,	5-1,	0.816496581));
        ret.add(new Edge(5-1,	14-1,	0.40824829));
        ret.add(new Edge(16-1,	6-1,	0.40824829));
        ret.add(new Edge(6-1,	7-1,	0.5));
        
        return ret;
    }
    
    private EdgeNode getEdgeNodeData(List<EdgeNode> A, int s, int d)
    {
//        double[] ret = new double[numOfFeature];
        
        for(EdgeNode e: A)
            if(e.scr == s && e.dst ==d)
            {
                return e;//.relatedValue;
            }
//        System.out.println("paper.SCC.getUVData() nul "+s+" "+d);
        return new EdgeNode(s, d, new double[numOfFeature]);
    }
}


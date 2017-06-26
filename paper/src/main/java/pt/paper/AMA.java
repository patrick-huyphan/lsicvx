/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author patrick_huy
 */
public class AMA extends Clustering{
    double [][] X;
    
    /**
     *
     * @param _data
     * @param lambda1
     * @param lambda2
     * @param rho
     * @param e1
     * @param e2
     * @throws IOException
     */
    public AMA(double [][] _data, double lambda1, double lambda2, double rho, double e1, double e2) throws IOException
    {
        super(_data,lambda1);
        A= Matrix.centered(A);
        Matrix.printMat(A, "centered");
        X = new double[numberOfVertices][numberOfVertices];
        List<EdgeNode> U = buildUV(numberOfVertices);
        List<EdgeNode> V;// = buildUV(edges, A.length);
        double[] u = new double[numberOfVertices];
        
        u = Vector.scale(u, lambda2);
        
        boolean stop = false;
        int loop = 0;
        while(loop<MAX_LOOP)
        {
            X= updateX(U, u);
            V= updateV(X, U, rho);
            U= updateU(X, U, V, rho);
            
            stop =checkStop(X, U, V, e1, e2);
            if(stop)
                break;
            loop++;
        }
        Matrix.printMat(X, "AMA x");
        cluster = new ArrayList<>();
        FileWriter fw = new FileWriter("cluster.txt");
        getCluster(fw);
        fw.close();
        
//        presentMat= new double[cluster.size()][A[0].length];
        getPresentMat();
    }
    
    @Override
    public final void getCluster(FileWriter fw)
    {
        int  ret[] = new int[numberOfVertices];
        HashMap<Integer, Integer> xxx = new HashMap<>();
        List<Integer>  intdex = new ArrayList<Integer>();
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
                                System.out.println("same: "+i+"-"+j);
                                xxx.put(j, i);
                        }
                }
            }
    	}
//    	count--;
//    	System.out.println("Num of sub mat: "+intdex.size());
    	
    	for(int i = 0; i<numberOfVertices; i++)
    	{
            List<Integer> sub = new ArrayList<Integer>();
            for(int j = i; j<numberOfVertices; j++)
            {
                if(xxx.get(j)==i)
                { 
                    sub.add(j+1);
                }
            }
            if(!sub.isEmpty())
                cluster.add(sub);
    	}
//    	cluster.add(intdex);
//Test    
        System.out.println("paper.AMA.getCluster() "+cluster.size());
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
       
    
    private double[][] updateX(List<EdgeNode> u, double[] u1)
    {
        double[][] ret = new double[numberOfVertices][numOfFeature];
//        double sic = rho*lambda2;
        for(int i = 0; i < numOfFeature; i++)
        {
            double[] z = calcZ(u, i);
            double sic = u1[i];
            double[] xi = Vector.proxN2(z, sic);
            ret = Matrix.updateCol(ret, z, i);
        }
//        Matrix.printMat(ret, "AMA");
        return ret;
    }

    /*
     * Z = Xj + rho sum_e((Uej)*(áº¸j - áº¸j,))
     */
    private double[] calcZ(List<EdgeNode> u, int col)
    {
        double[] sum = new double[numberOfVertices];
        for(EdgeNode e: u)
        {
            double uj = e.relatedValue[col];
            if(uj>0)
            {
                double[] Ei= Vector.eVector(numOfFeature, e.source);
                double[] Ej= Vector.eVector(numOfFeature, e.dest);
                double[] edge = Vector.scale(Vector.plus(Ei, Vector.scale(Ej,-1)),uj);

                sum = Vector.plus(sum, edge);
//                Vector.printV(sum, "z:"+col, true);
            }
        }
        sum = Vector.scale(sum, u.size());
        
        sum = Vector.plus(Matrix.getCol(A, col), sum);
//        Vector.printV(sum, "z:"+col, true);
        return sum;
    }
    //Ue = Ue + rho (Ve - Aj + Aj')
    private List<EdgeNode> updateU(double [][] _A, List<EdgeNode> U, List<EdgeNode> V, double rho)
    {
//        System.out.println("paper.AMA.updateU()");
        List<EdgeNode> ret = new ArrayList<>();
        for(EdgeNode e: U)
        {
//            System.out.println("paper.AMA.updateU()");
            double[] ve= V.get(U.indexOf(e)).relatedValue;
            //Ve - Aj + Aj'
            double[] data = Vector.plus(ve,Vector.plus(Vector.scale(Matrix.getRow(_A, e.source),-1),Matrix.getRow(_A, e.dest)));
            data = Vector.scale(data, rho);
            data = Vector.plus(e.relatedValue, data);
            EdgeNode updateU = new EdgeNode(e.source, e.dest, data);
//            Vector.printV(updateU.relatedValue, "updateU:"+e.source+""+e.dest, true);
            ret.add(updateU);
        }
        return ret;        
    }
    
        //Ve = prox(Aj-Aj'-Ue) = prox(Aj-Aj'-(1/rho)Ue) lambda*we/rho
    private List<EdgeNode> updateV(double[][] _A, List<EdgeNode> U, double rho)
    {
//        System.out.println("paper.AMA.updateV()");
        List<EdgeNode> ret = new ArrayList<>();
        double sic = lambda/rho;
        for(EdgeNode e: U)
        {
            double[] ai = Matrix.getRow(_A, e.source);
            double[] aj = Matrix.getRow(_A, e.dest);
            double we = edges.get(U.indexOf(e)).weight * Math.exp(Vector.norm(Vector.plus(ai, Vector.scale(aj, -1)))* 0.5);
            
            //Aj-Aj'-Ue
            double[] sum = Vector.plus(Vector.plus(ai,Vector.scale(aj, -1)),Vector.scale(e.relatedValue, -1));
            sic = sic * we;
            double[] data = Vector.proxN1(sum, sic);
//            Vector.printV(data, "updateV:"+e.source+""+e.dest, true);
            EdgeNode updateV = new EdgeNode(e.source, e.dest, data);
            ret.add(updateV);
        }   
        return ret;        
    }
    
    private List<EdgeNode> buildUV(int dataLength)
    {
        List<EdgeNode> ret = new ArrayList<>();
        for(Edge e:edges)
        {
//            System.out.println("paper.AMA.buildUV() "+e.sourcevertex+" "+e.destinationvertex);
            double[] value = new double[dataLength];
            EdgeNode n = new EdgeNode(e.sourcevertex, e.destinationvertex, value);
            ret.add(n);
        }
        return ret;        
    }
    
    private boolean checkStop(double[][] X, List<EdgeNode> U, List<EdgeNode> V, double e1, double e2)
    {
        return false;
    }
}

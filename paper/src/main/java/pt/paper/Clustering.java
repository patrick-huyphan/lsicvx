/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author patrick_huy
 */
public abstract class Clustering{
    double [][] A;
    double [][] presentMat;
    List<List<Integer>> cluster;
    static final int MAX_LOOP = 50;
    List<Edge> edges;
    int numberOfVertices;
    int numOfFeature;
    double lambda;
    
    public Clustering(double[][] _Matrix, double _lambda) throws IOException
    {
        numberOfVertices = _Matrix.length;
        numOfFeature = _Matrix[0].length;
        lambda = _lambda;
        System.out.println("paper.Clustering.<init>() "+numberOfVertices + " "+numOfFeature);
        A = _Matrix;
        edges = new LinkedList<Edge>();
        cluster = new ArrayList<List<Integer>>();
        FileWriter fw = new FileWriter("sim.txt");
        for (int source = 0; source < numberOfVertices; source++) {
            for (int destination = source + 1; destination < numberOfVertices; destination++) {
                double sim = Vector.cosSim(Matrix.getRow(_Matrix, source), Matrix.getRow(_Matrix, destination));
                if (sim > 0) 
                {
                    Edge edge = new Edge(source, destination, sim );
                    edges.add(edge);

//                    Edge edge2 = new Edge(destination, source, sim );
//                    edges.add(edge2);
//                    System.out.println("("+source+"-"+destination+")"+"Sim: "+sim);// + "- "+edge.weight );

                    fw.append("("+source+"-"+destination+")\t"+"Sim: "+sim +"\n");
                }
            }
        }
        fw.close();
    }

    public abstract void getCluster(FileWriter fw);
    
    public void getPresentMat()
    {
        presentMat = new double[numOfFeature][cluster.size()];//[numberOfVertices];
//        double[][] ret = new double[data[0].length][index.size()];
//        System.out.println("paper.MSTClustering.getPresentMath()");
        for(int j = 0; j< cluster.size(); j++)
        {
            List<Integer> edgesL = cluster.get(j);
            if(edgesL.isEmpty())
            {
//                System.out.println("paper.Clustering.getPresentMat() empty "+j);
                continue;
            }
            int shotestCol= edgesL.get(0);
            double min = 100;
            for(Integer node: edgesL)
            {
                double norm = Vector.norm(A[node-1]);
                if(norm<min)
                {
                    min = norm;
                    shotestCol = node-1;
                }
                
            }
//            System.out.println("\npaper.Paper.getPresentMath() "+j+" "+ shotestCol);
            
            for(int i = 0; i<numOfFeature  ; i++)
            {
                presentMat[i][j] = A[i][shotestCol]; //new Random().nextDouble(); // 
            }
        }
    }
//    private void 
}

//interface IClustering
//{
// void getCluster();
//}
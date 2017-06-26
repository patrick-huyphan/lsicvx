/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author patrick_huy
 */
public class kmean extends Clustering{
    int numOfCluster;
    int[] centroidID;
    List<int[]> ischecked;
    boolean stop = false;
//    int[][] cluster;
    public kmean(double[][] _Matrix, double _lambda, int _numOfCluster, int[] initCentroid) throws IOException {
        super(_Matrix, _lambda);
        numOfCluster = _numOfCluster;
        centroidID = initCentroid;
        ischecked = new ArrayList();
//        centroiD = Matrix.subMatR(A, centroidID);
        
        Arrays.sort(centroidID);
        ischecked.add(centroidID);
        
        HashMap<Integer, Double> maxSim = new HashMap();
//            double[][] maxSim = new double[numOfCluster][1];

        double maxSum = 0;
        for(int i: centroidID)
        {
            for(Edge e: edges)
            {
                if(e.sourcevertex == i)
                    maxSim.put(i, maxSim.get(i)+e.weight);
            }
            maxSum += maxSim.get(i);
        }
         
        
        int loop = 0;
        while(loop<MAX_LOOP)
        {
            int[] cCentroidID = genNewCent();
            
            HashMap<Integer, Double> tmpMaxSim = new HashMap<>();
            double tmpSum =0;
            for(int i: cCentroidID)
            {
                for(Edge e: edges)
                {
                    if(e.sourcevertex == i)
                        tmpMaxSim.put(i, tmpMaxSim.get(i)+e.weight);
                }
                tmpSum+=tmpMaxSim.get(i);
            }
            
            if(tmpSum>maxSum)
                updateCentroid(cCentroidID);
            
            if(stop)
                break;
            loop++;
        }
        
    }

    // calculate sim of point to cluster, already calculate: get from edge list. base on in
    private double[][] distanceM()
    {
        double[][]ret = new double[numOfCluster][A.length];
        for(Edge e: edges)
        {
            if(e.sourcevertex == centroidID[0])
                ret[1][1] = e.weight;
        }
//        distance
        return ret;
    }
    
    private int[] genNewCent()
    {
        int [] newCent = new int[numOfCluster];

        //check if already set;
        for(int[] isC :ischecked)
        {
            
        }
        Arrays.sort(newCent);
        ischecked.add(newCent);
        return newCent;
    }
    // calculate total sim in each cluster, change culuster
    private void updateCentroid(int [] cent)
    {
        for(int i = 0; i< numOfCluster; i++)
            centroidID[i] = cent[i];
    }
    @Override
    public void getCluster(FileWriter fw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}

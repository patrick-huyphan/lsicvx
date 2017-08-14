/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

/**
 *
 * @author patrick_huy
 */
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class KMeans_Ex4a extends Clustering
{
    private int NUM_CLUSTERS ;    // Total clusters.
    private int TOTAL_DATA;      // Total data points.
//    private double [][] dataS;

//    int mat[]={1,4,5,12,22};
    int mat[];
//    int mat[]={6,10,17,18,23};
//    int mat[]={4,5,16,9,20};
    private static ArrayList<Data> dataSet;// = new ArrayList<Data>();
    private static ArrayList<Centroid> centroids;// = new ArrayList<Centroid>();
    
   
    @Override
    public final void getCluster(FileWriter fw)
    {
        cluster = new ArrayList<>();
        
        for(int i = 0; i < NUM_CLUSTERS; i++)
        {
            List<Integer> subCl = new ArrayList<>();
            
            System.out.print("Centroids "+i+":\t");
//            for(int k = 0; k < A[0].length; k++)
//                System.out.print(centroids.get(i).data[k] + "\t");
//            System.out.print("\n");
            
//            System.out.println("Includes:");
            for(int j = 0; j < TOTAL_DATA; j++)
            {
                if(dataSet.get(j).cluster() == i){
                    System.out.print(j+" \t");
                    subCl.add(j+1);
//                    for(int k = 0; k < A[0].length; k++)
//                        System.out.print(dataSet.get(j).data[k] + "\t");
//                    System.out.print("\n");
                }
            }
            System.out.println();
            cluster.add(subCl);
        }
    }
            
    
            
    private void initialize()
    {
        System.out.println("Centroids initialized at:");
        for(int i = 0; i< NUM_CLUSTERS; i++)
        {
            centroids.add(new Centroid(A[mat[i]]));
//            Vector.printV(centroids.get(i).data, "centroids "+ i, true);
        }
//        System.out.print("\n");
    }
    

    private void kMeanCluster()
    {
        final double bigNumber = Math.pow(10, 10);    // some big number that's sure to be larger than our data range.
        double minimum = bigNumber;                   // The minimum value to beat. 
        double distance = 0.0;                        // The current minimum value.
        int sampleNumber = 0;
        int cluster = 0;
        boolean isStillMoving = true;

//        Matrix.printMat(A, "A");
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(dataSet.size() < numberOfVertices)
        {
            double[] a= A[sampleNumber];
            
            minimum = bigNumber;
            Data newData = new Data(a);
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                distance = dist(newData, centroids.get(i));
                if(distance>0 && distance <= minimum){
//                    System.err.println(" dis : " + sampleNumber+"-"+ mat[i]+" "+ distance);
                    minimum = distance;
                    if(distance < minimum)
                        cluster = i;
                    else if(Vector.cosSim(a, centroids.get(i).data)>Vector.cosSim(a, centroids.get(cluster).data))
                        cluster = i;
                                
                }
            }
//            System.err.println(" cluster : "+ sampleNumber+" -> " + mat[cluster]);
            newData.cluster(cluster);
            dataSet.add(newData);
            // calculate new centroids.
//            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
//                int i = cluster;
                int newC = cluster;
                minimum = bigNumber;
                for(int j = 0; j < dataSet.size(); j++)
                {
                    if(dataSet.get(j).cluster() == cluster){
                      distance = dist(dataSet.get(j), centroids.get(cluster));
                      if(distance>0 && distance <= minimum){
//                          System.err.println(distance+" "+minimum+" "+ newC);
                            minimum = distance;
//                            if(distance < minimum)
//                                newC = j;
//                            else if(Vector.cosSim(dataSet.get(j).data, dataSet.get(newC).data)>Vector.cosSim(dataSet.get(j).data, centroids.get(cluster).data))
                                newC = j;
                            
                        }
                    }
                }
                if(cluster!= newC)
                {
//                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() up date cent " + cluster  +"-> "+ newC);
                    centroids.get(cluster).data = dataSet.get(newC).data;
                }
            }
            sampleNumber++;
        }
        
        // Now, keep shifting centroids until equilibrium occurs.
        int run = 0;
        while(isStillMoving)
        {
            System.out.println("paper.KMeans_Ex4a.kMeanCluster()..."+run);
            if(run++>9)
                break;
            // calculate new centroids.
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
//                Vector.printV(centroids.get(i).data, " old i ", isStillMoving);
                int newC = i;
                minimum = bigNumber;
                for(int j = 0; j < dataSet.size(); j++)
                {
                    if(dataSet.get(j).cluster() == i){
                    distance = dist(dataSet.get(j), centroids.get(i));
                      if(distance>0 && distance <= minimum){
//                          System.err.println(distance+" "+minimum+" "+ newC);
                            minimum = distance;
//                            if(distance < minimum)
//                                newC = j;
//                            else if(Vector.cosSim(dataSet.get(j).data, dataSet.get(newC).data)>Vector.cosSim(dataSet.get(j).data, centroids.get(cluster).data))
                                newC = j;
//                            newC = j;
                        }
                    }
                }
                if(i!= newC)
                {
//                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() up date cent " + i  +"-> "+ newC);
                    centroids.get(i).data = dataSet.get(newC).data;
                }
            }
            
            // Assign all data to the new centroids
            isStillMoving = false;
            
            for(int i = 0; i < dataSet.size(); i++)
            {
                Data tempData = dataSet.get(i);
                minimum = bigNumber;
                for(int j = 0; j < NUM_CLUSTERS; j++)
                {
                    distance = dist(tempData, centroids.get(j));
                    if((distance>0) && (distance <= minimum)){
//                        System.out.println("paper.KMeans_Ex4a.kMeanCluster() "+ i+"-"+j+" "+ distance);
                        minimum = distance;
//                        cluster = j;
                            if(distance < minimum)
                                cluster = j;
                            else if(Vector.cosSim(dataSet.get(j).data, centroids.get(j).data)>Vector.cosSim(dataSet.get(j).data, centroids.get(cluster).data))
                                cluster = j;
                    }
                }
                
//                System.out.println("paper.KMeans_Ex4a.kMeanCluster() new centroid "+ cluster+ " " +tempData.cluster());
//                tempData.cluster(cluster);
                if(tempData.cluster() != cluster){
//                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() new centroid "+ cluster+ " " +tempData.cluster());
//                    dataSet.get(i).cluster(cluster);
                    isStillMoving = true;
                }
            }
        }
//        return;
    }
    
    private void kMeanCluster2()
    {
        /*
         - get 5 node
         - loop:
            + check diss |.| 5 node and other nodes.
            + min dis is cluster for other node
            + update cluster of other node in 5 node: in 5 node: has max sim with cluster.
        */
        
        final double bigNumber = Math.pow(10, 10);    // some big number that's sure to be larger than our data range.
        double minimum = bigNumber;                   // The minimum value to beat. 
        double distance = 0.0;                        // The current minimum value.
        int sampleNumber = 0;
        int cluster = 0;
        boolean isStillMoving = true;

//        Matrix.printMat(A, "A");
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(dataSet.size() < TOTAL_DATA)
        {
            double[] a= A[sampleNumber];
            
            minimum = bigNumber;
            Data newData = new Data(a);
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                distance = dist(newData, centroids.get(i));//Vector.cosSim(newData.data, centroids.get(i).data);//
//                System.err.println(" dis : " + sampleNumber+"-"+ mat[i]+" "+ distance);
                if(distance <= minimum){
                    minimum = distance;
                    cluster = i;
                }
            }
//            System.err.println(" cluster : "+ sampleNumber+" -> " + mat[cluster]);
            newData.cluster(cluster);
            dataSet.add(newData);
            // calculate new centroids.
//            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
//                int total[] = new int[numOfFeature];
//                int totalInCluster = 0;
                int i = cluster;
//                if(i == cluster)
                {
//                double dis = 0;
                int newC = i;
//                distance = 0;
                minimum = bigNumber;
                for(int j = 0; j < dataSet.size(); j++)
                {
                    if(dataSet.get(j).cluster() == i){
                      distance = dist(dataSet.get(j), centroids.get(i));
                      if(distance <= minimum){
//                          System.err.println(distance+" "+minimum+" "+ newC);
                            minimum = distance;
                            newC = i;
                        }
//                        for(int k = 0; k < numOfFeature; k++)
//                            total[k] += dataSet.get(j).data[k];
//                        totalInCluster++;
                    }
                }
                if(i!= newC)
                {
                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() up date cent " + i  +"-> "+ newC);
                    centroids.get(i).data = dataSet.get(newC).data;
                }
                    
//                if(totalInCluster > 0){
//                    
////                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() upadate center");
////                    Vector.printV(centroids.get(i).data, " 1 old i "+i, isStillMoving);
//                    for(int k = 0; k < numOfFeature; k++)
//                        centroids.get(i).data[k] =(total[k] / totalInCluster);
////                    Vector.printV(centroids.get(i).data, " 1 new i "+i, isStillMoving);
//                }
                }
            }
            sampleNumber++;
        }
        
        // Now, keep shifting centroids until equilibrium occurs.
        while(isStillMoving)
        {
            System.out.println("paper.KMeans_Ex4a.kMeanCluster()...");
            // calculate new centroids.
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
//                int[] total = new int[numOfFeature];
//                int totalInCluster = 0;
//                Vector.printV(centroids.get(i).data, " old i ", isStillMoving);
                int newC = i;
//                distance = 0;
                minimum = bigNumber;
                for(int j = 0; j < dataSet.size(); j++)
                {
                    if(dataSet.get(j).cluster() == i){
                    distance = dist(dataSet.get(j), centroids.get(i));
                      if(distance <= minimum){
//                          System.err.println(distance+" "+minimum+" "+ newC);
                            minimum = distance;
                            newC = i;
                        }
//                        System.out.println("paper.KMeans_Ex4a.kMeanCluster() "+j+" "+i);
//                        for(int k = 0; k < numOfFeature; k++)
//                            total[k] += dataSet.get(j).data[k];
//                        totalInCluster++;
                    }
                }
                if(i!= newC)
                {
                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() up date cent " + i  +"-> "+ newC);
                    centroids.get(i).data = dataSet.get(newC).data;
                }
//                if(totalInCluster > 0){
//                    for(int k = 0; k < numOfFeature; k++)
//                    centroids.get(i).data[k] = (total[k] / totalInCluster);
//                }
//                Vector.printV(centroids.get(i).data, " new i ", isStillMoving);
            }
            
            // Assign all data to the new centroids
            isStillMoving = false;
            
            for(int i = 0; i < dataSet.size(); i++)
            {
                Data tempData = dataSet.get(i);
                minimum = bigNumber;
                for(int j = 0; j < NUM_CLUSTERS; j++)
                {
                    distance = dist(tempData, centroids.get(j));//Vector.cosSim(tempData.data, centroids.get(j).data);// 
//                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() "+ i+"-"+j+" "+ distance);
                    if(distance <= minimum){
                        minimum = distance;
                        cluster = j;
                    }
                }
//                System.out.println("paper.KMeans_Ex4a.kMeanCluster() new centroid "+ cluster+ " " +tempData.cluster());
                tempData.cluster(cluster);
                if(tempData.cluster() != cluster){
                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() new centroid "+ cluster+ " " +tempData.cluster());
                    tempData.cluster(cluster);
                    isStillMoving = true;
                }
            }
        }
//        return;        
    }
    /**
     * // Calculate Euclidean distance.
     * @param d - Data object.
     * @param c - Centroid object.
     * @return - double value.
     */
    private double dist(Data d, Centroid c)
    {

        double value =0;
        for(int k = 0; k < d.data.length; k++)
            value += Math.pow((c.data[k] - d.data[k]), 2);
        
        return Math.sqrt(value);
                    
    }

    
    private static class Data
    {
        double[] data;
        private int mCluster = 0;
        
        public Data(double [] _data)
        {
            data = new double[_data.length];
            System.arraycopy(_data, 0, data, 0, _data.length);
        }
        
        public void cluster(int clusterNumber)
        {
            this.mCluster = clusterNumber;
            return;
        }
        
        public int cluster()
        {
            return this.mCluster;
        }
    }
    
    private static class Centroid
    {
        double [] data;
        
        public Centroid( double [] _data)
        {
            data = new double[_data.length];
            System.arraycopy(_data, 0, data, 0, _data.length);
        }
    }
    
    public KMeans_Ex4a(double [][] _data, double _lam, int [] initC ,int numOfC)throws IOException
    {
        super(_data, _lam);

        NUM_CLUSTERS = numOfC;
        TOTAL_DATA = numberOfVertices;// _data.length;
        dataSet = new ArrayList<>();
        centroids = new ArrayList<>();
        mat = initC;
        
        initialize();
        kMeanCluster();
        
        FileWriter fw = new FileWriter("cluster.txt");
        getCluster(fw);
        fw.close();
        
        getPresentMat();
    }
//    public static void main(String[] args) throws IOException {
//        double SAMPLES[][] = new double[][] {{1.0, 1.0, 1.0}, 
//                                            {1.5, 2.0, 0.5}, 
//                                            {3.0, 4.0, 2.0}, 
//                                            {5.0, 7.0, 1.5}, 
//                                            {3.5, 5.0, 0.5}, 
//                                            {4.5, 5.0, 1.0}, 
//                                            {3.5, 4.5, 1.5}};
//        new KMeans_Ex4a(SAMPLES, 0, 2);
//    }
}
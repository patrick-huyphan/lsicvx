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
    int NUM_CLUSTERS ;    // Total clusters.
    int TOTAL_DATA;      // Total data points.
//    private double [][] dataS;

//    int mat[]={1,4,5,12,22};
    int mat[];
//    int mat[]={6,10,17,18,23};
//    int mat[]={4,5,16,9,20};
    private static ArrayList<Data> dataSet;// = new ArrayList<Data>();
    private static ArrayList<Centroid> centroids;// = new ArrayList<Centroid>();
    int centroidList[];
   
    @Override
    public final void getCluster(FileWriter fw)
    {
        cluster = new ArrayList<>();
        
//        for(int i = 0; i < NUM_CLUSTERS; i++)
        for(int c: centroidList)    
        {
            List<Integer> subCl = new ArrayList<>();
            
            System.out.print("Centroids "+c+":\t");
//            for(int k = 0; k < A[0].length; k++)
//                System.out.print(centroids.get(i).data[k] + "\t");
//            System.out.print("\n");
            
//            System.out.println("Includes:");
            for(int j = 0; j < TOTAL_DATA; j++)
            {
                if(dataSet.get(j).cluster() == c){
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
        
        for(int i = 0; i< NUM_CLUSTERS; i++)
        {
//            System.out.println("Centroids initialized at: "+i);
            centroids.add(new Centroid(A[mat[i]]));
//            Vector.printV(centroids.get(i).data, "centroids "+ i, true);
        }
//        System.out.print("\n");
    }
    /**
     * - calculate distance for all points with init centroids
     * - check centroids
     * - update center, and check when still not finish
     */
    

    private void kMeanCluster()
    {
        int sampleNumber = 0;
        int cluster;
        boolean isStillMoving = true;

//        Matrix.printMat(A, "A");
        
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(dataSet.size() < numberOfVertices)
        {
            double[] a= A[sampleNumber];
            
//            minimum = bigNumber;
            Data newData = new Data(a);
            cluster = 0;
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                if(Vector.cosSim(a, centroids.get(i).data)>Vector.cosSim(a, centroids.get(cluster).data))
                        cluster = i;
            }
//            System.err.println(" Set nearest centroid: "+ sampleNumber+" -> " + mat[cluster]);
            newData.cluster(cluster);
            dataSet.add(newData);
            // calculate new centroids.
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
                int totalP = 0;
                double totalDis[] = new double[numOfFeature];
                
                // calc sum of distance in cluster 
                for(int j = 0; j < dataSet.size(); j++)
                {
//                    System.err.println(j+" Checking... "+cluster+" "+dataSet.get(j).cluster());
                    if(dataSet.get(j).cluster() == i){
                        for(int t= 0; t< numOfFeature; t++)
                            totalDis[t] += dataSet.get(j).data[t];
                        totalP++;
                    }
                }
                //reset centroid point with avr point in cluster
                if(totalP>0)    
                {
//                    System.out.println("update new centroid " + cluster  +"-> "+ newC);
                    centroids.get(i).data = Vector.scale(totalDis, 1./totalP);
                }
            }
            sampleNumber++;
        }
        
        // Now, keep shifting centroids until equilibrium occurs.
        int run = 0;
        while(isStillMoving)
        {
            System.out.println("paper.KMeans_Ex4a.kMeanCluster()..."+run);
            if(run++>29)
                break;
            // re-calculate new centroids for all node has centroid i.
            for(int i = 0; i < NUM_CLUSTERS; i++)
            {
//                Vector.printV(centroids.get(i).data, " old i ", isStillMoving);
                int totalP = 0;
                double totalDis[] = new double[numOfFeature];
                
                for(int j = 0; j < dataSet.size(); j++)
                {
                    if(dataSet.get(j).cluster() == i){
                        for(int t= 0; t< numOfFeature; t++)
                            totalDis[t] += dataSet.get(j).data[t];
                        totalP++;
                    }
                }
                if(totalP>0)    
                {
//                    System.out.println("update new centroid " + cluster  +"-> "+ newC);
                    centroids.get(i).data = Vector.scale(totalDis, 1./totalP);
                }
            }
            
            // Assign all data to the new centroids
            isStillMoving = false;
            
            for(int i = 0; i < dataSet.size(); i++)
            {
                Data tempData = dataSet.get(i);
                //check distance in new centroids
                cluster = dataSet.get(i).cluster();
                for(int j = 0; j < NUM_CLUSTERS; j++)
                {
                    if(Vector.cosSim(dataSet.get(i).data, centroids.get(j).data)>Vector.cosSim(dataSet.get(i).data, centroids.get(cluster).data))
                        cluster = j;
                }
                
//                System.out.println("paper.KMeans_Ex4a.kMeanCluster() new centroid "+ cluster+ " " +tempData.cluster());
                //  if change cluster, still moving untill centroid not change
                if(dataSet.get(i).cluster() != cluster){
                    System.out.println("paper.KMeans_Ex4a.kMeanCluster() new centroid "+ cluster+ " " +tempData.cluster());
                    dataSet.get(i).cluster(cluster);
                    isStillMoving = true;
                }
                else
                {
                    System.out.println("pt.paper.KMeans_Ex4a.kMeanCluster() stop "+run);
                }
            }
        }

        for(Centroid c: centroids)
        {
//            Vector.printV(c.data, " centroid data", true);
            double min = -10;
            
            for(int i = 0; i< dataSet.size(); i++)
            {
                double sim = Vector.cosSim(c.data, dataSet.get(i).data);
                if(sim>min)
                {
                    min = sim;
                    centroidList[centroids.indexOf(c)] = i;
                }
            }
            System.out.println("re-cal centroid "+centroids.indexOf(c)+" -> "+centroidList[centroids.indexOf(c)]);
        }
        for(Data d: dataSet)
        {
            d.mCluster = centroidList[d.mCluster];
        }
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
    
    /**
     * 
     * @param _data
     * @param _lam
     * @param initC
     * @param numOfC
     * @throws IOException 
     */
    
    public KMeans_Ex4a(double [][] _data, double _lam ,int numOfC, int [] initC)throws IOException
    {
        super(_data, _lam);

        NUM_CLUSTERS = numOfC;
        TOTAL_DATA = numberOfVertices;// _data.length;
        dataSet = new ArrayList<>();
        centroids = new ArrayList<>();
        mat = initC;
        centroidList = new int[numOfC];
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
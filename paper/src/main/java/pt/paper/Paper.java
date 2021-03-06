/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import com.sun.org.apache.regexp.internal.REUtil;
import java.io.BufferedReader;
import pt.DocTermBuilder.ReadingMultipleFile;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 *
 * @author patrick_huy
 */
public class Paper {
//    double[][] Q= {{}};
    public static void PaperRuner(double[][] D,double[][] Q, int top, int starti, int endi, int startj, int endj, double slambda, double st, double sti, boolean hl,  int loop, boolean logSave, int stepSave,String output
            , boolean runADMM, boolean isADMM,double admmlambda, double admmRho, boolean isOrth, int maxTimeADMM, double cdlambda, String logFacebook, String facemess) throws IOException, Exception {

//        Matrix.printMat(D, "D init");
//        Matrix.printMat(Q, "Q init");

        double[][] echelon = Matrix.echelon(D);//
        Clustering clt = null;
        LSI lsi;
//        CSVFile.saveMatrixData("echelon", echelon, "echelon");

//        double[][] echelon = CSVFile.readMatrixData("echelon.csv");//

////        printMat(echelon, false, "echelong");
//
        double[][] termDocMat = Matrix.Transpose(echelon);

//        if(D.length == 94)
//        clt = new KMeans_Ex4a(termDocMat, 0, 28, new int[]{88, 2, 16, 30,21,24,26,84,34,35,40, 45,58,49,50,54,55,56,67,71,75, 76, 78, 80,81,89 ,90, 92 });

        for(int i = starti; i<endi; i++)
        {
            for(int j = startj; j< endj; j++)
            {
                long start_timeF = System.nanoTime();
                double lambda = slambda*i;
                double t = st+sti*j;
                System.out.println(i+" SCC start "+lambda);
                long start_timeSCC = System.nanoTime();
                clt = new ASCC(termDocMat, lambda, 0.05, 0.01, 1e-5, 1e-5, hl,loop, t, logSave, stepSave,output);
                long end_timeSCC = System.nanoTime();
                //        clt = new KMeans_Ex4a(termDocMat, 0, 24, new int[]{88, 2, 16, 30,21,24,26,84,34,35,40,58,49,50,54,55,56,67,71,75,80,81,90, 92 });
                if(runADMM)
                {
            //        SCC scc = new SCC(termDocMat, 3.5, 0.15, 0.25, 1e-3, 1e-3);
            //        new SCC(scc.X, 3.5, 0.15, 0.25, 1e-3, 1e-3);

            //        clt = new kmean(termDocMat, 0.15, 24, new int[]{88, 2, 16, 30,21,24,26,84,34,35,40,58,49,50,54,55,56,67,71,75,80,81,90, 92 });

            //        clt = new MSTClustering(termDocMat,0.28);

            //        FileWriter fw = new FileWriter("mst");
            //        List<List<Integer>> cluster = mst.kruskalAlgorithm_getCluster(0.42, fw);
            //        fw.close();


            ////        List<Edge> E = buildE(termDocMat);
            ////        int[][] subCluster = clusteringMST(termDocMat, E, 0);
            //
                    double[][] B = clt.presentMat;// getPresentMath(cluster); //
            //////        CSVFile.saveMatrixData("B", B, "B");
            //////        double[][] B = CSVFile.readMatrixData("B.csv");//
            //        Matrix.printMat(B, "B");
            //////        Matrix.printMat(D, "D");

//            if(!isADMM)
//            {
//                long start_time = System.nanoTime();
//                    lsi = new CDNew(D, B, cdlambda);
//                long end_time = System.nanoTime();
//            }
            //        double[][] X= Matrix.Transpose(lsi.X);
            //        double[][] A = Matrix.mul(D, X); //n*k
            //        Matrix.printMat(A, "LSI");
            //        double[][] Q2 = Matrix.mul(Q, X);
            //        double[][] ret = Matrix.sim(A, Q2);
            //        Matrix.printMat(Matrix.Transpose(ret), "query 1");
            ////
//            else
            {
                for (int ia = 0; ia< maxTimeADMM; ia++)
                {
                    double ltmp = admmlambda+ ia*0.1;
                    System.out.println("pt.paper.Paper.PaperRuner() lambda = "+ ltmp);
                    if(!isADMM)
                    {
                        long start_time = System.nanoTime();
                            lsi = new CDNew(D, B, cdlambda);
                        long end_time = System.nanoTime();
                        long time = end_time - start_time;
                        System.out.println("CD pt.paper.Paper.PaperRuner() time "+ time);
                    }
            //        double[][] X= Matrix.Transpose(lsi.X);
            //        double[][] A = Matrix.mul(D, X); //n*k
            //        Matrix.printMat(A, "LSI");
            //        double[][] Q2 = Matrix.mul(Q, X);
            //        double[][] ret = Matrix.sim(A, Q2);
            //        Matrix.printMat(Matrix.Transpose(ret), "query 1");
            ////
                    else
                    {
                        long start_time = System.nanoTime();
                        lsi = new ADMM(D, B, admmRho, ltmp, 0.005, 0.0001, isOrth);
                        long end_time = System.nanoTime();
                        long time = end_time - start_time;
                        System.out.println(" ADMM pt.paper.Paper.PaperRuner() time "+ time);
                    }
                    double[][] X2= Matrix.Transpose(lsi.X);

            //        for(int i = 0; i< X2.length; i++)
            //        {
            //            System.out.println(i+ ": "+ Vector.norm(X2[i]));
            //        }

            //        CSVFile.saveMatrixData("ADMM", X2, "X2");
            //        Matrix.printMat(X2, "Projection");
                    double[][] A2 = Matrix.mul(D, X2); //kn = km x mn
            //        CSVFile.saveMatrixData("LATEN", A2, "A2");
            //        Matrix.printMat(A2, "New mat");
                    double[][] Q22 = Matrix.mul(Q, X2);

            //        Matrix.printMat(Q22, "New mat");
                    double[][] ret2 = Matrix.Transpose(Matrix.sim(A2, Q22));
            //        Matrix.printMat(ret2, "query result");

            //        for(int i = 0; i< Q22.length; i++)
            //        {
            //            System.out.println(i+ ": "+ Vector.norm(X2[i])+" - "+Vector.norm(A2[i])+" - "+Vector.norm(Q22[i])+" - "+Vector.norm(ret2[i]));
            //        }

                    List<List<Edge>> res = new ArrayList<>();

                    for(int i2 = 0; i2< ret2[0].length; i2++)
                    {
                        List<Edge> e = new ArrayList<>();
                        for(int j2 = 0; j2< ret2.length; j2++)
                        {
                            e.add(new Edge(i2,j2, ret2[j2][i2]));
                        }
            //            Collections.sort(e);
                        e.sort(new Comparator<Edge>() {
                            @Override
                            public int compare(Edge o1, Edge o2) {
                                   if(o1.weight>o2.weight) return -1;
                                   else if(o1.weight<o2.weight) return 1;
                                   else return 0;
                            }
                        });
                        List<Edge> e2 = new ArrayList<>();
                        for(int k = 0; k<top; k++)
                        {
                            e2.add(e.get(k));
                        }
                        res.add(e2);
                    }
                    
                    //List<String> nodeInfo = readLogFace(logFacebook, res);
                    for(List<Edge> e : res)
                    {
                        System.out.println("pt.paper.Paper.PaperRuner()");
                        for(Edge i2: e)
                        {
                            System.out.println(i2.scr+" "+i2.dst+":\t"+i2.weight);// +"\t"+ nodeInfo.get(i2.dst));
                            //BufferedReader rd1 = new BufferedReader(new FileReader( facemess+"\\"+nodeInfo.get(i2.dst)+".txt"));
                            //String s="";
                            //while ((s = rd1.readLine()) != null) {
                            //    System.out.println(s);
                            //}
                            //rd1.close();
                        }
                    }
        //          clt = new AMA(termDocMat, 2.4, 1e-3, 0.85, 5e-4, 5e-4);
        //          Matrix.printMat(clt.presentMat, "AMA");
                }
                long end_timef = System.nanoTime();
                long time = end_timef - start_timeF;
                System.out.println("pt.paper.Paper.PaperRuner() SCC+ "+( end_timeSCC-start_timeSCC));
                System.out.println("pt.paper.Paper.PaperRuner() time+ "+time);
            }
                }
            }
        }

    }

    public static List<String> readLogFace(String dirName, List<List<Edge>> queryRetList) throws FileNotFoundException, IOException
    {
//        HashMap<Integer, String> ret = new HashMap();
        BufferedReader rd1 = new BufferedReader(new FileReader(dirName +"/loguse_2.txt"));
        String s ="", st [];
        List<Integer> docID = new LinkedList<>();
        while ((s = rd1.readLine()) != null) {
            st = s.split(" ");
            docID.add(Integer.parseInt(st[0]));
        }
        rd1.close();

        rd1 = new BufferedReader(new FileReader(dirName +"/log+Data1.txt"));
        List<String> docID2 = new LinkedList<>();
        while ((s = rd1.readLine()) != null) {
            st = s.split(" ");
            if(docID.contains(st[2]))
                docID2.add(st[0]);
        }
        rd1.close();
        
        return docID2;
    }
    public static void PaperRuner(double[][] D, String echelonFile) throws IOException {
//        double[][] echelon = Matrix.echelon(D);//
//        CSVFile.saveMatrixData("echelon", echelon, "echelon");

        double[][] echelon = CSVFile.readMatrixData("echelon.csv");//

////        printMat(echelon, false, "echelong");
//
        double[][] termDocMat = Matrix.Transpose(echelon);

//        new KMeans_Ex4a(termDocMat, 0, 5);

        Clustering clt = new MSTClustering(termDocMat,0.5);
//        FileWriter fw = new FileWriter("mst");
//        List<List<Integer>> cluster = mst.kruskalAlgorithm_getCluster(0.42, fw);
//        fw.close();


////        List<Edge> E = buildE(termDocMat);
////        int[][] subCluster = clusteringMST(termDocMat, E, 0);
//
        double[][] B = clt.presentMat;// getPresentMath(cluster); //
        CSVFile.saveMatrixData("B", B, "B");
//        double[][] B = CSVFile.readMatrixData("B.csv");//
//        Matrix.printMat(B, "B");

        LSI lsi;
        lsi = new CD(D, B, 0.005);
        double[][] X= lsi.X;
        double[][] A = Matrix.mul(D, Matrix.Transpose(X));
        Matrix.printMat(A, "LSI");

//        lsi = new ADMM(D, B, 1e-2, 80e-2, 1e-3, 1e-3);
//        X= lsi.X;
//        A = Matrix.mul(D, Matrix.Transpose(X));
//        Matrix.printMat(A, "LSI2");
    }
    /*
		Calculate sim from row vector, create list egde base on sim of row data, weighEgde = sim.
     */



    /*
	return cluster and vecID
     */
//    private int[][] clusteringMST(double A[][], List<Edge> E, double alpha) {
//        int[][] ret = new int[A.length][A.length];
//
//        return ret;
//    }
//
//    private int[][] Kruscal(double A[][], List<Edge> E, double alpha) {
//        int[][] ret = new int[A.length][A.length];
//
//        return ret;
//    }
    /*
    TODO:
            return matrix with term reduce. index is cluster of term,
            get shorted vector in subCluster as presentation vector
     */
//    private double[][] getPresentMath(int[][] index, double[][] A) {
//        double[][] ret = new double[index.length][A[0].length];
//        for(int i = 0; i< ret.length ; i++)
//            for(int j = 0; j< ret[0].length; j++)
//            {
//                ret[i][j] = new Random().nextDouble();
//            }
//        return ret;
//    }


    public static void PaperRuner(double[][] D) throws IOException {
        double[][] echelon = Matrix.echelon(D);//
        CSVFile.saveMatrixData("echelon", echelon, "echelon");

//        double[][] echelon = CSVFile.readMatrixData("echelon.csv");//

////        printMat(echelon, false, "echelong");
//
        double[][] termDocMat = Matrix.Transpose(echelon);

        Clustering clt = new MSTClustering(termDocMat,0.5);
//        FileWriter fw = new FileWriter("mst");
//        List<List<Integer>> cluster = mst.kruskalAlgorithm_getCluster(0.42, fw);
//        fw.close();

////        List<Edge> E = buildE(termDocMat);
////        int[][] subCluster = clusteringMST(termDocMat, E, 0);
//
        double[][] B = clt.presentMat;// getPresentMath(cluster); //
        CSVFile.saveMatrixData("B", B, "B");
//        double[][] B = CSVFile.readMatrixData("B.csv");//
//        Matrix.printMat(B, "B");

        LSI lsi;
        lsi = new CD(D, B, 0.005);
        double[][] X= Matrix.Transpose(lsi.X);
        double[][] A = Matrix.mul(D, X);
        Matrix.printMat(A, "LSI CD");

//        lsi = new ADMM(D, B, 1e-2, 80e-2, 1e-3, 1e-3);
//        X= lsi.X;
//        A = Matrix.mul(D, Matrix.Transpose(X));
//        Matrix.printMat(A, "LSI2");

        lsi = new ADMM(D, B, 4e-2, 70e-2, 1e-2, 1e-4, true);
        double[][] X2= Matrix.Transpose(lsi.X);
        Matrix.printMat(X2, "LSI");
        double[][] A2 = Matrix.mul(D, X2); //kn = km x mn
        Matrix.printMat(A2, "LSI2");
    }



}

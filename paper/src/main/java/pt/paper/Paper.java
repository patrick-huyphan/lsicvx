/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import pt.DocTermBuilder.ReadingMultipleFile;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author patrick_huy
 */
public class Paper {
    double[][] Q= {{}}; 
    public Paper(double[][] D,double[][] Q) throws IOException {
        
//        Matrix.printMat(D, "D init");
//        Matrix.printMat(Q, "Q init");
        
        double[][] echelon = Matrix.echelon(D);//
        Clustering clt;
        LSI lsi;
//        CSVFile.saveMatrixData("echelon", echelon, "echelon");

//        double[][] echelon = CSVFile.readMatrixData("echelon.csv");//

////        printMat(echelon, false, "echelong");
//
        double[][] termDocMat = Matrix.Transpose(echelon);

                

//        new KMeans_Ex4a(termDocMat, 0, 5);
        
        clt = new SCC(termDocMat, 5, 0.05, 0.01, 1e-3, 1e-3);
//        SCC scc = new SCC(termDocMat, 3.5, 0.15, 0.25, 1e-3, 1e-3);
//        new SCC(scc.X, 3.5, 0.15, 0.25, 1e-3, 1e-3);

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
//        
//        lsi = new CD(D, B, 0.005);
//        double[][] X= Matrix.Transpose(lsi.X);
//        double[][] A = Matrix.mul(D, X); //n*k
//        Matrix.printMat(A, "LSI");
//        double[][] Q2 = Matrix.mul(Q, X); 
//        double[][] ret = Matrix.sim(A, Q2);
//        Matrix.printMat(Matrix.Transpose(ret), "query 1");
////        
        lsi = new ADMM(D, B, 0.04, 0.8, 0.005, 0.0001);
        double[][] X2= Matrix.Transpose(lsi.X);
//        CSVFile.saveMatrixData("ADMM", X2, "X2");
//        Matrix.printMat(X2, "Projection");
        double[][] A2 = Matrix.mul(D, X2); //kn = km x mn
//        CSVFile.saveMatrixData("LATEN", A2, "A2");
        Matrix.printMat(A2, "New mat");
        double[][] Q22 = Matrix.mul(Q, X2);
        Matrix.printMat(Q22, "New mat");
        double[][] ret2 = Matrix.sim(A2, Q22);
        Matrix.printMat(Matrix.Transpose(ret2), "query result");

//          clt = new AMA(termDocMat, 2.4, 1e-3, 0.85, 5e-4, 5e-4);
//          Matrix.printMat(clt.presentMat, "AMA");
    }

    public Paper(double[][] D, String echelonFile) throws IOException {
//        double[][] echelon = Matrix.echelon(D);//
//        CSVFile.saveMatrixData("echelon", echelon, "echelon");

        double[][] echelon = CSVFile.readMatrixData("echelon.csv");//

////        printMat(echelon, false, "echelong");
//
        double[][] termDocMat = Matrix.Transpose(echelon);

        new KMeans_Ex4a(termDocMat, 0, 5);
        
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
    

    public Paper(double[][] D) throws IOException {
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

        lsi = new ADMM(D, B, 4e-2, 70e-2, 1e-2, 1e-4);
        double[][] X2= Matrix.Transpose(lsi.X);
        Matrix.printMat(X2, "LSI");
        double[][] A2 = Matrix.mul(D, X2); //kn = km x mn
        Matrix.printMat(A2, "LSI2");
    }
    private static double[][] readDocData() throws IOException
    {
        File fileut;
        FileWriter fw;

        String fileOUname = "Output/" + Long.toString(System.currentTimeMillis()) + "/"; //
        String fileInname = "data/text/"; //
        new File(fileOUname).mkdir();
        String nfilename = fileOUname+ "/log.txt";

        fileut = new File(nfilename);
        fw = new FileWriter(fileut);

//        int [][]data2 = ReadingMultipleFile.calcCoundMat1st(fileOUname,5);
        int [][]data2 = ReadingMultipleFile.calcCoundMat2ndd(fileInname,fileOUname,5);
        ReadingMultipleFile xx2 = ReadingMultipleFile.buildMat(3, data2);//.new ReadingMultipleFile(inputdirName);
        CSVFile.saveMatrixData(fileOUname+"/data.csv", xx2.tdidf, "data2");
        fw.close();
//        Matrix.printMat(xx2.tdidf, "xx2.tdidf");
        return xx2.tdidf;
    }
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        double[][] DQ = Matrix.int2double(ReadData.readDataTestN());
//        Matrix.printMat(DQ, "DQ init");
        double[][] D = Matrix.subMat(DQ, 0, 26, 0, DQ[0].length);
        double[][] Q = Matrix.subMat(DQ, 26, 3, 0, DQ[0].length);
        
//        double[][] docTerm = readDocData();//
        double[][] docTerm = CSVFile.readMatrixData("data.csv");
        double[][] testQ = new double[5][docTerm[0].length];
        //printMat(docTerm, false,"docTerm");


        Paper run = new Paper(docTerm,testQ);
      
//    Paper run = new Paper(D,Q);

//      Paper run = new Paper(docTerm,"echelon.csv");
    }

}
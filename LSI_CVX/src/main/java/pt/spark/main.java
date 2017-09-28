package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;


import java.util.LinkedList;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.util.Comparator;
import org.apache.spark.mllib.linalg.Matrix;


public class main {

    /**
     * This is the entry point when the task is called from command line with
     * spark-submit.sh. See {
     *
     * @see http://spark.apache.org/docs/latest/submitting-applications.html}
     * args[0]: input doc args[1]: output args[2]: input query
     * 
     * TODO:
     * - SCC and ADMM give diff result with single mode????
     */
    public static void main(String[] args) throws IOException, Exception {
//        checkArgument(args.length > 0, "Please provide the path of input file as first parameter.");

        

        String s;
        String dataFile = "";
        // num of query
        int numofQ = 0;// = Integer.parseInt(args[1]);
        // start loop i
        int si = 0;// = Integer.parseInt(args[2]);
        int ei = 0;// = Integer.parseInt(args[3]);
        // start loop j
        int sj = 0;// = Integer.parseInt(args[4]);
        int ej = 0;// = Integer.parseInt(args[5]);
        
        int loop = 0;// = Integer.parseInt(args[6]);
        int stepSave = 0;// = Integer.parseInt(args[7]);
        boolean logSave = false;// = (Integer.parseInt(args[8]) == 1);
        boolean runADMM =false;// = (Integer.parseInt(args[9]) == 1);
        
        double slambda = 0.2;
        double st = 1;
        double sti = 0.0025;
        
        String ouputdir ="";// args[1]+"/"+System.currentTimeMillis();
//        int numofQ = Integer.parseInt(args[2]);
        boolean HL = false;//(Integer.parseInt(args[3]) ==1);
        boolean orthognomal = false;//(Integer.parseInt(args[4]) ==1);
//        int loop = Integer.parseInt(args[5]);
        double lambdaSCC = 0.2;//Double.parseDouble(args[6]);
        
        double rho = 0.04;
        double lambdaADMM = 0.8;
        double e1 = 0.005; 
        double e2 = 0.0001; 
        
        BufferedReader br = new BufferedReader(new FileReader("config.txt"));
        while ((s = br.readLine()) != null) {
            String value[] = s.split(" : ");
            if(value[0].contains("input"))
            {              
                dataFile = value[1];
                System.out.println("input: "+dataFile);
            }
            if(value[0].contains("output"))
            {              
                ouputdir = value[1];
                System.out.println("output : "+ouputdir);
            }
            if(value[0].contains("numq"))
            {
                numofQ = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("numq: "+numofQ);
            }
            if(value[0].contains("si"))
            {
                si = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("start i: "+si);
            }            
            if(value[0].contains("ei"))
            {
                ei = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("end i: "+ei);
            }
            if(value[0].contains("sj"))
            {
                sj = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("start j: "+sj);
            }
            if(value[0].contains("ej"))
            {
                ej = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("end j: "+ej);
            }
            if(value[0].contains("maxloop"))
            {
                loop = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("maxloop: "+loop);
            }
            if(value[0].contains("stepSave"))
            {
                stepSave = Integer.parseInt(value[1].replaceAll(" ", ""));
                System.out.println("stepSave: "+stepSave);
            }
            if(value[0].contains("slambda"))
            {
                slambda = Double.parseDouble(value[1].replaceAll(" ", ""));
                System.out.println("slambda: "+slambda);
            }
            if(value[0].contains("stlambda"))
            {
                st = Double.parseDouble(value[1].replaceAll(" ", ""));
                System.out.println("stlambda: "+st);
            }
            if(value[0].contains("sti"))
            {
                sti = Double.parseDouble(value[1].replaceAll(" ", ""));
                System.out.println("sti: "+sti);
            }
            if(value[0].contains("saveLog"))
            {
                logSave = (Integer.parseInt(value[1].replaceAll(" ", "")) == 1);
                System.out.println("saveLog: "+value[1]);
            }
            if(value[0].contains("runADMM"))
            {
                runADMM = (Integer.parseInt(value[1].replaceAll(" ", "")) == 1);
                System.out.println("runADMM: "+value[1]);
            }
            if(value[0].contains("HL"))
            {
                HL = (Integer.parseInt(value[1].replaceAll(" ", "")) == 1);
                System.out.println("HL: "+value[1]);
            }
            if(value[0].contains("orthognomal"))
            {
                orthognomal = (Integer.parseInt(value[1].replaceAll(" ", "")) == 1);
                System.out.println("orthognomal: "+value[1]);
            }     
            if(value[0].contains("lambda"))
            {
                lambdaSCC = Double.parseDouble(value[1].replaceAll(" ", ""));
                System.out.println("lambda: "+lambdaSCC);
            }
            if(value[0].contains("lambdaADMM"))
            {
                lambdaADMM = Double.parseDouble(value[1].replaceAll(" ", ""));
                System.out.println("lambdaADMM: "+lambdaADMM);
            }
//                        System.out.println();
        } // while ends 
        br.close();

        String master = "local[*]";
//        double[][] DQ = null;
//        if(dataFile.contains("Test"))
//            DQ = Matrix.int2double(ReadData.readDataTest(data.inputJobSearch));
//        else
//            DQ = pt.paper.CSVFile.readMatrixData(dataFile);//"../data/data_696_1109.csv"); //data_697_3187
        double[][] DQ = pt.paper.CSVFile.readMatrixData(dataFile);

        // currently, not support: matrix data should be prepared before
        // read output from parse data
        
// testing data
//        double[][] DQ = pt.paper.CSVFile.readMatrixData(args[0]);
        
//        double[][] docTermData = LocalMatrix.subMat(DQ, 0, 26, 0, DQ[0].length);
//        double[][] query = LocalMatrix.subMat(DQ, 26, 3, 0, DQ[0].length);
        
//        double[][] docTermData = pt.paper.CSVFile.readMatrixData(args[0]);
//        double[][] query = new double[10][docTermData[0].length];


        
        double[][] docTermData = LocalMatrix.subMat(DQ, 0, DQ.length-numofQ, 0, DQ[0].length);
        double[][] query = LocalMatrix.subMat(DQ, DQ.length-numofQ, numofQ, 0, DQ[0].length);

//        double[][] docTermData = pt.paper.CSVFile.readMatrixData("../data/data.csv");
        //TODO: parallel echelon 
        double[][] echelon = LocalMatrix.echelon(docTermData);//
        
        double[][] termDocData = LocalMatrix.Transpose(echelon); 
      
        
        
        // read output from echelon:         
        SparkConf conf = new SparkConf()
                .setAppName(" hk-LSA")
                .setMaster(master);
        JavaSparkContext sc = new JavaSparkContext(conf);
        
        sc.setLogLevel("ERROR");
        
//        List<Tuple2<Integer,Vector>> scc = 
        sSCC2 sscc= new sSCC2(sc, termDocData, HL,ouputdir, loop, lambdaSCC, sti);

//        double[][] rowsListDocTermRd = sscc.presentMat;//new double[docTermData.length][docTermData[0].length];
        // read outpur from parse data and echelon and sSCC: Ax-B
        
       
        sADMM admm = new sADMM(sc, docTermData, sscc.presentMat, orthognomal, rho, lambdaADMM, e1, e2, ouputdir);
//        List<Tuple2<Integer,Vector>> reduceData = admm.retMat;
//                new sADMM().run(sc, docTermData, sscc.presentMat, orthognomal, loop, 
//                ouputdir);
        

        // read output from parse+ sADMM 
/*        List<Tuple2<Integer,List<Tuple2<Integer, Double>>>> t = new sQuery().run(sc, 
                docTermData, 
                query,
                reduceData, //k*n
                ouputdir);
*/
        
        sc.close();

        query(docTermData, 
                query,
                admm.retMat);
    }

    private static void query(double[][] D,   //n*m
            double[][] Q,   //t*m
            List<Tuple2<Integer, Vector>> B //m*k
            ) {

        /**
         * Input: D n*m X k*m Q i*m TODO: D' = D*Xt -> (n*m x m*k)n*k Q' = Q*Xt
         * -> i*k sim(D',Q')
         */
        System.out.println("pt.spark.sQuery.run()");
//        Matrix mX = sCommonFunc.loadDenseMatrix(B);//.transpose(); //m,k
                
        double [][] B2 = new double[B.size()][B.get(0)._2.size()];
        for(int i =0; i< B.size(); i++)
        {
            B2[i] = B.get(i)._2.toArray();
        }

        //double [][] B3 = LocalMatrix.Transpose(B2);
        double[][] DM2 = LocalMatrix.mul(D,B2);
        double[][] QM2 = LocalMatrix.mul(Q,B2);
        
        double[][] ret2 = LocalMatrix.Transpose(LocalMatrix.sim(DM2, QM2));

        List<List<LocalEdge>> res = new ArrayList<>();
        
        for(int i = 0; i< ret2[0].length; i++)
        {
            List<LocalEdge> e = new ArrayList<>();
            for(int j = 0; j< ret2.length; j++)
            {
                e.add(new LocalEdge(i,j, ret2[j][i]));
            }
//            Collections.sort(e);
            e.sort(new Comparator<LocalEdge>() {
                @Override
                public int compare(LocalEdge o1, LocalEdge o2) {
                       if(o1.weight>o2.weight) return -1;
                       else if(o1.weight<o2.weight) return 1;
                       else return 0;
                }
            });
            List<LocalEdge> e2 = new ArrayList<>();
            for(int k = 0; k<30; k++)
            {
                e2.add(e.get(k));
            }
            res.add(e2);
        }
        
        for(List<LocalEdge> e : res)
        {
            System.out.println("pt.paper.Paper.PaperRuner()");
            for(LocalEdge i: e)
            {
                System.out.println(i.sourcevertex+" "+i.destinationvertex+":\t"+i.weight);
            }
        }
        
    }

}

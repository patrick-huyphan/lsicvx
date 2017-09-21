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
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import java.io.IOException;
import java.io.File;
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

        
        
        String master = "local[*]";
        
        double[][] DQ = pt.paper.CSVFile.readMatrixData(args[0]);
        String ouputdir = args[1]+"/"+System.currentTimeMillis();
        int numofQ = Integer.parseInt(args[2]);
        boolean HL = (Integer.parseInt(args[3]) ==1)?true: false;
        boolean orthognomal = (Integer.parseInt(args[4]) ==1)?true: false;
        int loop = Integer.parseInt(args[5]);
        double lambda = Double.parseDouble(args[6]);
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
        sSCC2 sscc= new sSCC2(sc, termDocData, HL,ouputdir, loop, lambda);

//        double[][] rowsListDocTermRd = sscc.presentMat;//new double[docTermData.length][docTermData[0].length];
        // read outpur from parse data and echelon and sSCC: Ax-B
        
        sADMM admm = new sADMM(sc, docTermData, sscc.presentMat, orthognomal, ouputdir);
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

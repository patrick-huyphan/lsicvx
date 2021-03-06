package pt.spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.LinkedList;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.mllib.linalg.distributed.RowMatrix;
import breeze.linalg.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.commons.lang.ArrayUtils;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import scala.Function1;
import static com.google.common.base.Preconditions.checkArgument;
import java.io.FileWriter;
import java.text.DecimalFormat;

/**
 * sSCC class, we will call this class to clustering data, return the row in
 * same cluster - map data to master, calculate edge weigh - each executer
 * calculate row data by: read Matrix, update D,X,V,U D,V,U related to edge
 */
public class sSCC2 {

    /**
     * We use a logger to print the output. Sl4j is a common library which works
     * with log4j, the logging system used by Apache Spark.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(sSCC2.class);

    /**
     *
     * @param context
     * @param A: matrix term doc
     * @param inputFilePath
     * @param outFilePath
     * @return
     */
    static double _rho0 = 0.05;
    public double[][] presentMat; //List<Tuple2<Integer, Vector>>
    public sSCC2(JavaSparkContext context,
            double[][] A,// term-doc
            boolean hl,
            String outFilePath, int maxloop,double ilamda , double t) throws Exception {

        
        double lamda = ilamda;
        double _lamda2 = 0.01;
        double _eps_abs = 1e-5;
        double _eps_rel = 1e-5;
        int numberOfVertices = A.length;
        int numOfFeature = A[0].length;

        LinkedList<Tuple2<Integer, Vector>> rowsListTermDoc = new LinkedList<>();
        for (int i = 0; i < A.length; i++) {
            Vector row = Vectors.dense(A[i]);
            rowsListTermDoc.add(new Tuple2<>(i, row));
        }

        List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = buildE(A);
////TODO: init global data : X, u, xAvr        
//        int[] ni = retSize(numberOfVertices, eSet);
////        Matrix.printMat(A, "centered");
//        //Init
//        double[][] X = new double[numberOfVertices][numOfFeature];
//        for (int i = 0; i < numOfFeature; i++) {
//            double x = 1 - ((_lamda2) / LocalVector.norm(LocalMatrix.getCol(A, i)));
//            x = (x > 0) ? x : 0;
//            //update by column i
//            for (int j = 0; j < numberOfVertices; j++) {
//                X[j][i] = x * A[j][i];
//            }
//        }
//        
//        double[] u = new double[numOfFeature];
        double[] xAvr = new double[numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
//            u[i] = LocalVector.norm(LocalMatrix.getCol(A, i));
            xAvr[i] = LocalVector.avr(LocalMatrix.getCol(A, i));
        }
//        u = LocalVector.scale(u, _lamda2);
        
//        for (int i = 0; i < numOfFeature; i++) {
//            double x = 1 - (u[i] / LocalVector.norm(LocalMatrix.getCol(A, i)));
//            x = (x > 0) ? x : 0;
//            for (int j = 0; j < numberOfVertices; j++) {
//                X[j][i] = x * A[j][i];
//            }
//        }

        double B[][] = new double[numberOfVertices][numOfFeature];
        for(int i = 0; i< numberOfVertices; i++)
            B[i]= LocalVector.plus(A[i], LocalVector.scale(xAvr, numberOfVertices));
//        for(Tuple2<Integer, Vector> mi: rowsList)
//        {
//            System.out.println("pt.spark.sSCC.run() "+mi._1+"\t "+ mi._2.toString());
//        }
        System.out.println("pt.spark.sSCC.run()");

        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
//        Broadcast<double[]> _xAvr = context.broadcast(xAvr);
//        Broadcast<int[]> _ni = context.broadcast(ni);

        Broadcast<Integer> _numOfFeature = context.broadcast(numOfFeature);
        Broadcast<Integer> _numberOfVertices = context.broadcast(numberOfVertices);
        Broadcast<double[][]> _A = context.broadcast(A);
        Broadcast<double[][]> _B = context.broadcast(B);

        //        LocalMatrix.printMat(X, "x init");
//        LocalVector.printV(xAvr, "xAvr", true);
        System.out.println("pt.spark.sSCC.run() 2 start map scc local");
        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        
        List<Tuple2<Integer, Vector>> retList = new LinkedList<>();
        
        List<LocalEdgeNode> U = initU(eSet, numOfFeature);
        List<LocalEdgeNode> V = initV(eSet, A);
        List<LocalEdgeNode> U0;
        List<LocalEdgeNode> V0;
        List<Tuple2<Integer, Vector>> X0 = rowsListTermDoc;
        List<Tuple2<Integer, Vector>> X1 = rowsListTermDoc;
        
        V0= V;
        U0= U;
        
        JavaRDD<Tuple2<Integer, Vector>> matI = context.parallelize(rowsListTermDoc);    
        JavaRDD<LocalEdgeNode> uv = context.parallelize(U);
        matI.saveAsTextFile(outFilePath+"/SCC");
        
//        boolean stop = false;
        int loop = 0;
        while(loop <maxloop)
        {
            /**
             * - calculator X in slaver
             * - master check stop
             * - update X, U, V, lambda, rho
             * TODO:
             * - parallel X, server update X
             * - parallel U, server update U
             * - parallel V, server update V
             * - parallel checkstop, server update
             */
//            System.out.println("pt.spark.sSCC2.run()  "+loop +": " + _rho0 + " - "+_lamda);
//            System.out.print(".");
            if(loop%300 ==0)
                System.out.println( loop+".");
            Broadcast<Double> rho0 = context.broadcast(_rho0);
            Broadcast<Double> _lamda = context.broadcast(lamda);
            Broadcast<List<LocalEdgeNode>> _U = context.broadcast(U);
            Broadcast<List<LocalEdgeNode>> _V = context.broadcast(V);

//update X, V, U           
            X1 = matI.mapToPair((Tuple2<Integer, Vector> t1) -> {
                return new Tuple2<>(t1._1,
                        updateXNode(t1,
                                _numberOfVertices.value(),
                                _numOfFeature.value(),
                                _V.value(),
                                _U.value(),
                                _B.value()[t1._1]
                        )
                );
                }).cache().collect();
//            retList = X0;

            Broadcast<List<Tuple2<Integer, Vector>>> _x1 = context.broadcast(X1);
            
            V=  uv.map((LocalEdgeNode v1) -> updateVNode(_x1.value(), v1, _U.value(), _lamda.value(), E.value())).cache().collect();
            
            Broadcast<List<LocalEdgeNode>> _V1 = context.broadcast(V);
            
            U=  uv.map((LocalEdgeNode u1) -> updateUNode(_x1.value(), _V1.value(), _U.value(), u1 )).cache().collect();
            FileWriter fw = null;
//            if(loop<5)
//            logData(loop, fw, outFilePath, X1, U, V, numOfFeature);
//checkstop            
            if(checkStop(A, X0, U0, V0, V, _eps_abs, _eps_rel, numberOfVertices, loop) && loop>1)
            {
                rho0.destroy();
                _lamda.destroy();
                _V.destroy();
                _U.destroy();
                _x1.destroy();
                _V1.destroy();
                break;
            }
//update for next process
            lamda = lamda * t; 
            
            V0= V;
            U0= U;
            X0 = X1;
            
//            FileWriter fwt = new FileWriter(outFilePath+"/"+loop+"_X_data.txt");
//            for (Tuple2<Integer, Vector> r: X0) {
//
//                double[] tmp = r._2.toArray();
//                for (int j = 0; j < numOfFeature; j++) {
//                    fwt.append(tmp[j] + "\t");
//                }
//                fwt.append("\n");
//            }
//            fwt.close();
            
            rho0.destroy();
            _lamda.destroy();
            _V.destroy();
            _V1.destroy();
            _U.destroy();
            _x1.destroy();

            loop++;
        }


//        List<Tuple2<Integer, Vector>> retList2 = new LinkedList<>();
//        FileWriter fw = new FileWriter(outFilePath+"/X_data.txt");
        for (Tuple2<Integer, Vector> r: X0) {
           
            double[] tmp = LocalVector.formV(r._2, "0.000000000");
//            for (int j = 0; j < numOfFeature; j++) {
//                fw.append(tmp[j] + "\t");
//            }
//            fw.append("\n");
            
            retList.add(new Tuple2<Integer, Vector>(r._1, Vectors.dense(tmp)));
        }

//        fw.close();

//        double[][] retArray = new double[numOfFeature][numOfFeature];

//        List<Tuple2<Integer, List<Vector>>> retList = ret.collect();
//        ret.saveAsTextFile(outFilePath + "/scc");
        System.out.println("pt.spark.sSCC.run() end");
        
        E.destroy();
        _numOfFeature.destroy();
        _numberOfVertices.destroy();
        _A.destroy();
        _B.destroy();
        
        presentMat = getPresentMat(retList, A, eSet, hl);
        
//        return getPresentMat(retList, A, eSet, hl);
    }

        private void logData(int loop, FileWriter fw, String outFilePath, List<Tuple2<Integer, Vector>> X0, List<LocalEdgeNode> U, List<LocalEdgeNode> V, int numOfFeature) throws IOException
    {
        fw = new FileWriter(outFilePath+"/"+loop+"_X_data.txt");

        for (Tuple2<Integer, Vector> r: X0) {
            double[] tmp = r._2.toArray();
            for (int j = 0; j < numOfFeature; j++) {
                fw.append( tmp[j]+ "\t");
            }
            fw.append("\n");
        }
        fw.close();

//        fw = new FileWriter(outFilePath+"/"+loop+"_z_data.txt");
//        for(LocalEdgeNode k: V)
//        {
//            fw.append("\n" + k.src+"-"+k.dst+"\t");
//            double [] tmp = k.value;
//            for (int j = 0; j < numOfFeature; j++) {
//                fw.append(tmp[j] + "\t");
//            }
//            fw.append("\n");
//        }
//        fw.close();

//        fw = new FileWriter("tmp/"+loop+"_u_data.txt");
//        for(LocalEdgeNode k: U)
//        {
//            fw.append("\n" + k.src+"-"+k.dst+"\t");
//            double [] tmp = k.value;
//            for (int j = 0; j < numOfFeature; j++) {
//                fw.append(tmp[j] + "\t");
//            }
//            fw.append("\n");
//        }
        fw.close();
    }

    private static List<Tuple2<Tuple2<Integer, Integer>, Double>> buildE(double [][] A) {
        List<Tuple2<Tuple2<Integer, Integer>, Double>> ret = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            for (int j = i + 1; j < A.length; j++) {
                double value = LocalVector.cosSim(A[i], A[j]);
                if(value>0.13)
                {
                    ret.add(new Tuple2<>(new Tuple2<>(i, j), value));
//                    ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(j)._1, rowsList.get(i)._1), value));
                }
            }
        }
        return ret;
    }

    private static int[] retSize(int numberOfVertices, List<Tuple2<Tuple2<Integer, Integer>, Double>> edges) {
        int[] ret = new int[numberOfVertices];
        for (Tuple2<Tuple2<Integer, Integer>, Double> e : edges) {
            ret[e._1._1] = ret[e._1._1] + 1;
            ret[e._1._2] = ret[e._1._2] + 1;
        }
       //for(int i = 0; i< numberOfVertices; i++)
       //     System.out.println("pt.spark.sSCC.retSize() "+i+": "+ ret[i]);
        return ret;
    }

//    public static double[][] getPresentMat(List<Tuple2<Integer, Vector>> scc, double[][] A, boolean HL) {
//        List<List<Integer>> cluster = getCluster(scc);
//        double[][] presentMat = new double[A.length][cluster.size()];//[numberOfVertices];
////        double[][] ret = new double[data[0].length][index.size()];
////        System.out.println("paper.MSTClustering.getPresentMath()");
//
//        //LocalMatrix.printMat(A, false,"At");    
//        for (int j = 0; j < cluster.size(); j++) {
//            List<Integer> edgesL = cluster.get(j);
//            if (edgesL.isEmpty()) {
////                System.out.println("paper.Clustering.getPresentMat() empty "+j);
//                continue;
//            }
//            int shotestCol = edgesL.get(0);
//            if(HL==false)
//            {
//                System.out.println("getPresentMat L");
//                double min = 100;
//                for (Integer node : edgesL) {
//                    double norm = LocalVector.norm(A[node - 1]);
//                    if (norm < min) {
//                        min = norm;
//                        shotestCol = node - 1;
//                    }
//                }
//            }
//            else
//            {
//                System.out.println("getPresentMat H");
//                double max = -100;
//                for (Integer node : edgesL) {
//                    double norm = LocalVector.norm(A[node - 1]);
//                    if (norm > max) {
//                        max = norm;
//                        shotestCol = node - 1;
//                    }
//                }
//            }
//            System.out.println("\npaper.Paper.getPresentMath() "+j+" "+ shotestCol);
//
//            for (int i = 0; i < A.length; i++) {
//                presentMat[i][j] = A[shotestCol][i]; //new Random().nextDouble(); // 
//            }
//        }
//        return presentMat;
//    }

    private static double[][] getPresentMat(List<Tuple2<Integer, Vector>> scc, double[][] A, List<Tuple2<Tuple2<Integer, Integer>, Double>> edges, boolean HL)
    {
        List<List<Integer>> cluster = getCluster(scc);
        double[][] presentMat = new double[A.length][cluster.size()];//[numberOfVertices];
//        List<Tuple2<Tuple2<Integer, Integer>, Double>> edges = buildE(A);
//        double[][] ret = new double[data[0].length][index.size()];
//        System.out.println("paper.MSTClustering.getPresentMath()");
//        Matrix.printMat(A, false, "A");
        for(int j = 0; j< cluster.size(); j++)
        {
            List<Integer> edgesL = cluster.get(j);
            if(edgesL.isEmpty())
            {
//                System.out.println("paper.Clustering.getPresentMat() empty "+j);
                continue;
            }
            int shotestCol= edgesL.get(0);
            if(edgesL.size() > 1)
            {        
//                double min = 100;
                double maxS = -1;
                for(Integer node: edgesL)
                {
                    for(Tuple2<Tuple2<Integer, Integer>, Double> e : edges)
                    {
                        if((e._1._2 == node || e._1._1 == node) && (edgesL.contains(e._1._2) && edgesL.contains(e._1._1)))
                        {
                            if(e._2>maxS)
                            {
                                maxS = e._2;
//                                System.out.println("getPresentMat() "+ e.dst+" "+e.scr+": "+e.weight);
                                shotestCol = (LocalVector.norm(A[e._1._2])>LocalVector.norm(A[e._1._1]))?e._1._2:e._1._1;
                            }
                        }
                    }
                }
            }
//            System.out.println("\npaper.Paper.getPresentMath() "+j+" "+ shotestCol);
            
            for(int i = 0; i<A.length  ; i++)
            {
                presentMat[i][j] = A[shotestCol][i]; //new Random().nextDouble(); // 
            }
        }
        
        return presentMat;
    }
        
    private static List<List<Integer>> getCluster(List<Tuple2<Integer, Vector>> scc) {
//        int  ret[] = new int[scc.size()];
        HashMap<Integer, Integer> xxx = new HashMap<>();
        List<List<Integer>> cluster = new ArrayList<>();
//        List<Integer>  intdex = new ArrayList<Integer>();
        for (int i = 0; i < scc.size(); i++) {
            if (!xxx.containsKey(i)) {
                xxx.put(i, i);
                for (int j = i + 1; j < scc.size(); j++) {
                    if (!xxx.containsKey(j)) {
                        if ((LocalVector.isSameVec(scc.get(i)._2.toArray(), scc.get(j)._2.toArray()))) //                        if((Vector.isDepen(Matrix.getRow(X, i),Matrix.getRow(X, j))))    
                        {
//                                System.out.println("same: "+i+"-"+j);
                            xxx.put(j, i);
                        }
                    }
                }
            }
        }
//    	count--;
//    	System.out.println("Num of sub mat: "+intdex.size());

        for (int i = 0; i < scc.size(); i++) {
            List<Integer> sub = new ArrayList<>();
            for (int j = i; j < scc.size(); j++) {
                if (xxx.get(j) == i) {
                    sub.add(j);
                }
            }
            if (!sub.isEmpty()) {
                cluster.add(sub);
            }
        }
//    	cluster.add(intdex);
//Test    

        System.out.println("paper.SCC.getCluster() " + cluster.size());
        for (int i = 0; i < cluster.size(); i++) {
            List<Integer> sub = cluster.get(i);
            System.out.print("Cluster " + i + ":\t");
            for (int j = 0; j < sub.size(); j++) {
                System.out.print(sub.get(j) + "\t");
            }
            System.out.println("");
        }

        return cluster;
    }
    
    
    private static double updateRho(double r, double s)
    {
        DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
        if (r > 10 * s) {
            _rho0 = Double.valueOf(twoDForm.format(_rho0 * 0.5));//(r/s);//2*rho;
        }
        if (s > 10 * r) {
            _rho0 = Double.valueOf(twoDForm.format(_rho0 * 2));//(r/s);//rho/2;
        }
//        if(r>10*s)
//            _rho0 =  Double.valueOf((_rho0* 0.5));//(r/s);//2*rho;
//        if(s>10*r)
//            _rho0 =  Double.valueOf((_rho0* 2));//(r/s);//rho/2;
        return _rho0;
    }
    
//    private static boolean checkStop(double[][] A, List<Tuple2<Integer, Vector>> X0, List<LocalEdgeNode> U, List<LocalEdgeNode> V0, List<LocalEdgeNode> V,  
//            double ea, double er, int numberOfVertices) throws Exception
//    {
//        double r = primalResidual(X0,V0);
//        double s = dualResidual(V0, V, _rho0);
////        System.err.println("rho "+rho);
//        updateRho(r, s);
//        
//        double maxAB= 0;
//        for(LocalEdgeNode b:V)
//        {
//            double be = LocalVector.norm(b.value);
//            double a = LocalVector.norm(A[b.src]);
//            double ab = (a>be)? a:be;
//            maxAB = (ab>maxAB)? ab:maxAB;
//        }
//        
//        double maxC = 0;
//        for(LocalEdgeNode c:U)
//        {
//            double value = LocalVector.norm(c.value);
//            maxC = (value>maxC)? value:maxC;
//        }
//        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
//        double ed = ea+er*maxC;//Cik?
//        
//        if(_rho0 ==0)
//            return true;
////        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
//        return (r<=ep) && (s<=ed);
//    } 

        private static boolean checkStop(double[][] A, List<Tuple2<Integer, Vector>> X0, List<LocalEdgeNode> U, List<LocalEdgeNode> V0, List<LocalEdgeNode> V,  
            double ea, double er, int numberOfVertices, int loop) throws Exception {
        double r = primalResidual(X0, V0);
        double s = dualResidual(V0, V, _rho0);
//        System.err.println("rho "+rho);

        double maxAB[] = new double[V.size()];
        int i = 0;
        for (LocalEdgeNode b : V) {
            double be = LocalVector.norm(b.value);
            double a = LocalVector.norm(A[b.src]);
            maxAB[i] = (a > be) ? a : be;
            i++;
//          maxAB   = (ab > maxAB) ? ab : maxAB;
        }

        double maxC[] = new double[U.size()];
        i=0;
        for (LocalEdgeNode c : U) {
            maxC[i] = LocalVector.norm(c.value);
            i++;
//            maxC = (value > maxC) ? value : maxC;
        }
//        double nz = Vector.norm(maxAB);
        double ed = ea + er * LocalVector.norm(maxC);//Cik?
        double ep = ea * Math.sqrt(numberOfVertices) + er * LocalVector.norm(maxAB); //Bik?

        updateRho(r, s);
        double nz[] = new double[V.size()];
        i= 0;        
        for (LocalEdgeNode b : V) {
            nz[i] = LocalVector.norm(b.value);
            if(nz[i]>0){
//                if(loop == 0)
//                    System.out.println(i+" pt.paper.SCCNew2.checkStop() " + b.src+ " - "+b.dst+": "+nz[i]);
                i++;
            }
        }
//
//        double nu[] = new double[U.size()];
//        i= 0;        
//        for (LocalEdgeNode b : U) {
//            nu[i] = LocalVector.norm(b.value);
//            if(nu[i]>0){
//                i++;
//            }
//        }        
//        
//        double nx[] = new double[X0.size()];
//        i = 0;
//        for(Tuple2<Integer, Vector> x: X0)
//        {
//            nx[i] = LocalVector.norm(x._2.toArray());
////            if(loop == 1)
////                System.out.println(x._1 + " pt.paper.SCCNew2.checkStop() "+i+": "+nx[i]);
//            i++;
//        }
            
//        if (rho == 0) {
//            System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed+" ==== "+count);
//            return true;
//        }
//        double noZ = LocalVector.norm(nz);
        
//        System.out.println("pt.paper.SCCNew2.checkStop() "+r+" - "+s +" - "+noZ +" - "+LocalVector.norm(nx)+" - "+LocalVector.norm(nu));
//        DecimalFormat twoDForml = new DecimalFormat("0.00000000");
//        noZ = (Double.isNaN(noZ))?0:Double.valueOf(twoDForml.format(noZ));
//        if(noZ == 0.)
//            return true;
        
        return (r <= ep) && (s <= ed);
    }
            
    private static double primalResidual(List<Tuple2<Integer, Vector>> X0, List<LocalEdgeNode> V0) {
//        double ret = 0;
        double []x = new double[V0.size()];
        int i =0;
        for (LocalEdgeNode k : V0) {
//            double normR 
            x[i] = LocalVector.norm(LocalVector.sub(getRow(X0, k.src), k.value));
//            ret = (ret > normR) ? ret : normR;
            i++;
        }
        
        return LocalVector.norm(x);
    }

    
    private static double dualResidual(List<LocalEdgeNode> Vp, List<LocalEdgeNode> V, double rho) throws Exception {
//        double ret = 0;
        double []x = new double[V.size()];
        int i =0;
        for (LocalEdgeNode k : V) {
            double[] bikp = getuv(Vp, k);// Vp.get(V.indexOf(n)).value;
            double[] ai = LocalVector.scale(LocalVector.sub(bikp, k.value), rho);
//            double normS = Vector.norm(ai);
//            ret = (ret > normS) ? ret : normS;
            x[i] = LocalVector.norm(ai);
            i++;    
        }
        return LocalVector.norm(x);
    }
    
    private static List<LocalEdgeNode> initV(List<Tuple2<Tuple2<Integer, Integer>, Double>> edges, double [][] A)
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
//        System.out.println("paper.NodeSCC.initV() "+(i+1));
        for(Tuple2<Tuple2<Integer, Integer>, Double> e:edges)
        {
                //ik
                double[] value1 = LocalMatrix.getRow(A, e._1._1) ;//new double[dataLength];
                ret.add(new LocalEdgeNode(e._1._1, e._1._2, value1));
                //ki
                double[] value2 = LocalMatrix.getRow(A, e._1._2) ;//new double[dataLength];
                ret.add(new LocalEdgeNode(e._1._2, e._1._1, value2));
        }
        return ret;        
    }
        
    private static List<LocalEdgeNode> initU(List<Tuple2<Tuple2<Integer, Integer>, Double>> edges, int numOfFeature)
    {
        List<LocalEdgeNode> ret = new ArrayList<>();
        for(Tuple2<Tuple2<Integer, Integer>, Double> e:edges)
        {
//                System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
                ret.add(new LocalEdgeNode(e._1._1, e._1._2, new double[numOfFeature]));
                ret.add(new LocalEdgeNode(e._1._2, e._1._1, new double[numOfFeature]));
        }
        return ret;        
    }
    
    /**
     * Update by row, each row to RDD
     * 
     */    
    private static Vector updateXNode(Tuple2<Integer, Vector> curruntI,
            int numberOfVertices,
            int numOfFeature,
            List<LocalEdgeNode> _V,
            List<LocalEdgeNode> _U,
            double[] _B
    )
    {           
        
//        System.out.println("pt.spark.sSCC2.updateXNode() " +curruntI._1);
        double[] sumdi = new double[numOfFeature];
        double[] sumdj = new double[numOfFeature];
               
        // (sum(j>i)(ui-zi)-sum(i>j)(uj-zj))
        //TODO: review i>j and i>j???         
        for(LocalEdgeNode k: _V)
        {
            if(curruntI._1 == k.src)
            {
                sumdi = LocalVector.plus(sumdi, LocalVector.plus(getuv(_U, k) , k.value));
            }
            if(curruntI._1 == k.dst)
            {
                sumdj = LocalVector.plus(sumdj, LocalVector.plus(getuv(_U, k) , k.value));
            }
        }     
        
        double[] sumd = LocalVector.sub(sumdi, sumdj);
        double[] X = LocalVector.scale(LocalVector.plus(_B,sumd), 1./(1+numberOfVertices));        
//        if(curruntI._1 == 5) LocalVector.printV(sumd, "pt.spark.sSCC2.updateXNode() " +curruntI._1, true);
        return Vectors.dense(X);
    }
    /**
     * update all node in V, each V is RDD
     * 
     */
    private static LocalEdgeNode updateVNode(List<Tuple2<Integer, Vector>> X, LocalEdgeNode V, List<LocalEdgeNode> U , double lambda, List<Tuple2<Tuple2<Integer, Integer>, Double>> edges)
    {
        double[] bbu = LocalVector.sub(LocalVector.sub(getRow(X, V.src), getRow(X, V.dst)), getuv(U, V));
        double w = getEdgeW(edges, V.src, V.dst);
        bbu = LocalVector.proxN2_2(bbu, lambda*w);
//        LocalVector.printV(bbu, "pt.spark.sSCC2.updateVNode() "+ V.src+" - "+V.dst, true);
        return new LocalEdgeNode(V.src, V.dst, bbu);
    }


    /**
     * update all U, each U is RDD.
     * 
     */
    private static LocalEdgeNode updateUNode(List<Tuple2<Integer, Vector>> X, List<LocalEdgeNode> V, List<LocalEdgeNode> U, LocalEdgeNode u)
    {
        double[] data = LocalVector.sub(getuv(V, u), LocalVector.sub(getRow(X, u.src), getRow(X, u.dst)));
        data = LocalVector.plus(getuv(U, u), data);
            
//        LocalVector.printV(data, "pt.spark.sSCC2.updateUNode() "+ U.src+" - "+U.dst, true);
        return new LocalEdgeNode(u.src, u.dst, data);    
    }  
    
   
    private static double getEdgeW(List<Tuple2<Tuple2<Integer, Integer>, Double>> E, int s, int d)
    {
        for(Tuple2<Tuple2<Integer, Integer>, Double> e: E)
        {
            if((e._1._1 == s &&  e._1._2 == d)||(e._1._2 == s &&  e._1._1 == d))
                return e._2;
        }
        System.out.println("pt.spark.sSCC2.getEdgeW() :" + s+"-"+d+" not availble");
        return 0;
    }
    private static double[] getuv(List<LocalEdgeNode> uv, LocalEdgeNode id)
    {
        return getuv(uv, id.src, id.dst);
    }
    private static double[] getuv(List<LocalEdgeNode> uv, int s, int d)
    {
        for(LocalEdgeNode n: uv)
        {
            if(n.src == s && n.dst == d)
                return n.value;
        }
        System.out.println("pt.spark.sSCC2.getuv() :" + s+"-"+d+" not availble");
        return new double[uv.get(0).value.length];
    }
    
    private static double[] getRow(List<Tuple2<Integer, Vector>> X, int i)
    {
//        System.out.println("pt.spark.sSCC2.getRow() " +i);
        for(Tuple2<Integer, Vector> r :X)
            if(r._1 == i)
                return r._2.toArray();
        System.out.println("pt.spark.sSCC2.getRow() :" + i+" not availble");
        return new double[X.get(0)._2.toArray().length];
    }
}

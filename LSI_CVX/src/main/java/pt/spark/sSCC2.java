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
    static double _rho0 = 0.01;
    
    public static List<Tuple2<Integer, Vector>> run(JavaSparkContext context,
            double[][] A,// term-doc
            String outFilePath) throws Exception {

        
        double _lamda = 1.75;
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

        List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = buildE(rowsListTermDoc);
////TODO: init global data : X, u, xAvr        
        int[] ni = retSize(numberOfVertices, eSet);
////        Matrix.printMat(A, "centered");
//        //Init
        double[][] X = new double[numberOfVertices][numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
            double x = 1 - ((_lamda2) / LocalVector.norm(LocalMatrix.getCol(A, i)));
            x = (x > 0) ? x : 0;
            //update by column i
            for (int j = 0; j < numberOfVertices; j++) {
                X[j][i] = x * A[j][i];
            }
        }
//        
        double[] u = new double[numOfFeature];
        double[] xAvr = new double[numOfFeature];
        for (int i = 0; i < numOfFeature; i++) {
            u[i] = LocalVector.norm(LocalMatrix.getCol(X, i));
            xAvr[i] = LocalVector.avr(LocalMatrix.getCol(A, i));
        }
        u = LocalVector.scale(u, _lamda2);
        
        for (int i = 0; i < numOfFeature; i++) {
            double x = 1 - (u[i] / LocalVector.norm(LocalMatrix.getCol(A, i)));
            x = (x > 0) ? x : 0;
            for (int j = 0; j < numberOfVertices; j++) {
                X[j][i] = x * A[j][i];
            }
        }

        double B[][] = new double[numberOfVertices][numOfFeature];
        for(int i = 0; i< numberOfVertices; i++)
            B[i]= LocalVector.plus(A[i], LocalVector.scale(xAvr, numberOfVertices));
//        for(Tuple2<Integer, Vector> mi: rowsList)
//        {
//            System.out.println("pt.spark.sSCC.run() "+mi._1+"\t "+ mi._2.toString());
//        }
        System.out.println("pt.spark.sSCC.run()");

        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
        Broadcast<double[]> _xAvr = context.broadcast(xAvr);
        Broadcast<int[]> _ni = context.broadcast(ni);

        Broadcast<Integer> _numOfFeature = context.broadcast(numOfFeature);
        Broadcast<Integer> _numberOfVertices = context.broadcast(numberOfVertices);
        Broadcast<double[][]> _A = context.broadcast(A);
        Broadcast<double[][]> _B = context.broadcast(B);

        //        LocalMatrix.printMat(X, "x init");
//        LocalVector.printV(xAvr, "xAvr", true);
        System.out.println("pt.spark.sSCC.run() 2 start map scc local");
        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        JavaRDD<Tuple2<Integer, Vector>> matI = context.parallelize(rowsListTermDoc);
       
        matI.saveAsTextFile(outFilePath+"/SCC");
        
        List<Tuple2<Integer, Vector>> retList = new LinkedList<>();
//        JavaPairRDD<Integer, List<Vector>> ret = new ;
        List<LocalEdgeNode> U = initU(eSet, numOfFeature);
        List<LocalEdgeNode> V = initV(eSet, A);
        List<LocalEdgeNode> U0;
        List<LocalEdgeNode> V0;
        List<Tuple2<Integer, Vector>> X0 = new LinkedList<>();
        List<Tuple2<Integer, Vector>> X1 = new LinkedList<>();
        
        V0= V;
        U0= U;
        X1 = rowsListTermDoc;
        JavaRDD<LocalEdgeNode> uv = context.parallelize(U);
         
//        boolean stop = false;
        int loop = 0;
        while(loop <900)
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
            System.out.println("pt.spark.sSCC2.run()  "+loop);
//transform and broadcast data to parallel
//            double tmprho = _rho0;
            Broadcast<Double> rho0 = context.broadcast(_rho0);
            Broadcast<Double> lamda = context.broadcast(_lamda);
//            Broadcast<double[][]> _X = context.broadcast(X);
            Broadcast<List<LocalEdgeNode>> _U = context.broadcast(U);
            Broadcast<List<LocalEdgeNode>> _V = context.broadcast(V);
            Broadcast<List<Tuple2<Integer, Vector>>> _x0 = context.broadcast(X0);


//update X, V, U
//            updateXNode();
            
            X0 = matI.mapToPair((Tuple2<Integer, Vector> t1)
                    -> {
    //            System.out.println("pt.spark.sSCC.run() driver "+t1._1+"\t "+ t1._2.toString());
                return new Tuple2<>(t1._1,
                        updateXNode(t1,
//                                _A.value(),
                                _numberOfVertices.value(),
                                _numOfFeature.value(),
//                                 _x0.value(),
                                _V.value(),
                                _U.value(),
                                _B.value(),
//                                _ni.value(),
//                                _xAvr.value(),
//                                rho0.value(),
                                lamda.value()
                        )
                );
                }
            ).cache().collect();
//            ret.cache();
//            X0 = ret.collect();
            retList = X0;
//            _x0.destroy();
            Broadcast<List<Tuple2<Integer, Vector>>> _x1 = context.broadcast(X0);
            
            V=  uv.map(new Function<LocalEdgeNode, LocalEdgeNode>() {
            @Override
            public LocalEdgeNode call(LocalEdgeNode v1) throws Exception {
                
                return updateVNode(_x1.value(), v1, _U.value(), lamda.value(), E.value());                
            }
            }).cache().collect();
            
            Broadcast<List<LocalEdgeNode>> _V1 = context.broadcast(V);
            
            U=  uv.map(new Function<LocalEdgeNode, LocalEdgeNode>() {
            @Override
            public LocalEdgeNode call(LocalEdgeNode u1) throws Exception {
                return updateUNode(_x1.value(), _V1.value(), u1 );                
            }
            }).cache().collect();
            
            
//checkstop            
            if(checkStop(A, X1, U0, V0, V, _eps_abs, _eps_rel, numberOfVertices) && loop>1)
            {
                rho0.destroy();
                lamda.destroy();
//                _X.destroy();
                _x0.destroy();
                _V.destroy();
                _U.destroy();
                _x1.destroy();
                _V1.destroy();
                break;
            }
//update rho
//            updateRho(_rho0, _rho0, _rho0);
//update lambda
            _lamda = _lamda * 1.005;
            
            V0= V;
            U0= U;
            X1 = X0;
            
            rho0.destroy();
            lamda.destroy();
//            _X.destroy();
            _x0.destroy();
            _V.destroy();
            _U.destroy();
            _x1.destroy();
            _V1.destroy();
            loop++;
        }


        List<Tuple2<Integer, Vector>> retList2 = new LinkedList<>();
        FileWriter fw = new FileWriter(outFilePath+"/X_data.txt");
        for (Tuple2<Integer, Vector> r: retList) {
           
            double[] tmp = LocalVector.formV(r._2, "0.000000000");
            for (int j = 0; j < numOfFeature; j++) {
                fw.append(tmp[j] + "\t");
            }
            fw.append("\n");
            
            retList2.add(new Tuple2<Integer, Vector>(r._1, Vectors.dense(tmp)));
            
//            Vector.printV(X[i], "X[i] " + i, true);
        }

        fw.close();

//        double[][] retArray = new double[numOfFeature][numOfFeature];

//        List<Tuple2<Integer, List<Vector>>> retList = ret.collect();
//        ret.saveAsTextFile(outFilePath + "/scc");
        System.out.println("pt.spark.sSCC.run() end");
        
        E.destroy();
        _xAvr.destroy();
        _ni.destroy();

        _numOfFeature.destroy();
        _numberOfVertices.destroy();
        _A.destroy();
        
        return retList2;
    }


    private static List<Tuple2<Tuple2<Integer, Integer>, Double>> buildE(LinkedList<Tuple2<Integer, Vector>> rowsList) {
        List<Tuple2<Tuple2<Integer, Integer>, Double>> ret = new ArrayList<>();
        for (int i = 0; i < rowsList.size(); i++) {
            for (int j = i + 1; j < rowsList.size(); j++) {
                double value = LocalVector.cosSim(rowsList.get(i)._2.toArray(), rowsList.get(j)._2.toArray());
                if(value>0)
                {
                    ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(i)._1, rowsList.get(j)._1), value));
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

    public static double[][] getPresentMat(List<Tuple2<Integer, Vector>> scc, double[][] A, boolean HL) {
        List<List<Integer>> cluster = getCluster(scc);
        double[][] presentMat = new double[A.length][cluster.size()];//[numberOfVertices];
//        double[][] ret = new double[data[0].length][index.size()];
//        System.out.println("paper.MSTClustering.getPresentMath()");

        //LocalMatrix.printMat(A, false,"At");    
        for (int j = 0; j < cluster.size(); j++) {
            List<Integer> edgesL = cluster.get(j);
            if (edgesL.isEmpty()) {
//                System.out.println("paper.Clustering.getPresentMat() empty "+j);
                continue;
            }
            int shotestCol = edgesL.get(0);
            if(HL==false)
            {
                System.out.println("getPresentMat L");
                double min = 100;
                for (Integer node : edgesL) {
                    double norm = LocalVector.norm(A[node - 1]);
                    if (norm < min) {
                        min = norm;
                        shotestCol = node - 1;
                    }
                }
            }
            else
            {
                System.out.println("getPresentMat H");
                double max = -100;
                for (Integer node : edgesL) {
                    double norm = LocalVector.norm(A[node - 1]);
                    if (norm > max) {
                        max = norm;
                        shotestCol = node - 1;
                    }
                }
            }
            System.out.println("\npaper.Paper.getPresentMath() "+j+" "+ shotestCol);

            for (int i = 0; i < A.length; i++) {
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
                    sub.add(j + 1);
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
        if(r>10*s)
            _rho0 =  Double.valueOf((_rho0* 0.5));//(r/s);//2*rho;
        if(s>10*r)
            _rho0 =  Double.valueOf((_rho0* 2));//(r/s);//rho/2;
        return _rho0;
    }
    
    private static boolean checkStop(double[][] A, List<Tuple2<Integer, Vector>> X0, List<LocalEdgeNode> U, List<LocalEdgeNode> V0, List<LocalEdgeNode> V,  
            double ea, double er, int numberOfVertices) throws Exception
    {
        double r = primalResidual(X0,V0);
        double s = dualResidual(V0, V, _rho0);
//        System.err.println("rho "+rho);
        updateRho(r, s);
        
        double maxAB= 0;
        for(LocalEdgeNode b:V)
        {
            double be = LocalVector.norm(b.value);
            double a = LocalVector.norm(A[b.src]);
            double ab = (a>be)? a:be;
            maxAB = (ab>maxAB)? ab:maxAB;
        }
        
        double maxC = 0;
        for(LocalEdgeNode c:U)
        {
            double value = LocalVector.norm(c.value);
            maxC = (value>maxC)? value:maxC;
        }
        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
        double ed = ea+er*maxC;//Cik?
        
        if(_rho0 ==0)
            return true;
//        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
        return (r<=ep) && (s<=ed);
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
            double[] bikp = getuv(Vp, k.src, k.dst);// Vp.get(V.indexOf(n)).value;
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
//            double[][] _A,
            int numberOfVertices,
            int numOfFeature,
//            List<Tuple2<Integer, Vector>> x,
            List<LocalEdgeNode> _V,
            List<LocalEdgeNode> _U,
            double[][] _B
            ,
//            int[] ni,
//            double[] xAvr,
            //            double [] ui,
//            Double rho0,
            Double lamda
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
                sumdi = LocalVector.plus(sumdi, LocalVector.plus(getuv(_U, k.src, k.dst) , k.value));
            }
            if(curruntI._1 == k.dst)
            {
                sumdj = LocalVector.plus(sumdj, LocalVector.plus(getuv(_U, k.src, k.dst) , k.value));
            }
        }     
        
        double[] sumd = LocalVector.sub(sumdi, sumdj);
        double[] X = LocalVector.scale(LocalVector.plus(_B[curruntI._1],sumd), 1./(1+numberOfVertices));        
//        if(curruntI._1 == 5) LocalVector.printV(X, "pt.spark.sSCC2.updateXNode() " +curruntI._1 +": "+_rho0+ " - "+lamda, true);
        return Vectors.dense(X);
    }
    /**
     * update all node in V, each V is RDD
     * 
     */
    private static LocalEdgeNode updateVNode(List<Tuple2<Integer, Vector>> X, LocalEdgeNode V, List<LocalEdgeNode> U , double lambda, List<Tuple2<Tuple2<Integer, Integer>, Double>> edges)
    {
//        System.out.println("pt.spark.sSCC2.updateVNode() "+ V.src+" - "+V.dst);
//        List<LocalEdgeNode> ret = new LinkedList<>();
        double[] bbu = LocalVector.sub(LocalVector.sub(getRow(X, V.src), getRow(X, V.dst)), getuv(U, V.src, V.dst));
        double w = getEdgeW(edges, V.src, V.dst);
        bbu = LocalVector.proxN2_2(bbu, lambda*w);
//        LocalVector.printV(bbu, "pt.spark.sSCC2.updateVNode() "+ V.src+" - "+V.dst, true);
        LocalEdgeNode ret = new LocalEdgeNode(V.src, V.dst, bbu);
        return ret;
    }


    /**
     * update all U, each U is RDD.
     * 
     */
    private static LocalEdgeNode updateUNode(List<Tuple2<Integer, Vector>> X, List<LocalEdgeNode> V, LocalEdgeNode U)
    {
//        System.out.println();
        double[] data = LocalVector.sub(getuv(V, U.src, U.dst), LocalVector.sub(getRow(X, U.src), getRow(X, U.dst)));
        data = LocalVector.plus(U.value, data);
            
//        LocalVector.printV(data, "pt.spark.sSCC2.updateUNode() "+ U.src+" - "+U.dst, true);
        LocalEdgeNode ret = new LocalEdgeNode(U.src, U.dst, LocalVector.scale(data,1));    
        return ret;
    }  
    
   
    private static double getEdgeW(List<Tuple2<Tuple2<Integer, Integer>, Double>> E, int s, int d)
    {
        for(Tuple2<Tuple2<Integer, Integer>, Double> e: E)
        {
            if((e._1._1 == s &&  e._1._2 == d)||(e._1._2 == s &&  e._1._1 == d))
                return e._2;
        }
        return 0;
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
        
        return new double[X.get(0)._2.toArray().length];
    }
}

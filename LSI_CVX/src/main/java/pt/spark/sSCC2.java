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
    public static List<Tuple2<Integer, Vector>> run(JavaSparkContext context,
            double[][] A,// term-doc
            String outFilePath) {

        double _rho0 = 0.8;
        double _lamda = 0.6;
        double _lamda2 = 0.01;
        double _eps_abs = 1e-6;
        double _eps_rel = 1e-6;
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

//        for(Tuple2<Integer, Vector> mi: rowsList)
//        {
//            System.out.println("pt.spark.sSCC.run() "+mi._1+"\t "+ mi._2.toString());
//        }
        System.out.println("pt.spark.sSCC.run()");

//        Broadcast<Double> rho0 = context.broadcast(_rho0);
//        Broadcast<Double> lamda = context.broadcast(_lamda);
//        Broadcast<double[][]> _X = context.broadcast(X);
        Broadcast<Double> eps_abs = context.broadcast(_eps_abs);
        Broadcast<Double> eps_rel = context.broadcast(_eps_rel);
        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
        Broadcast<double[]> _xAvr = context.broadcast(xAvr);
        Broadcast<int[]> _ni = context.broadcast(ni);

        Broadcast<Integer> _numOfFeature = context.broadcast(numOfFeature);
        Broadcast<Integer> _numberOfVertices = context.broadcast(numberOfVertices);
        Broadcast<double[][]> mat = context.broadcast(A);

        //        LocalMatrix.printMat(X, "x init");
//        LocalVector.printV(xAvr, "xAvr", true);
        System.out.println("pt.spark.sSCC.run() 2 start map scc local");
        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        JavaRDD<Tuple2<Integer, Vector>> matI = context.parallelize(rowsListTermDoc);
       
        List<Tuple2<Integer, Vector>> retList = new LinkedList<>();
//        JavaPairRDD<Integer, List<Vector>> ret = new ;
        List<LocalEdgeNode> U = initU(eSet, numOfFeature);
        List<LocalEdgeNode> V = initV(eSet, A);
        List<LocalEdgeNode> U0;
        List<LocalEdgeNode> V0;
        List<Tuple2<Integer, Vector>> X0 = new LinkedList<>();
        List<Tuple2<Integer, Vector>> X1 = new LinkedList<>();
        
        JavaRDD<LocalEdgeNode> uv = context.parallelize(U);
         
        boolean stop = false;
        while(stop)
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
//transform and broadcast data to parallel
        Broadcast<Double> rho0 = context.broadcast(_rho0);
        Broadcast<Double> lamda = context.broadcast(_lamda);
//        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
        Broadcast<double[][]> _X = context.broadcast(X);
        Broadcast<List<LocalEdgeNode>> _U = context.broadcast(U);
        Broadcast<List<LocalEdgeNode>> _V = context.broadcast(V);
        Broadcast<List<Tuple2<Integer, Vector>>> _x = context.broadcast(X0);
        
        V0= V;
        U0= U;
        X1 = X0;
//update X, V, U
//            updateXNode();
            
            JavaPairRDD<Integer, Vector> ret = matI.mapToPair((Tuple2<Integer, Vector> t1)
                    -> {
    //            System.out.println("pt.spark.sSCC.run() driver "+t1._1+"\t "+ t1._2.toString());
                return new Tuple2<>(t1._1,
                        updateXNode(t1,
                                mat.value(),
                                _numberOfVertices.value(),
                                _numOfFeature.value(),
                                E.value(),
                                _X.value(),
                                _x.value(),
                                _V.value(),
                                _U.value(),
                                _ni.value(),
                                _xAvr.value(),
                                rho0.value(),
                                lamda.value(),
                                eps_abs.value(),
                                eps_rel.value()));
                }
            );
            ret.cache();
            
            retList = ret.collect();
            X0 =         ret.collect();
            JavaRDD<LocalEdgeNode> vc=  uv.map(new Function<LocalEdgeNode, LocalEdgeNode>() {
            @Override
            public LocalEdgeNode call(LocalEdgeNode v1) throws Exception {
                
                return updateVNode();                
            }
            });
            vc.cache();
            V= vc.collect();
            JavaRDD<LocalEdgeNode> uc=  uv.map(new Function<LocalEdgeNode, LocalEdgeNode>() {
            @Override
            public LocalEdgeNode call(LocalEdgeNode v1) throws Exception {
                return updateUNode();                
            }
            });
            uc.cache();
                    
            U=uc.collect();
            
//checkstop            
            checkStop(A, X0, U, V0, V, _rho0, _lamda, _rho0, numberOfVertices);
//update rho
            updateRho(_rho0, _rho0, _rho0);
//update lambda
            _lamda = _lamda * 1.05;
            
            rho0.destroy();
            lamda.destroy();
            _X.destroy();
            _x.destroy();
            _V.destroy();
            _U.destroy();
        }



//        double[][] retArray = new double[numOfFeature][numOfFeature];

//        List<Tuple2<Integer, List<Vector>>> retList = ret.collect();
//        ret.saveAsTextFile(outFilePath + "/scc");
        System.out.println("pt.spark.sSCC.run() end");
        
//        rho0.destroy();
//        lamda.destroy();
//        _X.destroy();

        eps_abs.destroy();
        eps_rel.destroy();
        E.destroy();
        _xAvr.destroy();
        _ni.destroy();

        _numOfFeature.destroy();
        _numberOfVertices.destroy();
        mat.destroy();
        
        return retList;
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

/*        System.out.println("paper.SCC.getCluster() " + cluster.size());
        for (int i = 0; i < cluster.size(); i++) {
            List<Integer> sub = cluster.get(i);
            System.out.print("Cluster " + i + ":\t");
            for (int j = 0; j < sub.size(); j++) {
                System.out.print(sub.get(j) + "\t");
            }
            System.out.println("");
        }
*/
        return cluster;
    }
    
    
    private static double updateRho(double r, double s, double rho)
    {
        if(r>8*s)
            rho =  Double.valueOf((rho* 0.5));//(r/s);//2*rho;
        if(s>8*r)
            rho =  Double.valueOf((rho* 2));//(r/s);//rho/2;
        return rho;
    }
    
    private static boolean checkStop(double[][] A, List<Tuple2<Integer, Vector>> X0, List<LocalEdgeNode> U, List<LocalEdgeNode> V0, List<LocalEdgeNode> V,  
            double rho, double ea, double er, int numberOfVertices)
    {
        double r = primalResidual(X0.get(0)._2.toArray(),V0);
        double s = dualResidual(V0, V, rho, numberOfVertices);
//        System.err.println("rho "+rho);
        updateRho(r, s, rho);
        
        double maxAB= 0;
        for(LocalEdgeNode b:V)
        {
            double be = LocalVector.norm(b.relatedValue);
            double a = LocalVector.norm(A[b.source]);
            double ab = (a>be)? a:be;
            maxAB = (ab>maxAB)? ab:maxAB;
        }
        
        double maxC = 0;
        for(LocalEdgeNode c:U)
        {
            double value = LocalVector.norm(c.relatedValue);
            maxC = (value>maxC)? value:maxC;
        }
        double ep = ea*Math.sqrt(numberOfVertices)+er*maxAB; //Bik?
        double ed = ea+er*maxC;//Cik?
        
        if(rho ==0)
            return true;
//        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
        return (r<=ep) && (s<=ed);
    }
    
     
    private static double primalResidual(double[] X0, List<LocalEdgeNode> V0)
    {
        double ret = 0;
        for(LocalEdgeNode n: V0)
        {
            double normR = LocalVector.norm(LocalVector.plus(X0, n.relatedValue));
            ret = (ret>normR)?ret:normR;
        }
        return ret;
    }
    private static double primalResidual(double[][] X0, List<LocalEdgeNode> V0, int i)
    {
        double ret = 0;
        double[] x= LocalMatrix.getRow(X0, i);
        for(LocalEdgeNode n: V0)
        {
            double normR = LocalVector.norm(LocalVector.plus(x, n.relatedValue));
            ret = (ret>normR)?ret:normR;
        }
        return ret;
    }
        
    private static double dualResidual(List<LocalEdgeNode> Vp, List<LocalEdgeNode> V,  double rho, int numOfFeature)
    {
        double ret = 0;
        for(LocalEdgeNode n: V)
        {
            double[] bikp = getUVData(V, n.source, n.dest, numOfFeature).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = LocalVector.scale(LocalVector.plus(bikp, LocalVector.scale(n.relatedValue, -1)),rho);
            double normS = LocalVector.norm(ai);
            ret = (ret>normS)?ret:normS;
        }
        return ret;
    }
    private static double dualResidual(List<LocalEdgeNode> Vp, List<LocalEdgeNode> V, int i,  double rho, int numOfFeature)
    {
        double ret = 0;
        
        for(LocalEdgeNode n: V)
        {
            double[] bikp = getUVData(V, n.source, n.dest, numOfFeature).relatedValue;// Vp.get(V.indexOf(n)).relatedValue;
            double[] ai = LocalVector.scale(LocalVector.plus(bikp, LocalVector.scale(n.relatedValue, -1)),rho);
            double normS = LocalVector.norm(ai);
            ret = (ret>normS)?ret:normS;
        }
        return ret;
    }    

    private static LocalEdgeNode getUVData(List<LocalEdgeNode> A, int s, int d, int numOfFeature)
    {
//        double[] ret = new double[numOfFeature];
        
        for(LocalEdgeNode e: A)
            if(e.source == s && e.dest ==d)
            {
                return e;//.relatedValue;
            }
//        System.out.println("paper.NodeSCC.getUVData() nul "+s+" "+d);
        return new LocalEdgeNode(s, d, new double[numOfFeature]);
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
            double[][] _A,
            int numberOfVertices,
            int numOfFeature,
            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
            double[][] _X,
            List<Tuple2<Integer, Vector>> x,
            List<LocalEdgeNode> _V,
            List<LocalEdgeNode> _U,
            int[] ni,
            double[] xAvr,
            //            double [] ui,
            Double rho0,
            Double lamda,
            //            Double lamda2,
            Double eps_abs,
            Double eps_rel)
    {           
        
        double[] sumdi = new double[numOfFeature];
        double[] sumdj = new double[numOfFeature];
               
        // (sum(j>i)(ui-zi)-sum(i>j)(uj-zj))
        //TODO: review i>j and i>j???
        /**
         *
        for(Key k: V.E.keySet())
        {
            if(i == k.src)
            {
                sumdi = Vector.plus(sumdi, Vector.plus(U.get(k), V.get(k)));
            }
            if(i == k.dst)
            {
                sumdj = Vector.plus(sumdj, Vector.plus(U.get(k), V.get(k)));
            }
        }
//        double[] sumd = Vector.sub(sumdi, sumdj);
//        if(i==1)    Vector.printV(sumd,"X "+i,true);        
        //Vector.plus(A[i], Vector.scale(xAvr, numberOfVertices));
        X[i] = Vector.scale(Vector.plus(B,sumd), 1./(1+numberOfVertices));
         */
        
        double[] sumd = LocalVector.sub(sumdi, sumdj);
        
        return Vectors.dense(sumd);
    }
    /**
     * update all node in V, each V is RDD
     * 
     */
    private static LocalEdgeNode updateVNode()
    {
//        List<LocalEdgeNode> ret = new LinkedList<>();
        LocalEdgeNode ret = new LocalEdgeNode(0, 0, new double[10]);
        return ret;
    }
//    private List<LocalEdgeNode> updateV(List<LocalEdgeNode> V, List<LocalEdgeNode> U) throws Exception //B
//    {
////        System.out.println("paper.AMA.updateV()");
//        List<LocalEdgeNode> ret = new LinkedList<>();
//
//        V.E.keySet().stream().forEach((v) -> {
//            double[] bbu = Vector.sub(Vector.sub(X[v.src], X[v.dst]), U.get(v));
//            double w = Edge.getEdgeW(edges, v.src, v.dst);
//            bbu = Vector.proxN2_2(bbu, lambda*w);
//            ret.put(v.src, v.dst, bbu);
//        });//
//        return ret;
//    }

    /**
     * update all U, each U is RDD.
     * 
     */
    private static LocalEdgeNode updateUNode()
    {
        LocalEdgeNode ret = new LocalEdgeNode(0, 0, new double[10]);
        return ret;
    }  
    
//    private ListENode updateU(ListENode U, ListENode V) throws Exception //C
//    {
//        ListENode ret = new ListENode();
//        V.E.keySet().stream().forEach((v) -> {
////            if(v.scr == 50)  System.out.println("V D "+v.scr+" "+v.dst);
//            double[] data = Vector.sub(V.get(v), Vector.sub(X[v.src], X[v.dst]));
//            data = Vector.plus(U.get(v), data);
////            if(v.src == 5 && v.dst ==90)                Vector.printV(data, "U in "+v.src+" "+v.dst, true);
//
//            ret.put(v.src, v.dst, Vector.scale(data,1));//, Edge.getEdgeW(edges, v.src, v.dst)));
//        });
//        return ret;
//    }
}

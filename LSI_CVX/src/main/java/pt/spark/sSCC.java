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
import org.apache.spark.api.java.function.Function;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.mllib.linalg.distributed.CoordinateMatrix;
import scala.Function1;

/**
 * sSCC class, we will call this class to clustering data, return the row in
 * same cluster - map data to master, calculate edge weigh - each executer
 * calculate row data by: read Matrix, update D,X,V,U D,V,U related to edge
 */
public class sSCC {

    /**
     * We use a logger to print the output. Sl4j is a common library which works
     * with log4j, the logging system used by Apache Spark.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(sSCC.class);

    /**
     * The task body
     */
    public static JavaRDD<Tuple2<Integer, Vector>> run(String master, 
            LinkedList<Tuple2<Integer, Vector>> rowsList, 
            String inputFilePath, 
            String outFilePath) {
        

        int numberOfVertices = (int)rowsList.size();
        int numOfFeature = rowsList.get(0)._2.size();
////TODO: init global data : X, u, xAvr        
        int [] ni = retSize(numberOfVertices);
////        Matrix.printMat(A, "centered");
//        //Init
        double [][] X = new double[numberOfVertices][numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            double x = 1- ((lambda2)/pt.paper.LocalVector1D.norm(pt.paper.LocalVector2D.getCol(A, i)));
//            x = (x>0)? x:0;
//            //update by column i
//            for(int j = 0 ; j< numberOfVertices; j++)
//            {
//                X[j][i] = x*A[j][i];
//            }
//        }
//        
        double[] u = new double[numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            u[i] = pt.paper.LocalVector1D.norm(pt.paper.LocalVector2D.getCol(X, i));
//        }
//        u = pt.paper.LocalVector1D.scale(u, lambda2);        
        double[] xAvr = new double[numOfFeature];
//        for(int i = 0; i< numOfFeature; i++)
//        {
//            double x = 1- (u[i]/pt.paper.LocalVector1D.norm(pt.paper.LocalVector2D.getCol(A, i)));
//            x = (x>0)? x:0;
//            for(int j = 0 ; j< numberOfVertices; j++)
//            {
//                X[j][i] = x*A[j][i];
//            }
//            xAvr[i] = pt.paper.LocalVector1D.avr(pt.paper.LocalVector2D.getCol(A, i));
//        }

        List<Tuple2<Tuple2<Integer, Integer>, Double>> eSet = buildE(rowsList);
        
//    String master = "local[*]";
//    /*
//     * Initialises a Spark context.
//     */
        SparkConf conf = new SparkConf()
                .setAppName(sSCC.class.getName())
                .setMaster(master);
        JavaSparkContext context = new JavaSparkContext(conf);

        JavaRDD<Tuple2<Integer, Vector>> matI = context.parallelize(rowsList);
        
        for(Tuple2<Integer, Vector> mi: matI.collect())
        {
            System.out.println("pt.spark.sSCC.run() "+mi._1+"\t "+ mi._2.toString());
        }
        
        System.out.println("pt.spark.sSCC.run()");
//        CoordinateMatrix Cm = null;
        
//  double rho0 = 0.8;
//  double lamda = 0.6;
//  double lamda2 = 0.6;
//  double eps_abs= 1e-6;
//  double eps_rel= 1e-6;
        // (<r,c>,w)


        Broadcast<Double> rho0 = context.broadcast(0.8);
        Broadcast<Double> lamda = context.broadcast(0.6);
        Broadcast<Double> lamda2 = context.broadcast(0.01);
        Broadcast<Double> eps_abs = context.broadcast(1e-6);
        Broadcast<Double> eps_rel = context.broadcast(1e-6);
        Broadcast<List<Tuple2<Tuple2<Integer, Integer>, Double>>> E = context.broadcast(eSet);
        Broadcast<JavaRDD<Tuple2<Integer, Vector>>> mat = context.broadcast(matI);


        Broadcast<double[]> _u = context.broadcast(u);
        Broadcast<double[]> _xAvr = context.broadcast(xAvr);
        Broadcast<int[]> _ni = context.broadcast(ni);
        Broadcast<double[][]> _X = context.broadcast(X);
        Broadcast<Integer> _numOfFeature = context.broadcast(numOfFeature);
        Broadcast<Integer> _numberOfVertices = context.broadcast(numberOfVertices);
        // each solveADMM process for 1 column of input matrix -> input is rdd<vector>
        
        matI.cache();
        
        JavaRDD<Tuple2<Integer, Vector>> ret = matI.map((Tuple2<Integer, Vector> t1) -> new Tuple2<>(t1._1, 
                solveADMM(t1, 
                        mat.value(),
                        _numberOfVertices.value(),
                        _numOfFeature.value(),
                        E.value(), 
                        _X.value(),
                        _ni.value(),
                        _xAvr.value(),
                        rho0.value(), 
                        lamda.value(), 
                        lamda2.value(), 
                        eps_abs.value(), 
                        eps_rel.value()))//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        );

//        Cm.toRowMatrix().rows().map(new Function1<Vector, U>, ct);
        
        context.stop();
        System.out.println("pt.spark.sSCC.run() end");
        return ret;
    }

    private static Vector solveADMM(Tuple2<Integer, Vector> curruntI,
            JavaRDD<Tuple2<Integer, Vector>> mat,
            int numberOfVertices,
            int numOfFeature,
            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
            double[][] _X,
            int[]   ni,
            double [] xAvr,
            Double rho0,
            Double lamda,
            Double lamda2,
            Double eps_abs,
            Double eps_rel) throws IOException {
        /*
TODO: 
        - init U,V,D,X0
      (<r,c>,v[])
         */
        System.out.println("pt.spark.sSCC.solveADMM() "+curruntI._1);
        double[][] _A = new double[numberOfVertices][numOfFeature];// rebuild from mat
        
        List<Tuple2<Integer, Vector>> matT = mat.collect();
        for(int i = 0; i< matT.size(); i++)
        {
            System.arraycopy(matT.get(i)._2, 0, _A[matT.get(i)._1], matT.get(i)._1*matT.get(i)._2.size(), matT.get(i)._2.size());
        }
        
        List<LocalEdge> _edges = new ArrayList(); //rebuild from e
        for(int i =0; i< e.size(); i++)
        {
            _edges.add(new LocalEdge(e.get(i)._1._1, e.get(i)._1._2, e.get(i)._2));
        }
       
        NodeSCC xNode = new NodeSCC(curruntI._1, 
                _A, 
                _X[curruntI._1],
                _edges,
                ni, 
                xAvr,
                lamda,lamda2, rho0, 
                eps_abs, eps_rel);
        return Vectors.dense(xNode.X);
    }

//    private static boolean checkStop(Tuple2<Integer, Vector> currentX,
//            Vector x0,
//            double cRho,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> z,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> u,
//            double ea,
//            double er) {
//        double r = eps_dual();
//        double s = eps_pri();
//
//        cRho = updateRho(cRho, r, s);
//
//        double ep = ea*Math.sqrt(x0.size())+er;//*maxAB; //Bik?
//        double ed = ea+er;//*maxC;//Cik?
//        
//        if(cRho ==0)
//            return true;
////        System.err.println("new rho "+rho+": "+r+" - "+s +"\t"+ep+":"+ed);
//        return (r<=ep) && (s<=ed);
//    }
//
//    private static List<Tuple2<Tuple2<Integer, Integer>, Vector>> calD(List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> U,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> V,
//            Tuple2<Integer, Vector> curruntI,
//            Vector X0) {
//        int id = curruntI._1;
//        List<Tuple2<Tuple2<Integer, Integer>, Vector>> ret = new ArrayList();
//        for(int i = 0; i< e.size(); i++)
//        {
//            Tuple2<Tuple2<Integer, Integer>, Double> ei = e.get(i);
//            if(ei._1._1 == id || ei._1._2 == id)
//            {
//                Tuple2<Tuple2<Integer, Integer>, Vector> ui = U.get(i);
//                Tuple2<Tuple2<Integer, Integer>, Vector> vi = V.get(i);
//                
//                
//                double[] d = LocalVector1D.plus(X0.toArray(),  LocalVector1D.scale(vi._2.toArray(),-1));
//                d =         LocalVector1D.plus(d, ui._2.toArray());
//                ret.add(new Tuple2<>(ei._1, Vectors.dense(d)));
//            }
//            else
//                ret.add(new Tuple2<>(ei._1, Vectors.dense(new double[X0.size()])));
//        }
//        return ret;
//    }
//    private double[][] updateX(List<EdgeNode> D, int[] n, int i, double xAvr[])
//    {
//            double[] sumd = new double[numOfFeature];
//            
//            for(EdgeNode d:D)
//            {
//                if(d.source == i)
//                {
//                    sumd= Vector.plus(sumd,d.relatedValue);
//                }
//            }
//            sumd = Vector.scale(sumd, rho/n[i]);
////            Vector.printV(sumd, "sum "+i+" "+n[i], true);
////            X[i] = Vector.plus(X[i], sumd);
////            if(Vector.isZeroVector(sumd))
////                stop = true;
//            
//            for(int j = 0; j<numOfFeature; j++)
//            {
////                double x=  ;
//                X[i][j] = xAvr[j]+sumd[j];///(1+rho)); ///(rho*D.size()
//                if(Double.isNaN(X[i][j]))
//                    X[i][j]= 0;
////                X[i][j] = (x + (sumd[j]))/(1+(rho*n[i])); 
////                X[i][j] = X[i][j]+sumd[j];
////                X[i][j] = Double.valueOf(twoDForm.format(X[i][j]));
//            } 
//            
////        Vector.printV(X[i],"X"+i,true);    
//        return X;
//    }
//    private static Vector updateX(Tuple2<Integer, Vector> currentX,
//            JavaRDD<Tuple2<Integer,Vector>> mat, 
//            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> D) {
//        double[] xArr = new double[10];
//        
//        return Vectors.dense(xArr);
//    }

//    private static List<Tuple2<Tuple2<Integer, Integer>, Vector>> initU(List<Tuple2<Tuple2<Integer, Integer>, Double>> e, Tuple2<Integer, Vector> curruntI) {
//        List<Tuple2<Tuple2<Integer, Integer>, Vector>> ret = new ArrayList();
//        int id = curruntI._1;
//        for(int i = 0; i< e.size(); i++)
//        {
//            if(e.get(i)._1._1 == id)
//            {
//                ret.add(new Tuple2<>(e.get(id)._1, Vectors.dense(new double[curruntI._2.size()])));
//            }
//        }
//        return ret;
//    }
    
//    private List<pt.paper.EdgeNode> initU(int i, double [][]A, List<pt.paper.LocalEdge> edges)
//    {
//        List<pt.paper.EdgeNode> ret = new ArrayList<>();
////        HashMap<String, double[]> a = new HashMap<>();
//        int numOfFeature = A[0].length;
////        if(null!= a.put( "s-d", 1.))
////                    System.out.println("paper.NodeSCC.initU() insert ok "+i);
//        
//        for(pt.paper.LocalEdge e:edges)
//        {
////            System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
//            if(e.sourcevertex == i || e.destinationvertex == i)
//            {
////                System.out.println("paper.NodeSCC.initU() E "+e.sourcevertex+ " "+e.destinationvertex );
//                //ik
//                ret.add(new pt.paper.EdgeNode(e.sourcevertex, e.destinationvertex, new double[numOfFeature]));
//                //ki
//                ret.add(new pt.paper.EdgeNode(e.destinationvertex, e.sourcevertex, new double[numOfFeature]));
//                
////                double[] v= new double[numOfFeature];
////                int [] k = new int[]{e.destinationvertex, e.sourcevertex};
////                if(a.put( k[0]+"-"+k[1], v)!= null)
////                    System.out.println("paper.NodeSCC.initU() insert ok "+i);
//            }
//        }
////        for(EdgeNode e: ret)
////            System.out.println("paper.NodeSCC.initU() "+e.source+ " "+e.dest );
////        System.out.println("paper.NodeSCC.initU() "+ret.size());
//        return ret;        
//    }
    
        //Ue = Ue + (Ai - Ve)
//    private List<EdgeNode> updateU(List<EdgeNode> U, List<EdgeNode> V) //C
//    {
//        List<EdgeNode> ret = new ArrayList<>();
//        for(EdgeNode e: U)
//        {
//            EdgeNode ve= getUVData(V, e.source, e.dest);// V.get(U.indexOf(e));
//            if((e.source != ve.source) || (e.dest != ve.dest))
//                System.out.println("paper.SCC.updateU()wrong ve "+e.source+ " "+e.dest+" "+ve.source+" "+ve.dest);
//            //Ai - Be
//            double[] data = Vector.plus(Matrix.getRow(X, e.source),Vector.scale(ve.relatedValue, -1));
//            //data = Vector.scale(data, rho);
//            data = Vector.plus(e.relatedValue, data);
//            EdgeNode updateU = new EdgeNode(e.source, e.dest, data);
////            Vector.printV(updateU.relatedValue, "updateU:"+e.source+""+e.dest, true);
//            ret.add(updateU);
//        }
//        return ret;        
//    }
//    private static List<Tuple2<Tuple2<Integer, Integer>, Vector>> updateU(Tuple2<Integer, Vector> currentX,
////            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
//            Vector x, 
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> U,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> V) {
//        List<Tuple2<Tuple2<Integer, Integer>, Vector>> ret = new ArrayList();
//        
//        for(Tuple2<Tuple2<Integer, Integer>, Vector> ui:U)
//        {
//            Tuple2<Tuple2<Integer, Integer>, Vector> vi = V.get(U.indexOf(ui));
//            if(!ui._1().equals(vi._1))
//                 System.out.println("paper.SCC.updateU()wrong ve ");
//            double[] data = LocalVector1D.plus(x,LocalVector1D.scale(vi._2, -1));
//            
//            ret.add(new Tuple2<>(ui._1, Vectors.dense(data)));
//        }
//        return ret;
//    }


//    private static List<Tuple2<Tuple2<Integer, Integer>, Vector>> initV(List<Tuple2<Tuple2<Integer, Integer>, Double>> e, Tuple2<Integer, Vector> curruntI) {
//        List<Tuple2<Tuple2<Integer, Integer>, Vector>> ret = new ArrayList();
//        int id = curruntI._1;
//        
//        for(int i = 0; i< e.size(); i++)
//        {
//            if(e.get(i)._1._1 == id)
//            {
//                ret.add(new Tuple2<>(e.get(id)._1, Vectors.dense(new double[curruntI._2.size()])));
//            }
//        }
//        return ret;
//    }
//    
//    private List<pt.paper.EdgeNode> initV(int i, double [][]A, List<pt.paper.LocalEdge> edges)
//    {
//        List<pt.paper.EdgeNode> ret = new ArrayList<>();
////        System.out.println("paper.NodeSCC.initV() "+(i+1));
//        for(pt.paper.LocalEdge e:edges)
//        {
////            System.out.println("paper.AMA.buildUV() "+e.sourcevertex+" "+e.destinationvertex);
//            if(e.sourcevertex == i || e.destinationvertex == i)
//            {
//                System.out.println((e.sourcevertex+1)+":"+(e.destinationvertex+1));
//                //ik
//                double[] value1 = pt.paper.LocalVector2D.getRow(A, e.sourcevertex) ;//new double[dataLength];
//                ret.add(new pt.paper.EdgeNode(e.sourcevertex, e.destinationvertex, value1));
//                //ki
//                double[] value2 = pt.paper.LocalVector2D.getRow(A, e.destinationvertex) ;//new double[dataLength];
//                ret.add(new pt.paper.EdgeNode(e.destinationvertex, e.sourcevertex, value2));
//            }
//        }
////        for(EdgeNode e: ret)
////            System.out.println("paper.NodeSCC.initV() "+e.source+ " "+e.dest );
////        System.out.println("paper.NodeSCC.initV() " +ret.size());
//        return ret;        
//    }
//        private List<EdgeNode> updateV(List<EdgeNode> V, List<EdgeNode> U, int i) //B
//    {
////        System.out.println("paper.AMA.updateV()");
//        List<EdgeNode> ret = new ArrayList<>();
//        
////        for(EdgeNode e: V)
////        {
////                double[] Bi = getUVData(V, e.source, e.dest).relatedValue;//vik
////                double[] Bk = getUVData(V, e.dest, e.source).relatedValue;//vki
////                double[] Ci = getUVData(U, e.source, e.dest).relatedValue;//uik
////                double[] Ck = getUVData(U, e.dest, e.source).relatedValue;//uki
////                
////                double[] Ai =Matrix.getCol((i==e.source)?A:A, (i==e.source)?e.source:e.dest); //n
////                double[] Ak =Matrix.getCol((i==e.source)?A:A, (i==e.source)?e.dest:e.source);
////
////                double[] AiCi = Vector.plus(Ai,Ci); // u get ik
////                double[] AkCk = Vector.plus(Ak,Ck); // u get ki
////
////                double n = 1- ((lambda)/(Vector.norm(Vector.plus(Vector.plus(Bi, Vector.scale(Bk, -1)),Vector.plus(Ai, Vector.scale(Ak, -1))))/rho));
////                double theta = (0.5>n)? 0.5:n; //max
////
////    //            System.out.println("paper.SCC.updateV() "+ theta);
////
////                double[] b_ik = Vector.plus(Vector.scale(AiCi, theta), Vector.scale(AkCk, 1- theta));
////                ret.add(new EdgeNode(e.source, e.dest, b_ik));
////
//////                double[] b_ki = Vector.plus(Vector.scale(AiCi, 1- theta), Vector.scale(AkCk, theta));
//////                ret.add(new EdgeNode(e.dest, e.source, b_ki));
////        }
//        for(Edge e: edges)
//        {
//            if(e.sourcevertex == i || e.destinationvertex==i)
//            {
//                double[] Bi = null;//vik
//                double[] Bk = null;//vki
//                double[] Ci = null;//uik
//                double[] Ck = null;//uki
//                int get = 0;
//    //            System.out.println(e.sourcevertex+" "+e.destinationvertex);
//                for(EdgeNode B:V)
//                {
//                    EdgeNode C = getUVData(U, B.source, B.dest);// U.get(V.indexOf(B));
//                    if((B.source != C.source) || (B.dest != C.dest))
//                        System.out.println("paper.SCC.updateV()wrong ve "+B.source+ " "+B.dest+" "+C.source+" "+C.dest);
//
//                    if(B.source == e.sourcevertex && B.dest == e.destinationvertex)
//                    {
//                        Bi = B.relatedValue;
//                        Ci = C.relatedValue;
//                        get++;
//                    }
//                    if(B.source == e.destinationvertex && B.dest == e.sourcevertex)
//                    {
//                        Bk = B.relatedValue; 
//                        Ck = C.relatedValue; 
//                        get++;
//                    }
//
//                    if(get==2)
//                        break;
//                }
//    //                System.out.println("paper.SCC.updateV() "+ i+ ": "+e.sourcevertex+"-"+e.destinationvertex);
//                double[] Ai =Matrix.getRow((i==e.sourcevertex)?A:A, (i==e.sourcevertex)?e.sourcevertex:e.destinationvertex); //n
//                double[] Ak =Matrix.getRow((i==e.sourcevertex)?A:A, (i==e.sourcevertex)?e.destinationvertex:e.sourcevertex);
//
//                double[] AiCi = Vector.plus(Ai,Ci); // u get ik
//                double[] AkCk = Vector.plus(Ak,Ck); // u get ki
//
//                double n = 1- ((lambda*e.weight)/(Vector.norm(Vector.plus(Vector.plus(Bi, Vector.scale(Bk, -1)),Vector.plus(Ai, Vector.scale(Ak, -1))))/rho));
//                double theta = (0.5>n)? 0.5:n; //max
//
//    //            System.out.println("paper.SCC.updateV() "+ theta);
//
//                double[] b_ik = Vector.plus(Vector.scale(AiCi, theta), Vector.scale(AkCk, 1- theta));
//                ret.add(new EdgeNode(e.sourcevertex, e.destinationvertex, b_ik));
//
//                double[] b_ki = Vector.plus(Vector.scale(AiCi, 1- theta), Vector.scale(AkCk, theta));
//                ret.add(new EdgeNode(e.destinationvertex, e.sourcevertex, b_ki));
//
////            Vector.printV(b_ik, "Bik", true);
////            Vector.printV(b_ki, "Bki", true);
//            }
//        }
//        return ret;        
//    }
//    private static List<Tuple2<Tuple2<Integer, Integer>, Vector>> updateV(Tuple2<Integer, Vector> currentX,
//            List<Tuple2<Tuple2<Integer, Integer>, Double>> e,
//            Vector x, 
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> U,
//            List<Tuple2<Tuple2<Integer, Integer>, Vector>> V) {
//        List<Tuple2<Tuple2<Integer, Integer>, Vector>> ret = new ArrayList();
//        
//        for(Tuple2<Tuple2<Integer, Integer>, Double> ei:e)
//        {
//            
//            ret.add(new Tuple2<>(ei._1, x));
//        }
//        return ret;
//    }
//
//
//    private static double updateRho(double rho, double r, double s) {
//        if (r > 8 * s) {
//            rho = rho * 0.5;//(r/s);//2*rho;
//        }
//        if (s > 8 * r) {
//            rho = rho * 2;//(r/s);//rho/2;
//        }
//        return rho;
//    }
//
//    private static double eps_dual() {
//        
//        return 0;
//    }
//
//    private static double eps_pri() {
//        
//        return 0;
//    }

//    private double primalResidual(double[][] X0, ListENode V0)
//    {
//        double ret = 0;
//        for(int[] key: V0.keySet())
//        {
//            double normR = Vector.norm(Vector.plus(Matrix.getRow(X0, key[0]), V0.get(key[0], key[1])));
//            ret = (ret>normR)?ret:normR;
//        }
//        return ret;
//    }
        
    private static List<Tuple2<Tuple2<Integer, Integer>, Double>> buildE(LinkedList<Tuple2<Integer, Vector>> rowsList) {
        List<Tuple2<Tuple2<Integer, Integer>, Double>> ret = new ArrayList<>();
        for(int i = 0; i< rowsList.size(); i++)
        {
            for(int j = i+1; j< rowsList.size(); j++)
            {
                double value =  LocalVector1D.cosSim(rowsList.get(i)._2.toArray(), rowsList.get(j)._2.toArray());
                ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(i)._1,rowsList.get(j)._1),value));
                ret.add(new Tuple2<>(new Tuple2<>(rowsList.get(j)._1,rowsList.get(i)._1),value));
            }
        }
        return ret;
    }
    private static int[] retSize(int numberOfVertices)
    {
        int [] ret = new int[numberOfVertices];
//        for(pt.paper.LocalEdge e: edges)
//        {
//            ret[e.sourcevertex] = ret[e.sourcevertex]+1;
//            ret[e.destinationvertex] = ret[e.destinationvertex]+1;
//        }
        return ret;
    }
}

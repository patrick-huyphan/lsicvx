/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tint.IntFactory1D;
import cern.colt.matrix.tint.IntFactory2D;
//import com.joptimizer.exception.JOptimizerException;
//import com.joptimizer.functions.BarrierFunction;
//import com.joptimizer.functions.ConvexMultivariateRealFunction;
//import com.joptimizer.functions.FunctionsUtils;
//import com.joptimizer.functions.LinearMultivariateRealFunction;
//import com.joptimizer.functions.LogTransformedPosynomial;
//import com.joptimizer.functions.QuadraticMultivariateRealFunction;
//import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
//import com.joptimizer.functions.SDPLogarithmicBarrier;
//import com.joptimizer.functions.SOCPLogarithmicBarrier;
//import com.joptimizer.functions.SOCPLogarithmicBarrier.SOCPConstraintParameters;
//import com.joptimizer.optimizers.BIPLokbaTableMethod;
//import com.joptimizer.optimizers.BIPOptimizationRequest;
//import com.joptimizer.optimizers.BarrierMethod;
//import com.joptimizer.optimizers.JOptimizer;
//import com.joptimizer.optimizers.LPOptimizationRequest;
//import com.joptimizer.optimizers.LPPrimalDualMethod;
//import com.joptimizer.optimizers.NewtonLEConstrainedFSP;
//import com.joptimizer.optimizers.NewtonUnconstrained;
//import com.joptimizer.optimizers.OptimizationRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import static pt.paper.Paper.PaperRuner;

/**
 *
 * @author patrick_huy
 */
public class main {

    public static void main(String[] args) throws IOException, Exception {
        // TODO code application logic here

//        jopLP();
//        jopQP();
//        jopQQP();
//        jopGP();
//        jopSP();
//        jopSCP();
//        jopBIP();
        
        double[][] DQ = CSVFile.readMatrixData("../data/data_696_1109.csv"); //data_697_3187
//        double[][] DQ = Matrix.int2double(ReadData.readDataTest(data.inputJobSearch));
        int n = DQ.length;
        int q = 10;
//        CSVFile.saveMatrixData("DQ", DQ, "DQ");

        double[][] D = Matrix.subMat(DQ, 0, n - q, 0, DQ[0].length);
//        Matrix.printMat(D, "D init");
        double[][] Q = Matrix.subMat(DQ, n - q, q, 0, DQ[0].length);
//        Matrix.printMat(Q, "Q init");

        PaperRuner(D, Q, 10);

//        double[][] docTerm = CSVFile.readMatrixData("../data/data_697_3187.csv"); //data_696_1109
//        double[][] testD = Matrix.subMat(docTerm, 0, docTerm.length -10, 0, docTerm[0].length);
//        double[][] testQ = Matrix.subMat(docTerm, docTerm.length-10, 10, 0, docTerm[0].length);
//        PaperRuner(testD,testQ, 30);
//        //printMat(docTerm, false,"docTerm");
//
//
//      Paper run = new Paper(docTerm,"echelon.csv");
    }

    /**
     * min: c
     * s: Ax = b or Gx = h 
     * C: (-1)x+(-1)y=0
     * Ax = b: 
     *  4/3x    -y = 2
     *  -x/2    +y = 1/2
     *  -2x     -y = 2
     *  x/3     +y = 1/2
     * @throws JOptimizerException 
     * 
     * A = matrix([ [-1.0, -1.0, 0.0, 1.0], [1.0, -1.0, -1.0, -2.0] ])
     * b = matrix([ 1.0, -2.0, 0.0, 4.0 ])
     * c = matrix([ 2.0, 1.0 ])
     */
//    
//    private static void jopLP() throws JOptimizerException {
//        double[] c = new double[]{2., 1.};
//
//        //Inequalities constraints
////        double[][] G = new double[][]{{4. / 3., -1},
////        {-1. / 2., 1.},
////        {-2., -1.},
////        {1. / 3., 1.}};
////        double[] h = new double[]{2., 1. / 2., 2., 1. / 2.};
//
//        double[][] G = new double[][]{
//            {-1., 1.},
//            {-1., -1.},
//            {0, -1.},
//            {1., -2.},
//        };
//        double[] h = new double[]{1.,-2., 0, 4.};
//        //Bounds on variables
//        double[] lb = new double[]{0, 0};
//        double[] ub = new double[]{10, 10};
//
//        //optimization problem
//        LPOptimizationRequest or = new LPOptimizationRequest();
//        or.setC(c);
//        or.setG(G);
//        or.setH(h);
////        or.setLb(lb);
////        or.setUb(ub);
//        or.setDumpProblem(true);
//
//        //optimization
//        LPPrimalDualMethod opt = new LPPrimalDualMethod();
//
//        opt.setLPOptimizationRequest(or);
//        opt.optimize();
//
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE() LP :" + s);
//        }
//    }
//
///**
// *  minimizex (1/2)xTPx+qTx+r  s.t. 
// *  inequalities: Gx ≤ h 
// *  equalities: Ax = b,  
// *  
// *  x = (P+rho ATA)^-1 (rho ATc - q)
// * 
// * @throws JOptimizerException 
// */
//    private static void jopQP() throws JOptimizerException {
//
//        double[][] P = new double[][]{{1., 0.4}, {0.4, 1.}};
////        double[][] P = new double[][]{{0., 0.}, {0., 0.}};
//        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);
////        QuadraticMultivariateRealFunction objectiveFunction = new QuadraticMultivariateRealFunction(null, null, 0);
//
//        //equalities : x+y = 1
//        double[][] A = new double[][]{{1, 1}}; 
//        double[] b = new double[]{1};
//
//        //inequalities
//        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[2];
//        inequalities[0] = new LinearMultivariateRealFunction(new double[]{-1, 0}, 0); // -x < 0
//        inequalities[1] = new LinearMultivariateRealFunction(new double[]{0, -1}, 0); // -y < 0
//
//        //optimization problem
//        OptimizationRequest or = new OptimizationRequest();
//        or.setF0(objectiveFunction);
//        or.setInitialPoint(new double[]{0.1, 0.9});
////        or.setFi(inequalities); //if you want x>0 and y>0
//        or.setA(A);
//        or.setB(b);
//        or.setToleranceFeas(1.E-12);
//        or.setTolerance(1.E-12);
//
//        //optimization
//        JOptimizer opt = new JOptimizer();
//        opt.setOptimizationRequest(or);
//        opt.optimize();
//
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE() QP :" + s);
//        }
////                sol[0] = 1.5
////		sol[1]= 0.0
//    }
//    /**
//     *   minimizex (1/2)xTP0x+q0T+r0  s.t.  
//     * inequalities: (1/2)xTPix+qiT+ri ≤ 0,  i=1,...,m 
//     * Ax = b,  
//     * @throws JOptimizerException 
//     */
//    private static void jopQQP() throws JOptimizerException {
//
//        // Objective function
//        double[][] P = new double[][]{{1., 0.4}, {0.4, 1.}};
//        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, null, 0);
//
//        //inequalities
//        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[1];
//        inequalities[0] = FunctionsUtils.createCircle(2, 1.75, new double[]{-2, -2});
//
//        //optimization problem
//        OptimizationRequest or = new OptimizationRequest();
//        or.setF0(objectiveFunction);
//        or.setInitialPoint(new double[]{-2., -2.});
//        or.setFi(inequalities);
//        or.setCheckKKTSolutionAccuracy(true);
//
//        //optimization
//        JOptimizer opt = new JOptimizer();
//        opt.setOptimizationRequest(or);
//        opt.optimize();
//
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE() QQP :" + s);
//        }
//    }
//
//    /**
//     *  minimizex fTx  s.t. 
//     *  ||Aix+bi||2 ≤ ciTx+di,  i=1,...,m 
//     *  Fx = g,
//     * 
//     * @throws JOptimizerException 
//     */
//    private static void jopSCP() throws JOptimizerException {
//        // Objective function (plane)
//        double[] c = new double[]{-1., -1.};
//        LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(c, 6);
//
//        //equalities
//        double[][] A = new double[][]{{1. / 4., -1.}};
//        double[] b = new double[]{0};
//
//        List<SOCPConstraintParameters> socpConstraintParametersList = new ArrayList<>();
//        SOCPLogarithmicBarrier barrierFunction = new SOCPLogarithmicBarrier(socpConstraintParametersList, 2);
//
////      second order cone constraint in the form ||A1.x+b1||<=c1.x+d1,
//        double[][] A1 = new double[][]{{0, 1.}};
//        double[] b1 = new double[]{0};
//        double[] c1 = new double[]{1. / 3., 0.};
//        double d1 = 1. / 3.;
//        SOCPConstraintParameters constraintParams1 = barrierFunction.new SOCPConstraintParameters(A1, b1, c1, d1);
//        socpConstraintParametersList.add(socpConstraintParametersList.size(), constraintParams1);
//
////      second order cone constraint in the form ||A2.x+b2||<=c2.x+d2,
//        double[][] A2 = new double[][]{{0, 1.}};
//        double[] b2 = new double[]{0};
//        double[] c2 = new double[]{-1. / 2., 0};
//        double d2 = 1;
//        SOCPConstraintParameters constraintParams2 = barrierFunction.new SOCPConstraintParameters(A2, b2, c2, d2);
//        socpConstraintParametersList.add(socpConstraintParametersList.size(), constraintParams2);
//
//        //optimization problem
//        OptimizationRequest or = new OptimizationRequest();
//        or.setF0(objectiveFunction);
//        or.setInitialPoint(new double[]{0., 0.});
//        or.setA(A);
//        or.setB(b);
//        or.setCheckProgressConditions(true);
//
//        //optimization
//        BarrierMethod opt = new BarrierMethod(barrierFunction);
//        opt.setOptimizationRequest(or);
//        opt.optimize();
//
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE() SCP :" + s);
//        }
//    }
//
//    private static void jopSP() throws JOptimizerException {
//        // Objective function (variables (x,y,t), dim = 3)
//        double[] c = new double[]{0, 0, 1};
//        LinearMultivariateRealFunction objectiveFunction = new LinearMultivariateRealFunction(c, 0);
//
//        //constraint in the form (A0.x+b0)T.(A0.x+b0) - c0.x - d0 - t < 0
//        double[][] A0 = new double[][]{
//            {-Math.sqrt(21. / 50.), 0., 0},
//            {-Math.sqrt(2) / 5., -1. / Math.sqrt(2), 0}};
//        double[] b0 = new double[]{0, 0, 0};
//        double[] c0 = new double[]{0, 0, 1};
//        double d0 = 0;
//
//        //constraint (this is a circle) in the form (A1.x+b1)T.(A1.x+b1) - c1.x - d1 < 0
//        double[][] A1 = new double[][]{{1, 0, 0},
//        {0, 1, 0}};
//        double[] b1 = new double[]{2, 2, 0};
//        double[] c1 = new double[]{0, 0, 0};
//        double d1 = Math.pow(1.75, 2);
//
//        //matrix G for SDP
//        double[][] G = new double[][]{
//            {1, 0, b0[0], 0, 0, 0},
//            {0, 1, b0[1], 0, 0, 0},
//            {b0[0], b0[1], d0, 0, 0, 0},
//            {0, 0, 0, 1, 0, b1[0]},
//            {0, 0, 0, 0, 1, b1[1]},
//            {0, 0, 0, b1[0], b1[1], d1}};
//        //matrices Fi for SDP
//        double[][] F1 = new double[][]{
//            {0, 0, A0[0][0], 0, 0, 0},
//            {0, 0, A0[1][0], 0, 0, 0},
//            {A0[0][0], A0[1][0], c0[0], 0, 0, 0},
//            {0, 0, 0, 0, 0, A1[0][0]},
//            {0, 0, 0, 0, 0, A1[1][0]},
//            {0, 0, 0, A1[0][0], A1[1][0], c1[0]}};
//        double[][] F2 = new double[][]{
//            {0, 0, A0[0][1], 0, 0, 0},
//            {0, 0, A0[1][1], 0, 0, 0},
//            {A0[0][1], A0[1][1], c0[1], 0, 0, 0},
//            {0, 0, 0, 0, 0, A1[0][1]},
//            {0, 0, 0, 0, 0, A1[1][1]},
//            {0, 0, 0, A1[0][1], A1[1][1], c1[1]}};
//        double[][] F3 = new double[][]{
//            {0, 0, A0[0][2], 0, 0, 0},
//            {0, 0, A0[1][2], 0, 0, 0},
//            {A0[0][2], A0[1][2], c0[2], 0, 0, 0},
//            {0, 0, 0, 0, 0, A1[0][2]},
//            {0, 0, 0, 0, 0, A1[1][2]},
//            {0, 0, 0, A1[0][2], A1[1][2], c1[2]}};
//
//        double[][] GMatrix = new Array2DRowRealMatrix(G).scalarMultiply(-1).getData();
//        List<double[][]> FiMatrixList = new ArrayList<double[][]>();
//        FiMatrixList.add(FiMatrixList.size(), new Array2DRowRealMatrix(F1).scalarMultiply(-1).getData());
//        FiMatrixList.add(FiMatrixList.size(), new Array2DRowRealMatrix(F2).scalarMultiply(-1).getData());
//        FiMatrixList.add(FiMatrixList.size(), new Array2DRowRealMatrix(F3).scalarMultiply(-1).getData());
//
//        //optimization request
//        OptimizationRequest or = new OptimizationRequest();
//        or.setF0(objectiveFunction);
//        //or.setInitialPoint(new double[] { -0.8, -0.8, 10});
//
//        //optimization
//        BarrierFunction bf = new SDPLogarithmicBarrier(FiMatrixList, GMatrix);
//        BarrierMethod opt = new BarrierMethod(bf);
//        opt.setOptimizationRequest(or);
//        opt.optimize();
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE() SP  :" + s);
//        }
//    }
//
//    private static void jopGP() throws JOptimizerException {
//        // Objective function (variables (x,y), dim = 2)
//        double[] a01 = new double[]{2, 1};
//        double b01 = 0;
//        double[] a02 = new double[]{3, 1};
//        double b02 = 0;
//        ConvexMultivariateRealFunction objectiveFunction = new LogTransformedPosynomial(new double[][]{a01, a02}, new double[]{b01, b02});
//
//        //constraints
//        double[] a11 = new double[]{1, 0};
//        double b11 = Math.log(1);
//        double[] a21 = new double[]{0, 1};
//        double b21 = Math.log(1);
//        double[] a31 = new double[]{-1, -1.};
//        double b31 = Math.log(0.7);
//        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[3];
//        inequalities[0] = new LogTransformedPosynomial(new double[][]{a11}, new double[]{b11});
//        inequalities[1] = new LogTransformedPosynomial(new double[][]{a21}, new double[]{b21});
//        inequalities[2] = new LogTransformedPosynomial(new double[][]{a31}, new double[]{b31});
//
//        //optimization problem
//        OptimizationRequest or = new OptimizationRequest();
//        or.setF0(objectiveFunction);
//        or.setFi(inequalities);
////        System.out.println("pt.paper.main.jopGP() "+Math.log(0.9));
//        or.setInitialPoint(new double[]{Math.log(0.9), Math.log(0.9)});
//        //or.setInteriorPointMethod(JOptimizer.BARRIER_METHOD);//if you prefer the barrier-method
//
//        //optimization
//        JOptimizer opt = new JOptimizer();
//        opt.setOptimizationRequest(or);
//        opt.optimize();
//        double[] sol = opt.getOptimizationResponse().getSolution();
//        for (double s : sol) {
//            System.out.println("pt.paper.main.jopE GP() :" + s);
//        }
//    }
//    private static void jopBIP() throws JOptimizerException
//    {
//        DoubleFactory1D F1  = DoubleFactory1D.dense;
//        DoubleFactory2D F2 = DoubleFactory2D.dense;
//        DoubleMatrix1D c = F1.dense.make(new double[] { 1, 4, 0, 7, 0, 0, 8, 6, 0, 4 });
//        DoubleMatrix2D G = F2.dense.make(new double[][] { 
//                        { -3, -1, -4, -4, -1, -5, -4, -4, -1, -1 },
//                        {  0,  0, -3, -1, -5, -5, -5, -1,  0, 0 }, 
//                        { -4, -1, -5, -2, -4, -3, -2, -4, -4, 0 },
//                        { -3, -4, -3, -5, -3, -1, -4, -5, -1, -4 } });
//        DoubleMatrix1D h = F1.dense.make(new double[] { 0, -2, -2, -8 });
//
//        BIPOptimizationRequest or = new BIPOptimizationRequest();
//        or.setC(c);
//        or.setG(G);
//        or.setH(h);
//        or.setDumpProblem(true);
//
//        //optimization
//        BIPLokbaTableMethod opt = new BIPLokbaTableMethod();
//        opt.setBIPOptimizationRequest(or);
//        opt.optimize();
//        int[] sol = opt.getBIPOptimizationResponse().getSolution();
//        for (int s : sol) {
//            System.out.println("pt.paper.main.jopE BIP() :" + s);
//        } 
//
//    }
}

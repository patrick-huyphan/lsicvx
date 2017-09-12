/*
 * Copyright 2011-2017 joptimizer.com
 *
 * This work is licensed under the Creative Commons Attribution-NoDerivatives 4.0 
 * International License. To view a copy of this license, visit 
 *
 *        http://creativecommons.org/licenses/by-nd/4.0/ 
 *
 * or send a letter to Creative Commons, PO Box 1866, Mountain View, CA 94042, USA.
 */
package com.joptimizer.optimizers;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.QRDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.SingularValueDecomposition;

import cern.colt.function.tdouble.IntIntDoubleFunction;
import cern.colt.matrix.tdouble.DoubleFactory1D;
import cern.colt.matrix.tdouble.DoubleFactory2D;
import cern.colt.matrix.tdouble.DoubleMatrix1D;
import cern.colt.matrix.tdouble.DoubleMatrix2D;
import cern.colt.matrix.tdouble.algo.DenseDoubleAlgebra;
import cern.colt.matrix.tdouble.impl.SparseDoubleMatrix2D;

import com.joptimizer.algebra.QRSparseFactorization;
import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.FunctionsUtils;
import com.joptimizer.util.ColtUtils;
import com.joptimizer.functions.IConvexMultivariateRealFunction;


public abstract class OptimizationRequestHandler {
	protected OptimizationRequestHandler successor = null;
	protected OptimizationRequest request;
	protected OptimizationResponse response;
	protected int dim = -1;
	protected int meq = -1;
	protected int mieq = -1;
	protected DenseDoubleAlgebra ALG = DenseDoubleAlgebra.DEFAULT;
	protected DoubleFactory1D F1 = DoubleFactory1D.dense;
	protected DoubleFactory2D F2 = DoubleFactory2D.dense; 

	public void setOptimizationRequest(OptimizationRequest request) {
		this.request = request;
	}
	
	protected OptimizationRequest getOptimizationRequest() {
		return this.request;
	}
	
	protected void setOptimizationResponse(OptimizationResponse response) {
		this.response = response;
	}

	public OptimizationResponse getOptimizationResponse() {
		return this.response;
	}

	public void optimize() throws JOptimizerException {
		forwardOptimizationRequest();
	}

	protected void forwardOptimizationRequest() throws JOptimizerException {
		if (successor != null) {
			successor.setOptimizationRequest(request);
			successor.optimize();
			this.response = successor.getOptimizationResponse();
			return;
		}
		
		//failure
		throw new JOptimizerException();
	}
	
	/**
	 * Number of variables.
	 */
	protected int getDim(){
		if(dim < 0){
			dim = this.request.getF0().getDim();
		}
		return dim;
	}
	
	/**
	 * Number of equalities.
	 */
	protected int getMeq(){
		if(meq < 0){
			meq = (this.request.getA()==null)? 0 : this.request.getA().rows();
		}
		return meq;
	}
	
	/**
	 * Number of inequalities.
	 */
	protected int getMieq(){
		if(mieq < 0){
			mieq = getFi().length;
		}
		return mieq;
	}
	
	protected DoubleMatrix1D getInitialPoint(){
		return request.getInitialPoint();
	}
	
	protected DoubleMatrix1D getNotFeasibleInitialPoint(){
		return request.getNotFeasibleInitialPoint();
	}
	
	protected DoubleMatrix1D getInitialLagrangian(){
		return request.getInitialLagrangian();
	}
	
	protected final DoubleMatrix2D getA() {
		return request.getA();
	}
	
	private DoubleMatrix2D AT = null;
	protected final DoubleMatrix2D getAT() {
		if(AT==null && getA()!=null){
			AT = ALG.transpose(getA());
		}
		return AT;
	}
	
	protected final DoubleMatrix1D getB() {
//		if(request.getB()==null){
//			request.setB(new double[1]);
//		}
		return request.getB();
	}
	
	protected final int getMaxIteration(){
		return request.getMaxIteration();
	}
	protected final double getTolerance(){
		return request.getTolerance();
	}
	protected final double getToleranceFeas(){
		return request.getToleranceFeas();
	}
	protected final double getToleranceInnerStep(){
		return request.getToleranceInnerStep();
	}
	protected final double getAlpha(){
		return request.getAlpha();
	}
	protected final double getBeta(){
		return request.getBeta();
	}
	protected final double getMu(){
		return request.getMu();
	}
	protected final boolean isCheckProgressConditions(){
		return request.isCheckProgressConditions();
	}
	protected final boolean isCheckKKTSolutionAccuracy(){
		return request.isCheckKKTSolutionAccuracy();
	}
	protected final double getToleranceKKT(){
		return request.getToleranceKKT();
	}
	
	/**
	 * The chosen interior point method.
	 */
	protected final String getInteriorPointMethod() {
		return request.getInteriorPointMethod();
	}

	/**
	 * Objective function.
	 */
	protected final IConvexMultivariateRealFunction getF0() {
		return request.getF0();
	}
	
	/**
	 * Objective function domain.
	 */
	protected boolean isInDomainF0(DoubleMatrix1D X) {
		double F0X = request.getF0().value(X);
		return !Double.isInfinite(F0X) && !Double.isNaN(F0X);
	}
	
	/**
	 * Objective function value at X.
	 */
	protected double getF0(DoubleMatrix1D X) {
		return request.getF0().value(X);
	}

	/**
	 * Objective function gradient at X.
	 */
	protected DoubleMatrix1D getGradF0(DoubleMatrix1D X) {
		return request.getF0().gradient(X);
	}

	/**
	 * Objective function hessian at X.
	 */
	protected DoubleMatrix2D getHessF0(DoubleMatrix1D X) {
		DoubleMatrix2D hess = request.getF0().hessian(X);
		if(hess == FunctionsUtils.ZEROES_MATRIX_PLACEHOLDER){
			return F2.make((int)X.size(), (int)X.size());
		}else{
			return hess;
		}
	}
	
	/**
	 * Inequality functions.
	 */
	protected IConvexMultivariateRealFunction[] getFi() {
		return request.getFi();
	}
	
	/**
	 * Inequality functions values at X.
	 */
	protected DoubleMatrix1D getFi(DoubleMatrix1D X){
		if(request.getFi()==null){
			return null;
		}
		DoubleMatrix1D ret = F1.make(request.getFi().length);
		double[] x = X.toArray();
		for(int i=0; i<request.getFi().length; i++){
			ret.setQuick(i, request.getFi()[i].value(X));
		}
		return ret;
	}
	
	/**
	 * Inequality functions gradients values at X.
	 */
	protected DoubleMatrix2D getGradFi(DoubleMatrix1D X) {
		DoubleMatrix2D ret = F2.make(request.getFi().length, (int) X.size());
		for(int i=0; i<request.getFi().length; i++){
			ret.viewRow(i).assign(request.getFi()[i].gradient(X));
		}
		return ret;
	}
	
	/**
	 * Inequality functions hessians values at X.
	 */
	protected DoubleMatrix2D[] getHessFi(DoubleMatrix1D X){
		DoubleMatrix2D[] ret = new DoubleMatrix2D[request.getFi().length];
		for(int i=0; i<request.getFi().length; i++){
			DoubleMatrix2D hess = request.getFi()[i].hessian(X);
			if(hess == FunctionsUtils.ZEROES_MATRIX_PLACEHOLDER){
				ret[i] = FunctionsUtils.ZEROES_MATRIX_PLACEHOLDER;
			}else{
				ret[i] = hess;
			}
		}
		return ret;
	}
	
	/**
	 * Overriding this, a subclass can define some extra condition for exiting the iteration loop. 
	 */
	protected boolean checkCustomExitConditions(DoubleMatrix1D Y) {
		return false;
	}
	
	/**
	 * Find a solution of the linear (equalities) system A.x = b.
	 * A is a pxn matrix, with rank(A) = p < n.
	 * @see "S.Boyd and L.Vandenberghe, Convex Optimization, p. 682"
	 * NB: we are waiting for Csparsej to fix its qr decomposition issues.
	 * @TODO: sign this method with more restrictive class parameters
	 */
	protected DoubleMatrix1D findEqFeasiblePoint(DoubleMatrix2D AMatrix, DoubleMatrix1D bVector) throws JOptimizerException {
		
		int p = AMatrix.rows();
		int m = AMatrix.columns();
		if(m <= p){
			LogFactory.getLog(this.getClass().getName()).error("Equalities matrix A must be pxn with rank(A) = p < n");
			throw new RuntimeException("Equalities matrix A must be pxn with rank(A) = p < n");
		}
		
		if(AMatrix instanceof SparseDoubleMatrix2D){
			QRSparseFactorization qr = new QRSparseFactorization((SparseDoubleMatrix2D)AMatrix);
			qr.factorize();
			DoubleMatrix1D x = qr.solve(bVector);		
			return x;
		}else{
			return findEqFeasiblePoint2(AMatrix, bVector);
		}
	}
	
	/**
	 * Find a solution of the linear (equalities) system A.x = b.
	 * A is a pxn matrix, with rank(A) = p < n.
	 * @see "S.Boyd and L.Vandenberghe, Convex Optimization, p. 682"
	 * @TODO: sign this method with more restrictive class parameters
	 */
	protected DoubleMatrix1D findEqFeasiblePoint2(DoubleMatrix2D AMatrix, DoubleMatrix1D bVector) throws JOptimizerException {
		
		int p = AMatrix.rows();
		int m = AMatrix.columns();
		if(m <= p){
			LogFactory.getLog(this.getClass().getName()).error("Equalities matrix A must be pxn with rank(A) = p < n");
			throw new RuntimeException("Equalities matrix A must be pxn with rank(A) = p < n");
		}
		
		final RealMatrix AT = new Array2DRowRealMatrix(AMatrix.columns(), AMatrix.rows());
		
		if(AMatrix instanceof SparseDoubleMatrix2D){
			((SparseDoubleMatrix2D)AMatrix).forEachNonZero(new IntIntDoubleFunction() {
				public double apply(int i, int j, double aij) {
					AT.setEntry(j, i, aij);
					return aij;
				}
			});
		}else{
			for(int i=0; i<AMatrix.rows(); i++){
				for(int j=0; j<AMatrix.columns(); j++){
					AT.setEntry(j, i, AMatrix.getQuick(i, j));
				}
			}
		}
		
		SingularValueDecomposition dFact1 = new SingularValueDecomposition(AT);
		int  rankAT = dFact1.getRank();
		if(rankAT!=p){
			LogFactory.getLog(this.getClass().getName()).error("Equalities matrix A must have full rank: " + rankAT + " < " + p);
			throw new RuntimeException("Equalities matrix A must have full rank: " + rankAT + " < " + p);
		}
		
		QRDecomposition dFact = new QRDecomposition(AT);
//		if(!dFact.getSolver().isNonSingular()){
//			throw new RuntimeException("Equalities matrix A must have full rank");
//		}
		
		//A = QR
		RealMatrix Q1Q2 = dFact.getQ();
		RealMatrix R0 = dFact.getR();
		RealMatrix Q1 = Q1Q2.getSubMatrix(0, AT.getRowDimension()-1, 0, p-1); 
		RealMatrix R = R0.getSubMatrix(0, p-1, 0, p-1);
		
		//w = Q1 *	Inv([R]T) . b
		double[] w = null;
		
		//solve R[T].x = b (Inv(R))[T] = Inv(R[T])
		double[] x = new double[p];
		for (int i = 0; i < p; i++) {
	    	double sum = 0;
	    	for (int j = 0; j < i; j++) {
	        sum += R.getEntry(j, i) * x[j];
	    	}
	    	x[i] = (bVector.getQuick(i) - sum)/ R.getEntry(i,i);
	    }
		w = Q1.operate(x);
		return F1.make(w);
	}
	
	/**
	 * rPri := Ax - b
	 */
	protected DoubleMatrix1D rPri(DoubleMatrix1D X) {
		if(getA()==null){
			return F1.make(0);
		}
		//return getA().zMult(X, getB().copy(), 1., -1., false);
		return ColtUtils.zMult(getA(), X, getB(), -1);
	}
	
//	protected String dumpLPRequest(){
//		try{
//			LinearMultivariateRealFunction F0L = (LinearMultivariateRealFunction) getF0();
//			double[] c = F0L.getQ().toArray();
//			double[][] G = null;
//			double[] h = null;
//			if(getFi()!=null && getFi().length > 0){
//				G = new double[getFi().length][];
//				h = new double[getFi().length];
//				for(int i=0; i<getFi().length; i++){
//					LinearMultivariateRealFunction FiL = (LinearMultivariateRealFunction) getFi()[i];
//					G[i] = FiL.getQ().toArray();
//					h[i] = -FiL.getR();
//				}
//			}
//			StringBuffer sb = new StringBuffer();
//			sb.append("LP problem dump:");
//			sb.append("\nmin(c) s.t.");
//			if(G != null){
//				sb.append("\nG.x < h");
//			}
//			if(getA()!=null){
//				sb.append("\nA.x = b");
//			}
//			sb.append("\nc: " + Utils.toString(c));
//			if(G != null){
//				sb.append("\nG: " + Utils.toString(G));
//				sb.append("\nh: " + Utils.toString(h));
//			}
//			if(getA()!=null){
//				sb.append("\nA: " + Utils.toString(getA().toArray()));
//				sb.append("\nb: " + Utils.toString(getB().toArray()));
//			}
//			return sb.toString();
//		}catch(Exception e){
//			return "";
//		}
//	}
}
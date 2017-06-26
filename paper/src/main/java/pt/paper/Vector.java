/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.text.DecimalFormat;
import java.util.List;

/**
 *
 * @author patrick_huy
 */
public class Vector implements Cloneable, java.io.Serializable {

    private int n;
    DecimalFormat twoDForm = new DecimalFormat(" 0.0000000");
    private transient Node<Integer, Double> st;  // the vector, represented by index-value pairs

    public Node<Integer, Double> getNode() {
        return  st;
    }
    
    public Vector(int n) {
        this.n  = n;
        this.st = new Node<Integer, Double>();
    }

    public Vector(double row[]) {
//    	System.out.println("1");
//    	int count = 0;
        this.st = new Node<Integer, Double>();
//        System.out.println("2");
        for(int i = 0; i<row.length; i++)
        {
            if (row[i] == 0.0) st.delete(i);
            else
            	st.put(i, row[i]);
        }
//        System.out.println("4 "+count);
//        	this.put(i, row[i]);
        this.n  = row.length;//count;
//        this.weigh = 0;
    }
    
    public Vector(List<Double> row) {
//    	System.out.println("1");
//    	int count = 0;
        this.st = new Node<Integer, Double>();
//        System.out.println("2");
        for(int i = 0; i<row.size(); i++)
        {
            if (row.get(i) == 0.0) st.delete(i);
            else
            	st.put(i, row.get(i));
        }
//        System.out.println("4 "+count);
//        	this.put(i, row[i]);
        this.n  = row.size();//count;
//        this.weigh = 0;
    }
    public Vector(double row[], Boolean rmz) {
//    	System.out.println("1");
//    	int count = 0;
        this.st = new Node<Integer, Double>();
//        System.out.println("2");
        for(int i = 0; i<row.length; i++)
        {
//        	System.out.println("3");
        	if(rmz)
        	{
        		if (row[i] == 0.0) st.delete(i);
	            else
	            	st.put(i, row[i]);
        	}
            else
            	st.put(i, row[i]);
        }
        this.n  = row.length;//count;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean sameVect = false;
        if (object != null && object instanceof Vector)
        {
//        	sameVect = isDepen(a, b)Vector((Vector) object);
//            if(sameSame)
//            	System.err.println("equals "+ ((SparseVector) object).toString());
        }

        return sameVect;
    }
    public void put(int i, double value) {
        if (i < 0 || i >= n) throw new RuntimeException("Illegal index");
        twoDForm = new DecimalFormat("0.00000000");
        value = Double.valueOf(twoDForm.format(value));
        
        if (value == 0.0) st.delete(i);
        else              st.put(i, value);
    }
    
    public void put(int i, double value, Boolean rmz ) {
        if (i < 0 || i >= n) throw new RuntimeException("Illegal index");
        twoDForm = new DecimalFormat("0.00000000");
        value = Double.valueOf(twoDForm.format(value));
        if(rmz)
        {
	        if (value == 0.0) st.delete(i);
	        else              st.put(i, value);
        }
        else              st.put(i, value);
    }

    // return st[i]
    public double get(int i) {
        if (i < 0 || i >= n) throw new RuntimeException("Illegal index"+i+" "+n);
        if (st.contains(i)) return st.get(i);
        else                return 0.0;
    }
    
    public double[] toDoubleArray()
    {
        double[] ret = new double[n];
        for(int i = 0; i<n; i++)
            ret[i]= get(i);
        
        return ret;
    }
    /**
     *
     * @param a
     * @param b
     * @return
     */
    public static double cosSim(double[] a, double[] b) {
        if (a.length != a.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m" + a.length +" "+ a.length);
        }
        double dotp = 0, maga = 0, magb = 0;
        for (int i = 0; i < a.length; i++) {
            dotp += a[i] * b[i];
            maga += Math.pow(a[i], 2);
            magb += Math.pow(b[i], 2);
        }
        maga = Math.sqrt(maga);
        magb = Math.sqrt(magb);
        double d = dotp / (maga * magb);
//        System.out.println("paper.Vector.cosSim() "+d +" " + Double.NaN);
        return (Double.isNaN(d)) ? 0 : d;
    }

    /**
     *
     * @param data
     * @return
     */
    public static double norm(double[] data) {
        double norm = dotProduct(data, data);
        return Math.sqrt(norm);
    }

    /**
     *
     * @param A
     * @return
     */
    public static double[] NVec(double [] A) {
        return scale(A, 1/norm(A));
    }
    
    public static double max(double [] A)
    {
        
        return 0;
    }
    public static double min(double [] A)
    {
        
        return 0;
    }
    public static double avr(double [] A)
    {
        return sum(A)/A.length;
    }
    /**
     * 
     * @param a
     * @param b
     * @return
     */
    public static boolean isDepen(double a[], double b[]) {
        if (a.length != a.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m" + a.length +" "+ a.length);
        }
//        for(int i =-10; i!= 0 && i<=10;i++)
//        {
//            double sum = 0;
//            for (int j = 0; j < a.length; j++) {
//                sum += (a[j]+ (i*b[j]));
//            }
//            if(sum ==0)
//            {
//                System.out.println("paper.Vector.isDepen() "+i);
//                return true;
//            }
//        }
//        return false;
        
        for (int i = 0; i < a.length; i++) {
            //TODO: check condition: a,b !=0: aX+bY=0 
            if ((a[i] == 0 && b[i] != 0) || (a[i] != 0 && b[i] == 0))
            {
                return false;
            }
        }
        return true;
    }

    public static boolean isSameVec(double a[], double b[]) {
        if (a.length != a.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m" + a.length +" "+ a.length);
        }
        DecimalFormat twoDForm = new DecimalFormat("0.00000000");
        
        for (int i = 0; i < a.length; i++) {
            double ai = Double.valueOf(twoDForm.format(a[i]));
            double bi = Double.valueOf(twoDForm.format(b[i]));
//            if (a[i]- b[i]!= 0)
//                if ((a[i]- b[i] <1e-5) && (a[i]- b[i] >-1e-5))
            if( ai != bi)
            {
                return false;
            }
        }
        return true;
    }
    /**
     *
     * @param a
     * @return
     */
    public static boolean isZeroVector(double[] a) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     *
     * @param a
     * @param sic
     * @return
     */
    public static double[] proxN1(double[] a, double sic) {
        double[] ret = new double[a.length];
//        Vector.printV(a, "proxN1 sic=" + sic, true);
        for (int i = 0; i < a.length; i++) {
                double value = a[i];
                
                if (value > sic) {
                        ret[i]= value - sic;
                } else if (value < -sic) {
                        ret[i]= value + sic;
                } else {
                        ret[i]= 0;
                }
        }
        return ret;
    }

    /**
     *
     * @param a
     * @param sic
     * @return
     */
    public static double[] proxN2(double[] a, double sic) {
        double[] ret = copy(a);

        double norm = Vector.norm(a);
        double rate = 1. - 1. / (sic * norm);
        ret = (norm >= 1. / sic) ? Vector.scale(ret, rate): ret;// .scale(0);//

        return ret;
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public static double dotProduct(double[] A, double[] B) {
        if (A.length != B.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m");
        }
        double ret = 0;//new double[A.length];
        for (int i = 0; i < A.length; i++) {
            ret += A[i] * B[i];
        }
        return ret;
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public static double[] mulVector(double[] A, double[] B) {
        if (A.length != B.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m");
        }
        double[] ret = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            ret[i] = A[i] * B[i];
        }
        return ret;
    }

    /**
     *
     * @param A
     * @return
     */
    public static double[] copy(double[] A)
    {
        double[] ret = new double[A.length];
        System.arraycopy(A, 0, ret, 0, A.length);
        return ret;
    }

    /**
     *
     * @param A
     * @param B
     * @return
     */
    public static double[] plus(double[] A, double[] B) {
        if (A.length != B.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m" + A.length +" "+ B.length);
        }
        double[] ret = new double[A.length];
        for (int i = 0; i < A.length ; i++) {
            ret[i] = A[i] + B[i];
        }
        return ret;
    }
    public static double[] scale(double[] A, double scale) {
        double[] ret = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            ret[i] = A[i] * scale;
        }
        return ret;
    }
    
    public static double loss_primal_L2(double X[], double U[], double gamma, int ix[], int n, int p, int nK, double w[]) {
		double output;
		int one = 1;
		int j, k;
		double dU[] = new double[p];
		double penalty = 0.;
		double temp;

		for (k = 0; k < nK; k++) {
			for (j = 0; j < p; j++)
				dU[j] = U[(p) * ix[k] + j] - U[(p) * ix[nK + k] + j];
			penalty += w[k] * dnrm2_(p, dU, one);
		}

		temp = 0.;
		for (j = 0; j < p; j++)
			for (k = 0; k < n; k++)
				temp += Math.pow(X[k * (p) + j] - U[k * (p) + j], 2.);
		output = 0.5 * temp + gamma * penalty;
		// free(dU);
		return output;
	}

	public static double loss_primal_L1(double X[], double U[], double gamma, int ix[], int n, int p, int nK, double w[]) {
		double output;
		int j, k;
		double penalty = 0.;
		double temp = 0.;

		for (k = 0; k < nK; k++)
			for (j = 0; j < p; j++)
				penalty += Math.abs(U[(p) * ix[k] + j] - U[(p) * ix[nK + k] + j]);

		for (j = 0; j < p; j++)
			for (k = 0; k < n; k++)
				temp += Math.pow(X[k * (p) + j] - U[k * (p) + j], 2.);
		output = 0.5 * temp + (gamma) * penalty;

		return output;

	}

    /**
     *
     * @param X
     * @param Lambda
     * @param ix
     * @param n
     * @param p
     * @param nK
     * @param s1
     * @param s2
     * @param M1
     * @param M2
     * @param mix1
     * @param mix2
     * @return
     */
    public static double loss_dual(double X[], double Lambda[], int ix[], int n, int p, int nK, int s1[], int s2[], int M1[],
			int M2[], int mix1, int mix2) {
		double output;
		int ii, jj, kk;
		double first_term, second_term;
		double l1_ij, l2_ij;

		first_term = 0.;
		for (ii = 0; ii < n; ii++) {
			for (jj = 0; jj < p; jj++) {
				l1_ij = 0.;
				if (s1[ii] > 0)
					for (kk = 0; kk < s1[ii]; kk++)
						l1_ij += Lambda[jj + M1[ii * (mix1) + kk] * (p)];
				l2_ij = 0.;
				if (s2[ii] > 0)
					for (kk = 0; kk < s2[ii]; kk++)
						l2_ij += Lambda[jj + M2[ii * (mix2) + kk] * (p)];
				first_term += Math.pow(l1_ij - l2_ij, 2.);
			}
		}
		second_term = 0.;
		for (ii = 0; ii < nK; ii++)
			for (jj = 0; jj < p; jj++)
				second_term += (X[jj + ix[ii] * (p)] - X[jj + ix[nK + ii] * (p)]) * Lambda[jj + ii * (p)];

		output = -0.5 * first_term - second_term;
		return output;
	}

	public static double dnrm2_(int n, double x[], int incx) {
		int ix, nn, iincx;
		double norm, scale, absxi, ssq, temp;

		/*
		 * DNRM2 returns the euclidean norm of a vector via the function name,
		 * so that DNRM2 := sqrt( x'*x ) /* Dereference inputs
		 */
		nn = n;
		iincx = incx;

		if (nn > 0 && iincx > 0) {
			if (nn == 1) {
				norm = Math.abs(x[0]);
			} else {
				scale = 0.0;
				ssq = 1.0;

				/*
				 * The following loop is equivalent to this call to the LAPACK
				 * auxiliary routine: CALL SLASSQ( N, X, INCX, SCALE, SSQ )
				 */

				for (ix = (nn - 1) * iincx; ix >= 0; ix -= iincx) {
					if (x[ix] != 0.0) {
						absxi = Math.abs(x[ix]);
						if (scale < absxi) {
							temp = scale / absxi;
							ssq = ssq * (temp * temp) + 1.0;
							scale = absxi;
						} else {
							temp = absxi / scale;
							ssq += temp * temp;
						}
					}
				}
				norm = scale * Math.sqrt(ssq);
			}
		} else
			norm = 0.0;

		return norm;

	} /* dnrm2_ */
        
        public static void printV(double[] a, String mess, boolean full)
        {
            if(!full && Vector.isZeroVector(a))
            {
                System.out.print(mess+" Vec 0");
                return;
            }
            System.out.print(mess+" Vec:\t");
            for(int i=0; i< a.length;i++)
            {
                if(full)
                    System.out.print(a[i]+"\t");
                else
                    System.out.print(i+":"+a[i]+"\t");
            }
            System.out.println();
        }
        public static double sum(double[] a)
        {
            double ret = 0;
            for(int i=0; i<a.length; i++)
                ret+=a[i];
            return ret;
        }
        public static double[] eVector(int length, int index)
        {
            double[] ret = new double[length];
            for(int i=0; i<length; i++)
                ret[i] = (i==index)?1:0;
            return ret;
        }
        
        public static double[] rVector(int length, double value)
        {
            double[] ret = new double[length];
            for(int i=0; i<length; i++)
                ret[i] = value;
            return ret;
        }
}

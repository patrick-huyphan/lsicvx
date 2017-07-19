/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author patrick_huy
 */
public class LocalMatrix implements Cloneable, java.io.Serializable {
    LocalVector[] rows;
    int m, n;
    DecimalFormat twoDForm = new DecimalFormat(" 0.0000000");

        /**
     * Construct an m-by-n matrix of zeros.
     *
     * @param n Number of rows.
     * @param m Number of colums.
     */
    public LocalMatrix(int n, int m) {
        this.m = m;
        this.n = n;
        rows = new LocalVector[n];
        for (int i = 0; i < n; i++)
            rows[i] = new LocalVector(m);
    }
    /**
     * Construct a matrix quickly without checking arguments.
     *
     * @param A Two-dimensional array of doubles.
     * @param m Number of rows.
     * @param n Number of colums.
     */
    public LocalMatrix(double[][] A, int n, int m) {
//        this.A = A;
        this.m = m;
        this.n = n;
        for (int i = 0; i < n; i++) {
            if (A[i].length != m) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            rows[i] = new LocalVector(A[i]);
        }
    }

    public LocalMatrix(double vals[], int n) {
        this.n = n;
        m = (n != 0 ? vals.length / n : 0);
        if (m * n != vals.length) {
            throw new IllegalArgumentException("Array length must be a multiple of n.");
        }
        double A[][] = new double[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                A[i][j] = vals[i + j * n];
            }
            rows[i] = new LocalVector(A[i], false);
        }
    }
    

    /*
	 * ------------------------ Public Methods ------------------------
     */
    public double get(int i, int j) {
        return rows[i].get(j);
    }
    
    public void set(int i, int j, double value) {
        rows[i].put(j, value);
    }

    public LocalVector getRow(int index) {
        return rows[index];
    }

    public LocalVector getColumn(int index) {
    	LocalVector ret = new LocalVector(n);
    	int j = 0;
    	for(LocalVector row : rows)
    	{
    			ret.put(j, row.get(index),false);
    			j++;
    	}
        return ret;
    }

    public void setRow(int index, double row[]) {
        rows[index] = new LocalVector(row);
    }

    public void setColumn(int index, double col[]) {
        for (int i = 0; i < col.length; i++) {
            double value = col[i];
            rows[i].put(index, value, false);
        }
    }

    public LocalMatrix copy() {
        LocalMatrix X = new LocalMatrix(n,m);
        System.arraycopy(rows, 0, X.rows, 0, n);
        return X;
    }
    public LocalMatrix changeRow(int row1, int row2) {
        LocalMatrix X = this.copy();
        for (int i = 0; i < n; i++) {
            double value = X.get(row1, i);
            X.set(row1, i, X.get(row2, i));
            X.set(row2, i, value);
        }
        return X;
    }

    public LocalMatrix changeColumn(int col1, int col2) {
        LocalMatrix X = this.copy();
        for (int i = 0; i < n; i++) {
            double value = X.get(i, col1);
            X.set(i, col1, X.get(i, col2));
            X.set(i, col2, value);
        }
        return X;
    }

    
    
    public static double[][] Transpose(double[][] A) {
        double[][] B = new double[A[0].length][A.length];
//        System.out.println("paper.Paper.matTranspose() from " + A.length + " " + A[0].length + " to " + A[0].length + " " + A.length);
        for (int i = 0; i < A[0].length; i++) {
            for (int j = 0; j < A.length; j++) {
                B[i][j] = A[j][i];
            }
        }
        return B;
    }

    public static double[][] Copy(double[][] A) {
        double[][] B = new double[A.length][A[0].length];
        for (int i = 0; i < A.length; i++) {
            System.arraycopy(A[i], 0, B[i], 0, A[0].length);
        }
        return B;
    }

    public static double[][] updateRow(double[][] A, double[] a, int row) {
//        System.arraycopy(a, 0, A[row], 0, A[0].length);

        for (int j = 0; j < A[0].length; j++) {
            A[row][j] = (a[j]<1e-6 && a[j]>-1e-6)? 0: a[j];
        }
        return A;
    }

    public static double[][] updateCol(double[][] A, double[] a, int col) {
        for (int j = 0; j < A.length; j++) {
            A[j][col] = (a[j]<1e-6 && a[j]>-1e-6)? 0: a[j];
        }
        return A;
    }

    public static double[][] RemoveColumn(double[][] A, int col) {
        double[][] B = new double[A.length][A[0].length - 1];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length - 1; j++) {
                if (j < col) {
                    B[i][j] = A[i][j];
                } else {
                    B[i][j] = A[i][j + 1];
                }
            }
        }
        return B;
    }

    
    public static double[][] RemoveRow(double[][] A, int row) {
        double[][] B = new double[A.length - 1][A[0].length];
        for (int i = 0; i < A.length - 1; i++) {
            for (int j = 0; j < A[0].length; j++) {
                if (i < row) {
                    B[i][j] = A[i][j];
                } else {
                    B[i][j] = A[i + 1][j];
                }
            }
        }
        return B;
    }

    public static double[][] SwapColumn(double[][] A, int col1, int col2) {
        for (double[] A1 : A) {
            double tmp = A1[col1];
            A1[col1] = A1[col2];
            A1[col2] = tmp;
        }
        return A;
    }

    public static double[][] SwapRow(double[][] A, int row1, int row2) {
        //double[][] B = new double[A.length-1][A[0].length];
        for (int i = 0; i < A[0].length; i++) {
            double tmp = A[row1][i];
            A[row1][i] = A[row2][i];
            A[row2][i] = tmp;
        }
        return A;
    }

    public static double[] getRow(double A[][], int row) {
        double[] ret = new double[A[0].length];
        System.arraycopy(A[row], 0, ret, 0, A[0].length);
        return ret;
    }

    public static double[] getCol(double A[][], int col) {
        double[] ret = new double[A.length];
//        LocalMatrix.printMat(A, "");
        for (int i = 0; i < A.length; i++) {
//            System.out.println(i+"-"+col+": "+A[i][col]);
            ret[i] = A[i][col];
        }
        return ret;
    }


    public static void printMat(double[][] a, String mess) {
        System.out.println("\n" + mess);
        int i = 1;
        for (double[] a1 : a) {
            System.out.print(i+":");
            for (int j = 0; j < a[0].length; j++) {
                System.out.print("\t" + a1[j]);
            }
            i++;
            System.out.println("");
        }
    }

    public static void printMat(double[][] a, boolean full, String mess) {
        System.out.println("\n" + mess);
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                if (!full) {
                    if (a[i][j] != 0) {
                        System.out.print("(" + i + "-" + j + "):" + a[i][j] + "\t");
                    }
                } else {
                    System.out.print("(" + j + "):" + a[i][j] + "\t");
                }
            }
            System.out.println("");
        }
    }

    public static void printMat(int[][] a, String mess) {
        System.out.println("\n" + mess);
        for (int[] a1 : a) {
            for (int j: a1) {
                System.out.print("\t" + j);
            }
            System.out.println("");
        }
    }

    /**
     * @param input the command line arguments
     * @return 
     */
    public static double[][] int2double(int[][] input) {
        double[][] ret = new double[input.length][input[0].length];
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < input[0].length; j++) {
                ret[i][j] = input[i][j] * 1.;
            }
        }
        return ret;
    }

    /*
		return matrix, which is removed dependent-row
     */
    public static double[][] echelon(double A[][]) {
        //double[][] trans = Transpose(A);
//        int[][] dltt = new int[A[0].length][2];
        double[][] eMat = Copy(A);
        double[][] ret = Copy(A);
//        List<Integer> rmL = new ArrayList<>();
        // TODO: use list
//        List<Integer[]> swapRowTracking = new ArrayList<>();

        //set first column with first element != 0
        int n = eMat.length,
                m = eMat[0].length;
        if (eMat[0][0] == 0) {
            for (int i = 0; i < n; i++) {
                if (eMat[i][0] != 0) {
//                    System.out.println("paper.Paper.echelon() swap 0 "+i);
                    eMat = SwapRow(eMat, 0, i);
//                    ret = SwapRow(ret, 0, i);
                    //Save tracking data
//                    Integer[] swapR = new Integer[2];
//                    swapR[0] = 0;
//                    swapR[1] = i;
//                    swapRowTracking.add(swapR);
                    break;
                }
            }
        }
        //update remain row
        int j;//currentUpdateRow 
        int i = 0,//currentRow 
            l = 0;//currentColumn
//		printMat(eMat);
        while (i < n) {
            j = i + 1;
            //re-calculate remain row base on currentRow
//            System.out.print("\n"+i+"\t");
//            for(int k =0; k<m;k++)
//            {
//                if(eMat[i][k]!=0)
//                System.out.print(k+":"+eMat[i][k]+"\t");
//            }
//            System.out.print("\n"+i+"\t");
            while ((j < n) && (l < m)) {
                if (eMat[j][l] != 0) {
                    double[] rowC = getRow(eMat, j);
                    {
                        double tmp = (-1.0) * eMat[j][l] / eMat[i][l];
                        for (int k = 0; k < m; k++) {
                            rowC[k] = rowC[k] + (eMat[i][k] * tmp);
                            if ((rowC[k] < 1e-8) && (rowC[k] > -1e-8)) {
                                rowC[k] = 0;
                            }

                        }
//                        System.out.print(j+" ");
                        eMat = updateRow(eMat, rowC, j);
                    }
                }
                j++;
            }
            // increase row and column index

            i++;
            l++;
            //if full
            if ((i == n) || (l == m)) {
                break;
            } 
            if ((l < m) && (i < n) && (eMat[i][l] == 0)) {
                while ((l < m) && (i < n) && (eMat[i][l] == 0)) {
                    j = i + 1;
//                             check in case remain element in column is 0, check elemtn in next colum-same row to continue.
//                    System.out.println("echelon() column is 0 " + i + " - " + l + " - " + j);
                    while ((l < m) && (j < n) && (eMat[j][l] == 0)) {
                        j++;
                        if (j == n) {
//                            System.out.println("echelon() remain of column is 0 " + i + " - " + l + " - " + j);
                            j = i;
                            l++;
                        }
                    }
//                            System.err.println(i+" - "+l+" - "+j + " - "+n+" - "+m);
                    if (j == n) {
                        break;
                    }
//                    System.out.println("paper.Paper.echelon() swap 1 " + i + " " + j);
                    eMat = SwapRow(eMat, i, j);
//                    ret = SwapRow(ret, i, j);
//                    Integer[] swapR = new Integer[2];
//                    swapR[0] = i;
//                    swapR[1] = j;
//                    swapRowTracking.add(swapR);
                }
            }
        }
//        printMat(eMat, "Before remove");
        int dltt [] = new int[m];
        for(i = 0; i<m; i++)
        {
            dltt[i] = i;
        }
        for (i = m - 1; i >= 0; i--) {
            double[] curentCol = getCol(eMat, i);
            
            if (LocalVector.isZeroVector(curentCol)) {
                eMat = RemoveColumn(eMat, i);
                ret = RemoveColumn(ret, i);
//                System.out.println("echelon() zero col "+i);
                for(int e=0; e<m; e++)
                {
                    if(dltt[e] == i)
                        dltt[e] =0;
                    if(dltt[e]>i && dltt[e] !=0)
                        dltt[e] = dltt[e]-1;
                }
                
                i--;
            } else {
                int tmp = i;
                for (j = tmp - 1; j >= 0; j--) {
                    double[] checkCol = getCol(eMat, j);
                    /*
                    TODO:
                    check and remove dependent-row
                    */                    
                    if (LocalVector.isDepen(checkCol, curentCol)) {
                        eMat = RemoveColumn(eMat, tmp);
                        ret = RemoveColumn(ret, tmp);
                        System.out.println("paper.Paper.echelon() checking "+ i+" "+j+" "+tmp);
                        for(int e=0; e<m; e++)
                        {
                            if(dltt[e] == tmp)
                                dltt[e] =0;
                            if(dltt[e]>tmp && dltt[e] !=0)
                                dltt[e] = dltt[e]-1;
                        }
                        tmp = j;
                        i--;
                    }
                }
            }
        }
        if(ret.length == ret[0].length)
            return ret;
            
//        int numZ = 0;
//        for(int t = eMat.length-1; t>0;t--)
//        {
//            if(LocalVector.isZeroVector(getRow(eMat, t)))
//            {
//                numZ++;
////                System.out.println( t+" is zero "+ numZ);
//            }
//            else break;
//        }
        int k = eMat[0].length;
        for(int t = 1; t<k; t++)
        {
//                    System.out.println("paper.LocalMatrix.echelon() "+t+" "+k);
            if(t>= eMat.length){
//                System.out.println("paper.LocalMatrix.echelon() "+t+" "+k +" "+ eMat.length);
                while((t>=eMat.length)&&(k > eMat.length))
                {
                    if(eMat[t-1][k-1] == 0)
                    {
                        eMat = LocalMatrix.RemoveColumn(eMat, t+1);
                        ret = LocalMatrix.RemoveColumn(ret, t+1);

                        for(int e=0; e<m; e++)
                        {
                            if(dltt[e] == (t+1))
                                dltt[e] =0;
                            if(dltt[e]>(t+1) && dltt[e] !=0)
                                dltt[e] = dltt[e]-1;
                        }
                        k--;
                    }
                }    
            }
            else
            {
                while((k > eMat.length) && (eMat[t][t] == 0))
                {
                    eMat = LocalMatrix.RemoveColumn(eMat, t);
                    ret = LocalMatrix.RemoveColumn(ret, t);
                    
                    for(int e=0; e<m; e++)
                    {
                        if(dltt[e] == (t))
                            dltt[e] =0;
                        if(dltt[e]>(t) && dltt[e] !=0)
                            dltt[e] = dltt[e]-1;
                    }
                    k--;
                }
            }
        }

//for(i = 0; i<m; i++)
//{
//    if(dltt[i] !=0)
//    System.out.println((i+1)+" " +dltt[i]);
//}

        //check again, this line just for b1 data
//        ret = RemoveColumn(ret, ret[0].length-1);
        System.out.println("paper.Matrix.echelon() " + ret.length +" - "+ret[0].length);
//        printMat(ret, "after echelon");

//        LocalMatrix.printMat(eMat, " emath");
//        LocalMatrix.printMat(ret, " echelon");
        return ret;
    }

    public List<LocalEdge> buildE(double[][] A) {
        List<LocalEdge> E = new ArrayList<>();
        for (int i = 0; i < A.length; i++) {
            for (int j = i + 1; j < A.length; j++) {
                double sim = LocalVector.cosSim(A[i], A[i]);
                if(sim>0){
                    E.add(new LocalEdge(i, j, sim));
                }
            }
        }
        return E;
    }

    public static double[][] invert(double a[][]) {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i = 0; i < n; ++i) {
            b[i][i] = 1;
        }

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; ++i) {
            for (int j = i + 1; j < n; ++j) {
                for (int k = 0; k < n; ++k) {
                    b[index[j]][k]
                            -= a[index[j]][i] * b[index[i]][k];
                }
            }
        }

        // Perform backward substitutions
        for (int i = 0; i < n; ++i) {
            x[n - 1][i] = b[index[n - 1]][i] / a[index[n - 1]][n - 1];
            for (int j = n - 2; j >= 0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k = j + 1; k < n; ++k) {
                    x[j][i] -= a[index[j]][k] * x[k][i];
                }
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    public static double[][] scale(double[][] A, double rate) {
        double[][] ret = new double[A.length][A.length];
        for(int i = 0; i< A.length; i++)
//            LocalVector.scale(A[i], rate);
            for(int j = 0 ; j<A[0].length;j++)
                ret[i][j]= A[i][j]*rate;
        return ret;
    }

    public static double[][] mul(double[][] A, double[][] B) {
        if (A[0].length != B.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m");
        }
        double[][] ret = new double[A.length][B[0].length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < B[0].length; j++) {
                double value = 0;
                for (int k = 0; k < A[0].length; k++) {
                    value += A[i][k] * B[k][j];
                }
                
                value = Double.isNaN(value)? 0:value;
                ret[i][j] = (value<1e-6 && value> -1e-6)?0:value;
            }
        }
        return ret;
    }

    public static double[] mul(double[][] A, double[] B) {
        if (A[0].length != B.length) {
            throw new UnsupportedOperationException("Not support Am=! b.m");
        }
//        System.out.println("paper.LocalMatrix.mul()" +A.length+" "+A[0].length+" "+B.length);
        double[] ret = new double[A.length];
        for (int i = 0; i < A.length; i++) {
            double value = 0;
            for (int j = 0; j < B.length; j++) {
                    value += A[i][j] * B[j];
            }
            if(Double.isNaN(value))
                value = 0;
            ret[i] = (value<1e-6 && value> -1e-6)?0:value;
        }
        return ret;
    }
    public static double[][] plus(double[][] A, double[][] B) {
        if ((A.length != B.length) || (A[0].length != B[0].length)) {
            throw new UnsupportedOperationException("Not support Am=! b.m");
        }
        double[][] ret = new double[A.length][A[0].length];

        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < A[0].length; j++) {
                ret[i][j] = A[i][j] + B[i][j];
            }
        }
        return ret;
    }

    public static void gaussian(double a[][], int index[]) {
        int n = index.length;
        double c[] = new double[n];

        // Initialize the index
        for (int i = 0; i < n; ++i) {
            index[i] = i;
        }

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; ++i) {
            double c1 = 0;
            for (int j = 0; j < n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) {
                    c1 = c0;
                }
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
        int k = 0;
        for (int j = 0; j < n - 1; ++j) {
            double pi1 = 0;
            for (int i = j; i < n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; ++i) {
                double pj = a[index[i]][j] / a[index[j]][j];

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; ++l) {
                    a[index[i]][l] -= pj * a[index[j]][l];
                }
            }
        }
    }

    public static double[][] centered(double[][] A) {
        double[][] ret = new double[A.length][A[0].length];
        for (int i = 0; i < A[0].length; i++) {
            double tmp = LocalVector.sum(LocalMatrix.getCol(A, i))/A.length;
            for(int j = 0; j< A.length; j++)
            {          		
                ret[j][i]= A[j][i]-tmp; 
            }
        }
        return ret;
    }
    
    public static double[][] orthogonolGramShmidt(double[][] A) {
        double[][] ret = Copy(A);
        for (int i = 1; i < A[0].length; i++) {
            double[] xi = LocalMatrix.getCol(A, i);
            double[] sumj = new double[A.length];
            for (int j = 0; j < i; j++) {
                double[] vj = LocalMatrix.getCol(ret, j);
                double xj =  LocalVector.dotProduct(xi, vj)/LocalVector.dotProduct(vj, vj);
                sumj = LocalVector.plus(sumj, LocalVector.scale(vj, xj));
            }
            double[]  vi = LocalVector.plus(xi, LocalVector.scale(sumj, -1));
            /*
            TODO: around vi
            */
//            for(double v:vi)
//            {
//            }
            ret = LocalMatrix.updateCol(ret, vi, i);
        }
        return ret;
    }
    
    /**
     * orthogonol + ||X*j||=1
     * @param A
     * @return
     */
    public static double[][] orthonormal(double[][] A) {
//        double[][] ret = orthogonolGramShmidt(A);
//        return centered(ret);

        double[][] ret = Copy(A);// new double[A.length][A[0].length];
        ret = LocalMatrix.updateCol(ret, LocalVector.NVec(LocalMatrix.getCol(A, 0)), 0);
        for (int i = 1; i < A[0].length; i++) {
            double[] xi = LocalMatrix.getCol(A, i);
            double[] sumj = new double[A.length];
            for (int j = 0; j < i; j++) {
                double[] vj = LocalMatrix.getCol(ret, j);
                double xj =  LocalVector.dotProduct(xi, vj)/LocalVector.dotProduct(vj, vj);
                sumj = LocalVector.plus(sumj, LocalVector.scale(vj, xj));
            }
            ret = LocalMatrix.updateCol(ret, LocalVector.NVec(LocalVector.plus(xi, LocalVector.scale(sumj, -1))), i);
        }
        return ret;
    }
    
    /**
     *
     * @param A
     * @return
     */
    public static double[][] standardized(double[][] A) {
        double[][] ret = new double[A.length][A[0].length];
        for (int i = 0; i < A[0].length; i++) {
            double tmp = LocalVector.norm(LocalMatrix.getCol(A, i));
            for(int j = 0; j< A.length; j++)
            {
                ret[j][i] = A[j][i]/tmp;
            }
        }
        return ret;
    }    
    
    /**
     *
     * @param length
     * @return
     */
    public static double[][] IMtx(int length) {
        double[][] ret = new double[length][length];
        for (int i = 0; i < length; i++) {
            ret[i][i]=1.;
        }
        return ret;
    }

    /**
     *
     * @param A
     * @param rowList
     * @param colList
     * @return
     */
    public static double[][] subMat(double [][] A, int[] rowList, int[]colList) {
        double[][] ret = new double[rowList.length][colList.length];
        for (int i = 0; i < rowList.length; i++) {
            for (int j = 0; j < colList.length; i++) {
                ret[i][j]=A[rowList[i]][colList[j]];
            }
        }
        return ret;
    }

    public static double[][] subMatR(double [][] A, int[] rowList) {
        double[][] ret = new double[rowList.length][A[0].length];
        for (int i = 0; i < rowList.length; i++) {
            for (int j = 0; j < A[0].length; i++) {
                ret[i][j]=A[rowList[i]][j];
            }
        }
        return ret;
    }
    public static double[][] subMatC(double [][] A, int[]colList) {
        double[][] ret = new double[A.length][colList.length];
        for (int i = 0; i < A.length; i++) {
            for (int j = 0; j < colList.length; i++) {
                ret[i][j]=A[i][colList[j]];
            }
        }
        return ret;
    }
    /**
     *
     * @param A
     * @param sRow
     * @param eRow
     * @param scol
     * @param eCol
     * @return
     */
    public static double[][] subMat(double [][] A, int sRow, int rowL, int scol, int colL) {
        double[][] ret = new double[rowL][colL];
        for (int i = 0; i < rowL; i++) {
            for (int j = 0; j < colL; j++) {
                ret[i][j]=A[sRow+i][scol+j];
            }
        }
        return ret;
    }
    public static double[][] sim(double[][] D, double[][]Q)
    {
        double ret[][] = new double[Q.length][D.length];
        for(int i =0; i< Q.length; i++)
        for(int j =0; j< D.length; j++)
            ret[i][j] = LocalVector.cosSim(Q[i],D[j]);
        return ret;
    }
}

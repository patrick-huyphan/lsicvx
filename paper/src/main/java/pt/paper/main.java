/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.io.IOException;
import static pt.paper.Paper.PaperRuner;

/**
 *
 * @author patrick_huy
 */
public class main {
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        double[][] DQ = Matrix.int2double(ReadData.readDataTest(data.inputJobSearch));
        int n = DQ.length;
        int q = 5;
//        CSVFile.saveMatrixData("DQ", DQ, "DQ");
        
        double[][] D = Matrix.subMat(DQ, 0, n-q, 0, DQ[0].length);
//        Matrix.printMat(D, "D init");
        double[][] Q = Matrix.subMat(DQ, n-q, q, 0, DQ[0].length);
//        Matrix.printMat(Q, "Q init");
        PaperRuner(D,Q, 10);
        
//        double[][] docTerm = CSVFile.readMatrixData("../data/data_697_3187.csv"); //data_696_1109
//        double[][] testD = Matrix.subMat(docTerm, 0, docTerm.length -10, 0, docTerm[0].length);
//        double[][] testQ = Matrix.subMat(docTerm, docTerm.length-10, 10, 0, docTerm[0].length);
//        PaperRuner(testD,testQ, 30);

//        //printMat(docTerm, false,"docTerm");
//
//
//      Paper run = new Paper(docTerm,"echelon.csv");
    }
}

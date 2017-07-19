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
        double[][] DQ = Matrix.int2double(ReadData.readDataTestN());
//        CSVFile.saveMatrixData("DQ", DQ, "DQ");
//        Matrix.printMat(DQ, "DQ init");
//        double[][] D = Matrix.subMat(DQ, 0, 26, 0, DQ[0].length);
//        double[][] Q = Matrix.subMat(DQ, 26, 3, 0, DQ[0].length);
//        PaperRuner(D,Q);
        
        double[][] docTerm = CSVFile.readMatrixData("../data/data_697_3187.csv");
        double[][] testD = Matrix.subMat(docTerm, 0, docTerm.length -10, 0, docTerm[0].length);
        double[][] testQ = Matrix.subMat(docTerm, docTerm.length-10, 10, 0, docTerm[0].length);
        PaperRuner(testD,testQ);

//        //printMat(docTerm, false,"docTerm");
//
//
//      Paper run = new Paper(docTerm,"echelon.csv");
    }
}

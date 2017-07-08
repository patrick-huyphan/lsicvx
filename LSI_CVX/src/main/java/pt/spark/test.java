/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author patrick_huy
 */
public class test {
    public static void main(String[] args) throws IOException {
    readRawData("","");    
    }
        private static void readRawData(String input, String output) throws IOException
    {
        String fileOUname = "Output/" + Long.toString(System.currentTimeMillis()) + "/"; //
        String input2 = "..\\restfb_getdata\\vn.hus.nlp.tokenizer-4.1.1-bin\\outputT2017_07_08_10_43_4798"; 
        new File(fileOUname).mkdir();
        
        pt.DocTermBuilder.DocTermReadRawFile readRawFile = new pt.DocTermBuilder.DocTermReadRawFile(input2);
        int [][]data2 = readRawFile.calCountMat1st(fileOUname,5);
        pt.DocTermBuilder.DocTermMatrixTF xx2 = pt.DocTermBuilder.DocTermMatrixTF.buildMat(3, data2);

        pt.paper.CSVFile.saveMatrixData("data", xx2.tdidf, "data");
    }
}

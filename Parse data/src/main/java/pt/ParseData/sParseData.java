package pt.ParseData;

import java.io.File;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * sParseData class, we will call this class to:
 - read text data which already prepared( tokenize and remove stop work, reduce same meaning work).
 * - build VSM
 */
public class sParseData {
  /**
   * We use a logger to print the output. Sl4j is a common library which works with log4j, the
   * logging system used by Apache Spark.
   */
  private static final Logger LOGGER = LoggerFactory.getLogger(sParseData.class);

    public static void main(String[] args) throws IOException {
    readRawData("","");    
    }
    private static void readRawData(String input, String output) throws IOException
    {
        String fileOUname = "Output/" + Long.toString(System.currentTimeMillis()) + "/"; //
        String input2 = "..\\restfb_getdata\\vn.hus.nlp.tokenizer-4.1.1-bin\\outputT2017_07_08_10_43_4798"; 
        new File(fileOUname).mkdir();
        
        DocTermReadRawFile readRawFile = new DocTermReadRawFile(input2);
//        int [][]data2 = readRawFile.calCountMat1st(fileOUname,5);
        
        int [][]data2 = readRawFile.calCountMat("Output\\1499614027518\\", fileOUname, 5);
        DocTermMatrixTF xx2 = DocTermMatrixTF.buildMat(3, data2);

        CSVFile.saveMatrixData("data_"+data2.length+"_"+data2[0].length, xx2.tdidf, "data");
    }
}

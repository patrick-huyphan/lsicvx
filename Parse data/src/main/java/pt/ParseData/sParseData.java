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

        String mtime = Long.toString(System.currentTimeMillis());
        
        String input = "../data/OutputP/outputT2017_07_08_10_43_4798";
        String input2 = "../data/OutputP/input/";
        String fileOUname = "../data/OutputP/" + mtime + "/"; //
        readRawData(input, input2,fileOUname, mtime, 5);    

    }
    private static void readRawData(String input, String input2, String output, String mTime,int minLength) throws IOException
    {

        new File(output).mkdir();
        
        DocTermReadRawFile readRawFile = new DocTermReadRawFile(input, mTime);
        {
            int [][]data = readRawFile.calCountMat1st(output,input2,minLength);       
            DocTermMatrixTF xx2 = DocTermMatrixTF.buildMat(3, data);

            CSVFile.saveMatrixData(output+mTime+"_1_data_"+data.length+"_"+data[0].length, xx2.tdidf, "data");
        }
        {
            int [][]data2 = readRawFile.calCountMat(input2,output, minLength);
            DocTermMatrixTF xx2 = DocTermMatrixTF.buildMat(3, data2);

           CSVFile.saveMatrixData(output+mTime+"_2_data_"+data2.length+"_"+data2[0].length, xx2.tdidf, "data");
        }

    }
}

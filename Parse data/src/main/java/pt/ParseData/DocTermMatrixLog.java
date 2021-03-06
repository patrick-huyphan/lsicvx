/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ParseData;

//import static pt.DocTermBuilder.DocTermMatrixTF.countMatrix;
//import static pt.DocTermBuilder.DocTermMatrixTF.keywordList;
//import static pt.DocTermBuilder.DocTermMatrixTF.listOfFiles;
//import static DocTermBuilder.DocTermMatrixTF.tdidf;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;

/**
 *
 * @author patrick_huy
 */
public class DocTermMatrixLog extends DocTermMatrixTF {
    DecimalFormat twoDForm = new DecimalFormat("0.00000");

    public DocTermMatrixLog(int [][] countM)
    {
    	super(countM);
//    	tdidf = new double[doc][term];
//        tottal_no_words_in_doc = new int[doc];
//        num_of_doc_in_which_word_i_appears = new int[term];
    }
    /**
     *
     * @param x
     * @param y
     * @throws IOException
     */
    public void compute(int listOfFiles, int keywordListSize) throws IOException {
System.out.println("compute LogMatrix");
		// initializing the tdidf
		for (int i = 0; i < listOfFiles; i++) {
			for (int j = 0; j < keywordListSize; j++) {
				tdidf[i][j] = 0.00000;
			}

		}
		FileWriter fw0 = new FileWriter("Output/logMatrix.txt");
		// DocTermMatrixTF re = new DocTermMatrixTF();
		for (int i = 0; i < listOfFiles; i++) {
			for (int j = 0; j < keywordListSize; j++) {

				fw0.write(countMatrix[i][j] + " ");
//				tdidf[i][j] = (((countMatrix[i][j] * 10000) / 1 + tottal_no_words_in_doc[i])/10000)* (Math.log(50 / num_of_doc_in_which_word_i_appears[j]));
				if(countMatrix[i][j] >0)
				{
//					System.out.println("i: "+i+" j: "+j);
//					System.out.println("countMatrix[i][j]: "+countMatrix[i][j]);
//					System.out.println("tottal_no_words_in_doc[i] "+ tottal_no_words_in_doc[i]);
//					System.out.println("num_of_doc_in_which_word_i_appears[j]: "+num_of_doc_in_which_word_i_appears[j]);
					
//					double tmp = (Double.valueOf(twoDForm.format((countMatrix[i][j] * 10000) / (1 + tottal_no_words_in_doc[i]))).doubleValue() / 10000)
//							* (Math.log(50 / num_of_doc_in_which_word_i_appears[j]));
					double tmp = (Double.valueOf(twoDForm.format((countMatrix[i][j] * 10000) / (1 + tottal_no_words_in_doc[i]))).doubleValue() / 10000)
					* (Math.log(num_of_doc_in_which_word_i_appears.length / num_of_doc_in_which_word_i_appears[j]));
					tdidf[i][j] = Double.valueOf(twoDForm.format(tmp)).doubleValue();
				}
				else
					tdidf[i][j] = 0;
			} // for closing
			fw0.write("\n");
		} // for closing
		fw0.close();
		
		System.out.println("");
		System.out.println(" ************** LOG Matrix **************");
		System.out.println("");
//		PrintWriter pr = new PrintWriter("TDM.txt");
		FileWriter fw = new FileWriter("Output/LOG_M.txt");
		for (int i = 0; i < listOfFiles; i++) {
			for (int j = 0; j < keywordListSize; j++) {
//				System.out.print(tdidf[i][j] + "  ,  ");
//				pr.println("" + tdidf[i][j]);  
				fw.write(tdidf[i][j] + "\t");
			}
//			pr.println("\n");
			fw.write("\n");
//			System.out.println("");
		}
		fw.close();
//		pr.close();
		// computeSVD();
	}
    
}

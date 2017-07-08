package pt.DocTermBuilder;

//public class T {
//}
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import com.google.common.io.Files;

import breeze.macros.expand.valify;

/**
 *
 * @author shakthydoss
 */
public class DocTermMatrixTF {

//    public static List<String> keywordList = new ArrayList();

    public static int[][] countMatrix;
//    public static int[][] EMatrix;
    public double[][] tdidf; //static 

    public static int[] tottal_no_words_in_doc;
    public static int[] num_of_doc_in_which_word_i_appears;
    public static int docnum;
    public static int keynum;
        
    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) 
//    public DocTermMatrixTF() {
//        tdidf = new double[listOfFiles.length][keywordList.size()];
//        tottal_no_words_in_doc = new int[listOfFiles.length];
//        num_of_doc_in_which_word_i_appears = new int[keywordList.size()];
//    }

    public DocTermMatrixTF(int [][] countMat)
    {
        docnum = countMat.length;
        keynum = countMat[0].length;
        countMatrix = countMat;
    	tdidf = new double[docnum][keynum];
        tottal_no_words_in_doc = new int[docnum];
        num_of_doc_in_which_word_i_appears = new int[keynum];
        num_of_doc_in_which_word_i_appears = new int[keynum];
    }
    

    public static DocTermMatrixTF buildMat(int mathType, int [][] countMat) throws IOException
    {
        DocTermMatrixTF tM = null;
//        int docnum = countMat.length;
//        int keynum = countMat[0].length;
//        System.err.println(docnum+"-"+keynum);
//        tdidf = new double[docnum][keynum];
//        tottal_no_words_in_doc = new int[docnum];
//        num_of_doc_in_which_word_i_appears = new int[keynum];
//        countMatrix = countMat;
//        for (int i = 0; i < docnum; i++) {
//        	System.out.print("i: "+i);
//        	for (int j = 0; j < keynum; j++) {
//                if (countMatrix[i][j] > 0) {
//					System.out.print(" "+j);
//                }
//            }
//        	System.out.println();
//        }
        switch (mathType) {
            case 0:
	            tM = new DocTermMatrixTDIDF(countMat); 
	            tM.compute_tottal_no_words_in_doc(docnum,keynum); 
	            tM.compute_num_of_doc_in_which_word_i_appears(docnum,keynum); 
	            tM.compute(docnum, keynum);
	            //            saveToFile("DocTermMatrixTDIDF.txt", tM.tdidf);
            break;
                
            case 1:
	            tM = new DocTermMatrixLog(countMat); 
	            tM.compute_tottal_no_words_in_doc(docnum,keynum); 
	            tM.compute_num_of_doc_in_which_word_i_appears(docnum,keynum); 
	            tM.compute(docnum, keynum);
//            saveToFile("tfLogMatrix.txt", tM.tdidf);
            break;
            
            default:
                    tM = new DocTermMatrixTF(countMat);
	            tM.compute_tottal_no_words_in_doc(docnum,keynum); 
	            tM.compute_num_of_doc_in_which_word_i_appears(docnum,keynum); 
	            tM.compute(docnum, keynum);
            break;
        }
//        System.err.println(tdidf.length+" and "+tdidf[0].length);
        return tM;
    }
    
//    public DocTermMatrixTF calc(int mathType, String []input, String output) throws FileNotFoundException, IOException {
//
//        String temp;
//        StringTokenizer st;
////        int countKeyword = 0;
//        int [] countterm;
////        int [][] topicListTerrm;
////        int [][] docTopicListTerrm;
//        Mystemmer stem = new Mystemmer();
//        //  List keywordList = new  ArrayList();
//
//        List<Integer> keywordListCount = new ArrayList();
//        
//        StopWordList swl = new StopWordList();
//
//        
//// Create list of word, should save create matrix of work, to reduce read file 
//
//        for (int i = 0; i < input.length; i++) {
////        for (String s : input) {
//        	String s = input[i];
//            s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
//            s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "").replace("-", " ");
//            s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("_", "");
//            s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
//            s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
//            s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");
////                    	s = s.replaceAll("[.,<>:;()?@#$!~%&*-+={}\\]/\\'\"^]", "");
////          st = new StringTokenizer(s, " ", false);
////          while (st.hasMoreTokens()){ 
////        	temp = st.nextToken();
//            
//            String st2[] = s.split(" ");
////            System.out.println(s);
//            for (String strT : st2) {
//                temp = strT;
////                System.out.println(temp);    
//                
//                if (swl.stopWord.contains(temp)) {
////                    if (st.hasMoreTokens()) {
////                        st.nextToken();
////                    }
//                	continue;
//                    //System.out.println(temp); 
//                } else if (temp.length() <= 2 || temp.length() >= 35) {
////                    if (st.hasMoreTokens()) {
////                        st.nextToken();
////                    }
//                	continue;
//                } else {
//                	temp = temp.toLowerCase();
//                    temp = stem.DoSuffixStremmer(temp);
//                    // put the stemmer here 
////                    System.out.print(" ("+countKeyword+ " "+ temp+")");
//                    if (keywordList.contains(temp) == false) //checking in keyword_array 
//                    {
//                        temp = temp.replace(" ", "_").replace("'", "_");
////                        if (temp.length() <= 3)
////                        	continue;
//                        keywordList.add(temp); // adding keyword to keyword_array 
////                        countKeyword++;
//
//                        keywordListCount.add(keywordList.indexOf(temp),1);
////                        bw.newLine();
//                    }
//                    else
//                    {
//                    	
//                    	int countTem = keywordListCount.get(keywordList.indexOf(temp)) + 1;
//                    	keywordListCount.add(keywordList.indexOf(temp), countTem);
//                    	System.out.println(temp+ " "+ countTem);
//                    }
//                }
//            } // while ends 
////            System.out.println();
//        } // while ends 
//        
////        bw.close();
//        
//        System.out.println("");
//        System.out.println("No of Documents – " + input.length);
//        System.out.println("No of keywords – " + keywordList.size());
//        System.out.println("");
//        
//        HashMap<String, Integer> dicCodeToIndex = new HashMap<String, Integer>();
//        for (int i=keywordList.size()-1; i>0 ; i--) {
////        	if(keywordListCount.get(i)<2)
////        	{
////        		keywordListCount.remove(i);
////        		keywordList.remove(i);
////        	}
////        	else
//        		dicCodeToIndex.put(keywordList.get(i), keywordListCount.get(i));
//        }
//        
//        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);
//        BufferedWriter bw = new BufferedWriter(new FileWriter(output+"/keywordsList.txt"));
//        for (int i=0; i< keywordList.size(); i++) {
//        	bw.write(i+": "+keywordList.get(i)+"\t"+dicCodeToIndex.get(keywordList.get(i))+"\n");
//        	
//		}
//
//        
//        bw.close();
//
//        countMatrix = new int[input.length][keywordList.size()];
//
////         Arrays.fill(countMatrix, 0);
//        for (int i = 0; i < input.length; i++) {
//            for (int j = 0; j < keywordList.size(); j++) {
//                countMatrix[i][j] = 0;
//            }
//        }
//
//
//
//        // Create matrix doc - term.
////        if (listOfFiles.length > 0) {
//            for (int i = 0; i < input.length ; i++) {
//				String s = input[i];
//	            s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
//	            s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "").replace("-", " ");
//	            s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("_", "");
//	            s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
//	            s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
//	            s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");
////                st = new StringTokenizer(s, " ", false);
//                
////                while (st.hasMoreTokens()) {
////                temp = st.nextToken();
//				String st2[] = s.split(" ");
////				System.out.print(i+": ");
//				for (String strT : st2) {
//                	temp = strT;
//                    
//                    if (swl.stopWord.contains(temp)) {
////                        if (st.hasMoreTokens()) {
////                            st.nextToken();
////                        }
//                    	continue;
//                        //System.out.println(temp); 
//                    } else if (temp.length() <= 2 || temp.length() >= 35) {
////                        if (st.hasMoreTokens()) {
////                            st.nextToken();
////                        }
//                    	continue;
//                    } else {
//                        // put stemmer here 
//                    	temp = temp.toLowerCase();
//                        temp = stem.DoSuffixStremmer(temp);
////                    	System.out.println(i+ " "+ temp);
//                        temp = temp.replace(" ", "_").replace("-", "_").replace("'", "_");
////                        if (temp.length() <= 3)
////                        	continue;
//                        if (keywordList.contains(temp) == true) // checking the keyword in keyword_array 
//                        {
////                        	System.out.print(" ("+keywordList.indexOf(temp)+ " "+ temp+")");
//                            //generating count matrix 
//                            countMatrix[i][keywordList.indexOf(temp)] = countMatrix[i][keywordList.indexOf(temp)] + 1;
//                        }
//                    }
//
//                } // while ends
////				System.out.println();
//            } // while ends 
//            // System.out.println("no of keywords – "+ii); 
////        }
//
//        System.out.println("************************** Count Matrix *************************");
//        System.out.println("");
//        
//        /*
//         * TODO: Save matrix to file, read matrix from file
//         */
//        DocTermMatrixTF tM = null;
//        switch (mathType) {
//            case 0:
//	            tM = new DocTermMatrixTDIDF(input.length,keywordList.size()); 
//	            tM.compute_tottal_no_words_in_doc(input.length,keywordList.size()); 
//	            tM.compute_num_of_doc_in_which_word_i_appears(input.length,keywordList.size()); 
//	            tM.compute(input.length, keywordList.size());
//	            saveToFile(output+"/tfidf.txt", tM.tdidf);
//            break;
//                
//            case 1:
//	            tM = new DocTermMatrixLog(input.length,keywordList.size()); 
//	            tM.compute_tottal_no_words_in_doc(input.length,keywordList.size()); 
//	            tM.compute_num_of_doc_in_which_word_i_appears(input.length,keywordList.size()); 
//	            tM.compute(input.length, keywordList.size());
//	            saveToFile(output+"/tfidf.txt", tM.tdidf);
//            break;
//            
////            case 2:
////            	tM = new DocTermMatrixTF(input);
////            	tM.compute_tottal_no_words_in_doc(input.length,keywordList.size()); 
////                tM.compute_num_of_doc_in_which_word_i_appears(input.length,keywordList.size()); 
////                tM.compute(input.length, keywordList.size());
////                saveToFile(output+"/tfidf.txt", tM.tdidf);
//                
//            default:
//	        	tM = new DocTermMatrixTF(input.length,keywordList.size());
//	        	tM.compute_tottal_no_words_in_doc(input.length,keywordList.size()); 
//	            tM.compute_num_of_doc_in_which_word_i_appears(input.length,keywordList.size()); 
//	            tM.compute(input.length, keywordList.size());
//	            saveToFile(output+"/tfidf.txt", tM.tdidf);
//            break;
//        }
////        SparseMatrix sX = new SparseMatrix(new Matrix(tdidf));
////        sX.save("baseMatrixX6");
//        return tM;
////          
//    }// main closing 
    
    public void compute_tottal_no_words_in_doc(int listOfFiles, int keywordListSize) {
        int sum = 0;
        for (int i = 0; i < listOfFiles; i++) {
            for (int j = 0; j < keywordListSize; j++) {
                if ((countMatrix[i][j]) > 0) {
//                	System.out.println(i+"-"+j+":"+countMatrix[i][j]);
                    sum = sum + 1;
                }
            }
            tottal_no_words_in_doc[i] = sum;
            sum = 0;
        }

//		for (int i = 0; i < listOfFiles.length; i++) {
//			System.out.println("Total no of words in document : " + i + " –> " + tottal_no_words_in_doc[i]);
//		}
    }

    public void compute_num_of_doc_in_which_word_i_appears(int listOfFiles, int keywordListSize) {
        int sum = 0;
        for (int i = 0; i < keywordListSize; i++) {
            for (int j = 0; j < listOfFiles; j++) {
                if ((countMatrix[j][i]) > 0) {
                    sum = sum + 1;
                }
            }
            num_of_doc_in_which_word_i_appears[i] = sum;
            sum = 0;
        }

//		for (int i = 0; i < keywordList.size(); i++) {
//			System.out.println("word : " + i + " occured in " + num_of_doc_in_which_word_i_appears[i] + " documents ");
//		}
    }
    public void compute(int listOfFiles, int keywordListSize)throws IOException 
    {
        for (int i = 0; i < listOfFiles; i++) {
            for (int j = 0; j < keywordListSize; j++) {
            	tdidf[i][j] = countMatrix[i][j];
            }
        }
    	
    };
    
    /*
     * Save data:
     * first line = size
     * 
     */

    
    public static void saveToFile(String fileName, double [][] data) throws IOException 
    {
		File file = new File(fileName);

		String saveData;
		saveData = data.length+ " " + data[0].length +"\n";
		System.out.println(" ************** save Matrix **************" + saveData);
		for(int i =0; i< data.length; i++)
		{
			for(int j =0; j< data[i].length; j++)
			{
				saveData += data[i][j] + " ";
			}
			saveData += "\n";
		}
		try (FileOutputStream fop = new FileOutputStream(file)) {

			// if file doesn't exists, then create it
			if (!file.exists()) {
				file.createNewFile();
			}

			// get the content in bytes
			byte[] contentInBytes = saveData.getBytes();

			fop.write(contentInBytes);
			fop.flush();
			fop.close();

			System.out.println("save Done");

		} catch (IOException e) {
			e.printStackTrace();
		}
    };
    
    public static double [][] readFromFile(String fileName) throws IOException 
    {
        String s, temp;
        StringTokenizer st;
    	BufferedReader br = new BufferedReader(new FileReader(new File(fileName).getPath()));
    	double [][] data = null;
    	s = br.readLine();
    	if( s != null)
    	{
    		data = new double[Integer.parseInt(s.split(" ")[0])][Integer.parseInt(s.split(" ")[1])];
    	}    	
    	int i = 0;
    	while ((s = br.readLine()) != null) {
    		int j = 0;
            st = new StringTokenizer(s, " ", false);
            while (st.hasMoreTokens()) {
                temp = st.nextToken();
                data[i][j] = Double.parseDouble(temp);

            } // while ends 
        } // while ends

        return data;
    };
} // class closing


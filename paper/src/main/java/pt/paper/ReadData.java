/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import static pt.DocTermBuilder.ReadingMultipleFile.countMatrix;
import static pt.DocTermBuilder.ReadingMultipleFile.keywordList;
import static pt.DocTermBuilder.ReadingMultipleFile.listOfFiles;
import pt.DocTermBuilder.Mystemmer;
import pt.DocTermBuilder.StopWordList;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

/**
 *
 * @author patrick_huy
 */
public class ReadData {


        
    public static double[][] readData(String file) {
        double[][] ret = null;
        return ret;
    }

    public static double[][] readDataTest() throws IOException {
        double[][] ret = null;//countMatrix(inputTest, ".");
        return ret;
    }

    public static int[][] readDataTest(String [] data) throws IOException {
        return countMatrix(data, ".");
    }

    public static int[][] countMatrix(String[] input, String output) throws FileNotFoundException, IOException {

        String temp;
//        StringTokenizer st;
//        int countKeyword = 0;
//        int[] countterm;
        int[][] countMatrix;
        List<String> keywordList = new ArrayList();
//        int [][] topicListTerrm;
//        int [][] docTopicListTerrm;
        //Mystemmer stem = new Mystemmer();
        //  List keywordList = new  ArrayList();

        List<Integer> keywordListCount = new ArrayList();

        //StopWordList swl = new StopWordList();
// Create list of word, should save create matrix of work, to reduce read file 
        for (int i = 0; i < input.length; i++) {
//        for (String s : input) {
            String s = input[i];
//            s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
//            s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "").replace("-", " ");
//            s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("_", "");
//            s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
//            s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
//            s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");

//                    	s = s.replaceAll("[.,<>:;()?@#$!~%&*-+={}\\]/\\'\"^]", "");
//          st = new StringTokenizer(s, " ", false);
//          while (st.hasMoreTokens()){ 
//        	temp = st.nextToken();
            String st2[] = s.split(" | ");
//            System.out.println(s);
            for (String strT : st2) {
                temp = strT;
//                System.out.println(temp);    

                if (temp.length() <= 2 || temp.length() >= 35) {
//                    if (st.hasMoreTokens()) {
//                        st.nextToken();
//                    }
                    continue;
                } else {
                    temp = temp.toLowerCase();
                    // put the stemmer here 
//                    System.out.print(" ("+countKeyword+ " "+ temp+")");
                    if (keywordList.contains(temp) == false) //checking in keyword_array 
                    {
                        temp = temp.replace(" ", "_").replace("'", "_");
//                        if (temp.length() <= 3)
//                        	continue;
                        keywordList.add(temp); // adding keyword to keyword_array 
//                        countKeyword++;

                        keywordListCount.add(keywordList.indexOf(temp), 1);
//                        bw.newLine();
                    } else {

                        int countTem = keywordListCount.get(keywordList.indexOf(temp)) + 1;
                        keywordListCount.add(keywordList.indexOf(temp), countTem);
//                    	System.out.println(temp+ " "+ countTem);
                    }
                }
            } // while ends 
//            System.out.println();
        } // while ends 

//        bw.close();
        System.out.println("");
        System.out.println("No of Documents ??? " + input.length);
        System.out.println("No of keywords ??? " + keywordList.size());
        System.out.println("");

        HashMap<String, Integer> dicCodeToIndex = new HashMap<String, Integer>();
        for (int i = keywordList.size() - 1; i > 0; i--) {
            dicCodeToIndex.put(keywordList.get(i), keywordListCount.get(i));
        }

        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);
        BufferedWriter bw = new BufferedWriter(new FileWriter(output + "/keywordsList.txt"));
        for (int i = 0; i < keywordList.size(); i++) {
            bw.write(i + ": " + keywordList.get(i) + "\t" + dicCodeToIndex.get(keywordList.get(i)) + "\n");
//            System.out.println(i + ": " + keywordList.get(i) + "\n");
        }
        bw.close();

        countMatrix = new int[input.length][keywordList.size()];

//         Arrays.fill(countMatrix, 0);
        for (int i = 0; i < input.length; i++) {
            for (int j = 0; j < keywordList.size(); j++) {
                countMatrix[i][j] = 0;
            }
        }

        // Create matrix doc - term.
//        if (listOfFiles.length > 0) {
        for (int i = 0; i < input.length; i++) {
            String s = input[i];
//	            s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
//	            s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "").replace("-", " ");
//	            s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("_", "");
//	            s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
//	            s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
//	            s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");

//                st = new StringTokenizer(s, " ", false);
//                while (st.hasMoreTokens()) {
//                temp = st.nextToken();
            String st2[] = s.split(" ");
//				System.out.print(i+": ");
            for (String strT : st2) {
                temp = strT;

                if (temp.length() <= 2 || temp.length() >= 35) {
//                        if (st.hasMoreTokens()) {
//                            st.nextToken();
//                        }
                    continue;
                } else {
                    // put stemmer here 
                    temp = temp.toLowerCase();
//                    	System.out.println(i+ " "+ temp);
                    temp = temp.replace(" ", "_").replace("-", "_").replace("'", "_");
//                        if (temp.length() <= 3)
//                        	continue;
                    if (keywordList.contains(temp) == true) // checking the keyword in keyword_array 
                    {
//                        	System.out.print(" ("+keywordList.indexOf(temp)+ " "+ temp+")");
                        //generating count matrix 
                        countMatrix[i][keywordList.indexOf(temp)] = countMatrix[i][keywordList.indexOf(temp)] + 1;
                    }
                }
            } // while ends
        } // while ends 
        // System.out.println("no of keywords ??? "+ii); 
//        }

        System.out.println("************************** Count Matrix *************************");
        System.out.println("");

        /*
         * TODO: Save matrix to file, read matrix from file
         */
//        SparseMatrix sX = new SparseMatrix(new Matrix(tdidf));
//        sX.save("baseMatrixX6");
//        computeTdidf(countMatrix,
//                compute_tottal_no_words_in_doc(countMatrix, input.length ,keywordList.size()),
//                compute_num_of_doc_in_which_word_i_appears(countMatrix, input.length ,keywordList.size()),        
//                input.length ,keywordList.size());
        return countMatrix;
//          
    }// main closing 

    public static int[] compute_tottal_no_words_in_doc(int[][] countMatrix, int listOfFiles, int keywordListSize) {
        int sum = 0;
        int[] tottal_no_words_in_doc = new int[listOfFiles];
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
//			System.out.println("Total no of words in document : " + i + " ???> " + tottal_no_words_in_doc[i]);
//		}
        return tottal_no_words_in_doc;
    }

    public static int[] compute_num_of_doc_in_which_word_i_appears(int[][] countMatrix, int listOfFiles, int keywordListSize) {
        int sum = 0;
        int[] num_of_doc_in_which_word_i_appears = new int[keywordListSize];
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
        return num_of_doc_in_which_word_i_appears;
    }

    public static double[][] computeTdidf(int[][] countMatrix,
            int[] tottal_no_words_in_doc,
            int[] num_of_doc_in_which_word_i_appears,
            int listOfFiles, int keywordListSize) throws IOException {
        double[][] tdidf = new double[countMatrix.length][countMatrix[0].length];
        DecimalFormat twoDForm = new DecimalFormat("0.00000");
        for (int i = 0; i < listOfFiles; i++) {
            for (int j = 0; j < keywordListSize; j++) {
                tdidf[i][j] = 0.00000;
            }
        }

        System.err.println(listOfFiles + " and3 " + keywordListSize);

        // ReadingMultipleFile re = new ReadingMultipleFile();
        for (int i = 0; i < listOfFiles; i++) {
            for (int j = 0; j < keywordListSize; j++) {

//		tdidf[i][j] = (((countMatrix[i][j] * 10000) / 1 + tottal_no_words_in_doc[i])/10000)* (Math.log(50 / num_of_doc_in_which_word_i_appears[j]));
                if (countMatrix[i][j] > 0) {
//					System.out.println("countMatrix[i][j]: "+countMatrix[i][j]);
//					System.out.println("tottal_no_words_in_doc[i] "+ tottal_no_words_in_doc[i]);
//					System.out.println("num_of_doc_in_which_word_i_appears[j]: "+num_of_doc_in_which_word_i_appears[j]);

//					double tmp = (Double.valueOf(twoDForm.format((countMatrix[i][j] * 10000) / (1 + tottal_no_words_in_doc[i]))).doubleValue() / 10000)
//							* (Math.log(50 / num_of_doc_in_which_word_i_appears[j]));
                    double tmp = (Double.valueOf(twoDForm.format((countMatrix[i][j] * 10000) / (1 + tottal_no_words_in_doc[i]))).doubleValue() / 10000)
                            * (Math.log(num_of_doc_in_which_word_i_appears.length / num_of_doc_in_which_word_i_appears[j]));
                    tdidf[i][j] = Double.valueOf(twoDForm.format(tmp)).doubleValue();
                } else {
                    tdidf[i][j] = 0;
                }
            } // for closing
        } // for closing

        return tdidf;
    }
    
    /*
    read data from raw file
    */  
    public static int[][] calcCountMat1st(File[] listOfFiles, String output, int ntermPdoc) throws FileNotFoundException, IOException {

        String s, temp;
        StringTokenizer st;
        int countKeyword = 0;
        int numOfDoc = 0;
        List<String> keywordList = new ArrayList();
//        List<Integer> keyCount = new ArrayList<>();
        Mystemmer stem = new Mystemmer();
        //  List keywordList = new  ArrayList();
//        List<Integer> keywordListCount = new ArrayList();

        StopWordList swl = new StopWordList();

        BufferedWriter bwAll = new BufferedWriter(new FileWriter(output + "doc_Data.txt"));
//        int a = listOfFiles.length;
//        int docID[] = new int[listOfFiles.length];
// Create list of word, should save create matrix of work, to reduce read file 
        if (listOfFiles.length > 0) {
            for (File nFile : listOfFiles) {
                if (nFile.isFile()) {
//                    String p1 = listOfFile.getName();
//                      System.out.println("["+i+"] " + p1);
                    BufferedReader br = new BufferedReader(new FileReader(nFile.getPath()));
                    String doc = "";
                    int termPdoc = 0;
                    while ((s = br.readLine()) != null) {
                        s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
                        s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "");
                        s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("-", "_");
                        s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
                        s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
                        s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");
//                    	s = s.replaceAll("[.,<>:;()?@#$!~%&*-+={}\\]/\\'\"^]", "");
                        st = new StringTokenizer(s, " ", false);
                        while (st.hasMoreTokens()) {
                            temp = st.nextToken().toLowerCase();
//                            temp = swl.replsace(temp);
                            if (swl.stopWord.contains(temp) || swl.stopWordV(temp)) {
                                if (st.hasMoreTokens()) {
                                    st.nextToken();
                                }
//                                System.out.println(temp);
                            } else if (temp.length() <= 6
                                    || temp.length() >= 35) {
                                if (st.hasMoreTokens()) {
                                    st.nextToken();
                                }
                            } else {
//                                temp = stem.DoSuffixStremmer(temp);
// put the stemmer here 
//                                bwAll.write(temp+" | ");
                                doc = doc + temp + " | ";
                                termPdoc++;
                                if (keywordList.contains(temp) == false) //checking in keyword_array
                                {
                                    keywordList.add(temp); // adding keyword to keyword_array
//                                    System.out.println(temp);
                                    countKeyword++;
//                                    keyCount.add(keywordList.indexOf(temp),0);
                                }
//                                else
//                                    keyCount.add(keywordList.indexOf(temp),keyCount.get(keywordList.indexOf(temp))+1);
                            }
                        } // while ends in file
                    } // while ends file
                    br.close();
                    
                    if (termPdoc > ntermPdoc) {
                        bwAll.write(doc);
//                    	System.err.println(doc);
//                    	docID[numOfDoc] = i;
                        numOfDoc++;
                        bwAll.newLine();
                    }
                    else
                    {
                        //small file and un-use, move this file
                        nFile.renameTo(new File("data\\outR\\"+nFile.getName()));
//                        if(nFile.renameTo(new File("data\\outR\\"+nFile.getName())))
//                            System.out.println("move file "+nFile.getName());
                    }
                }
            }
            
        } else {
            bwAll.close();
            return null;
        }
        bwAll.close();
        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);

        System.out.println("");
        System.out.println("No of Documents ??? " + listOfFiles.length);
        System.out.println("No of keywords ??? " + countKeyword);
        System.out.println("No of Documents ??? " + numOfDoc);
        System.out.println("");
        // this key list stores all key used and un-used
        BufferedWriter bw0 = new BufferedWriter(new FileWriter(output + "/keywordsList1st.txt"));
        int k = 0;
        for (int i = 0; i < keywordList.size(); i++) {
                k++;
                bw0.write(keywordList.get(i) + "\t" + keywordList.get(i) + "\n");
            }
        bw0.close();
        
        int[][] countMatrixTmp = new int[numOfDoc][keywordList.size()];

        for (int i = 0; i < numOfDoc; i++) {
            for (int j = 0; j < keywordList.size(); j++) {
                countMatrixTmp[i][j] = 0;
            }
        }
        // kc stores count of used key.
        int[] kc = new int[keywordList.size()];
//        HashMap<String, Integer> dicCodeToIndex = new HashMap<String, Integer>();
        // Create matrix doc - term.
        int t = 0;
        //System.out.println("["+i+"] " + p1); 
        BufferedReader br = new BufferedReader(new FileReader(output + "doc_Data.txt"));//listOfFiles[i].getPath())); //output+"doc_Data.txt"
        while ((s = br.readLine()) != null) {
            st = new StringTokenizer(s, " | ", false);
            while (st.hasMoreTokens()) {
                temp = st.nextToken();
//                            System.out.print(temp+" ");
                if (keywordList.contains(temp) == true) // checking the keyword in keyword_array 
                {
                    int id = keywordList.indexOf(temp);
                    //generating count matrix 
                    countMatrixTmp[t][id] = countMatrixTmp[t][id] + 1;
                    kc[id] = kc[id] +1;
                    
//                    if (!dicCodeToIndex.containsKey(temp)) {
//                        dicCodeToIndex.put(temp, 1);
//                    } else {
//                        int countTem = dicCodeToIndex.get(temp) + 1;
//                        dicCodeToIndex.put(temp, countTem);
//                    }
                }

            } // while ends 
            t++;
//                        System.out.println();
        } // while ends 
        br.close();
//            bw.close();
            // System.out.println("no of keywords ??? "+ii); 


        
        int keyListSize = countMatrixTmp[0].length - 1;

//	      for (int i = 0; i < numOfDoc; i++) {
//	            for (int j = 0; j < keyListSize; j++) {
//	            	int v = countMatrixTmp[i][j];
//	            	if(v>0)
//	            		System.out.print(keywordList.get(j)+" ");
//	            }
//	            System.out.println();
//	      }
//      write used key
        BufferedWriter bw = new BufferedWriter(new FileWriter(output + "/keywordsList2nd.txt"));
//        k = 0;
        List<Integer> keyID = new ArrayList<>();
//	      for (int i=keywordList.size()-1; i>=0 ; i--) {
        for (int i = 0; i < keywordList.size(); i++) {
//            if (dicCodeToIndex.containsKey(keywordList.get(i))) 
            {
                if(kc[i]>0)
                {
                    bw.write(keywordList.get(i) + "\t" + keywordList.get(i) + "\t" + kc[i] + "\n");
                    keyID.add(i);
                }
            }
        }
        bw.close();

//	      System.err.println("\n");
        for (int j = 0; j < keyID.size(); j++) {
            System.err.println(j + " " + (keyListSize - keyID.get(j)) + " " + keyID.get(j));
        }
        int[][] countMatrix = new int[numOfDoc][keyID.size()];
//	        SparseMatrix tmp = new SparseMatrix(countMatrixTmp);

        for (int i = 0; i < numOfDoc; i++) {
//	            for (int j = 0; j < keyID.size(); j++) {
//            System.out.println();
            for (int j = 0; j < keyID.size(); j++) {
                countMatrix[i][j] = countMatrixTmp[i][keyID.get(j)];

//	        	for(int j = keyID.size()-1; j>= 0; j--){	
//	                countMatrix[i][keyID.size()-1 -j] = countMatrixTmp[i][keyID.get(j)];
//	            	if(countMatrix[i][keyID.size() -1 -j]>0)
//	            		System.out.print((keyID.size() -1 -j)+" "+ keywordList.get(keyID.get(j))+" ");
            }
        }

        System.out.println("************************** Count Matrix *************************");
        System.out.println("");
        System.out.println(" ************** calc DONE **************");
        return countMatrix;
    }// main closing 

    /**
     * read doc list and key list, re-produce doc-term matrix. map term in doc with key in key list
     * @param input
     * @param output
     * @param ntermPdoc
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public static int[][] calcCountMat2nd(String input, String output, int ntermPdoc) throws FileNotFoundException, IOException {

        String s, temp;
        StringTokenizer st;
        int countKeyword = 0, row = 0;
        int numOfDoc = 0;
        List<String> keywordList = new ArrayList();
        List<String> keywordList0 = new ArrayList<>(); // keyMap has size smaller than keywordList, reduce column of matrix 
        HashMap<String, String> keyMap = new HashMap<>();
        
//        StopWordList swl = new StopWordList();
        BufferedReader rd = new BufferedReader(new FileReader(input + "doc_Data.txt"));
        while (rd.readLine() != null) numOfDoc++;
        rd.close();

//        BufferedReader 
        rd = new BufferedReader(new FileReader(input + "keywordsList.txt"));
        while ((s = rd.readLine()) != null) {
            String[] tmp = s.split("\t");
//            System.out.println(tmp[0]+" - "+tmp[0]);
            //check if keymap unavailble, add key, increase count
            if( !keyMap.containsValue(tmp[1]))
            {
//                System.out.println(tmp[0]+" - "+tmp[0]);
                //value
                keywordList.add(tmp[1]);
                countKeyword++;
            }
            row++;
            //key
            keywordList0.add(tmp[0]);
            //key-value
            keyMap.put(tmp[0],tmp[1]);
        }
//        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);

        System.out.println("");
        System.out.println("No of Documents ??? " + listOfFiles.length);
        System.out.println("No of keywords ??? " + countKeyword);
        System.out.println("No of Documents ??? " + numOfDoc);
        System.out.println("raw "+row);
           
//        numOfDoc = numOfDoc;
//        countKeyword = countKeyword;
        int[] coutK2 = new int[countKeyword];
        int[][] countMatrixT = new int[numOfDoc][countKeyword];

//         Arrays.fill(countMatrix, 0);
        for (int i = 0; i < numOfDoc; i++) {
            for (int j = 0; j < countKeyword; j++) {
                countMatrixT[i][j] = 0;
            }
        }

        // Create matrix doc - term.
        int i = 0;
//        BufferedReader 
        rd = new BufferedReader(new FileReader(input + "doc_Data.txt"));
        while ((s = rd.readLine()) != null) {
            st = new StringTokenizer(s, " | ", false);
//            System.out.print(i+":\t");
            int termpd = 0; 
            while (st.hasMoreTokens()) {
                temp = st.nextToken();
//                            System.out.println(temp+" "+keywordList.indexOf(temp));
                if(!keyMap.containsKey(temp))
                    break;
                int key = keywordList.indexOf(keyMap.get(temp));
                if(key<countKeyword && i<numOfDoc)
                {
                    countMatrixT[i][key] = countMatrixT[i][key] + 1;
                    coutK2[key] = coutK2[key]+1; 
//                    System.out.print(key +"-"+countMatrix[i][key]+"\t");
                    termpd++;
                }
                else
                    break;
            } // while ends 
//            System.out.println();
            if(termpd>0)
                i++;
        } // while ends 
        rd.close();

        FileWriter fw = new FileWriter(output+"keywordsList_n.txt");
        int ck = 0;
        for(int j = 0; j< countKeyword; j++)
        {
            if(coutK2[j]>0){
                fw.append(keywordList0.get(j)+"\t"+keyMap.get(keywordList0.get(j))+"\t"+coutK2[j]+"\n");
                ck++;
            }
            
        }
        fw.close();
        
        int[][] countMatrix = new int[i][ck];
//        if(i<numOfDoc)
        {
            for(int j = 0; j<i; j++)
            {
                int tmp = 0;
                for(int k = 0; k<countKeyword; k++)
                {
                    if(coutK2[k]>0)
                    {
                        countMatrix[j][tmp] = countMatrixT[j][k];
                        tmp++;
                    }
                }
//                System.arraycopy(countMatrixT[j], 0, countMatrix[j], 0, countKeyword); 
            }
        }
        System.out.println(" ************** calc DONE **************"+i+":"+countKeyword);
        return countMatrix;
    }// main closing 
}

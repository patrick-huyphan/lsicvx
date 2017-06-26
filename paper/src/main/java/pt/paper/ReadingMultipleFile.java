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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import pt.paper.Matrix;

//import com.google.common.io.Files;
//
//import Jama.Matrix;
//import Jama.SparseMatrix;
//import breeze.macros.expand.valify;
/**
 *
 * @author shakthydoss
 */
public class ReadingMultipleFile {

    public static List<String> keywordList = new ArrayList();

    public static int[][] countMatrix;
    public static int[][] EMatrix;
    public static double[][] tdidf; //static 
    static String path = "data/out";
    //static String path = "/home/hduser/workspace/Java_prj/20160728/data/20news-18828/alt.atheism";
//  static String path = "Corpus2"; 
    public static File folder = new File(path);
    public static File[] listOfFiles = folder.listFiles();
    public static int[] tottal_no_words_in_doc;
    public static int[] num_of_doc_in_which_word_i_appears;

    /**
     * @param args the command line arguments
     */
//    public static void main(String[] args) 
    public ReadingMultipleFile() {
        tdidf = new double[listOfFiles.length][keywordList.size()];
        tottal_no_words_in_doc = new int[listOfFiles.length];
        num_of_doc_in_which_word_i_appears = new int[keywordList.size()];
    }

    public ReadingMultipleFile(int doc, int term) {
        tdidf = new double[doc][term];
        tottal_no_words_in_doc = new int[doc];
        num_of_doc_in_which_word_i_appears = new int[term];
    }

    public ReadingMultipleFile(String input) throws FileNotFoundException, IOException {
        path = input;
        tdidf = new double[listOfFiles.length][keywordList.size()];
        tottal_no_words_in_doc = new int[listOfFiles.length];
        num_of_doc_in_which_word_i_appears = new int[keywordList.size()];
    }

    public ReadingMultipleFile(String[] input) {
        tdidf = new double[input.length][keywordList.size()];
        tottal_no_words_in_doc = new int[listOfFiles.length];
        num_of_doc_in_which_word_i_appears = new int[keywordList.size()];
    }
//generate doc list and key list
    public static int[][] calcCoundMat1st(String output, int ntermPdoc) throws FileNotFoundException, IOException {

        String s, temp;
        StringTokenizer st;
        int countKeyword = 0;
        int numOfDoc = 0;
        keywordList = new ArrayList();
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
                        //move this file
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

        BufferedWriter bw0 = new BufferedWriter(new FileWriter(output + "/keywordsList0.txt"));
        int k = 0;
        for (int i = 0; i < keywordList.size(); i++) {
                k++;
                bw0.write(keywordList.get(i) + "\t" + keywordList.get(i) + "\n");
            }
        bw0.close();
        
        int[][] countMatrixTmp = new int[numOfDoc][keywordList.size()];

//         Arrays.fill(countMatrix, 0);
        for (int i = 0; i < numOfDoc; i++) {
            for (int j = 0; j < keywordList.size(); j++) {
                countMatrixTmp[i][j] = 0;
            }
        }

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
        BufferedWriter bw = new BufferedWriter(new FileWriter(output + "/keywordsList.txt"));
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
//        int[][] 
                countMatrix = new int[numOfDoc][keyID.size()];
//	        SparseMatrix tmp = new SparseMatrix(countMatrixTmp);

        for (int i = 0; i < numOfDoc; i++) {
//	            for (int j = 0; j < keyID.size(); j++) {
            System.out.println();
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

    // read doc list and key list, re-produce doc-term matrix. map term in doc with key in key list
    public static int[][] calcCoundMat2ndd(String input, String output, int ntermPdoc) throws FileNotFoundException, IOException {

        String s, temp;
        StringTokenizer st;
        int countKeyword = 0, row = 0;
        int numOfDoc = 0;
        keywordList = new ArrayList();
        List<String> keywordList0 = new ArrayList<>(); // keyMap has size smaller than keywordList, reduce column of matrix 
        HashMap<String, String> keyMap = new HashMap<>();
        
        StopWordList swl = new StopWordList();
        BufferedReader rd = new BufferedReader(new FileReader(input + "doc_Data.txt"));
        while (rd.readLine() != null) numOfDoc++;
        rd.close();

//        BufferedReader 
        rd = new BufferedReader(new FileReader(input + "keywordsList.txt"));
        while ((s = rd.readLine()) != null) {
            String[] tmp = s.split("\t");
//            System.out.println(tmp[0]+" - "+tmp[0]);
            //check if keymap unavailble, increase count
            
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
                int key = keywordList.indexOf(keyMap.get(temp));
                if(keyMap.containsKey(temp) && key<countKeyword && i<numOfDoc)
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
        for(int j = 0; j< countKeyword; j++)
        {
            if(coutK2[j]>0)
            fw.append(keywordList0.get(j)+"\t"+keyMap.get(keywordList0.get(j))+"\t"+coutK2[j]+"\n");
        }
        fw.close();
        
        countMatrix = new int[i][countKeyword];
        if(i<numOfDoc)
        {
            for(int j = 0; j<i; j++)
                System.arraycopy(countMatrixT[j], 0, countMatrix[j], 0, countKeyword); 
        }
        System.out.println(" ************** calc DONE **************"+i+":"+countKeyword);
        return countMatrix;
    }// main closing 

    public static ReadingMultipleFile buildMat(int mathType, int[][] countMat) throws IOException {
        ReadingMultipleFile tM = null;
        int docnum = countMat.length;
        int keynum = countMat[0].length;
        System.err.println(docnum + "-" + keynum);
        tdidf = new double[docnum][keynum];
        tottal_no_words_in_doc = new int[docnum];
        num_of_doc_in_which_word_i_appears = new int[keynum];
        countMatrix = countMat;
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
                tM = new TDIDF_Matrix(docnum, keynum);
                tM.compute_tottal_no_words_in_doc(docnum, keynum);
                tM.compute_num_of_doc_in_which_word_i_appears(docnum, keynum);
                tM.compute(docnum, keynum);
                //            saveToFile("TDIDF_Matrix.txt", tM.tdidf);
                break;

            case 1:
                tM = new LogMatrix(docnum, keynum);
                tM.compute_tottal_no_words_in_doc(docnum, keynum);
                tM.compute_num_of_doc_in_which_word_i_appears(docnum, keynum);
                tM.compute(docnum, keynum);
//            saveToFile("tfLogMatrix.txt", tM.tdidf);
                break;

            case 2:
                tM = new ReadingMultipleFile();
                tM.compute_tottal_no_words_in_doc(docnum, keynum);
                tM.compute_num_of_doc_in_which_word_i_appears(docnum, keynum);
                tM.compute(docnum, keynum);
                break;

            default:
                tM = new ReadingMultipleFile(docnum, keynum);
                tM.compute_tottal_no_words_in_doc(docnum, keynum);
                tM.compute_num_of_doc_in_which_word_i_appears(docnum, keynum);
                tM.compute(docnum, keynum);
                break;
        }
        System.err.println(tdidf.length + " and " + tdidf[0].length);
        return tM;
    }

    public ReadingMultipleFile calc(int mathType, String[] input, String output) throws FileNotFoundException, IOException {

        String temp;
        StringTokenizer st;
//        int countKeyword = 0;
        int[] countterm;
//        int [][] topicListTerrm;
//        int [][] docTopicListTerrm;
        Mystemmer stem = new Mystemmer();
        //  List keywordList = new  ArrayList();

        List<Integer> keywordListCount = new ArrayList();

        StopWordList swl = new StopWordList();

// Create list of word, should save create matrix of work, to reduce read file 
        for (int i = 0; i < input.length; i++) {
//        for (String s : input) {
            String s = input[i];
            s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
            s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "").replace("-", " ");
            s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("_", "");
            s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
            s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
            s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");
//                    	s = s.replaceAll("[.,<>:;()?@#$!~%&*-+={}\\]/\\'\"^]", "");
//          st = new StringTokenizer(s, " ", false);
//          while (st.hasMoreTokens()){ 
//        	temp = st.nextToken();

            String st2[] = s.split(" ");
//            System.out.println(s);
            for (String strT : st2) {
                temp = strT;
//                System.out.println(temp);    

                if (swl.stopWord.contains(temp)) {
//                    if (st.hasMoreTokens()) {
//                        st.nextToken();
//                    }
                    continue;
                    //System.out.println(temp); 
                } else if (temp.length() <= 2 || temp.length() >= 35) {
//                    if (st.hasMoreTokens()) {
//                        st.nextToken();
//                    }
                    continue;
                } else {
                    temp = temp.toLowerCase();
                    temp = stem.DoSuffixStremmer(temp);
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
                        System.out.println(temp + " " + countTem);
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
//        	if(keywordListCount.get(i)<2)
//        	{
//        		keywordListCount.remove(i);
//        		keywordList.remove(i);
//        	}
//        	else
            dicCodeToIndex.put(keywordList.get(i), keywordListCount.get(i));
        }

        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);
        BufferedWriter bw = new BufferedWriter(new FileWriter(output + "/keywordsList.txt"));
        for (int i = 0; i < keywordList.size(); i++) {
            bw.write(i + ": " + keywordList.get(i) + "\t" + dicCodeToIndex.get(keywordList.get(i)) + "\n");

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
            s = s.replace(">", "").replace(".", "").replace("!", "").replace("*", "").replace("?", "").replace("^", "").replace("<", "");
            s = s.replace("{", "").replace("}", "").replace("(", "").replace(")", "").replace("\"", "").replace("-", " ");
            s = s.replace("#", "").replace("~", "").replace("=", "").replace("+", "").replace("_", "");
            s = s.replace("/", "").replace(";", "").replace(":", "").replace(",", "").replace(".", "");
            s = s.replace("0", "").replace("1", "").replace("2", "").replace("3", "").replace("4", "").replace("5", "");
            s = s.replace("6", "").replace("7", "").replace("8", "").replace("9", "").replace(">", "").replace("|", "");
//                st = new StringTokenizer(s, " ", false);

//                while (st.hasMoreTokens()) {
//                temp = st.nextToken();
            String st2[] = s.split(" ");
//				System.out.print(i+": ");
            for (String strT : st2) {
                temp = strT;

                if (swl.stopWord.contains(temp)) {
//                        if (st.hasMoreTokens()) {
//                            st.nextToken();
//                        }
                    continue;
                    //System.out.println(temp); 
                } else if (temp.length() <= 2 || temp.length() >= 35) {
//                        if (st.hasMoreTokens()) {
//                            st.nextToken();
//                        }
                    continue;
                } else {
                    // put stemmer here 
                    temp = temp.toLowerCase();
                    temp = stem.DoSuffixStremmer(temp);
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
//				System.out.println();
        } // while ends 
        // System.out.println("no of keywords ??? "+ii); 
//        }

        System.out.println("************************** Count Matrix *************************");
        System.out.println("");

        /*
         * TODO: Save matrix to file, read matrix from file
         */
        ReadingMultipleFile tM = null;
        switch (mathType) {
            case 0:
                tM = new TDIDF_Matrix(input.length, keywordList.size());
                tM.compute_tottal_no_words_in_doc(input.length, keywordList.size());
                tM.compute_num_of_doc_in_which_word_i_appears(input.length, keywordList.size());
                tM.compute(input.length, keywordList.size());
                saveToFile(output + "/tfidf.txt", tM.tdidf);
                break;

            case 1:
                tM = new LogMatrix(input.length, keywordList.size());
                tM.compute_tottal_no_words_in_doc(input.length, keywordList.size());
                tM.compute_num_of_doc_in_which_word_i_appears(input.length, keywordList.size());
                tM.compute(input.length, keywordList.size());
                saveToFile(output + "/tfidf.txt", tM.tdidf);
                break;

            case 2:
                tM = new ReadingMultipleFile(input);
                tM.compute_tottal_no_words_in_doc(input.length, keywordList.size());
                tM.compute_num_of_doc_in_which_word_i_appears(input.length, keywordList.size());
                tM.compute(input.length, keywordList.size());
                saveToFile(output + "/tfidf.txt", tM.tdidf);

            default:
                tM = new ReadingMultipleFile(input.length, keywordList.size());
                tM.compute_tottal_no_words_in_doc(input.length, keywordList.size());
                tM.compute_num_of_doc_in_which_word_i_appears(input.length, keywordList.size());
                tM.compute(input.length, keywordList.size());
                saveToFile(output + "/tfidf.txt", tM.tdidf);
                break;
        }
//        SparseMatrix sX = new SparseMatrix(new Matrix(tdidf));
//        sX.save("baseMatrixX6");
        return tM;
//          
    }// main closing 

    public void compute_tottal_no_words_in_doc(int listDoc, int keywordListSize) {
        int sum = 0;
        for (int i = 0; i < listDoc; i++) {
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
    }

    public void compute_num_of_doc_in_which_word_i_appears(int listDoc, int keywordListSize) {
        int sum = 0;
        for (int i = 0; i < keywordListSize; i++) {
            for (int j = 0; j < listDoc; j++) {
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

    public void compute(int listOfFiles, int keywordListSize) throws IOException {
        for (int i = 0; i < listOfFiles; i++) {
            for (int j = 0; j < keywordListSize; j++) {
                tdidf[i][j] = countMatrix[i][j];
            }
        }

    }

    ;
    
    /*
     * Save data:
     * first line = size
     * 
     */

    
    public static void saveToFile(String fileName, double[][] data) throws IOException {
        File file = new File(fileName);

        String saveData;
        saveData = data.length + " " + data[0].length + "\n";
        System.out.println(" ************** save Matrix **************" + saveData);
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
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
    }

    ;
    
    public static double[][] readFromFile(String fileName) throws IOException {
        String s, temp;
        StringTokenizer st;
        BufferedReader br = new BufferedReader(new FileReader(new File(fileName).getPath()));
        double[][] data = null;
        s = br.readLine();
        if (s != null) {
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
    }
;
} // class closing


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.ParseData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
//import static pt.DocTermBuilder.DataBuilder.countMatrix;
//import static pt.DocTermBuilder.DataBuilder.listOfFiles;

/**
 *
 * @author patrick_huy
 */
public class DocTermReadRawFile {

//    static String path = "/home/hduser/Dropbox/project/Big assignment/paper/data/out";
    //static String path = "/home/hduser/workspace/Java_prj/20160728/data/20news-18828/alt.atheism";
//  static String path = "Corpus2";


    public static File[] listOfFiles;// = folder.listFiles();
    public static int[][] countMatrix;
    public static String mTime;
    
    public DocTermReadRawFile(String path, String _mtime) {
        File folder = new File(path);
//        keywordList = new ArrayList();
        listOfFiles = folder.listFiles();
        mTime = _mtime;
    }

    public int[][] calcCountMat(String output, int ntermPdoc) throws FileNotFoundException, IOException {
        
        String s, temp;
        StringTokenizer st;
        int countKeyword = 0;
        int numOfDoc = 0;

        Mystemmer stem = new Mystemmer();
        //  List keywordList = new  ArrayList();
//        List<Integer> keywordListCount = new ArrayList();
        List<String> keywordList = new LinkedList();
        StopWordList swl = new StopWordList();

        BufferedWriter bwAll = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(output + "doc_Data.txt"),"UTF-8"));
        BufferedWriter bwlog = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(output + "log_Data.txt"),"UTF-8"));
//        int a = listOfFiles.length;
//        int docID[] = new int[listOfFiles.length];
// Create list of word, should save create matrix of work, to reduce read file 
        if (listOfFiles.length > 0) {
            for (int i = 0; i < listOfFiles.length; i++) {
                if (listOfFiles[i].isFile()) {
                    String p1 = listOfFiles[i].getName();
                    //  System.out.println("["+i+"] " + p1); 
                    BufferedReader br = new BufferedReader(new FileReader(listOfFiles[i].getPath()));
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
                            temp = swl.replace(temp);
                            if (swl.stopWord.contains(temp) || swl.stopWordV(temp)) {
                                if (st.hasMoreTokens()) {
                                    st.nextToken();
                                }
                                //System.out.println(temp); 
                            } else if (temp.length() <= 6
                                    || temp.length() >= 35) {
                                if (st.hasMoreTokens()) {
                                    st.nextToken();
                                }
                            } else {

//                            	temp = temp.toLowerCase();
                                temp = stem.DoSuffixStremmer(temp);
                                // put the stemmer here 
//                                bwAll.write(temp+" | ");
                                doc = doc + temp + " | ";
                                termPdoc++;
                                if (keywordList.contains(temp) == false) //checking in keyword_array 
                                {
//                                    temp = temp.replace(" ", "").replace("-", "").replace("'", "");
                                    keywordList.add(temp); // adding keyword to keyword_array 

                                    countKeyword++;
//                                    bw.newLine();
//                                    keywordListCount.add(keywordList.indexOf(temp),1);
                                }
//                                else
//                                {
//                                	
//                                	int countTem = keywordListCount.get(keywordList.indexOf(temp)) + 1;
////                                	System.out.println(temp+": "+ keywordListCount.get(keywordList.indexOf(temp)));
//                                	keywordListCount.add(keywordList.indexOf(temp), countTem);
//                                }

                            }
                        } // while ends 
                    } // while ends 
                    if (termPdoc > ntermPdoc) {
                        bwAll.write(doc);
//                    	System.err.println(doc);
//                    	docID[numOfDoc] = i;
                        bwlog.write(listOfFiles[i].getName()+ "\t ID: "+numOfDoc);
                        bwlog.newLine();
                        numOfDoc++;
                        bwAll.newLine();
                    }
                    else
                    {
                        bwlog.write(listOfFiles[i].getName()+ "\t REMOVE: not enough length");
                        bwlog.newLine();
                    }
                }
            }
            bwlog.close();
            bwAll.close();
        } else {
            return null;
        }

        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);

        System.out.println("");
        System.out.println("No of Documents – " + listOfFiles.length);
        System.out.println("No of keywords – " + countKeyword);
        System.out.println("No of Documents – " + numOfDoc);
        System.out.println("");

        int[][] countMatrixTmp = new int[numOfDoc][keywordList.size()];

//         Arrays.fill(countMatrix, 0);
        for (int i = 0; i < numOfDoc; i++) {
            for (int j = 0; j < keywordList.size(); j++) {
                countMatrixTmp[i][j] = 0;
            }
        }

        HashMap<String, Integer> dicCodeToIndex = new HashMap<String, Integer>();
        // Create matrix doc - term.
        int t = 0;
        {
            //System.out.println("["+i+"] " + p1); 
            BufferedReader br = new BufferedReader(new FileReader(output + "doc_Data.txt"));//listOfFiles[i].getPath())); //output+"doc_Data.txt"
            while ((s = br.readLine()) != null) {
                st = new StringTokenizer(s, " | ", false);
                while (st.hasMoreTokens()) {
                    temp = st.nextToken();
//                            System.out.print(temp+" ");
                    if (keywordList.contains(temp) == true) // checking the keyword in keyword_array 
                    {
                        //generating count matrix 
                        countMatrixTmp[t][keywordList.indexOf(temp)] = countMatrixTmp[t][keywordList.indexOf(temp)] + 1;
                        if (!dicCodeToIndex.containsKey(temp)) {
                            dicCodeToIndex.put(temp, 1);
                        } else {
                            int countTem = dicCodeToIndex.get(temp) + 1;
                            dicCodeToIndex.put(temp, countTem);
                        }
                    }
                } // while ends 
                t++;
//                        System.out.println();
            } // while ends 
            br.close();
        }
//            bw.close();
        // System.out.println("no of keywords – "+ii); 

        int keyListSize = countMatrixTmp[0].length - 1;

//	      for (int i = 0; i < numOfDoc; i++) {
//	            for (int j = 0; j < keyListSize; j++) {
//	            	int v = countMatrixTmp[i][j];
//	            	if(v>0)
//	            		System.out.print(keywordList.get(j)+" ");
//	            }
//	            System.out.println();
//	      }
        BufferedWriter bw = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(output + "keywordsList.txt"),"UTF-8"));//(new FileWriter(output + "/keywordsList.txt"));
        int k = 0;
        List<Integer> keyID = new ArrayList<>();
        for (int i = 0; i < keywordList.size(); i++) {
            if (dicCodeToIndex.containsKey(keywordList.get(i))) {
                k++;
                bw.write(k + ": \t" + keywordList.get(i) + "\t" + dicCodeToIndex.get(keywordList.get(i)) + "\n");
                keyID.add(i);
//	    		  System.out.println("add "+i+" "+keywordList.get(i));
            }
        }

        bw.close();

//	      System.err.println("\n");
        for (int j = 0; j < keyID.size(); j++) {
            System.err.println(j + " " + (keyListSize - keyID.get(j)) + " " + keyID.get(j));
        }
        countMatrix = new int[numOfDoc][keyID.size()];
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
//        SparseMatrix sX = new SparseMatrix(new Matrix(tdidf));
//        sX.save("baseMatrixX6");
//          
        return countMatrix;
    }// main closing 
    
        public int[][] calCountMat1st(String output, String output2, int ntermPdoc) throws FileNotFoundException, IOException {

        String s, temp;
        StringTokenizer st;
        int countKeyword = 0;
        int numOfDoc = 0;
//        keywordList = new ArrayList();
//        List<Integer> keyCount = new ArrayList<>();
        List<String> keywordList = new LinkedList();
        Mystemmer stem = new Mystemmer();
        //  List keywordList = new  ArrayList();
//        List<Integer> keywordListCount = new ArrayList();

        StopWordList swl = new StopWordList();
        FileOutputStream fos = new FileOutputStream(output2 + mTime+"_1_doc_Data.txt");
        BufferedWriter bwAll = new BufferedWriter( new OutputStreamWriter(fos, "UTF-8"));//(new FileWriter(output + "doc_Data.txt"));
        BufferedWriter bwlog = new BufferedWriter(new FileWriter(output + "log_Data1.txt"));
//        int a = listOfFiles.length;
//        int docID[] = new int[listOfFiles.length];
// Create list of word, should save create matrix of work, to reduce read file 
        if (listOfFiles.length > 0) {
            for (File nFile : listOfFiles) {
                if (nFile.isFile()) {
//                    String p1 = listOfFile.getName();
//                      System.out.println("["+i+"] " + p1);
                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(nFile.getPath()), "UTF8"));//new FileReader(nFile.getPath()));
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
                        bwlog.write(nFile.getName()+"\t ID: "+numOfDoc);
                        bwlog.newLine();
//                    	System.err.println(doc);
//                    	docID[numOfDoc] = i;
                        numOfDoc++;
                        bwAll.newLine();
                    }
                    else
                    {
                        //move this file
//                        nFile.renameTo(new File("data\\outR\\"+nFile.getName()));
                        bwlog.write(nFile.getName()+"\t REMOVE ");
                        bwlog.newLine();
                        if(nFile.renameTo(new File("data\\outR\\"+nFile.getName())))
                            System.out.println("move file "+nFile.getName());
                    }
                }
            }
            
        } else {
            bwAll.close();
            bwlog.close();
            return null;
        }
        bwAll.close();
        bwlog.close();
        fos.close();
        
        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);

        System.out.println("");
        System.out.println("No of Documents � " + listOfFiles.length);
        System.out.println("No of keywords � " + countKeyword);
        System.out.println("No of Documents � " + numOfDoc);
        System.out.println("");

        fos = new FileOutputStream(output + "keywordsList0_1.txt");
        BufferedWriter bw0 = new BufferedWriter( new OutputStreamWriter(fos,"UTF-8"));//(new FileWriter(output + "/keywordsList0.txt"));
        int k = 0;
        for (int i = 0; i < keywordList.size(); i++) {
                k++;
                bw0.write(keywordList.get(i) + "\t" + keywordList.get(i) + "\n");
            }
        bw0.close();
        fos.close();
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

        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(output2 + mTime+"_1_doc_Data.txt"), "UTF8"));//(new FileReader(output + "doc_Data.txt"));//listOfFiles[i].getPath())); //output+"doc_Data.txt"
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
            // System.out.println("no of keywords � "+ii); 


        
        int keyListSize = countMatrixTmp[0].length - 1;

//	      for (int i = 0; i < numOfDoc; i++) {
//	            for (int j = 0; j < keyListSize; j++) {
//	            	int v = countMatrixTmp[i][j];
//	            	if(v>0)
//	            		System.out.print(keywordList.get(j)+" ");
//	            }
//	            System.out.println();
//	      }
        fos = new FileOutputStream(output + "keywordsList1_1.txt");
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos,"UTF-8"));//(new FileWriter(output + "/keywordsList.txt"));
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
        fos.close();

//	      System.err.println("\n");
//        for (int j = 0; j < keyID.size(); j++) {
//            System.err.println(j + " " + (keyListSize - keyID.get(j)) + " " + keyID.get(j));
//        }
//        int[][] 
                countMatrix = new int[numOfDoc][keyID.size()];
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

    // read doc list and key list, re-produce doc-term matrix. map term in doc with key in key list
    /**
     * TODO: save file info: file read, file delete, index in matrix
     * 
     * @param input
     * @param output
     * @param ntermPdoc
     * @return
     * @throws FileNotFoundException
     * @throws IOException 
     */
    public int[][] calCountMat(String input, String output, int ntermPdoc) throws FileNotFoundException, IOException {

        String s, temp;
        StringTokenizer st;
        int countKeyword = 0; 
//        int         row = 0;
        int numOfDoc = 0;
//        keywordList = new ArrayList();
        List<String> _keywordList= new LinkedList<>(); ;
        List<String> keywordList0 = new LinkedList<>(); // keyMap has size smaller than keywordList, reduce column of matrix 
        HashMap<String, String> keyMap = new HashMap<>();
        HashMap<String, Integer> keywordList1 = new HashMap<>();
        
//        StopWordList swl = new StopWordList();
        BufferedReader rd = new BufferedReader(new InputStreamReader(new FileInputStream(input + mTime+"_1_doc_Data.txt"), "UTF8"));//(new FileReader(input + "doc_Data.txt"));
        while (rd.readLine() != null) numOfDoc++;
        rd.close();
        rd = new BufferedReader(new InputStreamReader(new FileInputStream(input + "query_Data.txt"), "UTF8"));//(new FileReader(input + "doc_Data.txt"));
        while (rd.readLine() != null) numOfDoc++;
        rd.close();

//        BufferedReader 
        rd = new BufferedReader(new InputStreamReader(new FileInputStream(input + "keywordsList.txt"), "UTF8"));//(new FileReader(input + "keywordsList.txt"));
        while ((s = rd.readLine()) != null) {
            String[] tmp = s.split("\t");
            //check if keymap unavailble, increase count
            if( !keyMap.containsValue(tmp[1]))
            {
//                System.out.println(tmp[0]+" - "+tmp[0]);
                //value
                _keywordList.add(tmp[1]);
//                countKeyword++;
            }
//            row++;
            //key
            keywordList0.add(tmp[0]);
            //key-value
            keyMap.put(tmp[0],tmp[1]);
        }
//        Collections.sort(keywordList, String.CASE_INSENSITIVE_ORDER);
        countKeyword = _keywordList.size();
        System.out.println("");
        System.out.println("No of Documents � " + listOfFiles.length);
        System.out.println("No of keywords � " + countKeyword);
        System.out.println("No of Documents � " + numOfDoc);
        System.out.println("No of map - " + keyMap.size());
        System.out.println("No of keyword - " + keywordList0.size());
//        System.out.println("raw "+row);
           
//        numOfDoc = numOfDoc;
//        countKeyword = countKeyword;
//        int[] coutK2 = new int[countKeyword];
        HashMap<Integer, Integer> coutK2 = new HashMap<>(); 
        int[][] countMatrixT = new int[numOfDoc][countKeyword];

//         Arrays.fill(countMatrix, 0);
        for (int i = 0; i < numOfDoc; i++) {
            for (int j = 0; j < countKeyword; j++) {
                countMatrixT[i][j] = 0;
            }
        }

        // Create matrix doc - term.
        int i = 0, j=0;
//        BufferedReader 

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "not_use_2.txt"),"UTF-8"));
        BufferedWriter bw2 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "use_2.txt"),"UTF-8"));
        BufferedWriter bwlog = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "loguse_2.txt"),"UTF-8"));
        // face data
        rd = new BufferedReader(new InputStreamReader(new FileInputStream(input + mTime+"_1_doc_Data.txt"), "UTF8"));//(new FileReader(input + "doc_Data.txt"));
        while ((s = rd.readLine()) != null) {
            st = new StringTokenizer(s, " |", false);
            
//            System.out.print(i+":\t");
            int termpd = 0; 
            while (st.hasMoreTokens()) {
                temp = st.nextToken();
//                            System.out.println(temp+" "+keywordList.indexOf(temp));
                temp = temp.replaceAll(" ", "");
                int key = _keywordList.indexOf(keyMap.get(temp));
                
                if(keyMap.containsKey(temp) && key<countKeyword && i<numOfDoc)
                {
//                    System.out.println(temp+" "+key+ " "+keyMap.get(temp)+" "+coutK2[key]);
                    countMatrixT[i][key] = countMatrixT[i][key] + 1;
                    if(!coutK2.containsKey(key))
                        coutK2.put(key, 1);
                    else
                        coutK2.replace(key, coutK2.get(key)+1);// [key] = coutK2[key]+1;
                    termpd++;
//                    System.out.print(key +"-"+countMatrix[i][key]+"\t");
                }
                else
                {
//                    bw3.append(temp+"\n");
                    if(!keywordList1.containsKey(temp))
                        keywordList1.put(temp,1);
                    else
                        keywordList1.replace(temp, keywordList1.get(temp)+1);
//                    System.out.println(temp+" "+key+ " "+keyMap.get(temp));
                    break;
                }
            } // while ends 
//            System.out.println();
            if(termpd>2)
            {
                bw2.append(s+"\n");
                bwlog.append(j+"\t"+i+"\t"+termpd+"\t"+s+"\n");
                i++;
            }
            else
                bw.append(j+"\t"+i+"\t"+termpd+"\t"+s+"\n");
            j++;
        } // while ends 
        rd.close();
        //qeury data
        rd = new BufferedReader(new InputStreamReader(new FileInputStream(input + "query_Data.txt"), "UTF8"));
        while ((s = rd.readLine()) != null) {
            st = new StringTokenizer(s, " |", false);
            
//            System.out.print(i+":\t");
            int termpd = 0; 
            while (st.hasMoreTokens()) {
                temp = st.nextToken();
//                            System.out.println(temp+" "+keywordList.indexOf(temp));
                temp = temp.replaceAll(" ", "");
                int key = _keywordList.indexOf(keyMap.get(temp));
                
                if(keyMap.containsKey(temp) && key<countKeyword && i<numOfDoc)
                {
//                    System.out.println(temp+" "+key+ " "+keyMap.get(temp)+" "+coutK2[key]);
                    countMatrixT[i][key] = countMatrixT[i][key] + 1;
                    if(!coutK2.containsKey(key))
                        coutK2.put(key, 1);
                    else
                        coutK2.replace(key, coutK2.get(key)+1);// [key] = coutK2[key]+1;
                    termpd++;
//                    System.out.print(key +"-"+countMatrix[i][key]+"\t");
                }
                else
                {
//                    bw3.append(temp+"\n");
                    if(!keywordList1.containsKey(temp))
                        keywordList1.put(temp,1);
                    else
                        keywordList1.replace(temp, keywordList1.get(temp)+1);
//                    System.out.println(temp+" "+key+ " "+keyMap.get(temp));
                    break;
                }
            } // while ends 
//            System.out.println();
            if(termpd>2)
            {
                bw2.append(s+"\n");
                bwlog.append(j+"\t"+i+"\t"+termpd+"\t"+s+"\n");
                i++;
            }
            else
                bw.append(j+"\t"+i+"\t"+termpd+"\t"+s+"\n");
            j++;
        } // while ends 
        
        rd.close();
        bw.close();
        bw2.close();
        bwlog.close();
        
        BufferedWriter bw3 = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "keyNoU_2.txt"),"UTF-8"));
        for(String sk: keywordList1.keySet())    
        {
            bw3.append(sk+"\t"+keywordList1.get(sk)+"\n");
        }
        bw3.close();
//        FileWriter fw = new FileWriter(output+"keywordsList_n.txt");
        bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output + "keywordsList_U_2.txt"),"UTF-8"));
//        int[] countk3 = new int[countKeyword];
//        int k = 0;
        System.out.println("no of countk2 "+coutK2.size());
        for(j = 0; j< countKeyword; j++)
        {
            if(coutK2.containsKey(j)){
//                countk3[k]=j;
//                k++;
                bw.append(keywordList0.get(j)+"\t"+keyMap.get(keywordList0.get(j))+"\t"+coutK2.get(j)+"\n");
            }
        }
        bw.close();
        
        countMatrix = new int[i][coutK2.size()];
        if(i<=numOfDoc)
        {
            for( j = 0; j<i; j++)
            {
                int t = 0;
                for(int k = 0; k<countKeyword; k++)
                {
                    if(coutK2.containsKey(k))
                    {
                        countMatrix[j][t] = countMatrixT[j][k];
                        t++;
//                        System.out.println("pt.ParseData.DocTermReadRawFile.calCountMat() " +j+"-"+k+": "+countMatrix[j][k]);
                    }
                }
//                System.arraycopy(countMatrixT[j], 0, countMatrix[j], 0, countKeyword); 
            }
        }
        System.out.println(" ************** calc DONE **************"+i+":"+coutK2.size());
        return countMatrix;
    }// main closing 

}

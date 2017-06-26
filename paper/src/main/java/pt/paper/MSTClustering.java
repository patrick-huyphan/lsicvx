package paper;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

public class MSTClustering extends Clustering{
    public static final int MAX_VALUE = 999;
    private int visited[];
    private double spanning_tree[][];
    private double minE;
    private double maxE;
    
    /**
     *
     * @param docTermMatrix
     * @param lambda
     * @throws IOException
     */
    public MSTClustering (double[][] docTermMatrix, double lambda) throws IOException{
        super(docTermMatrix, lambda);
        minE = 1;
        //eliminate weak edge, use lamba as range to remove
//        for (Iterator<Edge> iter = edges.listIterator(); iter.hasNext();) {
//            Edge tmp = iter.next();
//            if (tmp.weight < lambda) {
////                System.out.println("MSTClustering remove edge "+tmp.sourcevertex+ " "+tmp.destinationvertex);
//                iter.remove();
//            }
//        }

//        for (Edge edge : edges)
//    	{
//        	System.out.println("----"+edge.sourcevertex+"-"+edge.destinationvertex + ": "+edge.weight);
//    	}
        
//        int egdeS = edges.size();
//        for(int i = 0; i<egdeS; i++)
//        {
////            System.out.println("----"+edge.sourcevertex+":"+e.destinationvertex);
//            Edge e = edges.get(i);
//            edges.add(new Edge(e.destinationvertex, e.sourcevertex, e.weight));
//        }
        visited = new int[this.numberOfVertices];
        spanning_tree = new double[numberOfVertices][numberOfVertices];
        System.out.println("paper.MSTClustering.<init>() "+numberOfVertices);
        
//        getCluster();
        
//        FileWriter fw  = new FileWriter("Cluster.txt");
//        getCluster(fw);
//        fw.close();
//        getPresentMat();

//
        presentMat = new double[numOfFeature][5];
//        int mat[]={2,5,6,13,23};
        int mat[]={7,11,18,19,24};
        for(int i = 0; i<numOfFeature; i++)
        {
            for(int j =0; j<5; j++)
            presentMat[i][j]= A[i][mat[j]];
        }
    }

    /*
     * TODO:
     *  check maximum sim in path
     */

    /**
     *
     * @param adjacencyMatrix
     * @param lamda
     * @param fw
     * @return
     */

    public double[][] kruskalAlgorithm(double adjacencyMatrix[][], double lamda, FileWriter fw) {
        boolean finished = false;
        /*
         * get all nodes, plus related weigh= sim + (related=true)
         */
//        List<List<Integer>> clusters = new ArrayList<List<Integer>>();

        //Check tree and eliminate edge, use lamba as range to remove
        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                // TODO Auto-generated method stub
                return (o1.weight < o2.weight)? -1: (o1.weight > o2.weight)? 1: 0;
            }
        });
        CheckCycle checkCycle = new CheckCycle();
        for (Edge edge : edges) {

            spanning_tree[edge.sourcevertex][edge.destinationvertex] = edge.weight;
            spanning_tree[edge.destinationvertex][edge.sourcevertex] = edge.weight;
            if (checkCycle.checkCycle(spanning_tree, edge.sourcevertex)) {
                System.out.println(edge.sourcevertex + " - " + edge.destinationvertex);
                spanning_tree[edge.sourcevertex][edge.destinationvertex] = 0;
                spanning_tree[edge.destinationvertex][edge.sourcevertex] = 0;
                edge.weight = -1;
                System.out.println("kruskalAlgorithm()- remove " +edge.sourcevertex+ " "+edge.destinationvertex);
                continue;
            }

            visited[edge.sourcevertex] = 1;
            visited[edge.destinationvertex] = 1;
            for (int i = 0; i < visited.length; i++) {
                if (visited[i] == 0) {
                    finished = false;
                    break;
                } else {
                    finished = true;
                }
            }
            if (finished) {
                break;
            }
        }
       
        System.out.println("The spanning tree is ");

        for (int source = 0; source < numberOfVertices; source++)
        {
            System.out.print(source + "\t");
            for (int destination = 0; destination < numberOfVertices; destination++)
            {
                if(spanning_tree[source][destination]>0)
                    System.out.print(destination+"\t");//+" "+spanning_tree[source][destination] + "\t");
            }
            System.out.println();
        }
         
        return spanning_tree;
    }


    public List<List<Edge>> kruskalAlgorithm_getPath(double lamda, FileWriter fw) {
        List<List<Integer>> clusters = new ArrayList<List<Integer>>();

        System.out.println(edges.size());
        for (Iterator<Edge> iter = edges.listIterator(); iter.hasNext();) {
            Edge tmp = iter.next();
            if (tmp.weight < lamda) {
                iter.remove();
            }
        }

        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                // TODO Auto-generated method stub
                return (o1.weight < o2.weight)? -1: (o1.weight > o2.weight) ? 1 :0;
            }
        });
        System.out.println(edges.size());

        Queue<Integer> st = new LinkedList<>();
        List<List<Edge>> path = new ArrayList<List<Edge>>();
        for (int source = 0; source < numberOfVertices; source++) {
            List<Integer> subClus = new ArrayList<>();
            List<Edge> subPath = new ArrayList<>();

            subClus.add(source);

            for (Edge edge : edges) {
                if (edge.sourcevertex == source) {
//        			System.out.println("checking..."+source + ":"+edge.destinationvertex);
                    st.add(edge.destinationvertex);
                    if (!subPath.contains(edge)) {
                        subPath.add(edge);
                    }
                }

                while (!st.isEmpty()) {
                    int ele = st.peek();

                    if (!subClus.contains(ele))// check (edge.weight> lamda) 
                    {
                        subClus.add(ele);
                    }

                    for (Edge edgex : edges) {
//    		        	System.out.println("checking..."+source);
                        if (edgex.sourcevertex == ele && !st.contains(edgex.destinationvertex) && !subClus.contains(edgex.destinationvertex)) {
//    		        		System.out.println(source+"-"+ ele+"-"+edgex.destinationvertex);
                            st.add(edgex.destinationvertex);
                            if (!subPath.contains(edgex)) {
                                subPath.add(edgex);
                            }
                        }
                    }
                    st.poll();
                }
            }

            Collections.sort(subClus, new Comparator<Integer>() {

                @Override
                public int compare(Integer o1, Integer o2) {
                    // TODO Auto-generated method stub
                    return (o1 < o2)?-1: (o1 > o2)? 1 : 0;
                }
            });

            if (subPath.isEmpty()) {
                Edge newEdge = new Edge(source, source, 1);
                subPath.add(newEdge);
            }
            if (!clusters.contains(subClus)) {
                clusters.add(subClus);
                path.add(subPath);
            }
        }

        for (int source = 0; source < path.size(); source++) {
            List<Edge> subpath = path.get(source);
            if (subpath.size() > 1) {
                System.out.print("Path: " + (source+1) + ":\t");
                for (int i = 0; i < subpath.size(); i++) {
                    System.out.print((subpath.get(i).sourcevertex+1) + "-" + (subpath.get(i).destinationvertex+1) + "\t");
                }
                System.out.println();
            }
        }
        return path;
    }

    /*
    - sort edges.
    loop untill full path:
    - get max, 
    - add to path: source- des, if list of node create cycle? or path?
    - check cycle in path: if cycle, roll back to parent node 
    */
   
    /*
    add node to stack.
    loop
    {
    get node in stack.
    check each node in path(path has edge: source-des)
        -if node available, return true
        -else ad source and des to stack, continue to check next edge.
    }
    */
    private boolean isCycle(List<Edge> path, int source)
    {
        boolean ret = false;
        Stack<Integer> st = new Stack<>();
        st.add(source);
        int element;
        boolean[] vis = new boolean[numberOfVertices];
        
        while(!st.empty())
        {
            element = st.peek();
            for(Edge e: path)
            {
                if(e.sourcevertex == element && vis[e.destinationvertex] == true)
                {
                    if(st.contains(e.destinationvertex))
                        return true;
                }
                if(e.sourcevertex == element && vis[e.destinationvertex] == false)
                {
                    if(!st.contains(e.destinationvertex))
                        st.push(e.destinationvertex);
                    vis[e.destinationvertex] = true;
                    element = e.destinationvertex;
                    continue;
                }
            }
            st.pop();
        }
        return ret;
    }
    /*
    TODO:
    - find max path cover graph
    - MSG: connect node in path with related node.
    - get sub path
    */
    
    /*
    Select max path untill cover all graph
    pick->check path
    */
    private List<Edge> primMST()
    {
        List<Edge> path = new ArrayList<>();
        
        return path;
    }
    private List<Edge> MST()
    {
        int[] edgeTimeM = new int[numberOfVertices];
        double[] ew = new double[numberOfVertices];
        for(Edge e: edges)
        {
            edgeTimeM[e.sourcevertex] = edgeTimeM[e.sourcevertex]+1;
            ew[e.sourcevertex] = ew[e.sourcevertex]>e.weight? ew[e.sourcevertex]:e.weight;
            edgeTimeM[e.destinationvertex] = edgeTimeM[e.destinationvertex]+1;
            ew[e.destinationvertex] = ew[e.destinationvertex]>e.weight? edgeTimeM[e.destinationvertex]:e.weight;
        }
        
        for(int i = 0; i< numberOfVertices;i++)
        {
            minE = (minE>ew[i])?ew[i]:minE;
        }
        Queue<Integer> st = new LinkedList<>();
        List<Edge> subPath = new ArrayList<>();
        int visN[] = new int[numberOfVertices];
        
        for (int source = 0; source < numberOfVertices; source++) {
        
            if(visN[source] !=1  && !st.contains(source))
                st.add(source);
            else
                continue;
            
            System.out.println("paper.MSTClustering.MST() next"+source);
            while(!st.isEmpty())
            {
                int top = st.poll();
                visN[top] = 1;
                
                for(Edge e: edges)
                {
                    if((e.sourcevertex == top) && (visN[e.destinationvertex]!= 1 && !st.contains(e.destinationvertex))&& e.weight>=minE)  
                    {
                        st.add(e.destinationvertex);
                        subPath.add(e);
                    }
                    if((e.destinationvertex == top) && (visN[e.sourcevertex]!= 1 && !st.contains(e.sourcevertex)) && e.weight>=minE) 
                    {
                        st.add(e.sourcevertex);
                        subPath.add(new Edge(e.destinationvertex, e.sourcevertex,e.weight));
                    }
                }
            }
//            
        }
//        int[] edgeTime = new int[numberOfVertices];
//        for(Edge e: subPath)
//        {
//            edgeTime[e.sourcevertex] = edgeTime[e.sourcevertex]+1;
//            edgeTime[e.destinationvertex] = edgeTime[e.destinationvertex]+1;
//        }
//        
//        for(int i = 0; i< numberOfVertices; i++)
//            System.out.println("paper.MSTClustering.MST() count " +(i+1)+" :" + edgeTime[i]);

//        Collections.sort(subPath, new Comparator<Edge>() {
//            @Override
//            public int compare(Edge o1, Edge o2) {
//                return (o1.weight < o2.weight)? -1: (o1.weight > o2.weight)? 1:0;
//            }
//        });
                
        for(Edge e: subPath)
        {
            System.out.println("path .MST2() "+(e.sourcevertex + 1)+"-"+ (e.destinationvertex + 1)+"\t"+e.weight );
        }
        return subPath;
    }
    
    private List<List<Integer>> MSG()
    {
//        List<List<Integer>> 
        cluster = new ArrayList<>();

        List<Edge> mst= MST();
        
        System.out.println(edges.size()+ " min "+minE);
        for (Iterator<Edge> iter = edges.listIterator(); iter.hasNext();) {
            Edge tmp = iter.next();
            if (tmp.weight <= minE) {
                iter.remove();
            }
        }

//        int[] edgeTime = new int[numberOfVertices];
//        for(Edge e: edges)
//        {
//            edgeTime[e.sourcevertex] = edgeTime[e.sourcevertex]+1;
//            edgeTime[e.destinationvertex] = edgeTime[e.destinationvertex]+1;
//        }
//        for(int i = 0; i< numberOfVertices; i++)
//            System.out.println("paper.MSTClustering.MST() count " +(i+1)+" :" + edgeTime[i]);
        
        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                return (o1.weight < o2.weight)? -1: (o1.weight > o2.weight)? 1:0;
            }
        });

        Queue<Integer> st = new LinkedList<>();
        List<List<Edge>> path = new ArrayList<>();
        int vis[] = new int[edges.size()];

        for (int source = 0; source < numberOfVertices; source++) {
            List<Integer> subClus = new ArrayList<>();
            List<Edge> subPath = new ArrayList<>();

            subClus.add(source+1);
//            st.add(source);
            for (Edge edge : edges) {
                if(vis[edges.indexOf(edge)]>0)
                    continue;
                if (edge.sourcevertex == source) {
                    st.add(edge.destinationvertex);
                    if (!subPath.contains(edge)) {
                        subPath.add(edge);
                        vis[edges.indexOf(edge)] = 1;
                    }
                }

                while (!st.isEmpty()) {
                    int ele = st.peek();

                    if (!subClus.contains(ele+1))// check (edge.weight> lamda) 
                    {
                        subClus.add(ele+1);
                    }

                    for (Edge edgex : edges) {
                        if(vis[edges.indexOf(edgex)]>0)
                            continue;
//    		        	System.out.println("checking..."+source);
                        if (edgex.sourcevertex == ele && !st.contains(edgex.destinationvertex) && !subClus.contains(edgex.destinationvertex+1)) {
//    		        		System.out.println(source+"-"+ ele+"-"+edgex.destinationvertex);
                            st.add(edgex.destinationvertex);
                            if (!subPath.contains(edgex)) {
                                subPath.add(edgex);
                                vis[edges.indexOf(edgex)] = 1;
                            }
                        }
                    }
                    st.poll();
                }
            }

            if (!cluster.contains(subClus) && subClus.size()>1) {
                Collections.sort(subClus, new Comparator<Integer>() {
                    @Override
                    public int compare(Integer o1, Integer o2) {
                        // TODO Auto-generated method stub
                        return (o1 < o2)?-1: (o1 > o2)? 1 : 0;
                    }
                });
                cluster.add(subClus);
                path.add(subPath);
            }
        }

        for (int source = 0; source < path.size(); source++) {
            List<Edge> subpath = path.get(source);
            {
                Collections.sort(subpath, new Comparator<Edge>() {
                    @Override
                    public int compare(Edge o1, Edge o2) {
                    return (o1.sourcevertex < o2.sourcevertex)? -1:(o1.sourcevertex > o2.sourcevertex)? 1:0;
                    }
                });
            }
        }
       
        Collections.sort(path, new Comparator<List<Edge>>() {
                    @Override
                    public int compare(List<Edge> o1, List<Edge> o2) {
                    if (o1.size() < o2.size()) return -1;
                    if (o1.size() > o2.size()) return 1;
                    return 0;
                    }
                });

        for (int source = 0; source < path.size(); source++) {
            List<Integer> subG = new ArrayList<>();
            List<Edge> subpath = path.get(source);
            {
                System.out.print("Path: " + (source+1) + ":\t");
                for (Edge e: subpath) {
                    System.out.print((e.sourcevertex+1) + "-" + (e.destinationvertex+1) +"\t"); //":"+e.weight+ 
                    if(!subG.contains(e.sourcevertex+1))
                        subG.add(e.sourcevertex+1);
                    if(!subG.contains(e.destinationvertex+1))
                        subG.add(e.destinationvertex+1);
                }
                System.out.println();
            }
            cluster.add(subG);
        }
        
        
        Collections.sort(cluster, new Comparator<List<Integer>>() {
                @Override
                public int compare(List<Integer> o1, List<Integer> o2) {
                    // TODO Auto-generated method stub
                    return (o1.size() < o2.size())? -1: ((o1.size() > o2.size())?1:0);
                }
            });
        
//        for(List<Integer> i: cluster)
//            System.out.println("G i "+i);
        // Remove
        boolean[] rm = new boolean[cluster.size()];
        for(int i = 0; i< cluster.size()-1; i++)
        {
            List<Integer> a= cluster.get(i);
            for(int j = i+1; j<cluster.size(); j++)
            {
                rm[i] = true;
                List<Integer> b= cluster.get(j);
                for(Integer v: a)
                {
                    if(!b.contains(v))
                    {
                        rm[i] = false;
                        break;
                    }
                }
                if(rm[i] == true)
                    break;
            }
        }
        
        int index = 0;
        for (Iterator<List<Integer>> iter = cluster.listIterator(); iter.hasNext();) {
            iter.next();
            if (rm[index]) {
                iter.remove();
            }
            index++;
        }
        
        index = 0;
        for (Iterator<List<Edge>> iter = path.listIterator(); iter.hasNext();) {
            iter.next();
            if (rm[index]) {
//                System.out.println("paper.MSTClustering.MSG() RM "+index);
                iter.remove();
            }
            index++;
        }
        
        for(List<Integer> i: cluster)
            System.out.println("newG i "+i);

        
        List<Edge> newListE = new ArrayList<>();
        for(List<Edge> subpath : path){
            if (subpath.size() > 1) {
                System.out.print("Path:\t");
                for (int i = 0; i < subpath.size(); i++) {
                    Edge e = subpath.get(i);

                    if(!newListE.contains(e) && e.weight>0.21)
                        newListE.add(e);
                    System.out.print((e.sourcevertex+1) + "-" + (e.destinationvertex+1) + "\t"); //":"+e.weight+ 
                }
                System.out.println();
            }
        }
        

        
        System.err.println("new list size "+newListE.size());
        Collections.sort(newListE, new Comparator<Edge>
        () {
            @Override
            public int compare(Edge o1, Edge o2) {
                    if (o1.sourcevertex < o2.sourcevertex) return -1;
                    if (o1.sourcevertex > o2.sourcevertex) return 1;
                    return 0;
            }
        });
        
//        for (int i = 0; i < newListE.size(); i++) {
//                    Edge e = newListE.get(i);
//                    if(subpath.get(i).weight>0.2)
//                    System.out.println((e.sourcevertex+1) + "-" + (e.destinationvertex+1) + ":"+e.weight+ "\t"); //
//        }
        
//        MSG(newListE);
        
/*
from here: 
split cluster has low sim-> remove low edge 
merge to other cluster has high sim
*/

        return cluster;
    }
    
    private List<List<Integer>> MSG(List<Edge> _path)
    {
        List<List<Integer>> listNext = new ArrayList<>();
        
        for(int i = 0; i< numOfFeature; i++)
        {
            List<Integer> list = new ArrayList<>();
            list.add(i);
            for(Edge node : _path)
            {
                if((node.sourcevertex == i)  && !list.contains(node.destinationvertex))
                    list.add(node.destinationvertex);
                if((node.destinationvertex == i) && !list.contains(node.sourcevertex))
                    list.add(node.sourcevertex);
            }
//            Collections.sort(list, new Comparator<Integer>() {
//                @Override
//                public int compare(Integer o1, Integer o2) {
//                    // TODO Auto-generated method stub
//                    return (o1 < o2)? -1: ((o1 > o2)?1:0);
//                }
//            });
            
            listNext.add(list);
        }
        Collections.sort(listNext, new Comparator<List<Integer>>() {
                @Override
                public int compare(List<Integer> o1, List<Integer> o2) {
                    // TODO Auto-generated method stub
                    return (o1.size() < o2.size())? -1: ((o1.size() > o2.size())?1:0);
                }
            });
        
        // Remove
        boolean[] rm = new boolean[numOfFeature];
        for(int i = 0; i< numOfFeature-1; i++)
        {
            List<Integer> a= listNext.get(i);
            for(int j = i+1; j<numOfFeature; j++)
            {
                rm[i] = true;
                List<Integer> b= listNext.get(j);
                for(Integer v: a)
                {
                    if(!b.contains(v))
                    {
                        rm[i] = false;
                        break;
                    }
                }
                if(rm[i] == true)
                {
//                    System.out.println("paper.MSG() rm a " +a + " "+i);
                    break;
                }
            }
        }
        int index = 0;
        for (Iterator<List<Integer>> iter = listNext.listIterator(); iter.hasNext();) {
            iter.next();
            if (rm[index]) {
//                System.out.println("paper.MSG() rm "+ tmp);
                iter.remove();
            }
            index++;
        }
        
        for(List<Integer> i: listNext)
            System.out.println("MSG i "+i);
        
        return listNext;
    }
    public void getCluster() throws IOException
    {
//        MST();
        MSG();
        //        MSG(edges);
    }
    /**
     *
     * @param fw
     */
    @Override
    public void getCluster(FileWriter fw) {
        
        boolean finished = false;
        System.out.println("getCluster() "+edges.size());

        Collections.sort(edges, new Comparator<Edge>() {
            @Override
            public int compare(Edge o1, Edge o2) {
                // TODO Auto-generated method stub
                return (o1.weight > o2.weight) ? -1: (o1.weight < o2.weight)? 1: 0;
            }
        });
        
//        for (Edge edge : edges)
//    	{
//        	System.out.println("----"+edge.sourcevertex+"- "+edge.destinationvertex +": "+edge.weight);
//    	}

//        System.out.println(edges.size());
//
//        CheckCycle checkCycle = new CheckCycle();
//        for (Edge edge : edges) {
//
//            spanning_tree[edge.sourcevertex][edge.destinationvertex] = edge.weight;
//            spanning_tree[edge.destinationvertex][edge.sourcevertex] = edge.weight;
//            if (checkCycle.checkCycle(spanning_tree, edge.sourcevertex)) {
//                System.out.println("remove "+edge.sourcevertex + " - " + edge.destinationvertex);
//                spanning_tree[edge.sourcevertex][edge.destinationvertex] = 0;
//                spanning_tree[edge.destinationvertex][edge.sourcevertex] = 0;
//                edge.weight = -1;
////                System.out.println("kruskalAlgorithm()- remove " +edge.sourcevertex+ " "+edge.destinationvertex);
//                continue;
//            }
//
//            visited[edge.sourcevertex] = 1;
//            visited[edge.destinationvertex] = 1;
//            for (int i = 0; i < visited.length; i++) {
//                if (visited[i] == 0) {
//                    finished = false;
//                    break;
//                } else {
//                    finished = true;
//                }
//            }
//            if (finished) {
//                break;
//            }
//        }
        
        for (Iterator<Edge> iter = edges.listIterator(); iter.hasNext();) {
            Edge tmp = iter.next();
            if (tmp.weight < lambda) {
                System.out.println("kruskalAlgorithm_getCluster() remove edge 2: "+tmp.sourcevertex+ " "+tmp.destinationvertex);
                iter.remove();
            }
        }

//        for (Edge edge : edges)
//    	{
//        	System.out.println("----"+edge.sourcevertex+"- "+edge.destinationvertex +": "+edge.weight);
//    	}
        
       
//        Matrix.printMat(spanning_tree, "spanning_tree");
        Queue<Integer> st = new LinkedList<>();
//        List<List<Edge>> path = new ArrayList<List<Edge>>();
        for (int source = 0; source < numberOfVertices; source++) {
            List<Integer> subClus = new ArrayList<>();
            if(visited[source]==0)
            {
                subClus.add(source);
                visited[source] = 1;
            
            for (Edge edge : edges) {
                if (edge.sourcevertex == source) {
//        			System.out.println("checking..."+source + ":"+edge.destinationvertex);
                    st.add(edge.destinationvertex);
                    visited[edge.destinationvertex] = 1;
                }
                if (edge.destinationvertex == source) {
//        			System.out.println("checking..."+source + ":"+edge.destinationvertex);
                    st.add(edge.sourcevertex);
                    visited[edge.sourcevertex] = 1;
                }

                while (!st.isEmpty()) {
                    int ele = st.peek();

                    if (!subClus.contains(ele))// check (edge.weight> lamda) 
                    {
                        subClus.add(ele);
                    }
                    for (Edge edgex : edges) {
//    		        	System.out.println("checking..."+source);
                        if (edgex.sourcevertex == ele && !st.contains(edgex.destinationvertex) && !subClus.contains(edgex.destinationvertex)) {
//    		        		System.out.println(source+"-"+ ele+"-"+edgex.destinationvertex);
                            st.add(edgex.destinationvertex);
                        }
                    }
                    st.poll();
                }
            }

            Collections.sort(subClus, new Comparator<Integer>() {
                @Override
                public int compare(Integer o1, Integer o2) {
                    // TODO Auto-generated method stub
                    return (o1 < o2) ? -1: (o1 > o2) ? 1: 0;
                }
            });
            
            
            if (!cluster.contains(subClus)&& subClus.size()>1) {
                cluster.add(subClus);
            }
            }
        }

        for(List<Integer>C:cluster)
        {
            if(!C.isEmpty())
            {
//            System.out.println("paper.MSTClustering.kruskalAlgorithm()"+clusters.indexOf(C));
            System.out.print("\n");
                for(Integer I:C)
                {
                    System.out.print(I+"\t");
                }
                System.out.print("\n");
            }
        }
//        return clusters;
    }

    
    /*
        TODO: from MST, build MSG, triple edge
    */

    public static List<List<Integer>> getSubClus(List<List<Edge>> path) {
        List<List<Integer>> ret = new ArrayList<>();

        for (List<Edge> sub : path) {
            List<Integer> subClus = new ArrayList<>();
            for (Edge edge : sub) {
                if (!subClus.contains(edge.sourcevertex)) {
                    subClus.add(edge.sourcevertex);
                }
                if (!subClus.contains(edge.destinationvertex)) {
                    subClus.add(edge.destinationvertex);
                }
            }
            ret.add(subClus);
        }
        return ret;
    }

    
    public static List<Integer> getRepesentVec(List<List<Edge>> path) {
        //TODO
        List<Integer> ret = new ArrayList<>();

        for (List<Edge> sub : path) {
            System.err.println("getRepesentVec");
            if (sub.size() > 0) {

                Map<Integer, Integer> count = new HashMap<Integer, Integer>();
                for (Edge edge : sub) {
                    if (!count.containsKey(edge.sourcevertex)) {
                        count.put(edge.sourcevertex, 1);
                    } else {
                        int value = count.get(edge.sourcevertex) + 1;
                        count.put(edge.sourcevertex, value);
                    }
                    if (!count.containsKey(edge.destinationvertex)) {
                        count.put(edge.destinationvertex, 1);
                    } else {
                        int value = count.get(edge.destinationvertex) + 1;
                        count.put(edge.destinationvertex, value);
                    }
                }
                int max = count.keySet().iterator().next();

                for (int i : count.keySet()) {
                    max = (count.get(max) > count.get(i) ? max : i);
                }

                ret.add(max);
                System.err.println("getRepesentVec > 0 " + max + " - " + count.get(max));
            } else {
                ret.add(0);
            }
        }
        return ret;
    }
//    public static List<Integer> getRepesentVec(double[][] path) {
//        //TODO
//        List<Integer> ret = new ArrayList<>();
//
//        return ret;
//}
}



class CheckCycle {

    private Stack<Integer> stack;
    private double adjacencyMatrix[][];

    public CheckCycle() {
        stack = new Stack<Integer>();
    }

    public boolean checkCycle(double adjacency_matrix[][], int source) {
        boolean cyclepresent = false;
        int number_of_nodes = adjacency_matrix[source].length - 1;

        adjacencyMatrix = new double[number_of_nodes + 1][number_of_nodes + 1];
        for (int sourcevertex = 0; sourcevertex < number_of_nodes; sourcevertex++) {
            for (int destinationvertex = 0; destinationvertex < number_of_nodes; destinationvertex++) {
                adjacencyMatrix[sourcevertex][destinationvertex] = adjacency_matrix[sourcevertex][destinationvertex];
            }
        }

        int visited[] = new int[number_of_nodes + 1];
        int element = source;
        int i = source;
        visited[source] = 1;
        stack.push(source);

        while (!stack.isEmpty()) {
            element = stack.peek();
            i = element;
            while (i <= number_of_nodes) {
                if (adjacencyMatrix[element][i] >= 1 && visited[i] == 1) {
                    if (stack.contains(i)) {
                        cyclepresent = true;
                        return cyclepresent;
                    }
                }
                if (adjacencyMatrix[element][i] >= 1 && visited[i] == 0) {
                    stack.push(i);
                    visited[i] = 1;
                    adjacencyMatrix[element][i] = 0;// mark as labelled;
                    adjacencyMatrix[i][element] = 0;
                    element = i;
                    i = 1;
                    continue;
                }
                i++;
            }
            stack.pop();
        }
        return cyclepresent;
    }
}

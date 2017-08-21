package pt.paper;

import java.util.Comparator;
import java.util.List;

public class Edge implements Comparator<Edge>{

    int scr;
    int dst;
    double weight;

    public Edge(int sourcevertex, int destinationvertex, double weight) {
		super();
		this.scr = sourcevertex;
		this.dst = destinationvertex;
		this.weight = weight;
	}
    
    @Override
    public int compare(Edge edge1, Edge edge2)
    {
        if (edge1.weight < edge2.weight)
            return -1;
        if (edge1.weight > edge2.weight)
            return 1;
        return 0;
    }
    
    public static double getEdgeW(List<Edge> edges, int s, int d)
    {
//        double ret = 0;
        for(Edge e:edges)
            if((e.scr == s && e.dst ==d)|| (e.scr == d && e.dst ==s))
                return e.weight;
        return 0.;
    }
    
    public static int[] retSize(List<Edge> edges, int numberOfVertices) {
        int[] ret = new int[numberOfVertices];
        for (Edge e : edges) {
            ret[e.scr] = ret[e.scr] + 1;
            ret[e.dst] = ret[e.dst] + 1;
        }
        return ret;
    }
}

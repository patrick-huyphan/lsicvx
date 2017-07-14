package pt.paper;

import java.util.Comparator;

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
}

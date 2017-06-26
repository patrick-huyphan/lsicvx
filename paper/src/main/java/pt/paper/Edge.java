package paper;

import java.util.Comparator;

public class Edge implements Comparator<Edge>{

	int sourcevertex;
    int destinationvertex;
    double weight;

    public Edge(int sourcevertex, int destinationvertex, double weight) {
		super();
		this.sourcevertex = sourcevertex;
		this.destinationvertex = destinationvertex;
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

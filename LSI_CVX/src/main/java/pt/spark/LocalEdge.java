package pt.paper;

import java.util.Comparator;

public class LocalEdge implements Comparator<LocalEdge>{

	int sourcevertex;
    int destinationvertex;
    double weight;

    public LocalEdge(int sourcevertex, int destinationvertex, double weight) {
		super();
		this.sourcevertex = sourcevertex;
		this.destinationvertex = destinationvertex;
		this.weight = weight;
	}
    
    @Override
    public int compare(LocalEdge edge1, LocalEdge edge2)
    {
        if (edge1.weight < edge2.weight)
            return -1;
        if (edge1.weight > edge2.weight)
            return 1;
        return 0;
    }
}

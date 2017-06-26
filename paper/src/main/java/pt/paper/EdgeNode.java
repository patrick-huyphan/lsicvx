/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.util.Comparator;

/**
 *
 * @author patrick_huy
 * Should create new class to optimize get value from edge
 */
public class EdgeNode  implements Comparator<EdgeNode>{
    int source;
    int dest;
    double[] relatedValue;
    public EdgeNode(int _s, int _d, double [] data)
    {
        source = _s;
        dest = _d;
        relatedValue = data; 
    }   

    @Override
    public int compare(EdgeNode o1, EdgeNode o2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}

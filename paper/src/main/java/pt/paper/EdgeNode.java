/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.util.Comparator;
import java.util.List;

/**
 *
 * @author patrick_huy
 * Should create new class to optimize get value from edge
 */
public class EdgeNode  implements Comparator<EdgeNode>{
    int scr;
    int dst;
    double[] relatedValue;
    public EdgeNode(int _s, int _d, double [] data)
    {
        scr = _s;
        dst = _d;
        relatedValue = data; 
    }   

    @Override
    public int compare(EdgeNode o1, EdgeNode o2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
//    
//    public static EdgeNode getUVData(List<EdgeNode> A, int s, int d)
//    {
////        double[] ret = new double[numOfFeature];
//        
//        for(EdgeNode e: A)
//            if(e.scr == s && e.dst ==d)
//            {
//                return e;//.relatedValue;
//            }
////        System.out.println("paper.SCC.getUVData() nul "+s+" "+d);
//        return new EdgeNode(s, d, new double[numOfFeature]);
//    }
}

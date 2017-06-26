/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paper;

import java.util.HashMap;

/**
 *
 * @author patrick_huy
 */
public class ListENode extends HashMap<int[], double[]>{
//    HashMap<int[], double[]> E;
//    int length;
//    public ListENode(int _length)
//    {
////        length = _length;
////        E = new HashMap<>();
//    }
//    
    public ListENode()
    {
        super();
    }
    public double[] get(int i, int j)
    {
//        double[] ret = new double[length];
        
        System.out.println("paper.ListENode.getByKey() "+i+"-"+j+": ");
        double[] d= get(new int[]{i, j});
        
        if(d==null)
        {
            System.out.println("paper.ListENode.get() NULL");
            return new double[26];
        }
        for(int t=0; t< d.length; t++)
            System.out.print("\t"+d[t]);
        System.out.println("");
        
        return d;
    }
//    public double[] getByKey(int[] key)
//    {
//        double[] d= E.get(key);
//        System.out.println("paper.ListENode.getByKey() "+key[0]+"-"+key[1]+" size"+E.size());
//        for(int i=0; i< d.length; i++)
//            System.out.print("\t"+d[i]);
//        System.out.println("");
//        return d;
//    }
//    
    public void put(int i, int j, double [] value)
    {
        double[] ret = this.put(new int[]{i,j}, value);
        if(ret == null)
            System.out.println("paper.ListENode.put() null"+i+"-"+j);
//        for(int t=0; t< ret.length; t++)
//            System.out.print("\t"+ret[i]);
//        System.out.println("");
    }
    
//    public void set(int[] key, double [] value)
//    {
//        E.put(key, value);
//    }
}

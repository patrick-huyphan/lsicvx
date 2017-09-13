/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.spark;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author patrick_huy
 */
public class ListENode{ //extends HashMap<Key, double[]>{
    
    HashMap<Key, double[]> E;
//    int length;
//    public ListENode(int _length)
//    {
////        length = _length;
////        E = new HashMap<>();
//    }
//    
    public ListENode()
    {
        E = new HashMap<>();         
    }
    public double[] get(int i, int j)
    {
//        double[] ret = new double[length];
        
//        System.out.println("paper.ListENode.getByKey() "+i+"-"+j+": ");
        double[] d= E.get(new Key(i, j));
        
        if(d==null)
        {
            System.out.println("paper.ListENode.get() NULL");
//            return new double[26];
        }
//        for(int t=0; t< d.length; t++)
//            System.out.print("\t"+d[t]);
//        System.out.println("");
        
        return d;
    }
        
    public double[] get(Key k)
    {
//        double[] ret = new double[length];
        
//        System.out.println("paper.ListENode.getByKey() "+k.src+"-"+k.dst+": ");
        double[] d= E.get(k);
        
        if(d==null)
        {
            System.out.println("paper.ListENode.get() NULL");
        }
//        for(int t=0; t< d.length; t++)
//            System.out.print("\t"+d[t]);
//        System.out.println("");
        
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
//        double[] ret = 
        if(!E.containsKey(new Key(i, j)))
            E.put(new Key(i, j), value);
        else
        {
            System.out.println("pt.paper.ListENode.update () "+i+" "+j);
            E.replace(new Key(i, j), value);
        }
//        if(ret == null)
//            System.out.println("paper.ListENode.put() null "+i+"-"+j);
//        for(int t=0; t< ret.length; t++)
//            System.out.print("\t"+ret[i]);
//        System.out.println("");
    }
    
//    public void set(int[] key, double [] value)
//    {
//        E.put(key, value);
//    }
    
    
    
}
class Key
    {
        int src;
        int dst;

        public Key(int  flag1, int  flag2) {
            this.src = flag1;
            this.dst = flag2;
        }

        @Override
        public boolean equals(Object object) {
            if (!(object instanceof Key)) {
                return false;
            }

            Key otherKey = (Key) object;
            return this.src == otherKey.src && this.dst == otherKey.dst;
        }

        @Override
        public int hashCode() {
            int result = 17; // any prime number
            result = 31 * result + Integer.valueOf(this.src).hashCode();
            result = 31 * result + Integer.valueOf(this.dst).hashCode();
            return result;
        }
    }
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author patrick_huy
 */
public class SCCNew  extends Clustering{

    double rho;
    double rho2;
    double lambda2;
    double ea, er;
//    public double [][] X;
    DecimalFormat twoDForm = new DecimalFormat(" 0.00000000");
    boolean stop = false;
    
    double[] u ;
    double[] xAvr;
    int numofEdge;
    double[][] X;
    List<EdgeNode> V;
    List<EdgeNode> U;    
    double[][] X0;
    List<EdgeNode> V0;
    List<EdgeNode> U0;
    /**
     * 
     * @param _Matrix
     * @param _lambda
     * @throws IOException 
     * Init: 
     * - X = A
     * - u and z base on the edges
     * X = min (fx + sum(r/2)||x-z+u||)
     * z = min(lamda*weigh||zi-zj||+ r/2(||xi-zi+ui||+||xj-zj+uj||))
     * u = u +(x-z)
     */
    public SCCNew(double[][] _Matrix, double _lambda, double _lambda2,double _rho, double _e1, double _e2) throws IOException {
        super(_Matrix, _lambda);
        
        init();
        initZ();
        initU();
        
        while(true)
        {
            for(int i = 0; i< numberOfVertices; i++)
                x_ADMM(i);
            for(int i = 0; i< numofEdge; i++)
                z_ADMM(i);
            for(int i = 0; i< numofEdge; i++)
                u_ADMM(i);
            
            if(checkStop())
                break;
            updateRho();
        }
    }

    @Override
    public void getCluster(FileWriter fw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private void init()
    {
        X=A;
        X0 = new double[numberOfVertices][numOfFeature];
        numofEdge = edges.size();
    }
    
    //for all edge
    private void initZ()
    {
        List<EdgeNode> ret = new ArrayList<>();
       
        for(Edge e:edges)
        {
            ret.add(new EdgeNode(e.scr, e.dst, new double[numOfFeature]));
            ret.add(new EdgeNode(e.dst, e.scr, new double[numOfFeature]));
        }    
    }
    //for all edge
    private void initU()
    {
        List<EdgeNode> ret = new ArrayList<>();
       
        for(Edge e:edges)
        {
            ret.add(new EdgeNode(e.scr, e.dst, new double[numOfFeature]));
            ret.add(new EdgeNode(e.dst, e.scr, new double[numOfFeature]));
        }    
    
    }    
    /**
     * for each node
     * should get related x-, z- and u-
     * solve min problem
     * @param id 
     */
    private void x_ADMM(int id)
    {
        
    }
    /**
     * for each edge
     * should get related z-, x and u-
     * solve min problem
     * @param id 
     */
    private void z_ADMM(int id)
    {
        
    }
    /**
     * for each edge: u = u +(x-z)
     * should get related u-, x and z
     * @param id 
     */    
    private void u_ADMM(int id)
    {
        
    }

    private boolean checkStop()
    {
        return true;
    }
    private void updateRho()
    {
        
    }
    
}

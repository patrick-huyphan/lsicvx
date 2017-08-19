/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

/**
 *
 * @author patrick_huy
 */
public class ADMMNew  extends LSI{
    
    double rho;
    
    public ADMMNew(double [][] _Ddata, double [][] _Bdata, double _rho,double _lambda, double e1, double e2) {
        super(_Ddata, _Bdata, _lambda);
    
        init();
        
        while(true)
        {
            for(int i = 0; i< m; i++)
                x_ADMM(i);
            for(int i = 0; i< m; i++)
                z_ADMM(i);
            for(int i = 0; i< m; i++)
                u_ADMM(i);
            
            if(checkStop())
                break;
            updateRho();
        }
    }
    
    private void init()
    {
        
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

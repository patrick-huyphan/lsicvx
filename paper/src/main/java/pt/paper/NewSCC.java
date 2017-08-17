/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pt.paper;

import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author patrick_huy
 */
public class NewSCC  extends Clustering{

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
    public NewSCC(double[][] _Matrix, double _lambda) throws IOException {
        super(_Matrix, _lambda);
        
        while(true)
        {
            x_ADMM();
            z_ADMM();
            u_ADMM();
            
            if(checkStop())
                break;
            updateRho();
        }
    }

    @Override
    public void getCluster(FileWriter fw) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void x_ADMM()
    {
        
    }
    
    private void z_ADMM()
    {
        
    }
    
    private void u_ADMM()
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

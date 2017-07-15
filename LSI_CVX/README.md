# LSI_CVX

Flow processing:
- save doc-term as sparse matrix => in future, this job should be done by spark
- echelon and save to file as sparse matrix => in future, this job should be done by spark
 
assume that we have had matrix data and matrix after echelon data.
input array is 2D matrix:
    dense matrix is column matrix -> should transpose in advance to create dense matrix
    sparse matrix has 3 input: index of x, index of y, array value 
 
Spark start:
- read matrix data to rowmatrix
- SCC
-> input: doc-term row matrix (n-m)
-> output: present matrix (k-m)

-> transpose to term-doc
-> broadcast transposed matrix, all node can refer to this matrix
-> broadcast lambda, rho(not change ???)
-> apply SCC for each row in matrix
init  local variable: x, u ,v
loop:
    update local x,u,v -> cache 
    check stop local -> cache
    update global
reduce

-> return id of present vector
-> get present vector as new matrix

- ADMM 
-> input from SCC(B: k-m) and original row matrix(D: n-m) 
-> output: return projection matrix: matrix n-k for latent space

-> broadcast matrix D, B
-> broadcast lambda, rho
-> apply ADMM for each colum in D
init local variable x, u , v, D
loop:
    update x,u,v 
    check stop
    update global
reduce

# Submit job:
submit --class pt.spark.main <.jar file name> <input file> <output file> <num of query> <loop for ADMM> <orthogonal> <HL vector>  

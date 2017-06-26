# lsicvx
Using spark to process:
- clustering data with SCC
- find projection matrix with ADMM

SW requirement:
- JDK 1.8
- spark 2.1
- mavent

TODO:
Full flow:
- Read matrix data: where is data stored? 

- SCC: what data should be read, flow processing
    + map matrix
    + lambda, rho
    + row calculate
    + reduce row
- ADMM: what data should be read, flow processing
    + map matrix D.
    + map matrix B.
    + column calculate
    + reduce column
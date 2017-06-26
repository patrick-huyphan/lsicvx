# lsicvx
Using spark to process:
- clustering data with SCC
- find projection matrix with ADMM

SW requirement:
- JDK 1.8
- spark 2.1
- maven

TODO:

Paper: Full flow, normal processing with SCC and ADMM

Parallel processing:
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

- Full flow for parallel: 
    + submit job to read data
    + submit SCC job
    + submit ADMM job
    + submit job to transform D matrix to D' 
    + submit cosine job
    + reduce result


Spark config:
- local dir
- worker dir
- executer-core- memory

Spark submit on ubuntu:
- start-master
- add worker to master
- submit job to master
# lsiCVX, in development processing...
Using spark to process:
- clustering data with SCC
- find projection matrix with ADMM
- query cosine similarity

# SW requirement:
- JDK 1.8
- Java and scala
- spark 2.1
- maven

# Main prj
- Paper: full flow in single processing
- LSI_CVX: full flow in parallel processing

# TODO:
Parallel processing:
- Data structure: 
    - spark matrix struct: parallel on rowmatrix
    - broadcast data: local matrix to process on driver, variable of local function    

https://spark.apache.org/docs/2.0.2/api/java/org/apache/spark/mllib/linalg/SparseMatrix.html


- Read matrix data: currently, read data from .csv matrix file
    + Should add process to read from raw text file, build VSM.
- Should process echelon in parallel

- SCC: what data should be read, flow processing
    + Calculate local node data and broadcast local data
    + map row matrix
    + lambda, rho
    + row calculate
    + reduce row
- ADMM: what data should be read, flow processing
    + Calculate local node data and broadcast local data
    + map row matrix D( D should transposed).
    + column calculate
    + reduce column

- Full flow for parallel: 
    + submit job to read data
    + echelon
    + SCC job
    + ADMM job
    + transform D matrix to D' 
    + cosine similarity job
    + reduce result

Need more package for full app processing 
- Data preparation: parsing text data to matrix, remove stop work, build VSM
- echelon
- calculate local node data in parallel


# Spark
Need run with configuration from file.

Spark config:
- local dir
- worker dir
- executer: 
    +core
    +memory

Spark submit on ubuntu (standalone mode):
- start-master
- add worker to master
- submit job to master



https://spark.apache.org/docs/latest/spark-standalone.html
- ./sbin/start-master.sh -h localhost -p 7077
- ./sbin/start-slave.sh spark://localhost:7077
- ./bin/spark-shell --master spark://localhost:7077


# Ref
http://snap.stanford.edu/snapvx/
http://snap.stanford.edu/snapvx/developer_doc.pdf
http://di.eteri.ch/projects/admm_paper.pdf
http://www.programcreek.com/java-api-examples/index.php?api=org.apache.spark.broadcast.Broadcast


# Un-use project
- Parse data

# Author:
Patrick HuyPhan(huyphan.aj@gmail.com)
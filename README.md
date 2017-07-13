# lsiCVX, in development ...
Using spark to process:
- clustering data with sparse convex clustering-SCC
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

# Processing
- Data structure: parallel on rowmatrix, also all matrix is sparse matrix.

https://spark.apache.org/docs/2.0.2/api/java/org/apache/spark/mllib/linalg/SparseMatrix.html

- Broadcast data: local matrix to process on driver, variable of local function    

- Read matrix data: currently, read data from .csv matrix file
    + TODO: Should add process to read from raw text file, build VSM.
- TODO: Should process echelon in parallel

- SCC:
    + broadcast lambda, rho, and read only local data
    + map row matrix
    + row calculate: calculate local node data 
    + reduce row
- ADMM:
    + broadcast lambda, rho, and read only local data
    + map row matrix D( D should transposed).
    + column calculate: calculate local node data
    + reduce column

- Full flow for parallel: 
    + submit job to read data
    + echelon
    + SCC job
    + ADMM job
    + transform D matrix to D' 
    + cosine similarity job
    + reduce result

# TODO
Need more package for full app processing 
- Data preparation: parsing text data to matrix, remove stop work, build VSM
- echelon
- calculate local node data in parallel: mul matrix, inverse, compare, get cluster...

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
- submit job to master:
 
spark-submit <jar file> 
            --class <main class> 
            --master <spark-master> 
            <args1: input file> 
            <args2: outpur dir> 
            <args3: num of query in data file>


https://spark.apache.org/docs/latest/spark-standalone.html
- ./sbin/start-master.sh -h localhost -p 7077
- ./sbin/start-slave.sh spark://localhost:7077
- ./bin/spark-shell --master spark://localhost:7077

# Ref
http://snap.stanford.edu/snapvx/
http://snap.stanford.edu/snapvx/developer_doc.pdf
http://di.eteri.ch/projects/admm_paper.pdf

# Un-use project
- Parse data

# Author:
Patrick HuyPhan(huyphan.aj@gmail.com)

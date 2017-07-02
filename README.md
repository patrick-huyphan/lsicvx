# lsiCVX, in development processing...
Using spark to process:
- clustering data with SCC
- find projection matrix with ADMM

# SW requirement:
- JDK 1.8
- spark 2.1
- maven

# TODO:

Paper: Full flow, normal processing with SCC and ADMM

Parallel processing:
- Data structure: 
    - sparse matrix with row and column
        - key-value: <(rowID-colID):value>
    - spark matrix struct? matrix, rowmatrix, sparse matrix...  
    - broadcast data: ???    

https://spark.apache.org/docs/2.0.2/api/java/org/apache/spark/mllib/linalg/SparseMatrix.html


- Read matrix data: 
    + where is data stored? 
    + What data will be mapped and reduced?

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

Need more package for full app processing 
- Data preparation: parsing text data to matrix, remove stop work, build VSM
- transform matrix to new space and query


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

# Main content prj
- Paper: full flow in single processing
- LSI_CVX: full flow in parallel processing


# Un-use project
- Scc
- ADMM
- Parse data
- Query
# Author:
Patrick HuyPhan(huyphan.aj@gmail.com)
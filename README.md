- # General
    - #### Team#: hullo
    
    - #### Names: Alex Huang and Farhaan Rasool
    
    - #### Project 5 Video Demo Link: https://youtu.be/RXO6CHYdSaM

    - #### Instruction of deployment: clone, use mvn clean package, move war to tomcat

    - #### Collaborations and Work Distribution: Farhaan worked on Part 1, Alex worked on part 2 and part 3 and 4 was done together.


- # Connection Pooling
    - #### Include the filename/path of all code/configuration files in GitHub of using JDBC Connection Pooling.
2023-fall-cs122b-hullo/WebContent/META-INF/context.xml
2023-fall-cs122b-hullo/src
    
    - #### Explain how Connection Pooling is utilized in the Fabflix code.
	In Fabflix, connection pooling is typically implemented not by individual servlets managing database connections directly, but through a connection pool managed by the application server or a dedicated library. Servlets access this pool to obtain a connection for database operations and return the connection to the pool after use. This approach allows for efficient reuse of connections, reducing the overhead associated with establishing new connections for each request. The connection pool handles the creation, maintenance, and closing of connections, ensuring optimal resource management and scalability. This method is preferable to having servlets individually create and manage connections, as it avoids issues like resource leaks and concurrency problems.
    
    - #### Explain how Connection Pooling works with two backend SQL.
    	In a setup with two backend SQL databases, connection pooling functions by maintaining separate pools for each database. Each pool manages a set of connections specific to one of the databases. When an application component requires access to a database, it retrieves a connection from the corresponding pool. This ensures efficient management of resources for both databases, as connections are reused instead of being frequently opened and closed. The pools independently handle the lifecycle of connections, including their creation, validation, and closure, thereby optimizing performance and resource utilization for each backend SQL database. This dual-pool system allows for parallel and efficient operations across both databases, maintaining robustness and scalability in the application's architecture.

- # Master/Slave
    - #### Include the filename/path of all code/configuration files in GitHub of routing queries to Master/Slave SQL.
master: 54.161.25.219

slave: 54.211.52.253

load-balancer: 3.84.100.162

cs122b-project1-api-example/src/MovieSearchServlet.java

cs122b-project1-api-example/src/PlaceOrderServlet.java

    - #### How read/write requests were routed to Master/Slave SQL?
    	SQL read and write requests are managed through two resources specified in the `context.xml` file, both accessing a MySQL database on port 3306. Read requests are directed to the "moviedbexample" resource, which connects to a local MySQL database co-located with the user's Tomcat server within an AWS instance, typical of slave databases. Write requests, however, utilize the "moviedbexample-master" resource, which is configured with a direct, private IP address to connect exclusively to the master MySQL database in the master AWS instance. This setup ensures efficient read operations through local databases and centralized, consistent write operations to the master database.

- # JMeter TS/TJ Time Logs
    - #### Instructions of how to use the `log_processing.*` script to process the JMeter logs.

Use this command and change the string in file to update files you want to process. 
“java /path/to/mycs122b-projects/2023-fall-cs122b-hullo/logs/LogWriter.java”

- # JMeter TS/TJ Time Measurement Report
https://docs.google.com/document/d/1uGcAhmHfGOpIcvvHH0LB4UxkV3EfjwKiaB0VPVyaU7Q/view
| **Single-instance Version Test Plan**          | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread | logs/graphs/single-1-thread.png  |  62            | 13                               | 7                  |       The one threaded test had the fastest average query time |

| Case 2: HTTP/10 threads                        | logs/graphs/single-10-thread.png    |132                        |  34                                 | 14         | The 10 thread test was significantly slower than the 1 threaded test
| Case 3: HTTPS/10 threads                       | logs/graphs/single-10-thread-https.png
   | 133                       | 36                               | 12                      | There is not much difference between the https and http thread times         |
| Case 4: HTTP/10 threads/No connection pooling  |  logs/graphs/single-1-thread-no-pooling.png  |      136             |31                      | 11          |There is no notable difference in times between all three 10 threads |  

| **Scaled Version Test Plan**                   | **Graph Results Screenshot** | **Average Query Time(ms)** | **Average Search Servlet Time(ms)** | **Average JDBC Time(ms)** | **Analysis** |
|------------------------------------------------|------------------------------|----------------------------|-------------------------------------|---------------------------|--------------|
| Case 1: HTTP/1 thread                          | logs/graphs/scaled-1-thread.png                        | 64                                 | 27                        | 8           | This has a slower query time than the single instance and a faster search time
| Case 2: HTTP/10 threads                        | logs/graphs/scaled-10-thread.png                         | 67                                  | 23                        | 5           | This just has a slower response time on all metrics
| Case 3: HTTP/10 threads/No connection pooling  | logs/graphs/scaled-no-pooling.png  | 65                         | 30                                  | 7           | No connection pooling has no notable difference

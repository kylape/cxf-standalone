#Standalone CXF in JBoss
###Based off of pooled WS client example

Invoke test using the url `http://localhost:8080/hello/test?count=10&thread=5`.  `count`
can be any arbitrary number, and it will be used to determine the number of
test iterations per thread.  `threads` determines the number of threads to use.

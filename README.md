### How to run the application using Docker
1. Download the code
2. unzip it
3. go to `File-based-database-master` folder
4. open cmd prompt
5. run: `docker-compose build` -> this will build the docker image
6. run: `docker-compose up` to run the project


### Test End points (port using : 8080)
1. `http://localhost:8080/execute` <br>
   body: `CREATE   TABLE     myTabl32   (  col1   integer  ,  col2   STRING, col3 STRING)` <br>
   or <br>
   body: `INSERT into myTabl32 (col1, col2) VALUES (10, "Hi world!")` <br>

2. `http://localhost:8080/redis/SUCCESS`
3. `http://localhost:8080/redis/FAILURE`

### Note: postman collection is provided with code, you can import and test

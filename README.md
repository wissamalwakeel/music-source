# music-source
To start the service localy you will need to run a dockerized version of Mongo if you do not have one running already with default settings. 
- Exampe for running mongoDB using docker-compose and docker file: 

docker-compose.yaml example conetn
```
version: "3.8"
services:
 mongodb:
  image : 'mongo'
  container_name: 'mongodb'
  environment:
   - MONGO_INITDB_DATABASE=<some-name>
#   - MONGO_INITDB_ROOT_USERNAME=<username>
#   - MONGO_INITDB_ROOT_PASSWORD=<password>
  volumes:
   - /Users/<user_name>/mongodb/database:/data/db
  ports:
   - 27017:27017
```
  

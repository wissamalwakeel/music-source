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
to run the container you need to be in the directory where you have the dockerfile and execute the command 
```
docker-compose up 
```
or 
```
docker-compose up  -d
```
to run as a demon

to run the application you need to clone the repository onto your local computer and build using the following command
```
mvn clean package
```

and then start the application with the follwing command
```
java -cp music-source-<version>.jar -Dloader.main=com.musicsource.app.MusicSourceApplication org.springframework.boot.loader.PropertiesLauncher
```

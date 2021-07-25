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

to Start the service application you need to clone the repository onto your local computer and build using the following command
```
mvn clean package
```

and then start the application with the follwing command
```
cd target
java -cp music-source-<version>.jar -Dloader.main=com.musicsource.app.MusicSourceApplication org.springframework.boot.loader.PropertiesLauncher
```
you need the latest JVM installation on your device, this app is build using Java 11


example urls for testing from postman or any other rest client

Nirvana 
```
http://localhost:8080/api/v1/musicsource/5b11f4ce-a62d-471e-81fc-a69a8278c7da
```

Metalica
```
http://localhost:8080/api/v1/musicsource/65f4f0c5-ef9e-490c-aee3-909e7ae6b2ab
```

Luke Bryan
```
http://localhost:8080/api/v1/musicsource/aab35942-f176-4f77-bbf9-1d6aa98ccf3f
```

Example response: 
```
{
    "mbid": "aab35942-f176-4f77-bbf9-1d6aa98ccf3f",
    "description": "<p class=\"mw-empty-elt\">\n</p>\n<p><b>Thomas Luther \"Luke\" Bryan</b> (born July 17, 1976) is an American country music singer and songwriter. He began his music career writing songs for Travis Tritt and Billy Currington before signing with Capitol Nashville in 2007.\n</p><p>Bryan's first ten albums – <i>I'll Stay Me</i> (2007), <i>Doin' My Thing</i> (2009), <i>Tailgates &amp; Tanlines</i> (2011), <i>Crash My Party</i> (2013), <i>Spring Break...Here to Party</i> (2013), <i>Spring Break...Checkin' Out</i> (2015), <i>Kill the Lights</i> (2015), <i>Farm Tour... Here's to the Farmer</i> (2016), <i>What Makes You Country</i> (2017), and <i>Born Here Live Here Die Here</i> (2020) – have included 23 number-one hits. Bryan often co-writes with Jeff Stevens. Since 2018, Bryan has been a judge on American Idol.\n</p><p>In 2013, Bryan was named \"Entertainer of the Year\" by both the Academy of Country Music Awards and the Country Music Association. In 2019, Bryan's 2013 album <i>Crash My Party</i> received the first Album of the Decade award from the Academy of Country Music. He is one of the world's best-selling music artists, with over 75 million records sold.</p>\n\n\n",
    "albums": [
        {
            "id": "37a5b751-1d1c-459b-bb68-0d493f746517",
            "title": "I Don't Want This Night to End",
            "image": "Image Not Found"
        },
        {
            "id": "b51d8683-2427-4805-8eca-cec74cff9690",
            "title": "Run Run Rudolph",
            "image": "Image Not Found"
        },
        .....
        {
            "id": "cc8ec26e-0f98-421d-8c1b-e849a017c5fc",
            "title": "Doin’ My Thing",
            "image": "http://coverartarchive.org/release/1c5a9818-9644-4053-9473-4ee49f4d747d/27554527925.jpg"
        }
    ]
}
```

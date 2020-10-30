# Project Marley

Marley is a person recognition service that runs locally. Users walk into the office and are greeted by Marley.

## Table of Contents
- [Use Cases](#use-cases)
- [Structure](#structure)
- [Getting Started](#getting-started)
- [Client - React App](#client---react-app)
- [Person Recognition - Java Rest API](#person-recognition---java-rest-api)
- [Face Recognition - Python Rest API](#face-recognition---python-rest-api)

## Use Cases
PREDICTION <br>
The client application takes images and tries to check if a face can be found in each image, and if so predict which person is in the image. If Marley recognizes a person, it will greet them by name. If not, that person will still be greeted without a name and asked to register.

REGISTRATION <br>
If a person chooses to register, face encodings are saved along with their name which Marley can then use to recognize them. If no face is found, then no greeting is displayed.

DELETION <br>
A person can choose to no longer be recognized by Marley, in which case their personal data and face encodings will be removed.

MOTION DETECTION<br>
The client application will switch to motion detection mode to save CPU usage if no faces are detected by the backend for a specific period of time, then switch back to sending requests to identify faces if motion is detected.

## Structure
<pre>
Client application (React, typescript)	
&#129047;			 🠕
Person Recognition (Java rest API, Spring Boot)		⮌	PostgreSql - person info
	🠗	🠕
Face Recognition (Python Flask rest API)	        ⮌	MongoDB - face encodings
</pre>

Both databases use faceId generated by MongoDb as a common id so the person recognition service can consolidate the information. Person recognition validates data and makes sure the client gets the correct information. Face recognition handles face encoding comparisons and is called upon when needed by person recognition.  <br>

MongoDb is run in the cloud while the rest is run locally. Docker compose handles the backend and the client application is started manually. <br>
DOCKER uses 4 containers that are run via docker-compose.yml: <br>
<pre>
marley-postgres		(PostgreSql) 
pgadmin			(PgAdmin 4) 
person-recognition	(Java rest API)
face-recognition	(Python rest API)
</pre>

## Getting Started
<ol>
	<li>Make sure Docker is installed. If on windows or mac, Docker Desktop is recommended. If you don't have Docker Desktop then Docker Compose will need to be installed separetely</li>
	<li>Clone the github repository</li>
	<li>In the top folder of the project a file with the exact name “.env” must be created with the variables listed in example.env, also found in the top folder. These variables are used in docker-compose.yml to run the backend docker containers. FACE_REC_URL should reference face-recognition (the docker container), NOT localhost and can be copied from example.env.</li>
	<li>A mongoDB database must be created with a collection called faces, which then face-recognition (the python rest API) can connect to.</li>
	<li>In the terminal, navigate to the root folder and run “docker-compose up” to launch the backend, which will be built if necessary. “docker-compose config” can be used to verify environment variables and setup before running "docker-compose up".</li>
	<li>(optional) The postgresql database can be viewed at http://localhost:5555 when the docker container network is running. Use credentials for pgadmin entered into the .env file to login. Use the host name "marley-postgres" and credentials you saved in the .env file to establish a connection.</li>
	<li>(optional) Navigate to the Client folder and create a file “.env” there as well if changing the variables for the client is desired. The file example.env in the Client folder shows the variables that can be altered.</li>
	<li>Yarn must be installed. Run “yarn” to install dependencies, then “yarn start” can run the client application.</li>
	<li>Now the client application should start automatically in the default browser at http://localhost:3000</li>
	<li>(optional) Use Docker Desktop to view the running backend containers and their respective logs</li>
</ol>

## Client - React App
The client application is built with React and typescript. <br><br>

PREDICTION<br>
Images are taken and a request is sent to the backend for each image. For more information see the prediction use case as described above. The requests are made as fast as possible unless they are below the minimum delay time as defined in the variable REACT_APP_MINIMUM_PREDICT_INTERVAL in the .env file.
<br><br>
Request:
<pre>
{
	“Image”: String (single image in base64 String format)
}
</pre>
REGISTRATION<br>
If a face is found but the person is unknown to Marley, the user can register with their name as prompted by the application. Upon registering the user will is asked to enter their name and then stand still for a few seconds as the application records a 2 second video, splits that into 60 images and sends a registration request to the backend with the following format:
<pre>
{
	“name”: String,
	“Images”: String[] (array with images in base64 String format)
}
</pre>
This data is handled by the backend and saved so that the person may be recognized by Marley during prediction requests. The application will wait for the registration request to be completed before sending more prediction requests.

DELETION<br>
If a user chooses to be removed from the system a request is sent to the backend with the id supplied in a path variable. 

MOTION DETECTION<br>
To save CPU usage, the app will switch to motion detection when not detecting faces. A timer is running when doing face recognition and each time a face is detected the timer is refreshed. If the timer expires then face recognition requests are no longer sent and motion detection is turned on. If motion is detected then the app switches back to face recognition.<br><br>
## Person Recognition - Java Rest API
[REST API DOCUMENTATION](http://localhost:8080) (The docker network must be running for the link to work!) <br>
Java doc comments are in place so further documentation can easily be generated.
<br><br>
The person recognition backend is a java rest API using Spring Boot and functions as the main communication hub between the client application and the python face recognition application. All requests are validated before they are executed. Every request to face recognition involves an image String in base64 format. Label put requests also include a faceId.
<br><br>
/predict (post)<br>
The request base64 image is sent to face recognition. If a known face is found, that faceId is used to look up the corresponding person. The following response is delivered to the client:
<pre>
{
	“id”: String,
	”name”: String,
	“isFace”: boolean,
	“isKnownFace”: boolean
}
</pre>

/register (post)<br>
First a prediction request is sent to face recognition to verify that the person isn’t already registered. Then a label post request is sent to save the first image to the face encoding database. After that, the faceId that was generated is used to send label put requests and add more encodings to the same faceId. If all images were successful, then 60 images will be saved as face encodings in the face recognition database. Any failed images will be ignored and as long as not all 60 images fail, the person will be registered.
<br><br>
/delete/{id} (delete)<br>
The supplied id is used to remove the person from postgreSql as well as the face encodings from the face recognition database.
<br>
## Face Recognition - Python Rest API
SWAGGER DOCUMENTATION URL: {insert URL here}<br>
All endpoints take a face encoding from the supplied image. If no face is found an exception is thrown. 
<br><br>
face-recognition/predict (post)<br>
The face encoding is compared to all the encodings in the database to find the euclidean distance and the minimum value (closest match encoding) for each face is compared to see which face is the closest match to the input encoding. If no match is lower than a certain threshold value then the faceId is None, otherwise the faceId for the closest match is the response.
<br><br>
face-recognition/label (post)<br>
The face encoding is saved to the database and a faceId is generated by mongoDB, which is returned as the response.
<br><br>
face-recognition/label (put)<br>
Saves the face encoding to the faceId supplied in the request.
<br><br>
face-recognition/delete/{id} (delete)<br>
Deletes all the related encodings of the face that matches the faceId.<br>
<br>
[Back To Top](#project-marley)

# Project Marley

## Introduction

Marley is a person recognition service that runs locally. Users walk into the office and are greeted by Marley by name if recognized and with calendar notifications if they have allowed it. If not recognized the user will be asked to register. If they want to see calendar notifications, they can either allow it when register or on the home page when being recognized. If the user no longer wish to be registered or if they don’t want to see their calendar notifications, they can either choose to remove themself as users or disconnect the calendar. To save CPU, motion detection is used to determine when to start sending requests to the backend.<br><br>
The application has a graphical interface which runs in the browser using localhost. To use the application a webcam is needed and permission to use the webcam must be granted. The client application sends requests to a person recognition spring boot rest api, which consolidates and manages information. The api in turn sends requests to a face recognition service to verify if the face is known or not, then looks up the personal information for the client application to greet the user.<br><br>
The face recognition service is a python rest api that uses the library [face_recognition](https://github.com/ageitgey/face_recognition), which in turn is built using [dlib](http://dlib.net/)'s state-of-the-art face recognition built with deep learning. The model has an accuracy of 99.38% on the [Labeled Faces in the Wild](http://vis-www.cs.umass.edu/lfw/) benchmark. Only 1 person at a time is greeted. If multiple people faces are found, the face recognition api will only choose the face with the highest face detection rate.

## Table of Contents

- [Use Cases](#use-cases)
- [Backend Information](#backend-information)
- [Google Calendar Integration](#google-calendar-integration)
- [Getting Started](#getting-started)
- [Client - React App](#client---react-app)
- [Person Recognition - Java Rest API](#person-recognition---java-rest-api)
- [Face Recognition - Python Rest API](#face-recognition---python-rest-api)

## Use Cases

### PREDICTION

The client application takes images and sends requests to the backend to check if a face can be found in the image, and if so predict which person it is. If Marley recognizes a person, it will greet them by name. If not, that person will still be greeted without a name and asked to register.
![Prediction Use Case](https://github.com/Dynabyte/marley/blob/production/Diagrams/Prediction%20Request.png)

### REGISTRATION

If a person chooses to register, up to 60 face encodings are saved along with their name which Marley can then use to recognize them.
![Registration Use Case](https://github.com/Dynabyte/marley/blob/production/Diagrams/Registration%20Request.png)

### DELETION

A person can choose to no longer be recognized by Marley, in which case their personal data and face encodings will be removed.
![Deletion Use Case](https://github.com/Dynabyte/marley/blob/production/Diagrams/Deletion%20Request.png)

### REMOVE GOOGLE CALENDAR ACCESS

If a person no longer wants to see there calendar notifications they can choose to remove the Google Calendar access.

### MOTION DETECTION

The client application will switch to motion detection mode to save CPU usage if no faces are detected by the backend for a specific period of time, then switch back to sending prediction requests to identify faces if motion is detected.
![Motion Detection Use Case](https://github.com/Dynabyte/marley/blob/production/Diagrams/Motion%20Detection.png)

## Backend Information

Both databases use faceId generated by MongoDb as a common id so the person recognition service can consolidate the information. Person recognition validates data before every request and makes sure the client gets the correct information. Face recognition handles face encoding comparisons and is called upon when needed by person recognition. <br>

MongoDb is run in the cloud while the rest is run locally. Docker compose handles the backend and the client application is started manually. <br>
DOCKER uses 4 containers that are run via docker-compose.yml: <br>

<pre>
marley-postgres		(PostgreSql) 
pgadmin			(PgAdmin 4) 
person-recognition	(Java rest API)
face-recognition	(Python rest API)
</pre>

## Google Calendar Integration

<ol>
	<li>Go to http://console.cloud.google.com</li>
	<li>Select Marley project. If you don't have access, ask Alexandra Onegård, Niklas Furberg or Tao Wan to be granted access.</li>
	<li>Select "APIS & Services" in the menu.</li>
	<li>Go to "Credentials". Press the download button to the right under "OAuth 2.0 Client IDs". It generates a file that you will add in the project to get access to the Google Calendar API.</li>
	<li>Copy the API key, it will be added to the .env file in the backend.</li>
</ol>

## Getting Started

<ol>
	<li>Make sure Docker is installed. If on windows or mac, Docker Desktop is recommended. If you don't have Docker Desktop then Docker Compose will need to be installed separetely</li>
	<li>Clone the github repository</li>
	<li>Navigate to the resourses folder inside server. Add the file created following the step under section "Google Calendar Integration"</li>
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

**PREDICTION**<br>
Images are taken and a request is sent to the backend for each image. For more information see the prediction use case as described above. The requests are made as fast as possible unless they are below the minimum delay time as defined in the variable REACT_APP_MINIMUM_PREDICT_INTERVAL in the .env file.
<br><br>
Request:

<pre>
{
	“Image”: String (single image in base64 String format)
}
</pre>

**REGISTRATION**<br>
If a face is found but the person is unknown to Marley, the user can register with their name as prompted by the application. Upon registering the user will is asked to enter their name and then stand still for a few seconds as the application records a 2 second video, splits that into 60 images and sends a registration request to the backend with the following format:

<pre>
{
	“name”: String,
	“Images”: String[] (array with images in base64 String format)
}
</pre>

This data is handled by the backend and saved so that the person may be recognized by Marley during prediction requests. The application will wait for the registration request to be completed before sending more prediction requests.

**DELETION**<br>
If a user chooses to be removed from the system a request is sent to the backend with the id supplied in a path variable.

**REMOVE GOOGLE CALENDAR ACCESS**<br>
If a use choose to remove their calendar notifications, a request is sent to the backend with supplied in a path variable.

**MOTION DETECTION**<br>
To save CPU usage, the app will switch to motion detection when not detecting faces. A timer is running when doing face recognition and each time a face is detected the timer is refreshed. If the timer expires then face recognition requests are no longer sent and motion detection is turned on. If motion is detected then the app switches back to face recognition.<br><br>

## Person Recognition - Java Rest API

[REST API DOCUMENTATION](http://localhost:8080) (The docker network must be running for the link to work!) <br>
Java doc comments are in place so further documentation can easily be generated.

### Description

The person recognition backend is a java rest API using Spring Boot and functions as the main communication hub between the client application and the python face recognition application. All requests are validated before they are executed. Every request to face recognition involves an image String in base64 format. Label put requests also include a faceId.

### Database

The database is a simple postgresql database with a single table representing a person using two columns:<br>

<pre>
faceId: String
name: String
</pre>

The faceId is received from the face recognition service at the time of registration. This way the api can aggregate information from the two databases.

### Endpoints

**/predict (post)**<br>
The request base64 image is sent to face recognition. If a known face is found, that faceId is used to look up the corresponding person. The following response is delivered to the client:

<pre>
{
	“id”: String,
	”name”: String,
	“isFace”: boolean,
	“isKnownFace”: boolean
}
</pre>

**/register (post)**<br>
First a prediction request is sent to face recognition to verify that the person isn’t already registered. Then a label post request is sent to save the first image to the face encoding database. After that, the faceId that was generated is used to send label put requests and add more encodings to the same faceId. If all images were successful, then 60 images will be saved as face encodings in the face recognition database. Any failed images will be ignored and as long as not all 60 images fail, the person will be registered.
<br><br>

**/delete/{id} (delete)**<br>
The supplied id is used to remove the person from postgreSql as well as the face encodings from the face recognition database.
<br>

**/calendar/credentials (GET)**<br>
Recieve Google API key and Client ID for Google Calendar API. Uses the information to access Google Auth2 which gives us an authorization code for the user.

**/calendar/tokens (POST)**<br>
Sends the Authorization code (from the endpoint above) and faceId to backend. Backend exchanges the authorization code to get access token and refresh token. These are used to get information from the Google Calendar API.

**/calendar/{id} (GET)**<br>
Get calendar events if the user has allowed it. The backend fetch data from the Google Calendar API with the generated access token.

**/calendar/tokens/{id} (DELETE)** <br>

Remove access token and refresh token from the user so we no longer have access to the calendar data.

## Face Recognition - Python Rest API

[REST API DOCUMENTATION](http://localhost:5000) (The docker network must be running for the link to work!)<br>

### Description

The face recognition service is a flask rest api that uses the library [face_recognition](https://github.com/ageitgey/face_recognition), which in turn is built using [dlib](http://dlib.net/)'s state-of-the-art face recognition built with deep learning. The model has an accuracy of 99.38% on the [Labeled Faces in the Wild](http://vis-www.cs.umass.edu/lfw/) benchmark.<br>
All endpoints take a face encoding from the supplied base64 image string. If no face is found an exception is thrown. The api saves an in-memory copy of the face encodings from the database in a variable which is reset to an empty list when the database is changed. If the variable is empty when a prediction request is received, then the database will be downloaded to that variable.

### Database

Each face in the database has a faceId, generated by MongoDb. Each face also gets a list of face encodings. Each encodings is a numpy array with 128 numeric values which is used to measure the euclidean distance when comparing encodings. That is in turn used to predict if the face encoding from the input image matches any face in the database.<br>
A face in the MongoDb database looks like this:<br>

<pre>
{
	_Id:ObjectId("IdString")
	encodings:[array with 60 face encodings, each being a numpy array with 128 numeric values] 
}
</pre>

### Endpoints

**face-recognition/predict (post)**<br>
The face encoding is compared to all the encodings in the database to find the euclidean distance and the minimum value (closest match encoding) for each face is compared to see which face is the closest match to the input encoding. If no match is lower than a certain threshold value then the faceId is None, otherwise the faceId for the closest match is the response.
<br><br>
**face-recognition/label (post)**<br>
The face encoding is saved to the database and a faceId is generated by mongoDB, which is returned as the response.
<br><br>
**face-recognition/label (put)**<br>
Saves the face encoding to the faceId supplied in the request.
<br><br>
**face-recognition/delete/{id} (delete)**<br>
Deletes all the related encodings of the face that matches the faceId.<br>
<br>
[Back To Top](#project-marley)

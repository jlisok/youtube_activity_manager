# Project description
YouTube Activity Manager is an application that I have whipped up as a showcase of my programming skills and build my portfolio as an inspiring Junior Java Developer.

YouTube Activity Manager is a web application that integrates with Google, YouTube and AWS S3 APIs to present you with your Youtube activity. You can use this app to view your favourite YouTube videos, channels that you are subscribed to and some associated stats. The application uploads anonymized portions of acquired data to the cloud, to simulate data gathering for BigData processing. Just to let you know, they are not processed any further.

# Technical stuff
YouTube Activity Manager uses a handful of open source libraries / frameworks to work seamlessly:

##### Backend:
* [Java14] - core functionalities
* [Spring Boot] - building dependency tree and much more
* [Hibernate] - management of the persistence model
* [PostgreSQL] - database management system
* [Liquibase] - database version control
* [Docker] - running the application on the production environment, building application images

##### Frontend:
* [React.js] - help in creating interactive web UIs
* [Axios] - promise-based HTTP client
* [Bootstrap4] - designing and styling components

##### Cloud:
*  [AWS EC2] - docker containers with application components
* [AWS ECR] - docker images of application components
* [AWS S3] - data storage
* [AWS ELB] - passes requests to target groups and terminates HTTPS
* [AWS RDS] - PostgreSQL instance
* [AWS Route53] - receives requests

# External APIs
The application integrates with the following APIs:
* Google OAuth2 - where users are being authorized and where the application gets access token to YouTube Api
* YouTube Data Api - where the application receives resources associated with users YouTube activity
* AWS S3 - where the appliaction stores select data to simulate their gathering for BigData processing

# Instructions
To run YouTube Activity Manager on your local machine, you should install JDK14, IDE of your choice and PostgreSQL prior to application import. Next, build the application dependencies.
```sh
$ cd [MAIN_FOLDER_OF_THE_PROJECT]
$ ./gradlew build
```
Before starting the application locally, you need to add .environemnt.local file to the main folder of the project, where you supply the application with such environment properties, as:
- DB_USERNAME - postgreSQL login
- DB_PASSWORD - postgreSQL password
- FRONTEND_URL - adress to localhost
- AWS_S3_ACCESS_KEY_ID - access key for AWS S3
- AWS_S3_SECRET_ACCESS_KEY_ID - secret access key for AWS S3
- URL_TO_GOOGLE_CREDENTIALS - path to google client secrets

# Docs
## How does the application run on production?
The application code lives in three [Backend], [Frontend], [Deployment] projects. [Backend] and frontend contain scripts that automatically via GitHub CI jobs, build Docker images from the master branch, and upload them to the ECR. [Backend] container image is built based on production-optimised Spring Boot Paketo Buildpack. The [Frontend] container contains a Nginx deployment serving static, production-optimized files built by NPM.

![AWS](https://d2xvpza2vzjrcj.cloudfront.net/aws.jpg)

The deployment project contains GitHub CI jobs that start the above images on the target EC2 machine, using docker-compose. We have to host both services on a single machine in order not to exceed the AWS Free Tier resource quotas. The [Backend] service uses S3 and RDS (PostgreSQL) to store application data. The instance is exposed via two Target Groups, one for API requests, the other for loading static frontend assets.

The ELB Application Load Balancer routes incoming requests to one of the above Target Groups based on their paths. The ELB Application Load Balancer also handles HTTPS termination, by means of a SSL certificate managed by AWS Certificate Manager. All network traffic inside the secure AWS network is unencrypted. ELB enforces HTTPS by redirecting all incoming HTTP traffic to HTTPS on port 443.

DNS mapping of the jlisok.pl URL to the ELB instance is handled by Route53. React.js "almost single-page" web application is served under the jlisok.pl URL by the youtube_activity_manager_frontend container running in the EC2 instance. All REST API requests are handled by the youtube_activity_manager container running in the EC2 instance.

## How does the Database look like?
As previously mentioned, data gathered by YouTube Activity Manager are stored in PostgreSQL DBMS. Database relations are presented below. Login and registration data are stored within two entities: users and user_personal_data related to each other with one-to-one relationship via id primary keys. Users table holds information necessary for authentication and authorization purposes within the application itself and YouTube client API. On the other hand, user_personal_data stores data acquired from the user's registration form.

Synchronization statuses is a utility table that holds information on all past synchronizations with YouTube API. User_id attribute is a foreign key that holds a many-to-one relationship with the user's entity.

![AWS](https://d2xvpza2vzjrcj.cloudfront.net/postgres_diagram.png)

Channels and videos are representations of data fetched from YouTube API. Both entities store basic information on videos, that users rated and channels that users are subscribed to. They are related to users accounts with many-to-many relationships, which are handled by users_channels and users_videos tables respectively.

Further to this, the videos table is additionally connected to channels and video_categories entities via channel_id and video_category_id foreign keys (many-to-one relationship).

All of the above relationships are vital for the successful run of YouTube Activity Manager, i.e. to display authorized users their YouTube activity and associated stats.

## How does the user's login with Google look like and how does it integrate with YouTube and AWS S3 APIs?
The schema below showcases the process flow behind authorizing the user in YouTube Activity Manager. User authorization starts with submitting credentials by the user in a browser and, based out of that, establishing the connection with Google API. The API validates users credentials and generates tokens necessary later on for a connection with YouTube API.

Tokens received from Google API are then sent by a browser to a backend endpoint. At the backend side, the user's credentials are processed. Upon a successful finish of credentials processing, the user's account is updated / created.

![AWS](https://d2xvpza2vzjrcj.cloudfront.net/flow_charts-user_google_login.jpg)

Next, an asynchronous process is being triggered simultaneously with granting the user access to YouTube Activity Manager and sending JWT to the web client. An async process consists of two separate workflows. The first one establishes a connection with YouTube API via previously received google access token and downloads user's activity on the YouTube platform (lists of rated videos and subscribed channels). Next, data are processed and inserted into the database. Second async job processes select data and sends them to AWS S3 to simulate data gathering for BigData processing.

The described workflow is being triggered each time user logs in to YouTube Activity Manager with google account.

## How can you login / register to YouTube Activity Manager?
Each user has 4 different options to acquire access to YouTube Activity manager:
- by filling up the registration form available at https://jlisok.pl/signup. Note that the application will run with limited functionality until a google account is eventually connected.
- via traditional login, where a user needs to submit login and password at https://jlisok.pl/. Note that the application will run with limited functionality until a google account is eventually connected.
- by connecting YouTube Activity Manager with a google account via "sign in with google" button at https://jlisok.pl/
- with the use of Demo User account available via "demo user" button at https://jlisok.pl/. Demo user has already been authorized with Google API, therefore, the user can enjoy the application to its fullest potential.

![AWS](https://d2xvpza2vzjrcj.cloudfront.net/flow_charts-user_login.jpg)

## YouTube Activity Manager Endpoints
Below, we present basic docs of all endpoint functionalities available in YouTube Activity Manager, so you can get a gist of what the application can do and designed for.

#### /api/v1/aboutus
The endpoint accepts basic GET method and serves the information on the current version of the application as well as the contact email. This endpoint does not require authentication credentials.

An example request is presented below:
````
GET /api/v1/aboutus HTTP/1.1
Host: jlisok.pl
````

The successful application's response body should be similar to:
```json
{
    "version": "0.0.1-SNAPSHOT",
    "email": "justyna.lisok@gmail.com"
}
```

#### /health
By the means of this endpoint, you can perform the application's health check. To do so, send the following HTTP request:
````
GET /health HTTP/1.1
Host: jlisok.pl
````

This endpoint does not require authentication credentials.

A healthy application instance responds with (body):
```json
OK
```

#### /api/v1/login
This endpoint  (all users permitted) handles traditional login workflow by accepting POST request with the following body content:

````
POST /api/v1/login HTTP/1.1
Host: jlisok.pl
Content-Type: application/json
````
```json
{
    "email" : "example@ex.com",
    "password" : "1111"
}
```

Successful login process sends JWT in the response body, similar to the following:
```json
{
eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmMDAwMDZhOS04ZWU0LTQ5NzgtYjI0NS0wZWI3 NDg0MWM4MGQiLCJpc3MiOiJcImNvbS5qbGlzb2sueW91dHViZV9hY3Rpdml0eV9tYW5hZ2VyXCIiLCJleHAiOjE2MDQ0MDc2NzYsImlhdCI6MTYwNDQwMjI3Nn0.v6J0CeHMdGb-2Oe_xYtWo9dEozwd-7_xlNgvZ2KS_MhpsFL-F443IP1YD2QXN5cMW0F9szAkLabySoS9bTx3mA
}
```

#### /api/v1/login/viaGoogle
This endpoint (permitted to all users) handles login with Google workflow by accepting POST request from with the following body content:

````
POST /api/v1/login/viaGoogle HTTP/1.1
Host: jlisok.pl
Content-Type: application/json
````
```json
{
    "googleIdToken":"exampleDummyGoogleIdToken",
    "accessToken":"exampleDummyGoogleAccessToken"
}
```

The successful response body contains valid JWT (as presented in /login).

#### /api/v1/login/authorize
This endpoint is available for authenticated users only. It links google credentials with the user's account. The endpoint accepts POST method with the following structure:

````
POST /api/v1/login/authorize HTTP/1.1
Host: jlisok.pl
Authorization:
        Bearer: exampleDummyJWToken
Content-Type: application/json
````
```json
{
    "googleIdToken":"exampleDummyGoogleIdToken",
    "accessToken":"exampleDummyGoogleAccessToken"
}
```

The successful response body contains valid JWT (as presented in /login).

#### /api/v1/registration
This endpoint  (all users permitted) handles user registration workflow by accepting POST request with the following body content:
````
POST /api/v1/registration HTTP/1.1
Host: jlisok.pl
Content-Type: application/json
````
```json
{
    "gender" : "MALE",
     "email" : "example@mail.com",
     "password" : "1111",
     "password2" : "1111",
     "birthYear" : "2000",
     "country" : "Slovenia",
     "phonePrefix" : "0030",
     "phoneNumber" : "500500500",
     "firstName" : "John"
}
```
The successful response body contains valid JWT (as presented in /login).

#### /api/v1/statistics/category
This endpoint handles calculating rated video statistics grouped by video categories (authorized users only). It accepts GET method, similar to the following:
````
GET /api/v1/statistics/category HTTP/1.1
Host: jlisok.pl
Authorization:
        Bearer: exampleDummyJWToken
````

A successful response contains the body with statistics and timestamp as well as the state of the last YouTube API synchronization.

```json
{
"statistics":
    [{
        "averageTime":"00:41:30",
        "totalTime":"00:13:50:19",
        "numberVideos":20,
        "categoryName":"Education"
    },
    {
        "averageTime": "00:22:58",
        "totalTime": "00:16:50:51",
        "numberVideos": 44,
        "categoryName": "Entertainment"
    }],
"state": "SUCCEEDED",
"stateCreatedAt": "2020-11-03T12:34:42.836938Z"
}
```

#### /api/v1/statistics/creator
The endpoint is the same as the previous one, except that statistics are grouped by creators (authorized users only). Here is the example HTTP request with GET method:
````
GET /api/v1/statistics/creator HTTP/1.1
Host: jlisok.pl
Authorization:
        Bearer: exampleDummyJWToken
````

A successful response contains the body with statistics and timestamp as well as the state of the last YouTube API synchronization.

```json
{
"statistics":
    [{
        "averageTime":"00:41:30",
        "totalTime":"00:13:50:19",
        "numberVideos":20,
        "creatorName":"Tito The Raccoon"
    },
    {
        "averageTime": "00:22:58",
        "totalTime": "00:16:50:51",
        "numberVideos": 44,
        "creatorName": "Happy Tails"
    }],
"state": "SUCCEEDED",
"stateCreatedAt": "2020-11-03T12:34:42.836938Z"
}
```
#### /api/v1/synchronization
Returns the timestamp of the last user's successful synchronization with YouTube API. It accepts GET method from authorized users only, yielding:
````
GET /api/v1/synchronization HTTP/1.1
Host: jlisok.pl
Authorization:
        Bearer: exampleDummyJWToken
````

A successful response body should be similar to:
```json
"2020-11-03T11:23:00Z"
```

#### /api/v1/youtube/videos
The endpoint returns the list of rated (liked / disliked) YouTube videos by the user. It accepts GET method (authorized users only). Below, you can find an example request:
````
GET /api/v1/youtube/videos?rating=LIKE HTTP/1.1
Host: jlisok.pl
Authorization:
        Bearer: exampleDummyJWToken
````

A successful response contains a list of attributes associated with videos, i.e. and timestamp as well as state of the last YouTube API synchronization.

```json
{
"youTubeActivities":
    [{
        "id":"d8290f50-f68a-4a9f-9229-9961961d1c79",
        "title":"Raccoon Tries Most Popular Chips to see Which Chip is the Best",
        "duration":"PT4M45S",
        "publishedAt":"2020-10-05T18:00:02Z",
        "channelTitle":"Tito The Raccoon",
        "videoCategory":"Entertainment"
    }],
"state":"SUCCEEDED",
"stateCreatedAt":"2020-11-03T12:34:42.836938Z"
}
```

#### /api/v1/youtube/channels

This endpoint is like the previous one, except it returns the list of subscribed channels by the user.
````
GET /api/v1/youtube/channels HTTP/1.1
Host: jlisok.pl
Authorization:
        Bearer: exampleDummyJWToken
````

A successful response contains a list of attributes associated with channels, i.e channel id, title, channel publish date, channel stats and timestamp as well as state of the last YouTube API synchronization.

```json
{
"youTubeActivities":
    [{
        "id":"4d78f305-753e-43b5-9892-590f9ea64b46",
        "title":"freeCodeCamp.org",
        "publishedAt":"2014-12-16T21:18:48Z",
        "viewNumber":126393536,
        "subscriberNumber":2690000,
        "videoNumber":1190
    }],
"state":"SUCCEEDED",
"stateCreatedAt":"2020-11-03T12:34:42.836938Z"
}
```

### Todos
 - Add thumbnails and links to lists of channels and videos
 - Add YouTube video suggestions customized for the user

### License
Creative Commons

[Java14]: <https://openjdk.java.net/projects/jdk/14/>
   [Spring Boot]: <https://spring.io/projects/spring-boot>
   [Hibernate]: <https://hibernate.org/>
   [PostgreSQL]: <https://www.postgresql.org/>
   [Liquibase]: <https://www.liquibase.org/>
   [Docker]: <https://www.docker.com/>

   [AWS EC2]: <https://aws.amazon.com/ec2/>
   [AWS ECR]: <https://aws.amazon.com/ecr/>
   [AWS S3]: <https://aws.amazon.com/s3/>
   [AWS ELB]: <https://aws.amazon.com/elasticloadbalancing/>
   [AWS RDS]: <https://aws.amazon.com/rds/>
   [AWS Route53]: <https://aws.amazon.com/route53/>

   [React.js]: <https://reactjs.org/>
   [Axios]: <https://github.com/axios/axios>
   [Bootstrap4]: <https://getbootstrap.com/>

   [Backend]: <https://github.com/jlisok/youtube_activity_manager>
   [Frontend]: <https://github.com/jlisok/youtube_activity_manager_frontend>
   [Deployment]: <https://github.com/jlisok/youtube_activity_manager_deployment>

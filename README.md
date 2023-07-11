# my-website-pro-be
## Validating JWT Token from Front-end (which used jwt token from firebase)

The application use Spring Security, Firebase to generate and validate JWT Token.
The JWT Token will be passed from front-end, and then use firebase admin to verify the token
Roles of user will be stored inside Firebase Store and will be access once the jwt token had been verified

## Implementing Docker file and Docker Compose

Application (Both Backend and Frontend) use Dockerfile to generete Docker image, and use docker compose to create a compose including:
+ Server Container
+ FE Container
+ Database

## Server sent event
![image](https://github.com/aqsakd123/my-website-pro-be/assets/112476093/7c446230-34aa-4a35-8158-2e44fb6afeb8)

The server sent event is designed so every 1 second, the server will run function getTaskBoard and pass to frontend

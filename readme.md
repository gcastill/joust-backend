# Instructions

Heroku can be used to run this application locally. However, Google login may not work when run locally.

### Building
- mvn clean install 

### Heroku 
#### Setup
- heroku config -a joust-backend > .env
- remove all lines that are not parameters
- replace all colons with "=" and remove all whitespace

#### Manual setup
- create an empty .env file
- add DATABASE_URL=postgres://<user>:<password>@<host>/<db>
#### Running
- heroku local



Remote - Heroku
===============
This application is also hosted on Heroku. It can be accessed at the following URL:
https://joust-backend.herokuapp.com/login

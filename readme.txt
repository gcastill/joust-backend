Local
=====
Heroku can be used to run this application locally. However, Google login may not work when run locally.

To build:

- mvn clean install

To configure:

- heroku config -a joust-backend > .env
- remove all lines that are not parameters
- replace all colons with "=" and remove all whitespace
- add "PORT=8080"

To run:

- heroku local


Remote - Heroku
===============
This application is also hosted on Heroku. It can be accessed at the following URL:
https://joust-backend.herokuapp.com/


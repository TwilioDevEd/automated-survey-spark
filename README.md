<a href="https://www.twilio.com">
  <img src="https://static0.twilio.com/marketing/bundles/marketing/img/logos/wordmark-red.svg" alt="Twilio" width="250" />
</a>

# Automated Survey Spark

[![Build Status](https://travis-ci.org/TwilioDevEd/automated-survey-spark.svg?branch=master)](https://travis-ci.org/TwilioDevEd/automated-survey-spark)

An application example that implements an Automated Survey using Twilio.

## Heroku

[![Deploy](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)  

Heroku will automatically configure the environment variables necessary to launch this application. Heroku automatically uses a ["Free" dyno](https://www.heroku.com/pricing), and provisions a ["Sandbox" MongoLab](https://mongolab.com/plans/pricing/) instance, so you can play around with this application at no charge.

## Run the application

1. Clone the repository and `cd` into it.

1. The application uses Maven to manage dependencies.

1. Use the following variables to configure this application when running it locally. Heroku will automatically configure these variables for you.

   * `PORT`: the application's port number (defaults to port 4567).
   * `MONGO_URI`: the address of a MongoDB instance to use (defaults to a MongoDB instance listening on the localhost).

1. Configure Twilio to call your webhooks.

   You will need to configure Twilio to call your application when SMSs and calls are received.

   You will need to provision at least one Twilio number with SMS and voice capabilities
   so the application's users can trigger the survey. You can buy a number [right
   here](//www.twilio.com/user/account/phone-numbers/search). Once you have
   a number you need to configure it to work with your application. Open
   [the number management page](//www.twilio.com/user/account/phone-numbers/incoming)
   and open a number's configuration by clicking on it.

   ![Configure Voice](http://howtodocs.s3.amazonaws.com/twilio-number-config-all-med.gif)

1. Run `mvn install` to run the accompanying test suite, and build the application.

1. Use `java -jar target/server.jar` to run the application.

1. Expose the application to the wider Internet using [ngrok](https://ngrok.com/).

   ```bash
   $ ngrok http 8080
   ```

   Once you have started ngrok, update your Twilio's number SMS and voice URL
   setting to use your ngrok hostname. It will look something like
   this:

   ```
   http://<your-ngrok-subdomaon/automated-survey-servlets/survey
   ```

### Run the tests

1. Run at the top-level directory.

   ```bash
   $ mvn test
   ```

## Meta

* No warranty expressed or implied. Software is as is. Diggity.
* [MIT License](http://www.opensource.org/licenses/mit-license.html)
* Lovingly crafted by Twilio Developer Education.

<a href="https://www.twilio.com">
  <img src="https://static0.twilio.com/marketing/bundles/marketing/img/logos/wordmark-red.svg" alt="Twilio" width="250" />
</a>

# Automated Survey Spark

[![Build Status](https://github.com/TwilioDevEd/automated-survey-spark/actions/workflows/gradle.yml/badge.svg)](https://github.com/TwilioDevEd/automated-survey-spark/actions/workflows/gradle.yml)


[![Build Status](https://travis-ci.org/TwilioDevEd/automated-survey-spark.svg?branch=master)](https://travis-ci.org/TwilioDevEd/automated-survey-spark)

An application example that implements an Automated Survey using Twilio.

## Run the application

1. Clone the repository and `cd` into it.

1. The application uses Gradle to manage dependencies.

1. Use the following variables to configure this application when running it locally.

   * `PORT`: the application's port number (defaults to port 4567).
   * `MONGODB_URI`: the address of a MongoDB instance to use (defaults to a MongoDB instance listening on the localhost).
     Defaults to `Test` if not specified.

1. Configure Twilio to call your webhooks.

   You will need to configure Twilio to call your application when SMSs and calls are received.

   You will need to provision at least one Twilio number with SMS and voice capabilities
   so the application's users can trigger the survey. You can buy a number [right
   here](//www.twilio.com/user/account/phone-numbers/search). Once you have
   a number you need to configure it to work with your application. Open
   [the number management page](//www.twilio.com/user/account/phone-numbers/incoming)
   and open a number's configuration by clicking on it.

   ![Configure Voice](http://howtodocs.s3.amazonaws.com/twilio-number-config-all-med.gif)

1. Run `./gradlew run` to run the application.

1. Expose the application to the wider Internet using [ngrok](https://ngrok.com/).

   ```bash
   $ ngrok http 4567
   ```

   Once you have started ngrok, update your Twilio's number voice URL
   setting to use your ngrok hostname. It should look something like
   this:

   ```
   http://<your-ngrok-subdomain/interview (POST)
   ```
1. Call your Twilio number and answer the survey! You can check the results at `http://<your-ngrok-subdomain/`

### Run the tests

1. Run at the top-level directory.

   ```bash
   $ ./gradle check
   ```

### Running MongoDB on Mac OS

To run MongoDB on Mac OS in the foreground, after installing from brew:

```bash
mongod --config /opt/homebrew/etc/mongod.conf
```

## Meta

* No warranty expressed or implied. Software is as is. Diggity.
* [MIT License](http://www.opensource.org/licenses/mit-license.html)
* Lovingly crafted by Twilio Developer Education.

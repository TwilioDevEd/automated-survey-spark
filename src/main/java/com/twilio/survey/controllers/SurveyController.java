package com.twilio.survey.controllers;

import com.twilio.survey.Server;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.SurveyService;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.IncomingCall;
import com.twilio.survey.util.Question;
import com.twilio.sdk.verbs.Gather;
import com.twilio.sdk.verbs.Record;
import com.twilio.sdk.verbs.Say;
import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.sdk.verbs.TwiMLResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import spark.Route;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SurveyController {
  private static SurveyService surveys = new SurveyService(Server.config.getMongoURI());

  // Main interview loop.
  public static Route interview = (request, response) -> {
    IncomingCall call = new IncomingCall(parseBody(request.body()));
    TwiMLResponse twiml = new TwiMLResponse();
    Survey existingSurvey = surveys.getSurvey(call.getFrom());
    if (existingSurvey == null) {
      Survey survey = surveys.createSurvey(call.getFrom());
      twiml.append(new Say("Thanks for taking our survey."));
      continueSurvey(survey, twiml);
    } else if (!existingSurvey.isDone()) {
      existingSurvey.appendResponse(new Response(call.getInput()));
      surveys.updateSurvey(existingSurvey);
      if (!existingSurvey.isDone()) {
        continueSurvey(existingSurvey, twiml);
      }
    }
    twiml.append(new Say("Your responses have been recorded. Thank you for your time!"));
    return twiml.toXML();
  };

  // Results accessor route
  public static Route results = (request, response) -> {
    Gson gson = new Gson();
    JsonObject json = new JsonObject();
      // Add questions to the JSON response object
      json.add("survey", gson.toJsonTree(Server.config.getQuestions()));
      // Add user responses to the JSON response object
      json.add("results", gson.toJsonTree(surveys.findAllFinishedSurveys()));
      response.type("application/json");
      return json;
    };

  // Transcription route (called by Twilio's callback, once transcription is complete)
  public static Route transcribe = (request, response) -> {
    IncomingCall call = new IncomingCall(parseBody(request.body()));
      // Get the phone and question numbers from the URL parameters provided by the "Record" verb
      String surveyId = request.params(":phone");
      int questionId = Integer.parseInt(request.params(":question"));
      // Find the survey in the DB...
      Survey survey = surveys.getSurvey(surveyId);
      // ...and update it with our transcription text.
      survey.getResponses()[questionId].setAnswer(call.getTranscriptionText());
      surveys.updateSurvey(survey);
      response.status(200);
      return "OK";
    };

  // Helper methods
  protected static String continueSurvey(Survey survey, TwiMLResponse twiml) throws TwiMLException,
      UnsupportedEncodingException {
    Question question = Server.config.getQuestions()[survey.getIndex()];
    // Depending on the question type, create different TwiML verbs.
    switch (question.getType()) {
      case "text":
        twiml = buildTextQuestionTwiML(survey, question.getText());
        break;
      case "boolean":
        buildBooleanQuestionTwiML(question.getText());
        break;
      case "number":
        twiml = buildNumberQuestionTwiML(question.getText());
        break;
    }
    return twiml.toXML();
  }

  private static TwiMLResponse buildTextQuestionTwiML(Survey survey, String questioText)
          throws TwiMLException, UnsupportedEncodingException {

    TwiMLResponse response = new TwiMLResponse();
    response.append(new Say(questioText));
    Say textInstructions =
        new Say(
            "Your response will be recorded after the tone. Once you have finished recording, press the #.");
    response.append(textInstructions);
    Record record = new Record();
    record.setFinishOnKey("#");
    // Use the Transcription route to receive the text of a voice response.
    record.setTranscribe(true);
    record.setTranscribeCallback("/interview/" + urlEncode(survey.getPhone()) + "/transcribe/"
        + survey.getIndex());
    response.append(record);

    return response;
  }

  private static TwiMLResponse buildBooleanQuestionTwiML(String questionText) throws TwiMLException {
    TwiMLResponse response = new TwiMLResponse();

    response.append(new Say(questionText));
    Say boolInstructions =
        new Say("Press 0 to respond 'No,' and press any other number to respond 'Yes.'");
    response.append(boolInstructions);
    Gather booleanGather = new Gather();
    // Listen only for one digit.
    booleanGather.setNumDigits(1);
    response.append(booleanGather);

    return response;
  }

  private static TwiMLResponse buildNumberQuestionTwiML(String questionText) throws TwiMLException {
    TwiMLResponse response = new TwiMLResponse();
    response.append(new Say(questionText));
    Say numInstructions = new Say("Enter the number on your keypad, followed by the #.");
    response.append(numInstructions);
    Gather numberGather = new Gather();
    // Listen until a user presses "#"
    numberGather.setFinishOnKey("#");
    response.append(numberGather);

    return response;
  }

  // Spark has no built-in body parser, so let's roll our own.
  public static Map<String, String> parseBody(String body) throws UnsupportedEncodingException {
    String[] unparsedParams = body.split("&");
    Map<String, String> parsedParams = new HashMap<String, String>();
    for (int i = 0; i < unparsedParams.length; i++) {
      String[] param = unparsedParams[i].split("=");
      if (param.length == 2) {
        parsedParams.put(urlDecode(param[0]), urlDecode(param[1]));
      } else if (param.length == 1) {
        parsedParams.put(urlDecode(param[0]), "");
      }
    }
    return parsedParams;
  }

  // Wrap the URLEncoder and URLDecoder for cleanliness.
  public static String urlEncode(String s) throws UnsupportedEncodingException {
    return URLEncoder.encode(s, "utf-8");
  }

  public static String urlDecode(String s) throws UnsupportedEncodingException {
    return URLDecoder.decode(s, "utf-8");
  }
}

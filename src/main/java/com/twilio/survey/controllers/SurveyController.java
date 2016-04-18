package com.twilio.survey.controllers;

import com.twilio.survey.Server;
import com.twilio.survey.models.Response;
import com.twilio.survey.models.SurveyService;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.IncomingCall;

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
    Map<String, String> parameters = parseBody(request.body());
    IncomingCall call = IncomingCall.createInstance(parameters);
    AbstractMessageFactory messageFactory = AbstractMessageFactory.createInstance(parameters);

    Survey existingSurvey = surveys.getSurvey(call.getFrom());
    if (existingSurvey == null) {
      Survey survey = surveys.createSurvey(call.getFrom());
      return messageFactory.firstTwiMLQuestion(survey);
    } else if (!existingSurvey.isDone()) {
      existingSurvey.appendResponse(new Response(call.getInput()));
      surveys.updateSurvey(existingSurvey);
      if (!existingSurvey.isDone()) {
        return messageFactory.nextTwiMLQuestion(existingSurvey);
      }
    }
    return messageFactory.goodByeTwiMLMessage();
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
    IncomingCall call = IncomingCall.createInstance(parseBody(request.body()));
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

  public static String urlDecode(String s) throws UnsupportedEncodingException {
    return URLDecoder.decode(s, "utf-8");
  }
}

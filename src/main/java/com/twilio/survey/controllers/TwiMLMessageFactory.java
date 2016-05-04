package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.Server;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.Question;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class TwiMLMessageFactory extends AbstractMessageFactory {

    String firstTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException {
      TwiMLResponse response = new TwiMLResponse();
      response.append(new Say("Thanks for taking our survey."));
      return nextTwiMLQuestion(survey, response).toXML();
    }

    String nextTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException {
      return nextTwiMLQuestion(survey, null).toXML();
    }

    private TwiMLResponse nextTwiMLQuestion(Survey survey, TwiMLResponse twiml) throws TwiMLException,
            UnsupportedEncodingException {
      Question question = Server.config.getQuestions()[survey.getIndex()];
      return buildQuestionTwiML(survey, question, twiml);
    }

    private TwiMLResponse buildQuestionTwiML(Survey survey, Question question, TwiMLResponse twiml) throws TwiMLException, UnsupportedEncodingException {
      TwiMLResponse response = (twiml != null) ? twiml : new TwiMLResponse();
      Say say = new Say(question.getText());
      response.append(say);
      // Depending on the question type, create different TwiML verbs.
      switch (question.getType()) {
        case "text":
          response = appendTextQuestion(survey, response);
          break;
        case "boolean":
          response = appendBooleanQuestion(response);
          break;
        case "number":
          response = appendNumberQuestion(response);
          break;
      }
      return response;
    }

    private TwiMLResponse appendNumberQuestion(TwiMLResponse twiml) throws TwiMLException {
      Say numInstructions = new Say("Enter the number on your keypad, followed by the #.");
      twiml.append(numInstructions);
      Gather numberGather = new Gather();
      // Listen until a user presses "#"
      numberGather.setFinishOnKey("#");
      twiml.append(numberGather);

      return twiml;
    }

    private TwiMLResponse appendBooleanQuestion(TwiMLResponse twiml) throws TwiMLException {
      Say boolInstructions =
          new Say("Press 0 to respond 'No,' and press any other number to respond 'Yes.'");
      twiml.append(boolInstructions);
      Gather booleanGather = new Gather();
      // Listen only for one digit.
      booleanGather.setNumDigits(1);
      twiml.append(booleanGather);

      return twiml;
    }

    private TwiMLResponse appendTextQuestion(Survey survey, TwiMLResponse twiml)
            throws TwiMLException, UnsupportedEncodingException {

      Say textInstructions =
          new Say(
              "Your response will be recorded after the tone. Once you have finished recording, press the #.");
      twiml.append(textInstructions);
      Record text = new Record();
      text.setFinishOnKey("#");
      // Use the Transcription route to receive the text of a voice response.
      text.setTranscribe(true);
      text.setTranscribeCallback(String.format("/interview/%s/transcribe/%s",
              urlEncode(survey.getPhone()), survey.getIndex()));
      twiml.append(text);

      return twiml;
    }

    // Wrap the URLEncoder and URLDecoder for cleanliness.
    private String urlEncode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "utf-8");
    }

    String goodByeTwiMLMessage() throws TwiMLException {
      TwiMLResponse twiml = new TwiMLResponse();
      twiml.append(new Say("Your responses have been recorded. Thank you for your time!"));
      return twiml.toXML();
    }
}

package com.twilio.survey.controllers;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import com.twilio.survey.Server;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.Question;
import com.twilio.twiml.TwiMLException;
import com.twilio.twiml.VoiceResponse;
import com.twilio.twiml.voice.Gather;
import com.twilio.twiml.voice.Record;
import com.twilio.twiml.voice.Say;

public class TwiMLMessageFactory extends AbstractMessageFactory {

  String firstTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException {
    VoiceResponse.Builder voiceResponseBuilder = new VoiceResponse.Builder()
        .say(new Say.Builder("Thanks for taking our survey.").build());

    return nextTwiMLQuestion(survey, voiceResponseBuilder).toXml();
  }

  String nextTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException {
    return nextTwiMLQuestion(survey, null).toXml();
  }

  private VoiceResponse nextTwiMLQuestion(Survey survey, VoiceResponse.Builder twiml)
      throws TwiMLException, UnsupportedEncodingException {
    Question question = Server.config.getQuestions()[survey.getIndex()];

    return buildQuestionTwiML(survey, question, twiml);
  }

  private VoiceResponse buildQuestionTwiML(Survey survey, Question question,
      VoiceResponse.Builder twiml) throws TwiMLException, UnsupportedEncodingException {
    VoiceResponse.Builder response = twiml != null ? twiml : new VoiceResponse.Builder();
    Say say = new Say.Builder(question.getText()).build();
    response.say(say);
    // Depending on the question type, create different TwiML verbs.
    switch (question.getType()) {
      case "text":
        appendTextQuestion(survey, response);
        break;
      case "boolean":
        appendBooleanQuestion(response);
        break;
      case "number":
        appendNumberQuestion(response);
        break;
    }
    return response.build();
  }

  private VoiceResponse.Builder appendNumberQuestion(VoiceResponse.Builder twiml)
      throws TwiMLException {
    Say numInstructions =
        new Say.Builder("Enter the number on your keypad, followed by the #.").build();
    twiml.say(numInstructions);
    // Listen until a user presses "#"
    Gather numberGather = new Gather.Builder().finishOnKey("#").build();

    twiml.gather(numberGather);

    return twiml;
  }

  private VoiceResponse.Builder appendBooleanQuestion(VoiceResponse.Builder twiml)
      throws TwiMLException {
    Say boolInstructions =
        new Say.Builder("Press 0 to respond 'No,' and press any other number to respond 'Yes.'")
            .build();
    twiml.say(boolInstructions);

    // Listen only for one digit.
    Gather booleanGather = new Gather.Builder().numDigits(1).build();
    twiml.gather(booleanGather);

    return twiml;
  }

  private VoiceResponse.Builder appendTextQuestion(Survey survey, VoiceResponse.Builder twiml)
      throws TwiMLException, UnsupportedEncodingException {

    Say textInstructions = new Say.Builder(
        "Your response will be recorded after the tone. Once you have finished recording, press the #.")
            .build();
    twiml.say(textInstructions);
    Record text = new Record.Builder()
        .finishOnKey("#")
        .transcribe(true)
        .transcribeCallback(
            String.format("/interview/%s/transcribe/%s", urlEncode(survey.getPhone()), survey.getIndex())
        ).build();

    twiml.record(text);

    return twiml;
  }

  // Wrap the URLEncoder and URLDecoder for cleanliness.
  private String urlEncode(String s) throws UnsupportedEncodingException {
    return URLEncoder.encode(s, "utf-8");
  }

  String goodByeTwiMLMessage() throws TwiMLException {
    VoiceResponse voiceResponse = new VoiceResponse.Builder()
        .say(new Say.Builder("Your responses have been recorded. Thank you for your time!").build())
        .build();

    return voiceResponse.toXml();
  }
}

package com.twilio.survey.controllers;

import com.twilio.survey.Server;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.Question;
import com.twilio.twiml.Body;
import com.twilio.twiml.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;

public class SMSTwiMLMessageFactory extends AbstractMessageFactory {
  public String goodByeTwiMLMessage() throws TwiMLException {
    MessagingResponse messagingResponse = new MessagingResponse.Builder()
        .message(new Message.Builder()
                  .body(new Body("Your responses have been recorded. Thank you for your time!"))
                  .build())
        .build();

    return messagingResponse.toXml();
  }

  public String firstTwiMLQuestion(Survey survey) throws TwiMLException {
    MessagingResponse.Builder messagingResponseBuilder = new MessagingResponse.Builder()
        .message(new Message.Builder()
                  .body(new Body("Thanks for taking our survey."))
                  .build());

    return nextTwimlQuestion(survey, messagingResponseBuilder);
  }

  public String nextTwiMLQuestion(Survey survey) throws TwiMLException {
    return nextTwimlQuestion(survey, null);
  }

  private String nextTwimlQuestion(Survey survey, MessagingResponse.Builder baseResponseBuilder)
      throws TwiMLException {
    Question question = Server.config.getQuestions()[survey.getIndex()];

    MessagingResponse.Builder twiMLResponseBuilder =
        baseResponseBuilder == null ? new MessagingResponse.Builder() : baseResponseBuilder;

    twiMLResponseBuilder = twiMLResponseBuilder
        .message(new Message.Builder()
                  .body(new Body(question.getText()))
                  .build());

    return twiMLResponseBuilder.build().toXml();
  }
}

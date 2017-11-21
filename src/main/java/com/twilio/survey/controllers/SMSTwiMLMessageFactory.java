package com.twilio.survey.controllers;

import com.twilio.survey.Server;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.Question;
import com.twilio.twiml.messaging.Body;
import com.twilio.twiml.messaging.Message;
import com.twilio.twiml.MessagingResponse;
import com.twilio.twiml.TwiMLException;

public class SMSTwiMLMessageFactory extends AbstractMessageFactory {
  public String goodByeTwiMLMessage() throws TwiMLException {
    MessagingResponse messagingResponse = new MessagingResponse.Builder()
        .message(new Message.Builder()
                .body(new Body.Builder("Your responses have been recorded. Thank you for your time!")
                        .build())
                .build())
        .build();

    return messagingResponse.toXml();
  }

  public String firstTwiMLQuestion(Survey survey) throws TwiMLException {
    MessagingResponse.Builder messagingResponseBuilder = new MessagingResponse.Builder()
        .message(new Message.Builder()
                  .body(new Body.Builder("Thanks for taking our survey.").build())
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
                  .body(new Body.Builder(question.getText()).build())
                  .build());

    return twiMLResponseBuilder.build().toXml();
  }
}

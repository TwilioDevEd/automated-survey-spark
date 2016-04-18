package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.*;
import com.twilio.survey.Server;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.Question;

public class SMSTwiMLMessageFactory extends AbstractMessageFactory {
    public String goodByeTwiMLMessage() throws TwiMLException {
        TwiMLResponse twiml = new TwiMLResponse();
        twiml.append(new Message("Your responses have been recorded. Thank you for your time!"));
        return twiml.toXML();
    }

    public String firstTwiMLQuestion(Survey survey) throws TwiMLException {
        TwiMLResponse response = new TwiMLResponse();
        response.append(new Message("Thanks for taking our survey."));
        return nextTwimlQuestion(survey, response);
    }

    public String nextTwiMLQuestion(Survey survey) throws TwiMLException {
        return nextTwimlQuestion(survey, null);
    }

    private String nextTwimlQuestion(Survey survey, TwiMLResponse baseResponse) throws TwiMLException {
        Question question = Server.config.getQuestions()[survey.getIndex()];
        TwiMLResponse twiMLResponse = baseResponse == null ? new TwiMLResponse() : baseResponse;
        twiMLResponse.append(new Message(question.getText()));
        return twiMLResponse.toXML();
    }
}

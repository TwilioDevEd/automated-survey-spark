package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.survey.models.Survey;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public abstract class AbstractMessageFactory {

    public static AbstractMessageFactory createInstance(Map<String, ?> parameters) {
        if (parameters.get("MessageSid") != null) {
            return new SMSTwiMLMessageFactory();
        } else {
            return new TwiMLMessageFactory();
        }
    }

    abstract String firstTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException;

    abstract String nextTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException;

    abstract String goodByeTwiMLMessage() throws TwiMLException;
}

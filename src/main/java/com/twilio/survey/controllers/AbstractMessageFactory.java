package com.twilio.survey.controllers;

import com.twilio.sdk.verbs.TwiMLException;
import com.twilio.survey.models.Survey;

import java.io.UnsupportedEncodingException;

public abstract class AbstractMessageFactory {

    abstract String firstTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException;

    abstract String nextTwiMLQuestion(Survey survey) throws TwiMLException, UnsupportedEncodingException;

    abstract String goodByeTwiMLMessage() throws TwiMLException;
}

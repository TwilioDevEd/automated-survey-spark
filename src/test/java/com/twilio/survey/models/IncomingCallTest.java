package com.twilio.survey.models;

import com.twilio.survey.util.IncomingCall;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class IncomingCallTest {

    @Test
    public void shouldExtractInputFromNumericAnswer() {
        Map<String, String> numericAnswer = new HashMap<String, String>(){{
            put("Digits", "1");
        }};

        IncomingCall incomingCall = IncomingCall.createInstance(numericAnswer);

        assertThat(incomingCall.getInput(), is("1"));
    }

    @Test
    public void shouldExtractInputFromVoiceAnswer() {
        Map<String, String> voiceAnswer = new HashMap<String, String>(){{
            put("RecordingUrl", "test");
        }};

        IncomingCall incomingCall = IncomingCall.createInstance(voiceAnswer);

        assertThat(incomingCall.getInput(), is("test"));
    }

    @Test
    public void shouldExtractInputFromSMSAnswer() {
        Map<String, String> voiceAnswer = new HashMap<String, String>(){{
            put("MessageSid", "1");
            put("Body", "sms body");
        }};

        IncomingCall incomingCall = IncomingCall.createInstance(voiceAnswer);

        assertThat(incomingCall.getInput(), is("sms body"));
    }
}

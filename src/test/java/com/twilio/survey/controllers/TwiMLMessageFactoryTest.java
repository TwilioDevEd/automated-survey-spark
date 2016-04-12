package com.twilio.survey.controllers;

import com.twilio.survey.models.Survey;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TwiMLMessageFactoryTest {
    @Test
    public void testContinueSurvey() {
      // Confirms that, if a TwiML response is returned, the XML includes a "Say" verb, and the user
      // will receive some feedback.
      Survey survey = new Survey();
      try {
        String twiMLResponse = new TwiMLMessageFactory().nextTwiMLQuestion(survey);
        assertTrue(twiMLResponse.contains("Say"));
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }

    }
}

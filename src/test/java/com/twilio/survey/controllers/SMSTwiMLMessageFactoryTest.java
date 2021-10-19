package com.twilio.survey.controllers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.xml.HasXPath.hasXPath;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import com.twilio.twiml.TwiMLException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.twilio.survey.Server;
import com.twilio.survey.models.Survey;
import com.twilio.survey.util.Config;

public class SMSTwiMLMessageFactoryTest {
  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    Server.config = new Config();
  }

  @Test
  public void shouldBuildATwimMLGoodByeMessageForSMS()
      throws IOException, SAXException, ParserConfigurationException, TwiMLException {

    SMSTwiMLMessageFactory smsFactory = new SMSTwiMLMessageFactory();

    String twiml = smsFactory.goodByeTwiMLMessage();

    Document twiMLDocument = XMLTestHelper.createDocumentFromXml(twiml);
    Node responseNode = twiMLDocument.getElementsByTagName("Response").item(0);

    assertThat(responseNode, hasXPath("/Response/Message[text() = "
        + "'Your responses have been recorded. Thank you for your time!']"));
  }

  @Test
  public void shouldBuildAHelloMessageOnTheFirstMessage()
      throws IOException, SAXException, ParserConfigurationException, TwiMLException {

    SMSTwiMLMessageFactory smsFactory = new SMSTwiMLMessageFactory();

    String twiml = smsFactory.firstTwiMLQuestion(new Survey("000000"));

    Document twiMLDocument = XMLTestHelper.createDocumentFromXml(twiml);
    Node responseNode = twiMLDocument.getElementsByTagName("Response").item(0);

    assertThat(responseNode,
        hasXPath("/Response/Message[text() = " + "'Thanks for taking our survey.']"));
    assertThat(responseNode,
        hasXPath("/Response/Message[text() = " + "'Please tell us your age.']"));
  }

  @Test
  public void shouldBuildTwiMLVoiceQuestion()
      throws IOException, SAXException, ParserConfigurationException, TwiMLException {

    SMSTwiMLMessageFactory smsFactory = new SMSTwiMLMessageFactory();

    Survey survey = new Survey("123456");
    String twiml = smsFactory.nextTwiMLQuestion(survey);

    Document twiMLDocument = XMLTestHelper.createDocumentFromXml(twiml);
    Node responseNode = twiMLDocument.getElementsByTagName("Response").item(0);

    Assert.assertThat(responseNode,
        hasXPath("/Response/Message[text() = 'Please tell us your age.']"));
  }
}

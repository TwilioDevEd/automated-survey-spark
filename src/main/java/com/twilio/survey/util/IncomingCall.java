package com.twilio.survey.util;

import java.util.Map;

public class IncomingCall {

  private String from;
  private String input;
  private String transcriptionText;

  public static IncomingCall createInstance(Map<String, String> parameters) {
    String retrievedInput;
    if (parameters.containsKey("MessageSid")) {
      retrievedInput = parameters.get("Body");
    } else {
      retrievedInput = parameters.containsKey("RecordingUrl") ? parameters.get("RecordingUrl") :
          parameters.get("Digits");
    }

    return new IncomingCall(parameters.get("From"), retrievedInput, parameters.get("TranscriptionText"));
  }

  // Constructor
  private IncomingCall(String from, String input, String transcriptionText) {
    this.from = from;
    this.input = input;
    this.transcriptionText = transcriptionText;
  }
  
  public IncomingCall() {
    this("+0000000000", null, null);
  }

  // Accessors
  public String getFrom() {
    return from;
  }

  public String getInput() {
    return input;
  }

  public String getTranscriptionText() {
    return transcriptionText;
  }
}

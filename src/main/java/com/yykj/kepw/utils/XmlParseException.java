package com.yykj.kepw.utils;

public class XmlParseException extends Exception {
  private static final long serialVersionUID = -6089798664483298023L;
  
  public XmlParseException() {}
  
  public XmlParseException(String message) {
    super(message);
  }
  
  public XmlParseException(String message, Throwable reas) {
    super(message, reas);
  }
  
  public XmlParseException(Throwable reas) {
    super(reas);
  }
}

package com.yykj.kepw.support.encryption;

public class PasswordEncoderException extends Exception {
  private static final String CR = System.getProperty("line.separator");
  
  public PasswordEncoderException() {}
  
  public PasswordEncoderException(String message) {
    super(message);
  }
  
  public PasswordEncoderException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public String getMessage() {
    StringBuilder retval = new StringBuilder();
    retval.append(CR);
    retval.append(super.getMessage()).append(CR);
    Throwable cause = getCause();
    if (cause != null) {
      String message = cause.getMessage();
      if (message != null) {
        retval.append(message).append(CR);
      } else {
        StackTraceElement[] ste = cause.getStackTrace();
        for (int i = ste.length - 1; i >= 0; i--)
          retval.append(" at ").append(ste[i].getClassName()).append(".").append(ste[i].getMethodName())
            .append(" (").append(ste[i].getFileName())
            .append(":").append(ste[i].getLineNumber()).append(")").append(CR); 
      } 
    } 
    return retval.toString();
  }
}

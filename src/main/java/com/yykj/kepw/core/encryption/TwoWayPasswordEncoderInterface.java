package com.yykj.kepw.core.encryption;

import com.yykj.kepw.support.encryption.PasswordEncoderException;

public interface TwoWayPasswordEncoderInterface {
  void init() throws PasswordEncoderException;
  
  String encode(String paramString);
  
  String encode(String paramString, boolean paramBoolean);
  
  String decode(String paramString, boolean paramBoolean);
  
  String decode(String paramString);
  
  String[] getPrefixes();
}

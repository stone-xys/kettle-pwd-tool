package com.yykj.kepw.support.encryption;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import com.yykj.kepw.core.encryption.TwoWayPasswordEncoderInterface;
import com.yykj.kepw.utils.StringUtil;

public class KettleTwoWayPasswordEncoder implements TwoWayPasswordEncoderInterface {
  private static final KettleTwoWayPasswordEncoder instance = new KettleTwoWayPasswordEncoder();
  
  private static final int RADIX = 16;
  
  private String Seed;
  
  public static final String PASSWORD_ENCRYPTED_PREFIX = "Encrypted ";
  
  public KettleTwoWayPasswordEncoder() {
    String envSeed = System.getProperty("KETTLE_TWO_WAY_PASSWORD_ENCODER_SEED", "0933910847463829827159347601486730416058");
    this.Seed = envSeed;
  }
  
  public void init() throws PasswordEncoderException {}
  
  public String encode(String rawPassword) {
    return encode(rawPassword, true);
  }
  
  public String encode(String rawPassword, boolean includePrefix) {
    if (includePrefix)
      return encryptPasswordIfNotUsingVariablesInternal(rawPassword); 
    return encryptPasswordInternal(rawPassword);
  }
  
  public String decode(String encodedPassword) {
    if (encodedPassword != null && encodedPassword.startsWith("Encrypted "))
      encodedPassword = encodedPassword.substring("Encrypted ".length()); 
    return decryptPasswordInternal(encodedPassword);
  }
  
  public String decode(String encodedPassword, boolean optionallyEncrypted) {
    if (encodedPassword == null)
      return null; 
    if (optionallyEncrypted) {
      if (encodedPassword.startsWith("Encrypted ")) {
        encodedPassword = encodedPassword.substring("Encrypted ".length());
        return decryptPasswordInternal(encodedPassword);
      } 
      return encodedPassword;
    } 
    return decryptPasswordInternal(encodedPassword);
  }
  
  protected String encryptPasswordInternal(String password) {
    if (password == null)
      return ""; 
    if (password.length() == 0)
      return ""; 
    BigInteger bi_passwd = new BigInteger(password.getBytes());
    BigInteger bi_r0 = new BigInteger(getSeed());
    BigInteger bi_r1 = bi_r0.xor(bi_passwd);
    return bi_r1.toString(16);
  }
  
  protected String decryptPasswordInternal(String encrypted) {
    if (encrypted == null)
      return ""; 
    if (encrypted.length() == 0)
      return ""; 
    BigInteger bi_confuse = new BigInteger(getSeed());
    try {
      BigInteger bi_r1 = new BigInteger(encrypted, 16);
      BigInteger bi_r0 = bi_r1.xor(bi_confuse);
      return new String(bi_r0.toByteArray());
    } catch (Exception e) {
      return "";
    } 
  }
  
  protected String getSeed() {
    return this.Seed;
  }
  
  public String[] getPrefixes() {
    return new String[] { "Encrypted " };
  }
  
  protected final String encryptPasswordIfNotUsingVariablesInternal(String password) {
    String encrPassword = "";
    List<String> varList = new ArrayList<>();
    StringUtil.getUsedVariables(password, varList, true);
    if (varList.isEmpty()) {
      encrPassword = "Encrypted " + encryptPasswordInternal(password);
    } else {
      encrPassword = password;
    } 
    return encrPassword;
  }
  
  protected final String decryptPasswordOptionallyEncryptedInternal(String password) {
    if (!StringUtil.isEmpty(password) && password.startsWith("Encrypted "))
      return decryptPasswordInternal(password.substring("Encrypted ".length())); 
    return password;
  }
}

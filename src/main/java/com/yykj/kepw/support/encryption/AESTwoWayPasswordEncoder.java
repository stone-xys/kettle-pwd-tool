package com.yykj.kepw.support.encryption;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


import com.yykj.kepw.core.encryption.TwoWayPasswordEncoderInterface;
import com.yykj.kepw.utils.StringUtil;

public class AESTwoWayPasswordEncoder implements TwoWayPasswordEncoderInterface {
  private Cipher cipher;
  
  private byte[] aesKey;
  
  private SecretKeySpec secretKey;
  
  public static final String AES_PREFIX = "AES ";
  
  public static final String KETTLE_AES_KEY_FILE = "KETTLE_AES_KEY_FILE";
  
  public static final String KETTLE_AES_KEY_TYPE = "KETTLE_AES_KEY_TYPE";
  
  public static final String KETTLE_AES_KEY_TYPE_BINARY = "BINARY";
  
  public static final String KETTLE_AES_KETTLE_PASSWORD_HANDLING = "KETTLE_AES_KETTLE_PASSWORD_HANDLING";
  
  public static final String KETTLE_AES_KETTLE_PASSWORD_HANDLING_DECODE = "DECODE";
  
  public static final Charset XML_ENCODING = StandardCharsets.UTF_8;
  
  public static final String GENERAL_ERROR_MESSAGE = "Unable to initialize AES encoder";
  
  private KettleTwoWayPasswordEncoder kettleEncoder = null;
  
  private boolean decodeKettlePasswords;
  
  private String keyFile;
  
  private String keyType;
  
  private String kettlePasswordHandling;
  
  static String kettlePropertiesPath = System.getProperty("user.home") + File.separator + ".kettle" + File.separator + "kettle.properties";
  
  public void init() throws PasswordEncoderException {
    setParameters();
    if (this.keyFile == null)
      throw new PasswordEncoderException("Kettle/system property KETTLE_AES_KEY_FILE is not defined."); 
    try {
      File file = new File(this.keyFile);
      if (!file.exists())
        throw new PasswordEncoderException("Unable to find file specified by Kettle/system property KETTLE_AES_KEY_FILE : " + this.keyFile); 
      this.aesKey = Files.readAllBytes(file.toPath());
      if (!"BINARY".equalsIgnoreCase(this.keyType)) {
        String keyString = new String(this.aesKey, XML_ENCODING);
        this.aesKey = keyString.trim().getBytes(XML_ENCODING);
      } 
      initSecretKey();
    } catch (Exception e) {
      throw new PasswordEncoderException("Unable to initialize AES encoder", e);
    } 
    configureDecodeKettlePasswords();
  }
  
  protected void configureDecodeKettlePasswords() {
    this.kettleEncoder = new KettleTwoWayPasswordEncoder();
    this.decodeKettlePasswords = "DECODE".equalsIgnoreCase(this.kettlePasswordHandling);
  }
  
  void init(String aesKeyString) throws PasswordEncoderException {
    init(aesKeyString.getBytes(XML_ENCODING));
  }
  
  void init(byte[] aesKey) throws PasswordEncoderException {
    setParameters();
    try {
      this.aesKey = aesKey;
      initSecretKey();
    } catch (Exception e) {
      throw new PasswordEncoderException("Unable to initialize AES encoder", e);
    } 
    configureDecodeKettlePasswords();
  }
  
  private void initSecretKey() throws PasswordEncoderException {
    try {
      this.cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
      this.secretKey = new SecretKeySpec(this.aesKey, "AES");
    } catch (Exception e) {
      throw new PasswordEncoderException("Unable to initialize AES encoder", e);
    } 
  }
  
  public String encode(String password) {
    if (StringUtil.isEmpty(password))
      return ""; 
    try {
      synchronized (this.cipher) {
        this.cipher.init(1, this.secretKey);
        byte[] encryptedBinary = this.cipher.doFinal(password.getBytes(XML_ENCODING));
        return new String(Base64.getEncoder().encode(encryptedBinary), XML_ENCODING);
      } 
    } catch (Exception e) {
      throw new IllegalStateException("Unable to AES encrypt password", e);
    } 
  }
  
  public String encode(String password, boolean includePrefix) {
    if (StringUtil.isEmpty(password))
      return ""; 
    List<String> varList = new ArrayList<>();
    StringUtil.getUsedVariables(password, varList, true);
    if (!varList.isEmpty())
      return password; 
    if (includePrefix)
      return "AES " + encode(password); 
    return encode(password);
  }
  
  public String decode(String encodedPassword, boolean optionallyEncrypted) {
    if (optionallyEncrypted) {
      String kettlePrefix = this.kettleEncoder.getPrefixes()[0];
      if (!StringUtil.isEmpty(encodedPassword) && encodedPassword.startsWith(kettlePrefix)) {
        if (this.decodeKettlePasswords)
          return this.kettleEncoder.decode(encodedPassword, optionallyEncrypted); 
        throw new IllegalStateException("A Kettle encoded password was used: '" + encodedPassword + "'");
      } 
      if (!StringUtil.isEmpty(encodedPassword) && encodedPassword.startsWith("AES "))
        return decode(encodedPassword.substring("AES ".length())); 
      return encodedPassword;
    } 
    return decode(encodedPassword);
  }
  
  public String decode(String encodedPassword) {
    if (StringUtil.isEmpty(encodedPassword))
      return ""; 
    try {
      synchronized (this.cipher) {
        this.cipher.init(2, this.secretKey);
        byte[] passwordBinary = Base64.getDecoder().decode(encodedPassword.getBytes(XML_ENCODING));
        byte[] encryptedBinary = this.cipher.doFinal(passwordBinary);
        return new String(encryptedBinary, XML_ENCODING);
      } 
    } catch (Exception e) {
      throw new IllegalStateException("Unable to AES decrypt password", e);
    } 
  }
  
  public String[] getPrefixes() {
    return new String[] { "AES " };
  }
  
  private void setParameters() throws PasswordEncoderException {
    Properties kettleProperties = readProperties(kettlePropertiesPath);
    this.keyFile = getSetting(kettleProperties, "KETTLE_AES_KEY_FILE");
    this.keyType = getSetting(kettleProperties, "KETTLE_AES_KEY_TYPE");
    this.kettlePasswordHandling = getSetting(kettleProperties, "KETTLE_AES_KETTLE_PASSWORD_HANDLING");
  }
  
  private Properties readProperties(String filePath) throws PasswordEncoderException {
    Properties properties = null;
    try {
      FileInputStream fis = new FileInputStream(filePath);
      try {
        properties = new Properties();
        properties.load(fis);
        fis.close();
      } catch (Throwable throwable) {
        try {
          fis.close();
        } catch (Throwable throwable1) {
          throwable.addSuppressed(throwable1);
        } 
        throw throwable;
      } 
    } catch (IOException e) {
      throw new PasswordEncoderException("Could not find kettle.properties", e);
    } 
    return properties;
  }
  
  private String getSetting(Properties kettleProperties, String settingName) {
    return kettleProperties.getProperty(settingName, System.getProperty(settingName));
  }
}

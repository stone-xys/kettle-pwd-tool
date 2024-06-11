package com.yykj.kepw.utils;

import java.util.Arrays;
import java.util.List;

public class StringUtil {
  private static final String UNIX_OPEN = "${";
  
  private static final String UNIX_CLOSE = "}";
  
  private static final String WINDOWS_OPEN = "%%";
  
  private static final String WINDOWS_CLOSE = "%%";
  
  private static final String[] SYSTEM_PROPERTIES = new String[] { 
      "java.version", "java.vendor", "java.vendor.url", "java.home", "java.vm.specification.version", "java.vm.specification.vendor", "java.vm.specification.name", "java.vm.version", "java.vm.vendor", "java.vm.name", 
      "java.specification.version", "java.specification.vendor", "java.specification.name", "java.class.version", "java.class.path", "java.library.path", "java.io.tmpdir", "java.compiler", "java.ext.dirs", "os.name", 
      "os.arch", "os.version", "file.separator", "path.separator", "line.separator", "user.name", "user.home", "user.dir", "user.country", "user.language", 
      "user.timezone", "org.apache.commons.logging.Log", "org.apache.commons.logging.simplelog.log.org.apache.http", "org.apache.commons.logging.simplelog.showdatetime", "org.eclipse.swt.browser.XULRunnerInitialized", "org.eclipse.swt.browser.XULRunnerPath", "sun.arch.data.model", "sun.boot.class.path", "sun.boot.library.path", "sun.cpu.endian", 
      "sun.cpu.isalist", "sun.io.unicode.encoding", "sun.java.launcher", "sun.jnu.encoding", "sun.management.compiler", "sun.os.patch.level" };
  
  private StringUtil() {
    throw new IllegalStateException("Utility Class");
  }
  
  public static void getUsedVariables(String aString, List<String> list, boolean includeSystemVariables) {
    getUsedVariables(aString, "${", "}", list, includeSystemVariables);
    getUsedVariables(aString, "%%", "%%", list, includeSystemVariables);
  }
  
  public static void getUsedVariables(String aString, String open, String close, List<String> list, boolean includeSystemVariables) {
    if (aString == null)
      return; 
    int p = 0;
    while (p < aString.length()) {
      if (aString.startsWith(open, p)) {
        int from = p + open.length();
        int to = aString.indexOf(close, from + 1);
        if (to >= 0) {
          String variable = aString.substring(from, to);
          if (list.indexOf(variable) < 0)
            if (includeSystemVariables || Arrays.<String>asList(SYSTEM_PROPERTIES).indexOf(variable) < 0 || 
              System.getProperty(variable) == null)
              list.add(variable);  
          p = to + close.length();
        } 
      } 
      p++;
    } 
  }
  
  public static boolean isEmpty(CharSequence val) {
    return (val == null || val.length() == 0);
  }
  
  public static String NVL(String source, String def) {
    if (source == null || source.length() == 0)
      return def; 
    return source;
  }
}

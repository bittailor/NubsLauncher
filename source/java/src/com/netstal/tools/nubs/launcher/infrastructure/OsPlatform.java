package com.netstal.tools.nubs.launcher.infrastructure;

public class OsPlatform {

   
   public static String getRakeOsCommand() {
      if (isWindows()) {
         return "rake.bat";
      }
      return "rake";
   }
   
   
   public static boolean isWindows() {
      return System.getProperty("os.name").toLowerCase().indexOf( "windows" ) >= 0;
   }
   
}

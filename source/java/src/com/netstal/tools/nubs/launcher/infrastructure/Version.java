package com.netstal.tools.nubs.launcher.infrastructure;

import com.netstal.tools.nubs.launcher.application.NubsLauncher;

public class Version {
   
   public static final String LOCAL_BUILD = "(local build)";

   static public String getVersion() {
      String implementationVersion = NubsLauncher.class.getPackage().getImplementationVersion();
      if (implementationVersion == null) {
         implementationVersion = LOCAL_BUILD;
      }
      return implementationVersion;
   }
   
}

package com.netstal.tools.nubs.launcher.infrastructure;

import com.netstal.tools.nubs.launcher.application.NubsLauncher;

public class Version {
   
   static public String getVersion() {
      String implementationVersion = NubsLauncher.class.getPackage().getImplementationVersion();
      if (implementationVersion == null) {
         implementationVersion = "(local build)";
      }
      return implementationVersion;
   }
   
}

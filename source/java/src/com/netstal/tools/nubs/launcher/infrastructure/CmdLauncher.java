//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;

public class CmdLauncher {

   private static final String DOUBLE_QUOTE = "\"";
   private String cmd;
   private ProcessBuilder processBuilder;
   
   public CmdLauncher(String cmd) {
      this.cmd = cmd;
      processBuilder = new ProcessBuilder("cmd","/C","start","cmd","/K",DOUBLE_QUOTE + this.cmd + DOUBLE_QUOTE);
   }
   
   public CmdLauncher directory(File directory) {
      processBuilder.directory(directory);
      return this;
   }

   public void launch() throws IOException {
      processBuilder.start();
   }
   
}

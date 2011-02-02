//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;

import com.netstal.tools.nubs.launcher.domain.Command;

public class RakeCmdLauncher {

   private static final String DOUBLE_QUOTE = "\"";
   private static final String RAKE = "rake ";
   
   public void launch(Command command,File directory) throws IOException {
      ProcessBuilder processBuilder = new ProcessBuilder("cmd","/C","start","cmd","/K",DOUBLE_QUOTE + RAKE + command.toString() + DOUBLE_QUOTE);
      processBuilder.directory(directory);
      processBuilder.start();
   }
   
}

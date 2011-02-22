package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OutputGrabber {

   private static Logger LOG = Logger.getLogger(OutputGrabber.class.getName());
   
   public String grab(File directory, String... command) {
      final StringBuilder lines = new StringBuilder();
      ProcessBuilder processBuilder = new ProcessBuilder(command);
      processBuilder.redirectErrorStream(true);
      Process process;
      try {
         process = processBuilder.start();
         Thread pump = new Thread(new StreamPumper(process.getInputStream(),new I_LineConsumer() {        
            @Override
            public void consumeLine(String line) {
               lines.append(line).append("\n");
            }
         }));    
         process.waitFor();
         pump.join();
      }
      catch (IOException e) {
         LOG.log(Level.WARNING ,"Failed to grab output of " + commandString(command), e);
      }
      catch (InterruptedException e) {
         LOG.log(Level.WARNING ,"Failed to grab output of " + commandString(command), e);
      }   
      
      return lines.toString();
   }
   
   private String commandString (String... command) {
      StringBuilder commandString = new StringBuilder();
      for (String part : command) {
         commandString.append(part).append(" ");
      }
      return commandString.toString();
   }
   
}

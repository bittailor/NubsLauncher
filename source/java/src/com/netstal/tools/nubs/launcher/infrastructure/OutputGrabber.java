package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;

public class OutputGrabber {

   private static Logger LOG = Logger.getLogger(OutputGrabber.class.getName());
   private IProcessBuilder processBuilder;
   
   @Inject
   public OutputGrabber(IProcessBuilder processBuilder) {
      this.processBuilder = processBuilder;
   }

   public String grab(File directory, String... command) {
      final StringBuilder lines = new StringBuilder();
      processBuilder.directory(directory);
      processBuilder.outputConsumer(new ILineConsumer() {        
         @Override
         public void consumeLine(String line) {
            lines.append(line).append("\n");
         }
      });

      IProcess process;
      try {
         process = processBuilder.start();   
         process.waitFor();
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

package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
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
   
   public OutputGrabber directory(File directory) {
      processBuilder.directory(directory);
      return this;
   }

   public List<String> grab(String... command) {
      final List<String> lines = new LinkedList<String>();
      
      processBuilder.outputConsumer(new ILineConsumer() {        
         @Override
         public void consumeLine(String line) {
            lines.add(line);
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
      return lines;
   }
   
   private String commandString (String... command) {
      StringBuilder commandString = new StringBuilder();
      for (String part : command) {
         commandString.append(part).append(" ");
      }
      return commandString.toString();
   }
   
}

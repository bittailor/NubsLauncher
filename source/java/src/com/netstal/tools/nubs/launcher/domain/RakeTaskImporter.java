package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.StreamPumper;

public class RakeTaskImporter implements IRakeTaskImporter {

   private final String rakeOsCommand;
   private Provider<IRakeTaskParser> parserProvider;
   
   @Inject
   public RakeTaskImporter(Provider<IRakeTaskParser> parserProvider, IConfiguration properties) {
      rakeOsCommand = properties.getRakeOsCommand();
      this.parserProvider = parserProvider;
   }

   @Override
   public SortedMap<String, RakeTask> importTasks(File root) {
      IRakeTaskParser parser = parserProvider.get();
      ProcessBuilder pb = new ProcessBuilder(rakeOsCommand, "-P");
      pb.directory(root);
      Process process;
      try {
         process = pb.start();
         StreamPumper streamPumper = new StreamPumper(process.getInputStream(),parser); 
         Thread pumperThread = new Thread(streamPumper);
         pumperThread.start();
         process.waitFor();
         pumperThread.join();
         System.out.println(process.exitValue());        
      }
      catch (IOException e) {
         // TODO - fsc - logging - log exception.
         e.printStackTrace();
      }
      catch (InterruptedException e) {
         // TODO - fsc - logging - log exception.
         e.printStackTrace();
      }
      return parser.getTasks();   
   }

}

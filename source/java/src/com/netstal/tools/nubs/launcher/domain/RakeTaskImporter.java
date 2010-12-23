package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.StreamPumper;

public class RakeTaskImporter implements IRakeTaskImporter {

   private Provider<IRakeTaskParser> parserProvider;
   
   @Inject
   public RakeTaskImporter(Provider<IRakeTaskParser> parserProvider) {
      this.parserProvider = parserProvider;
   }

   @Override
   public SortedMap<String, RakeTask> importTasks(File root) {
      IRakeTaskParser parser = parserProvider.get();
      ProcessBuilder pb = new ProcessBuilder("rake.bat", "-P");
      pb.directory(root);
      Process process;
      try {
         process = pb.start();
         StreamPumper streamPumper = new StreamPumper(process.getInputStream(),parser); 
         Thread pumperThread = new Thread(streamPumper);
         pumperThread.run();
         process.waitFor();
         pumperThread.join();
         System.out.println(process.exitValue());        
      }
      catch (IOException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (InterruptedException e) {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      return parser.getTasks();   
   }

}

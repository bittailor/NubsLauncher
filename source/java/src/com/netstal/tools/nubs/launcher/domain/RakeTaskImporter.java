package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.IOException;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.IProcess;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;

public class RakeTaskImporter implements IRakeTaskImporter {

   private static Logger LOG = Logger.getLogger(RakeTaskImporter.class.getName());
   
   private final String rakeOsCommand;
   private Provider<IRakeTaskParser> parserProvider;
   private Provider<IProcessBuilder> processBuilderProvider;
   
   @Inject
   public RakeTaskImporter(Provider<IRakeTaskParser> parserProvider, Provider<IProcessBuilder> processBuilderProvider, IConfiguration configuration) {
      rakeOsCommand = configuration.getRakeOsCommand();
      this.parserProvider = parserProvider;
      this.processBuilderProvider = processBuilderProvider;
   }

   @Override
   public SortedMap<String, RakeTask> importTasks(File root) {
      IRakeTaskParser parser = parserProvider.get();
           
      IProcessBuilder processBuilder = processBuilderProvider.get();
      try {
         IProcess process = processBuilder
            .command(rakeOsCommand,"-P")
            .directory(root)
            .outputConsumer(parser)
            .start();         
         process.waitFor();
         LOG.log(Level.INFO, "Rake Tasks Import Finished With " + process.exitValue());       
      }
      catch (IOException e) {
         LOG.log(Level.SEVERE, "Problem Importing The Rake Tasks", e);
      }
      catch (InterruptedException e) {
         LOG.log(Level.SEVERE, "Problem Importing The Rake Tasks", e);
      }
      return parser.getTasks();   
   }

}

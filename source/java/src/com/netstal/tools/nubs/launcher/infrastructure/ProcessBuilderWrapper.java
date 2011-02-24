package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ProcessBuilderWrapper implements IProcessBuilder {

   private ProcessBuilder processBuilder;
   private ILineConsumer outputConsumer;
   private ILineConsumer errorConsumer;
    
   public ProcessBuilderWrapper() {
      processBuilder = new ProcessBuilder();
   }
   
   @Override
   public IProcessBuilder command(String... command) {
      processBuilder.command(command);
      return this;
   }

   @Override
   public IProcessBuilder directory(File directory) {
      processBuilder.directory(directory);
      return this;
   }
   
   @Override
   public IProcessBuilder outputConsumer(ILineConsumer consumer) {
      outputConsumer = consumer;
      return this;
   }
   
   @Override
   public IProcessBuilder errorConsumer(ILineConsumer consumer) {
      errorConsumer = consumer;
      return this;
   }
   
   @Override
   public Map<String,String> environment() {
      return processBuilder.environment();
   }
   
   @Override
   public IProcess start() throws IOException {
      if (errorConsumer == null) {
         return new ProcessWrapper(outputConsumer, processBuilder);
      }
      
      return new ProcessWrapper(outputConsumer, errorConsumer, processBuilder);
   }
   
   
   
   
   
   
   
}

package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.IOException;
import java.io.OutputStream;

public class ProcessWrapper implements IProcess {

   private ILineConsumer outputConsumer;
   private Thread outputThread;
   private ILineConsumer errorConsumer;
   private Thread errorThread;
   private Process process;
   
   
   public ProcessWrapper(ILineConsumer outputConsumer, ProcessBuilder processBuilder) throws IOException {
      this.outputConsumer = outputConsumer;
      processBuilder.redirectErrorStream(true);
      launch(processBuilder);
   }
   
   public ProcessWrapper(ILineConsumer outputConsumer, ILineConsumer errorConsumer, ProcessBuilder processBuilder) throws IOException {
      this.outputConsumer = outputConsumer;
      this.errorConsumer = errorConsumer;
      processBuilder.redirectErrorStream(false);
      launch(processBuilder);
   }
   
   public OutputStream getOutputStream() {
      return process.getOutputStream();
   }
   
   @Override
   public int waitFor() throws InterruptedException {
      int exit = process.waitFor();
      outputThread.join();
      if (errorThread != null) {
         errorThread.join();
      }
      return exit;
   }
   
   @Override
   public int exitValue() {
      return process.exitValue();
   }
    
   private void launch(ProcessBuilder processBuilder) throws IOException {
      process = processBuilder.start();
      outputThread = new Thread(new StreamPumper(process.getInputStream(), outputConsumer));
      outputThread.start();
      if (errorConsumer != null) {
         errorThread = new Thread(new StreamPumper(process.getErrorStream(), errorConsumer));
         errorThread.start();
      }
   }

   
   
   
   
   
   
   
   
   
}

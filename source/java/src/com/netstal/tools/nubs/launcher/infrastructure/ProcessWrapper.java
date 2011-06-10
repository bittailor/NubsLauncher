package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ProcessWrapper implements IProcess {

   private static final int MAX_WAIT_FOR_STREAM = 1000;
   private ILineConsumer outputConsumer;
   private ThreadWrapper outputThread;
   private ILineConsumer errorConsumer;
   private ThreadWrapper errorThread;
   private Process process;
   private PrintStream out;
   
   public ProcessWrapper(ProcessBuilder processBuilder) throws IOException {
      this(new ILineConsumer() {
         @Override
         public void consumeLine(String line) {}
      },processBuilder);
   }
   
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
   
   @Override
   public OutputStream getOutputStream() {
      return process.getOutputStream();
   }
   
   @Override
   public int waitFor() throws InterruptedException {
      int exit = process.waitFor();
      outputThread.join(MAX_WAIT_FOR_STREAM);
      if (errorThread != null) {
         errorThread.join(MAX_WAIT_FOR_STREAM);
      }
      out.close();
      return exit;
   }
    
   @Override
   public int exitValue() {
      return process.exitValue();
   }
      
   @Override
   public PrintStream out() {
      return out;
   }

   private void launch(ProcessBuilder processBuilder) throws IOException {
      process = processBuilder.start();
      outputThread = new ThreadWrapper(new StreamPumper(process.getInputStream(), outputConsumer),"OutputPumper");
      outputThread.getWrappedThread().setDaemon(true);
      outputThread.start();
      if (errorConsumer != null) {
         errorThread = new ThreadWrapper(new StreamPumper(process.getErrorStream(), errorConsumer),"ErrorPumper");
         errorThread.getWrappedThread().setDaemon(true);
         errorThread.start();
      }
      out = new PrintStream(process.getOutputStream());
   }

   
   
   
   
   
   
   
   
   
}

package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.ILineConsumer;
import com.netstal.tools.nubs.launcher.infrastructure.IProcess;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;

public class RakeJob extends EventSource<IRakeJob> implements IRakeBuildOutputListener, IRakeJob, ILineConsumer {

   private static Logger LOG = Logger.getLogger(RakeJob.class.getName());
   
   private JobState state;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IWorkspace workspace;
   private Command command;
   private IProcess process;
   private IRakeBuildOutputParser outputParser;
   private boolean retry;
   private String currentTask;
   private File logFile;
   private PrintWriter log;
   
   
   @Inject
   public RakeJob(Provider<IProcessBuilder> processBuilderProvider, IRakeBuildOutputParser outputParser, IWorkspace workspace) {
      this.processBuilderProvider = processBuilderProvider;
      this.workspace = workspace;
      this.outputParser = outputParser;
      this.state = JobState.IDLE;
      this.currentTask = "-";
   }

   @Override
   public IRakeJob command(Command command) {
      this.command = command;
      return this;
   }
   
   @Override
   public int launch(){
      outputParser.addListener(this);
      try {
         logFile = File.createTempFile("NubsRakeJob", "Log.txt");
         logFile.deleteOnExit();
         log = new PrintWriter(logFile);
         process = processBuilderProvider.get()
            .command(command.command())
            .directory(workspace.getRoot())
            .outputConsumer(this)
            .start();
         setState(JobState.BUILDING);
         int exitValue = process.waitFor();
         LOG.log(Level.INFO, "Job finished with " + exitValue);
         if (exitValue==0) {
            setState(JobState.FINISHED_SUCESSFULLY);
         }
         else 
         {
            setState(JobState.FINISHED_FAILURE);
         }
         return exitValue;
      }
      catch (IOException e) {
         LOG.log(Level.SEVERE, "Probelm launching rake", e);
      }
      catch (InterruptedException e) {
         LOG.log(Level.SEVERE, "Probelm waiting for rake to finish", e);
      } finally {
         StreamUtility.close(log);
         log = null;
      }
      outputParser.removeListener(this);
      setState(JobState.FINISHED_EXCEPTION);
      return 1;
   }
   
   @Override
   public JobState getState() {
      return state;
   }
   
   @Override
   public String getCurrentTask() {
      return currentTask;
   }

   @Override
   public Command getCommand() {
      return command;
   }
   
   

   @Override
   public File getLogFile() {
      return logFile;
   }

   private void setState(JobState newState) {
      state = newState; 
      notifyEventListeners(this);
   }
   
   @Override
   public void retry() {
      if(isFinished()) {
         return;
      }
      setState(JobState.BUILDING);
      process.out().println("y");
      process.out().flush();   
   }
   
   

   @Override
   public void ignore() {
      if(isFinished()) {
         return;
      }
      setState(JobState.BUILDING);
      process.out().println("i");
      process.out().flush();
   }
   
   @Override
   public void fail() {
      if(isFinished()) {
         return;
      }
      setState(JobState.BUILDING);
      process.out().println("n");
      process.out().flush();
   }

   @Override
   public void notifyTaskFailed(String taskName) {
      if(retry) {
         retry();
      } else {
         setState(JobState.FAILED);   
      }
   }
   
   @Override
   public void notifyExecuteTask(String taskName) {
      currentTask = taskName;
      notifyEventListeners(this);
   }

   @Override
   public String toString() {
      return "Rake Job " + command  + " " + state;
   }

   @Override
   public void consumeLine(String line) {
      if (log != null) {
         log.println(line);
         log.flush();
      }
      outputParser.consumeLine(line);
   }
   
   @Override
   public boolean isFinished() {
      return state.isFinished();
   }

   @Override
   public void dispose() {
      if (logFile != null) {
         if (!logFile.delete()) {
            LOG.log(Level.WARNING, "Could not delete log file of rake job on dispose");
         }
      }
   }
   
   
   
}

   package com.netstal.tools.nubs.launcher.domain.job;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.EventSource;
import com.netstal.tools.nubs.launcher.domain.IRakeBuildOutputListener;
import com.netstal.tools.nubs.launcher.domain.IRakeBuildOutputParser;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobState;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;
import com.netstal.tools.nubs.launcher.infrastructure.ILineConsumer;
import com.netstal.tools.nubs.launcher.infrastructure.IProcess;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;

public class RakeJob extends EventSource<IRakeJob> implements IRakeBuildOutputListener, IRakeJob, ILineConsumer {

   private static Logger LOG = Logger.getLogger(RakeJob.class.getName());
   
   private IJobState state;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IWorkspace workspace;
   private Command command;
   private IProcess process;
   private IRakeBuildOutputParser outputParser;
   private IRakeLauncher launcher;
   private boolean retry;
   private String currentTask;
   private File logFile;
   private PrintWriter log;

   
   
   @Inject
   public RakeJob(
            Provider<IProcessBuilder> processBuilderProvider, 
            IRakeBuildOutputParser outputParser, 
            IWorkspace workspace , 
            IRakeLauncher launcher) {
      this.processBuilderProvider = processBuilderProvider;
      this.workspace = workspace;
      this.outputParser = outputParser;
      this.launcher = launcher;
      this.state = Idle.INSTANCE;
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
         setState(Building.INSTANCE);
         int exitValue = process.waitFor();
         LOG.log(Level.INFO, "Job finished with " + exitValue);
         if (exitValue==0) {
            setState(FinishedSucessfully.INSTANCE);
         }
         else 
         {
            setState(FinishedFaultily.INSTANCE);
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
      setState(FinishedExceptionally.INSTANCE);
      return 1;
   }
   
   @Override
   public IJobState getState() {
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

   private void setState(IJobState newState) {
      state = newState; 
      notifyEventListeners(this);
   }
   
   @Override
   public void retry() {
      if(isFinished()) {
         return;
      }
      setState(Building.INSTANCE);
      process.out().println("y");
      process.out().flush();   
   }
   
   

   @Override
   public void ignore() {
      if(isFinished()) {
         return;
      }
      setState(Building.INSTANCE);
      process.out().println("i");
      process.out().flush();
   }
   
   @Override
   public void fail() {
      if(isFinished()) {
         return;
      }
      setState(Building.INSTANCE);
      process.out().println("n");
      process.out().flush();
   }

   @Override
   public void notifyTaskFailed(String taskName) {
      if(retry) {
         retry();
      } else {
         setState(Failed.INSTANCE);   
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

   @Override
   public void relaunch() throws IOException {
      launcher.launch(command);
   }
   
   
   
}

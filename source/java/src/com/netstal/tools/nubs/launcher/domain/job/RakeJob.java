   package com.netstal.tools.nubs.launcher.domain.job;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.EventSource;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IEventSource;
import com.netstal.tools.nubs.launcher.domain.IRakeBuildOutputListener;
import com.netstal.tools.nubs.launcher.domain.IRakeBuildOutputParser;
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
import com.netstal.tools.nubs.launcher.infrastructure.RingBuffer;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;

public class RakeJob extends EventSource<IRakeJob.Event> implements IRakeBuildOutputListener, IRakeJob, ILineConsumer {

   private static Logger LOG = Logger.getLogger(RakeJob.class.getName());
   
   private IJobState state;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IWorkspace workspace;
   private Command command;
   private IProcess process;
   private IRakeBuildOutputParser outputParser;
   private AtomicInteger totalNumberOfRetries;
   private boolean autoRetry;
   private AtomicInteger currentNumberOfAutoRetries;
   final private int maximumNumberOfAutoRetries;
   private String currentTask;
   private File logFile;
   private PrintWriter log;
   private RingBuffer<String> tailLog;
   private EventSource<Collection<String>> tailLogEventSource;
   private boolean isDisposed;

   
   
   @Inject
   public RakeJob(
            Provider<IProcessBuilder> processBuilderProvider, 
            IRakeBuildOutputParser outputParser, 
            IWorkspace workspace, 
            IConfiguration configuration) {
      this.processBuilderProvider = processBuilderProvider;
      this.workspace = workspace;
      this.outputParser = outputParser;
      this.totalNumberOfRetries = new AtomicInteger();
      this.autoRetry = false;
      this.currentNumberOfAutoRetries = new AtomicInteger();
      this.maximumNumberOfAutoRetries = configuration.getInteger("job.MaximumNumberOfAutoRetries");
      this.state = Idle.INSTANCE;
      this.currentTask = "-";
      this.tailLog = new RingBuffer<String>(configuration.getInteger("job.TailSize"));
      this.tailLogEventSource = new EventSource<Collection<String>>();
      this.isDisposed = false;
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
      notifyEventListeners(stateChangeEvent());
   }

   @Override
   public void retry() {
      currentNumberOfAutoRetries.set(0);
      sendRetry();   
   }
   
   public void sendRetry() {
      if(isFinished()) {
         return;
      }
      totalNumberOfRetries.incrementAndGet();
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
      if(!autoRetry()) {
         setState(Failed.INSTANCE);   
      }
   }
   
   private boolean autoRetry() {
      if (!autoRetry) {
         return false;
      }
       
      if (currentNumberOfAutoRetries.incrementAndGet() > maximumNumberOfAutoRetries ) {
         return false;
      }
      
      notifyEventListeners(event());
      sendRetry();
      return true;
   }

   

   @Override
   public void notifyExecuteTask(String taskName) {
      if (currentTask.equals(taskName)) {
         return;
      }
      
      currentTask = taskName;
      currentNumberOfAutoRetries.set(0);
      notifyEventListeners(event());
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
      tailLog.add(line);
      tailLogEventSource.notifyEventListeners(getTailLog());
   }
   
   @Override
   public boolean isFinished() {
      return state.isFinished();
   }
   
   @Override
   public boolean isDisposed() {
      return isDisposed;
   }
   
   @Override
   public int getTotalNumberOfRetries() {
      return totalNumberOfRetries.get();
   }

   @Override
   public boolean isAutoRetry() {
      return autoRetry;
   }

   @Override
   public void setAutoRetry(boolean autoRetry) {
      this.autoRetry = autoRetry;
      currentNumberOfAutoRetries.set(0);
      notifyEventListeners(event());
   }

   @Override
   public int getCurrentNumberOfAutoRetries() {
      return currentNumberOfAutoRetries.get();
   }

   @Override
   public int getMaximumNumberOfAutoRetries() {
      if(autoRetry) {
         return maximumNumberOfAutoRetries;
      }
      return 0;
   }

   @Override
   public void dispose() {
      if (logFile != null) {
         if (!logFile.delete()) {
            LOG.log(Level.WARNING, "Could not delete log file of rake job on dispose");
         }
      }
      isDisposed = true;
      notifyEventListeners(event());
   }
 
   @Override
   public Collection<String> getTailLog() {
      return Collections.unmodifiableCollection(tailLog);
   }
   

   @Override
   public IEventSource<Collection<String>> getTailLogEventSource() {
      return tailLogEventSource;
   }

   private Event event() {
      return new Event(this, false);
   }
   
   private Event stateChangeEvent() {
      return new Event(this,true);
   }

   
   
  
   
   
   
   
}

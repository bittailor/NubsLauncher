package com.netstal.tools.nubs.launcher.domain;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.IProcess;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;

public class RakeJob extends EventSource<IRakeJob> implements IRakeBuildOutputListener, IRakeJob {

   private static Logger LOG = Logger.getLogger(RakeJob.class.getName());
   
   private State state;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IWorkspace workspace;
   private Command command;
   private IProcess process;
   private IRakeBuildOutputParser outputParser;
   private boolean retry;
   private String currentTask;
   
   
   @Inject
   public RakeJob(Provider<IProcessBuilder> processBuilderProvider, IRakeBuildOutputParser outputParser, IWorkspace workspace) {
      this.processBuilderProvider = processBuilderProvider;
      this.workspace = workspace;
      this.outputParser = outputParser;
      this.state = State.IDLE;
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
         process = processBuilderProvider.get()
            .command(command.command())
            .directory(workspace.getRoot())
            .outputConsumer(outputParser)
            .start();
         setState(State.BUILDING);
         int exitValue = process.waitFor();
         LOG.log(Level.INFO, "Job finished with " + exitValue);
         if (exitValue==0) {
            setState(State.FINISHED_SUCESSFULLY);
         }
         else 
         {
            setState(State.FINISHED_FAILURE);
         }
         return exitValue;
      }
      catch (IOException e) {
         LOG.log(Level.SEVERE, "Probelm launching rake", e);
      }
      catch (InterruptedException e) {
         LOG.log(Level.SEVERE, "Probelm waiting for rake to finish", e);
      }
      outputParser.removeListener(this);
      setState(State.FINISHED_EXCEPTION);
      return 1;
   }
   
   @Override
   public State getState() {
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

   private void setState(State newState) {
      state = newState; 
      notifyEventListeners(this);
   }
   
   @Override
   public void retry() {
      setState(State.BUILDING);
      process.out().println("y");
      process.out().flush();   
   }
   
   @Override
   public void ignore() {
      setState(State.BUILDING);
      process.out().println("i");
      process.out().flush();
   }
   
   @Override
   public void fail() {
      setState(State.BUILDING);
      process.out().println("n");
      process.out().flush();
   }

   @Override
   public void notifyTaskFailed(String taskName) {
      if(retry) {
         retry();
      } else {
         setState(State.FAILED);   
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
   
}

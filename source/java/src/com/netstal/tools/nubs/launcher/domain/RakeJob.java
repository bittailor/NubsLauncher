package com.netstal.tools.nubs.launcher.domain;

import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.IProcess;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;

public class RakeJob implements IRakeBuildOutputListener {

   public static enum State {IDLE,BUILDING,FAILED,FINISHED};
   
   private State state;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IWorkspace workspace;
   private Command command;
   private IProcess process;
   private IRakeBuildOutputParser outputParser;
   private List<IRakeJobListener> listeners;
   private boolean retry;
   
   
   
   public RakeJob(Provider<IProcessBuilder> processBuilderProvider,IRakeBuildOutputParser outputParser, IWorkspace workspace) {
      this.processBuilderProvider = processBuilderProvider;
      this.workspace = workspace;
      this.outputParser = outputParser;
      this.listeners = new LinkedList<IRakeJobListener>();
   }

   public void command(Command command) {
      this.command = command;  
   }
   
   public int launch() throws IOException, InterruptedException {
      process = processBuilderProvider.get()
         .command(command.rakeCmdLine())
         .directory(workspace.getRoot())
         .outputConsumer(outputParser)
         .start();
      int exitValue = process.waitFor();
      if (exitValue==0) {
         setState(State.FINISHED);
      }
      else 
      {
         setState(State.FAILED);
      }
      return exitValue;
   }
   
   public State getState() {
      return state;
   }

   private void setState(State newState) {
      state = newState; 
      for (IRakeJobListener listener : listeners) {
         listener.notifyState(state);
      }
   }
   
   private void retry(String taskName) {
      PrintStream out = new PrintStream(process.getOutputStream());
      out.println("y");
      out.flush();
   }

   @Override
   public void notifyTaskFailed(String taskName) {
      if(retry) {
         retry(taskName);
      } else {
         for (IRakeJobListener listener : listeners) {
            listener.notifyTaskFailed(taskName);
         }   
      }
   }
   
   @Override
   public void notifyExecuteTask(String taskName) {
      for (IRakeJobListener listener : listeners) {
         listener.notifyExecuteTask(taskName);
      }    
   }
   
}

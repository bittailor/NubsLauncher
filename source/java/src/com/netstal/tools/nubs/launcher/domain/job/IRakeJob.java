package com.netstal.tools.nubs.launcher.domain.job;

import java.io.File;
import java.util.Collection;

import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.IEventSource;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobState;


public interface IRakeJob extends IEventSource<IRakeJob.Event> {
   
   static class Event {
         
      public final IRakeJob job;
         public final boolean stateChanged;
         
         public Event(IRakeJob job, boolean stateChanged) {
            super();
            this.job = job;
            this.stateChanged = stateChanged;
         } 
   }
   
   public IRakeJob command(Command command);

   public int launch();
   public void dispose();
   
   public IJobState getState();
   public String getCurrentTask();
   public int getTaskCounter();
   public int getFinalTaskCount();
   public Command getCommand();
   public File getLogFile();
   public boolean isFinished();
   public boolean isDisposed();
   
   public Collection<String> getTailLog();
   public IEventSource<Collection<String>> getTailLogEventSource();

   public void retry();
   public void ignore();
   public void fail();

   public int getRetryCounter();
   public int getMaximumNumberOfAutoRetries();
   public int getAutoRetryCounter();
   public void setAutoRetry(boolean autoRetry);
   public boolean isAutoRetry();


}

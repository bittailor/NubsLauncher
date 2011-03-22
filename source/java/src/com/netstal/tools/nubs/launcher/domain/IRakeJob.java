
package com.netstal.tools.nubs.launcher.domain;

import java.io.File;


public interface IRakeJob extends IEventSource<IRakeJob> {
   
   public IRakeJob command(Command command);

   public int launch();
   public void dispose();
   
   public JobState getState();
   public String getCurrentTask();
   public Command getCommand();
   public File getLogFile();
   public boolean isFinished();

   public void retry();
   public void ignore();
   public void fail();
   

}

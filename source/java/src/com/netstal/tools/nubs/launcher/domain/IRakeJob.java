
package com.netstal.tools.nubs.launcher.domain;

import java.io.File;


public interface IRakeJob extends IEventSource<IRakeJob> {
   
   public static enum State {
      IDLE,
      BUILDING,
      FAILED,
      FINISHED_SUCESSFULLY,
      FINISHED_FAILURE,
      FINISHED_EXCEPTION
   };

   public IRakeJob command(Command command);

   public int launch();
   public void dispose();
   
   public State getState();
   public String getCurrentTask();
   public Command getCommand();
   public File getLogFile();
   public boolean isFinished();

   public void retry();
   public void ignore();
   public void fail();
   

}

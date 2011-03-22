
package com.netstal.tools.nubs.launcher.domain.job;

import java.io.File;

import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.IEventSource;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobState;


public interface IRakeJob extends IEventSource<IRakeJob> {
   
   public IRakeJob command(Command command);

   public int launch();
   public void dispose();
   
   public IJobState getState();
   public String getCurrentTask();
   public Command getCommand();
   public File getLogFile();
   public boolean isFinished();

   public void retry();
   public void ignore();
   public void fail();
   

}

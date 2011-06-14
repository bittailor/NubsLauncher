package com.netstal.tools.nubs.launcher.domain;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;




public interface IRakeLauncher {

   public void launch(Command command);
   public void relaunch(IRakeJob job);

}

package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class RelaunchAction extends AbstractFinishedJobDependentAction {
   private static final long serialVersionUID = 1L;

   private IRakeLauncher launcher;
     
   public RelaunchAction(IRakeLauncher launcher) {
      super("Relaunch",new ImageIcon(NubsLauncherFrame.class.getResource("images/Launch.png")));
      this.launcher = launcher;
      this.putValue(SHORT_DESCRIPTION, "Launch This Job Again");
   }
   
   @Override
   protected void actionPerformed(IRakeJob job) {
      launcher.relaunch(job);           
   }

}
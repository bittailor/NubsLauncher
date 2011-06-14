package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class RetryAction extends AbstractFailedJobDependentAction {
   private static final long serialVersionUID = 1L;

   public RetryAction() {
      super("Retry",new ImageIcon(NubsLauncherFrame.class.getResource("images/RetryFw.png")));
      this.putValue(SHORT_DESCRIPTION, "Retry The Failed Task");
   }
   
   @Override
   protected void actionPerformed(IRakeJob job) {
      job.retry();          
   }
      
}
package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class ToggleAutoRetryAction extends AbstractJobDependentAction {
   private static final long serialVersionUID = 1L;

   public ToggleAutoRetryAction() {
      super("AutoRetry",new ImageIcon(NubsLauncherFrame.class.getResource("images/AutoRetryFw.png")));
      this.putValue(SHORT_DESCRIPTION, "Toggle Auto Retry");
   }
   
   @Override
   protected void actionPerformed(IRakeJob job) {
      job.setAutoRetry(!job.isAutoRetry());           
   }
       
}
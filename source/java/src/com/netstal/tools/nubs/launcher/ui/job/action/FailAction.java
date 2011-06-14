package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class FailAction extends AbstractFailedJobDependentAction {
   private static final long serialVersionUID = 1L;

   public FailAction() {
      super("Fail",new ImageIcon(NubsLauncherFrame.class.getResource("images/FailFw.png")));
      this.putValue(SHORT_DESCRIPTION, "Quit The Job");
   }
   
   @Override
   protected void actionPerformed(IRakeJob job) {
      job.fail();           
   }  
       
}
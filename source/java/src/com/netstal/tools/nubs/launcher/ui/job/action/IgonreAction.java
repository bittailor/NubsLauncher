package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class IgonreAction extends AbstractFailedJobDependentAction {
   private static final long serialVersionUID = 1L;

   public IgonreAction() {
      super("Ignore",new ImageIcon(NubsLauncherFrame.class.getResource("images/IgnoreFw.png")));
      this.putValue(SHORT_DESCRIPTION, "Ignore The Failed Task And Continue");
   }
   
   @Override
   protected void actionPerformed(IRakeJob job) {
      job.ignore();           
   }     
}
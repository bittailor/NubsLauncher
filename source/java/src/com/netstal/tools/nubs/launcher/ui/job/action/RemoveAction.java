package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class RemoveAction extends AbstractFinishedJobDependentAction {
   private static final long serialVersionUID = 1L;

   private IRakeJobRepository rakeJobRepository;
   
   public RemoveAction(IRakeJobRepository rakeJobRepository) {
      super("Remove",new ImageIcon(NubsLauncherFrame.class.getResource("images/Remove.gif")));
      this.rakeJobRepository = rakeJobRepository;
      this.putValue(SHORT_DESCRIPTION, "Remove This Job");
   }
   
   @Override
   protected void actionPerformed(IRakeJob job) {
      rakeJobRepository.clear(job);           
   }
          
}
package com.netstal.tools.nubs.launcher.ui.job.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class RemoveAllFinishedAction extends AbstractAction {
   private static final long serialVersionUID = 1L;

   private IRakeJobRepository rakeJobRepository;
   
   public RemoveAllFinishedAction(IRakeJobRepository rakeJobRepository) {
      super("Remove All Finished Jobs",new ImageIcon(NubsLauncherFrame.class.getResource("images/RemoveAll.gif")));
      this.rakeJobRepository = rakeJobRepository;
      this.putValue(SHORT_DESCRIPTION, "Remove All Finished Jobs");
   }
   
   @Override
   public void actionPerformed(ActionEvent e) {
      rakeJobRepository.clearFinished();
   }     
}
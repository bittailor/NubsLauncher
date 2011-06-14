package com.netstal.tools.nubs.launcher.ui.job.action;

import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class OpenLogAction extends AbstractJobDependentAction {
   
   private static Logger LOG = Logger.getLogger(OpenLogAction.class.getName()); 
   private static final long serialVersionUID = 1L;
   
   public static void openLogfile(IRakeJob job) {
      Desktop desktop = Desktop.getDesktop();
      try {
         desktop.open(job.getLogFile());
      }
      catch (Exception exception) {
         LOG.log(Level.WARNING, "Could Not Launch log file editor", exception);
      }
   }
   
   public OpenLogAction() {
      super("Open Log",new ImageIcon(NubsLauncherFrame.class.getResource("images/OpenLog.gif")));
      this.putValue(SHORT_DESCRIPTION, "Open The Log File");
   }

   @Override
   protected void actionPerformed(IRakeJob job) {
      openLogfile(job);         
   }
      
}

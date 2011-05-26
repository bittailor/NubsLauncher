package com.netstal.tools.nubs.launcher.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;
import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobStateVisitor;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;

public class TrayNotification implements INotification, IEventListener<IRakeJob>  {

   private static Logger LOG = Logger.getLogger(TrayNotification.class.getName());
   private TrayIcon trayIcon;
   private IRakeJobRepository repository;

   @Inject
   public TrayNotification(IRakeJobRepository repository) {      
      this.repository = repository;
      createTrayIcon();
      this.repository.getJobsEventSource().addListenerNotifyInSwingDispatchThread(this);          
   }

   private void createTrayIcon() {
      SystemTray tray = SystemTray.getSystemTray();
      Image image = new ImageIcon(NubsLauncherFrame.class.getResource("images/RocketSmall.png")).getImage();
      trayIcon = new TrayIcon(image, "Nubs Launcher");
      LOG.log(Level.INFO, "Icon Size "+trayIcon.getSize().toString());
      // ...
      // add the tray image
      try {
         tray.add(trayIcon);
      } catch (AWTException e) {
         LOG.log(Level.SEVERE, "Could not add tray icon", e);
      }
   }

   @Override
   public void notifyEvent(final IRakeJob job) {
      if (job.isDisposed()) {
         return;
      }
      
      job.getState().accept(new IJobStateVisitor() {

         @Override
         public void visit(FinishedExceptionally state) {
            trayIcon.displayMessage("NUBS", "Build finished with a exeption", TrayIcon.MessageType.WARNING);        
         }

         @Override
         public void visit(FinishedFaultily state) {
            trayIcon.displayMessage("NUBS", "Build finished with a failure", TrayIcon.MessageType.WARNING);       
         }

         @Override
         public void visit(FinishedSucessfully state) {
            trayIcon.displayMessage("NUBS", "Build finished sucessfully", TrayIcon.MessageType.INFO);         
         }

         @Override
         public void visit(Failed state) {
            trayIcon.displayMessage("NUBS", "Build failed at task " + job.getCurrentTask(), TrayIcon.MessageType.ERROR);
         }

         @Override
         public void visit(Building state) {
         }

         @Override
         public void visit(Idle state) {
         }

      });

   }  
}

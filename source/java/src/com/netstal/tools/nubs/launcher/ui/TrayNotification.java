package com.netstal.tools.nubs.launcher.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.IRakeJobRepository;

public class TrayNotification implements INotification, IEventListener<IRakeJob>  {

   private static Logger LOG = Logger.getLogger(TrayNotification.class.getName());
   private TrayIcon trayIcon;
   private IRakeJobRepository repository;

   @Inject
   public TrayNotification(IRakeJobRepository repository) {      
      this.repository = repository;
      repository.getJobsEventSource().addListener(this);
      
      createTrayIcon();
     
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
   public void notifyEvent(IRakeJob job) {
      /*
      if (job.getState().equals(IRakeJob.State.FAILED)) {
         
      }
      */
      
      
   }
   
   
   
   
}

package com.netstal.tools.nubs.launcher.application;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;
import com.netstal.tools.nubs.launcher.infrastructure.ProcessOutputCapture;
import com.netstal.tools.nubs.launcher.infrastructure.Version;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class SelfUpdate {
   
   private static Logger LOG = Logger.getLogger(SelfUpdate.class.getName());
   
   static final String SERVER_LOCATION = "update.ServerLocation";
   static final String DISABLE_UPDATE = "update.DisableUpdate";
   static final String ENABLE_AUTO_UPDATE = "update.EnableAutoUpdate";
   
   IConfiguration configuration;
   Provider<ProcessOutputCapture> outputGrabberProvider;
   Provider<IProcessBuilder> processBuilderProvider;

   @Inject
   public SelfUpdate(IConfiguration configuration, Provider<ProcessOutputCapture> outputGrabberProvider, Provider<IProcessBuilder> processBuilderProvider) {
      this.configuration = configuration;
      this.outputGrabberProvider = outputGrabberProvider;
      this.processBuilderProvider = processBuilderProvider;
   }
   
   public boolean selfUpdate() {   
      if (checkIfUpdatePossible()) {
         if(checkIfUpdateWanted()){
            return launchSelfUpdate();
         }
      }
      return false;
   }

   private boolean checkIfUpdatePossible() {
      if (configuration.getFlag(DISABLE_UPDATE)) {
         LOG.log(Level.CONFIG ,"Update is disabled");
         return false;
      }
      
      File serverPath = getServerDirectory();
      if(!serverPath.exists()){
         LOG.log(Level.INFO ,"Update server directory (" + serverPath.getAbsolutePath() + ") does not exist");
         return false;
      }
      
      String ownVersionString = Version.getVersion();
      
      List<String> outputLines = outputGrabberProvider.get().grab("java", "-jar", serverPath + "\\nubs.exe","--version");
      if (outputLines.isEmpty()) {
         LOG.log(Level.WARNING, "Could not read version of nubs on server");
         return false;
      }
      
      String serverVersionString = outputLines.get(0).trim();
      LOG.log(Level.INFO, "Server Version String is " + serverVersionString);
      LOG.log(Level.INFO, "Own Version String is    " + ownVersionString);
      
      int ownVersion = 0;
      if (!ownVersionString.equals(Version.LOCAL_BUILD)) {
         try {
            ownVersion = Integer.parseInt(ownVersionString);
         } catch (NumberFormatException exception) {
            LOG.log(Level.WARNING, "Could not parse own version " + ownVersion);
            return true;
         }
      }
      
      int serverVersion = 0; 
      try {         
         serverVersion = Integer.parseInt(serverVersionString);
      } catch (NumberFormatException e) {
         LOG.log(Level.WARNING, "Could not parse server version " + serverVersionString);
         return false;
      }      
      
      return serverVersion > ownVersion;
   }
   
   private File getServerDirectory() {
      return new File(configuration.get(SERVER_LOCATION));
   }
   
   private boolean checkIfUpdateWanted() {
      if (configuration.getFlag(ENABLE_AUTO_UPDATE)) {
         return true;
      }
      
      return askIfUpdateWanted();    
   }

   private boolean askIfUpdateWanted() {
      Object[] options = {"Update","Skip"};
      int option = JOptionPane.showOptionDialog(null,
               "New Version Available.",
               "Update NUBS Launcher",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               new ImageIcon(NubsLauncherFrame.class.getResource("images/Rocket.png")),
               options,
               options[0]);
      return option==0; 
   }

   private boolean launchSelfUpdate() {
      try {
         processBuilderProvider.get()
            .command("cmd","/C","start","cmd","/C","ruby.exe " + new File(getServerDirectory(),"install.rb").getAbsolutePath(),"-u")
            .start();
         return true;
      }
      catch (IOException exception) {
         LOG.log(Level.SEVERE, "Failed To Launch Self Update", exception);
      }
      return false;
   }

}

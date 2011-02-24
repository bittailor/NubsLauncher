package com.netstal.tools.nubs.launcher.application;

import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

import com.google.inject.*;
import com.netstal.tools.nubs.launcher.domain.*;
import com.netstal.tools.nubs.launcher.infrastructure.*;
import com.netstal.tools.nubs.launcher.ui.*;

public class SelfUpdate {
   
   private static Logger LOG = Logger.getLogger(SelfUpdate.class.getName());
   
   static final String SERVER_LOCATION = "update.ServerLocation";
   static final String DISABLE_UPDATE = "update.DisableUpdate";
   static final String ENABLE_AUTO_UPDATE = "update.EnableAutoUpdate";
   
   IConfiguration configuration;
   
   @Inject
   public SelfUpdate(IConfiguration configuration) {
      this.configuration = configuration;
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
      
      try {
         LineConsumer lineConsumer = new LineConsumer();
         String cmd = "java -jar " + serverPath + "\\nubs.exe --version";
         Process process = Runtime.getRuntime().exec(cmd);
         new Thread(new StreamPumper(process.getInputStream(),lineConsumer)).start();
         process.waitFor();
         if(lineConsumer.lines.isEmpty()) {
            return false;
         }
         String serverVersionString = lineConsumer.lines.get(0);
         int ownVersion = 0;
         try {
            ownVersion = Integer.parseInt(ownVersionString);
         } catch (NumberFormatException exception) {}
         int serverVersion = Integer.parseInt(serverVersionString);
         return serverVersion > ownVersion;
         
      } catch (NumberFormatException e) {
      }
      catch (IOException e) {
         LOG.log(Level.WARNING, "Problem Parsing Version Number", e);
      }
      catch (InterruptedException e) {
         LOG.log(Level.WARNING, "Problem Parsing Version Number", e);
      }
      
      return false;
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
         ProcessBuilder processBuilder = new ProcessBuilder("cmd","/C","start","cmd","/C",new File(getServerDirectory(),"install.rb").getAbsolutePath(),"-u");
         processBuilder.start();
         return true;
      }
      catch (IOException exception) {
         LOG.log(Level.SEVERE, "Failed To Launch Self Update", exception);
      }
      return false;
   }
   
   
   private static class LineConsumer implements ILineConsumer {        
      List<String> lines = new LinkedList<String>();    
      @Override
      public void consumeLine(String line) {
         if(!line.isEmpty()) {
            lines.add(line);
         }
      }
   }
}

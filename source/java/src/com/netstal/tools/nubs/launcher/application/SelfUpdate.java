package com.netstal.tools.nubs.launcher.application;

import java.io.*;
import java.util.*;

import javax.swing.*;

import com.google.inject.*;
import com.netstal.tools.nubs.launcher.domain.*;
import com.netstal.tools.nubs.launcher.infrastructure.*;
import com.netstal.tools.nubs.launcher.ui.*;

public class SelfUpdate {
   
   IConfiguration configuration;
   
   @Inject
   public SelfUpdate(IConfiguration configuration) {
      this.configuration = configuration;
   }

   public boolean checkIfUpdatePossible() {
      String ownVersionString = configuration.getVersion();
      File serverPath = new File(configuration.getInstallationServerPath());
      if(!serverPath.exists()){
         return false;
      }
      try {
         LineConsumer lineConsumer = new LineConsumer();
         String cmd = "java -jar " + serverPath + "\\nubs.exe --version";
         System.out.println(cmd);
         Process process = Runtime.getRuntime().exec(cmd);
         new Thread(new StreamPumper(process.getInputStream(),lineConsumer)).start();
         process.waitFor();
         System.out.println(process.exitValue());
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
         e.printStackTrace();
      }
      catch (InterruptedException e) {
         e.printStackTrace();
      }
      
      return false;
   }
   
   public boolean selfUpdate() {   
      if(checkIfUpdatePossible()){
         Object[] options = {"Update","Skip"};
         int option = JOptionPane.showOptionDialog(null,
                  "New Version Available.",
                  "Update NUBS Launcher",
                  JOptionPane.YES_NO_OPTION,
                  JOptionPane.INFORMATION_MESSAGE,
                  new ImageIcon(NubsLauncherFrame.class.getResource("images/Rocket.png")),
                  options,
                  options[0]);
         if(option==0){
            return launchSelfUpdate();
         }
      }
      return false;
   }

   private boolean launchSelfUpdate() {
      try {
         ProcessBuilder processBuilder = new ProcessBuilder("cmd","/C","start","cmd","/C",new File(configuration.getInstallationServerPath(),"install.rb").getAbsolutePath(),"-u");
         processBuilder.start();
         return true;
      }
      catch (IOException exception) {
         exception.printStackTrace();
      }
      return false;
   }
   
   
   private static class LineConsumer implements I_LineConsumer {        
      List<String> lines = new LinkedList<String>();    
      @Override
      public void consumeLine(String line) {
         if(!line.isEmpty()) {
            lines.add(line);
         }
      }
   }
}

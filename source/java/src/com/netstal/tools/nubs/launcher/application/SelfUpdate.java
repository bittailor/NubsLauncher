package com.netstal.tools.nubs.launcher.application;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.infrastructure.IConsoleLauncher;
import com.netstal.tools.nubs.launcher.infrastructure.I_LineConsumer;
import com.netstal.tools.nubs.launcher.infrastructure.StreamPumper;

public class SelfUpdate {
   
   IConfiguration configuration;
   IConsoleLauncher consoleLauncher;
   
   @Inject
   public SelfUpdate(IConfiguration configuration, IConsoleLauncher consoleLauncher) {
      this.configuration = configuration;
      this.consoleLauncher = consoleLauncher;
   }

   public boolean checkIfUpdatePossible() {
      String ownVersionString = configuration.getVersion();
      File serverPath = new File(configuration.getInstallationServerPath());
      if(!serverPath.exists()){
         return false;
      }
      try {
         Process process = Runtime.getRuntime().exec("java -jar " + serverPath + "--version");
         LineConsumer lineConsumer = new LineConsumer();
         new Thread(new StreamPumper(process.getInputStream(),lineConsumer)).start();
         process.waitFor();
         if(lineConsumer.lines.isEmpty()) {
            return false;
         }
         String serverVersionString = lineConsumer.lines.get(0);
         int ownVersion = Integer.parseInt(ownVersionString);
         int serverVersion = Integer.parseInt(serverVersionString);
         return serverVersion > ownVersion;
         
      }
      catch (Exception e) {}
      return false;
   }
   
   public void selfUpdate() {
      try {
         consoleLauncher.launch("install.bat -u", new File(System.getProperty("user.dir")));
         System.exit(0);
      }
      catch (IOException exception) {
         exception.printStackTrace();
      }
   }
   
   
   private class LineConsumer implements I_LineConsumer {        
      List<String> lines = new LinkedList<String>();    
      @Override
      public void consumeLine(String line) {
         if(!line.isEmpty()) {
            lines.add(line);
         }
      }
   }
}

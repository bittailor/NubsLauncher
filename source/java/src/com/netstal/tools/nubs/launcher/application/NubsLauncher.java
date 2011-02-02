package com.netstal.tools.nubs.launcher.application;

import java.io.File;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.netstal.tools.nubs.launcher.domain.CommandHistory;
import com.netstal.tools.nubs.launcher.domain.FilterChain;
import com.netstal.tools.nubs.launcher.domain.ICommandHistory;
import com.netstal.tools.nubs.launcher.domain.IFilterChain;
import com.netstal.tools.nubs.launcher.domain.IRakeTaskImporter;
import com.netstal.tools.nubs.launcher.domain.IRakeTaskParser;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.domain.RakeTaskImporter;
import com.netstal.tools.nubs.launcher.domain.RakeTaskParser;
import com.netstal.tools.nubs.launcher.domain.Workspace;
import com.netstal.tools.nubs.launcher.ui.LoadTasksPanel;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;

public class NubsLauncher {

   public NubsLauncher() {
      Injector injector = createInjector();
      String userDirectory = System.getProperty("user.dir");
      NubsLauncherFrame mainFrame = injector.getInstance(NubsLauncherFrame.class);      
      mainFrame.setVisible(true);
      if(new File(userDirectory,"rakefile").exists()) {
         mainFrame.changeWorkspace(new File(userDirectory));
      } else {
         mainFrame.selectWorkspaceDirectory();
      }   
   }
   
   private Injector createInjector() {
      Injector injector = Guice.createInjector(new AbstractModule(){
         @Override
         protected void configure() {
            bind(IRakeTaskParser.class).to(RakeTaskParser.class);
            bind(IRakeTaskImporter.class).to(RakeTaskImporter.class);
            bind(IWorkspace.class).to(Workspace.class).in(Singleton.class) ; 
            bind(IFilterChain.class).to(FilterChain.class).in(Singleton.class);
            bind(ICommandHistory.class).to(CommandHistory.class).in(Singleton.class);
            bind(LoadTasksPanel.class); 
            bind(NubsLauncherFrame.class);  
         }   
      });
      return injector;
   }
   
   public static void main(final String[] args) {
      if(args.length==1 && args[0].equals("--version")) {
         showVersion();
         return;
      }
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (ClassNotFoundException exception) {
         exception.printStackTrace();
      }
      catch (InstantiationException exception) {
         exception.printStackTrace();
      }
      catch (IllegalAccessException exception) {
         exception.printStackTrace();
      }
      catch (UnsupportedLookAndFeelException exception) {
         exception.printStackTrace();
      }
      
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new NubsLauncher();
         }
      });
   }

   private static void showVersion() {
      String implementationVersion = NubsLauncher.class.getPackage().getImplementationVersion();
      if (implementationVersion == null) {
         implementationVersion = "local build";
      }
      System.out.println(implementationVersion);   
      return;
   }
   
}

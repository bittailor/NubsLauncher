package com.netstal.tools.nubs.launcher.application;

import java.io.File;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.netstal.tools.nubs.launcher.domain.CommandHistory;
import com.netstal.tools.nubs.launcher.domain.Configuration;
import com.netstal.tools.nubs.launcher.domain.ConsoleRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.FilterChain;
import com.netstal.tools.nubs.launcher.domain.ICommandHistory;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IFilterChain;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.IRakeTaskImporter;
import com.netstal.tools.nubs.launcher.domain.IRakeTaskParser;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.domain.RakeTaskImporter;
import com.netstal.tools.nubs.launcher.domain.RakeTaskParser;
import com.netstal.tools.nubs.launcher.domain.Workspace;
import com.netstal.tools.nubs.launcher.infrastructure.ConsoleLauncher;
import com.netstal.tools.nubs.launcher.infrastructure.IConsoleLauncher;
import com.netstal.tools.nubs.launcher.infrastructure.Version;
import com.netstal.tools.nubs.launcher.ui.LoadTasksPanel;
import com.netstal.tools.nubs.launcher.ui.NubsLauncherFrame;
import com.netstal.tools.nubs.launcher.ui.RakeTasksField;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.CommandHistoryTool;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.IToolsFactory;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.LaunchRakeTool;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.SuggestTaskTool;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.ToolsFactory;

public class NubsLauncher {
   
   private static Logger LOG = Logger.getLogger(NubsLauncher.class.getName());

   public NubsLauncher() {
      Injector injector = createInjector();
      
      SelfUpdate selfUpdate = injector.getInstance(SelfUpdate.class);
      if(selfUpdate.selfUpdate()) {
         return;
      }
      
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
            bind(IConsoleLauncher.class).to(ConsoleLauncher.class);
            
            bind(IRakeLauncher.class).to(ConsoleRakeLauncher.class);
            bind(IRakeTaskParser.class).to(RakeTaskParser.class);
            bind(IRakeTaskImporter.class).to(RakeTaskImporter.class);
            bind(IWorkspace.class).to(Workspace.class).in(Singleton.class) ; 
            bind(IFilterChain.class).to(FilterChain.class).in(Singleton.class);
            bind(ICommandHistory.class).to(CommandHistory.class).in(Singleton.class);
            bind(IToolsFactory.class).to(ToolsFactory.class).in(Singleton.class);
            bind(IConfiguration.class).to(Configuration.class).in(Singleton.class);
            bind(RakeTasksField.class).in(Singleton.class); 
            bind(NubsLauncherFrame.class).in(Singleton.class);  
            bind(LoadTasksPanel.class); 
            bind(SuggestTaskTool.class);
            bind(CommandHistoryTool.class);
            bind(LaunchRakeTool.class);
         }   
      });
      
      return injector;
   }
   
   public static void main(final String[] args) {
      Logger logger = Logger.getLogger("");
      ConsoleHandler consoleHandler = new ConsoleHandler();
      logger.addHandler(consoleHandler);
      logger.setLevel(Level.WARNING);
      consoleHandler.setLevel(Level.WARNING);
      // logger.setLevel(Level.CONFIG);
      // consoleHandler.setLevel(Level.CONFIG);
      
      if(args.length==1 && args[0].equals("--version")) {
         showVersion();
         return;
      }
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch (ClassNotFoundException exception) {
         LOG.log(Level.WARNING, "Could Not Set Look And Feel", exception);
      }
      catch (InstantiationException exception) {
         LOG.log(Level.WARNING, "Could Not Set Look And Feel", exception);
      }
      catch (IllegalAccessException exception) {
         LOG.log(Level.WARNING, "Could Not Set Look And Feel", exception);
      }
      catch (UnsupportedLookAndFeelException exception) {
         LOG.log(Level.WARNING, "Could Not Set Look And Feel", exception);
      }
      
      javax.swing.SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            new NubsLauncher();
         }
      });
   }

   private static void showVersion() {
      System.out.println(Version.getVersion());   
      return;
   }
   
}

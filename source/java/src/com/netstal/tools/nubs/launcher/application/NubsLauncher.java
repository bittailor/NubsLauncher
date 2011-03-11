package com.netstal.tools.nubs.launcher.application;

import java.io.*;
import java.util.logging.*;

import javax.swing.*;

import com.google.inject.*;
import com.netstal.tools.nubs.launcher.domain.*;
import com.netstal.tools.nubs.launcher.infrastructure.*;
import com.netstal.tools.nubs.launcher.ui.*;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.*;

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
            
            //bind(IRakeLauncher.class).to(ConsoleRakeLauncher.class);
            bind(IRakeLauncher.class).to(InternalRakeLauncher.class);
            bind(IRakeTaskParser.class).to(RakeTaskParser.class);
            bind(IRakeTaskImporter.class).to(RakeTaskImporter.class);
            bind(IProcessBuilder.class).to(ProcessBuilderWrapper.class);
            bind(IRakeBuildOutputParser.class).to(RakeOutputParser.class);
            bind(IRakeJob.class).to(RakeJob.class);
            
            
            bind(IRakeJobRepository.class).to(RakeJobRepository.class).in(Singleton.class);
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
            bind(ProcessOutputCapture.class);
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

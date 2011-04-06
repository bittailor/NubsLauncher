package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;

public class Workspace extends EventSource<IWorkspace> implements IWorkspace {

   private static Logger LOG = Logger.getLogger(Workspace.class.getName());
   
   private IRakeTaskImporter importer;
   private IConfiguration configuration;
   private SortedMap<String, RakeTask> tasks;
   private File workspaceRoot = new File(System.getProperty("user.dir"));
   private InitialRootStrategy initialRootStrategy;
   
   @Inject
   public Workspace(IRakeTaskImporter importer, IConfiguration configuration) {
      this.importer = importer;
      this.configuration = configuration;
      this.initialRootStrategy = new InitialRootStrategy();
      determineInitialRoot();
   }
   
   private void determineInitialRoot() {
      File initialRoot = initialRootStrategy.determineInitialRoot();
      if (isValidRoot(initialRoot)) {
         setRoot(initialRoot);
         return;
      }
   }
   
   @Override
   public void setRoot(File root) {
      File newRoot = root.getAbsoluteFile();
      if (newRoot.getName().equals(".")) {
         workspaceRoot = newRoot.getParentFile();
      } else {
         workspaceRoot = newRoot;
      }
      if (isValidRoot(workspaceRoot)) {
         initialRootStrategy.storeRecentRoot();
      }
      tasks = null;
      notifyEventListeners(this);
   }

   @Override
   public boolean hasValidRoot() {
      return isValidRoot(workspaceRoot);
   }
   
   @Override
   public boolean areTasksLoaded() {
      return tasks != null;
   }

   @Override
   public File getRoot() {
      return workspaceRoot;
   }

   @Override
   public Collection<RakeTask> getTasks() {
      if (tasks == null) {
         return new ArrayList<RakeTask>();
      }
      return tasks.values();
   }
   
   @Override
   public void loadTasks() {
      tasks = importer.importTasks(workspaceRoot);
      notifyEventListeners(this);
      
   }
   
   private boolean isValidRoot(File root) {
      return new File(root,"rakefile").exists(); 
   }
   
   private class InitialRootStrategy {
      private static final String WORKSPACE = "RecentWorkspace";
      
      public void storeRecentRoot() {
         Properties initialWorkspace = new Properties();
         initialWorkspace.setProperty(WORKSPACE, workspaceRoot.getAbsolutePath());
         OutputStream out = null;
         try {
            out = new FileOutputStream(getStorageFile());         
            initialWorkspace.store(out,"Nubs Workspace Storage");
         }
         catch (IOException e) {
            LOG.log(Level.WARNING, "Problem saving sworkspace properties", e);
         }
         finally {
            StreamUtility.close(out);
         }
         
      }
      
      public File determineInitialRoot() {
         if (isValidRoot(startDirectory())) {
            return startDirectory();
         }
         
         if (!getStorageFile().exists()) {
            return startDirectory();
         }
         
         Properties initialWorkspace = new Properties();
         InputStream in = null;
         try {
            in = new FileInputStream(getStorageFile());         
            initialWorkspace.load(in);
         }
         catch (IOException e) {
            LOG.log(Level.WARNING, "Problem loading workspace properties", e);
         }
         finally {
            StreamUtility.close(in);
         }
         
         if (!initialWorkspace.containsKey(WORKSPACE)) {
            return startDirectory();
         }
         
         return new File(initialWorkspace.getProperty(WORKSPACE));
      }
      
      private File startDirectory() {
         return new File(System.getProperty("user.dir"));
      }
      
      private File getStorageFile() {
         return new File(configuration.getConfigurationDirectory(),"workspace.properties");
      }
      
   }
   
}

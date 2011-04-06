package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.SortedMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;
import com.netstal.tools.nubs.launcher.infrastructure.StringUtility;

public class Workspace extends EventSource<IWorkspace> implements IWorkspace {

   private static Logger LOG = Logger.getLogger(Workspace.class.getName());
   
   private IRakeTaskImporter importer;
   private IConfiguration configuration;
   private SortedMap<String, RakeTask> tasks;
   private File workspaceRoot = new File(System.getProperty("user.dir"));
   
   @Inject
   public Workspace(IRakeTaskImporter importer, IConfiguration configuration) {
      this.importer = importer;
      this.configuration = configuration;
      determineInitialRoot();
   }
   
   private void determineInitialRoot() {
      File userDirectory = new File(System.getProperty("user.dir"));
      
      if (isValidRoot(userDirectory)) {
         setRoot(userDirectory);
         return;
      }
      
      if (setStoredRoot()) {
         return;
      }
   }
   
   private boolean setStoredRoot() {
      if (getLastRootFile().exists())
      {
         try {
            String root = StringUtility.join(" ", StreamUtility.readLines(getLastRootFile())).trim();
            setRoot(new File(root));
            return true;
         }
         catch (IOException e) {
            LOG.log(Level.WARNING, "Problem loading last workspace location", e);
         }
      }
      return false;
   }
   
   private void storeRoot() {
      PrintWriter writer = null;
      try {
         writer = new PrintWriter(getLastRootFile());
         writer.println(workspaceRoot);
      }
      catch (IOException e) {
         LOG.log(Level.WARNING, "Problem saving last workspace location", e);
      }
      finally {
         StreamUtility.close(writer);
      }
      
   }
   
   private File getLastRootFile() {
      return new File(configuration.getConfigurationDirectory(),"LastWorkspace.txt");
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
         storeRoot();
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
   
}

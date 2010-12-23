package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import com.google.inject.Inject;

public class Workspace extends EventSource<IWorkspace> implements IWorkspace {

   IRakeTaskImporter importer;
   SortedMap<String, RakeTask> tasks = new TreeMap<String, RakeTask>();
   File workspaceRoot = new File(System.getProperty("user.dir"));
   
   @Inject
   public Workspace(IRakeTaskImporter importer) {
      this.importer = importer;
   }
   
   @Override
   public void setRoot(File root) {
      File newRoot = root.getAbsoluteFile();
      if (newRoot.getName().equals(".")) {
         workspaceRoot = newRoot.getParentFile();
      } else {
         workspaceRoot = newRoot;
      }
      loadTasks();
   }

   @Override
   public File getRoot() {
      return workspaceRoot;
   }

   @Override
   public Collection<RakeTask> getTasks() {
      return tasks.values();
   }
   
   private void loadTasks() {
      tasks = importer.importTasks(workspaceRoot);
      notify(this);
   }
   
}

package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.util.Collection;

public interface IWorkspace extends IEventSource<IWorkspace> {
   
   public boolean hasValidRoot();
   public boolean areTasksLoaded();
   public void setRoot(File rootFolder);
   public File getRoot();
   public void loadTasks();
   public void reloadTasks();
   public Collection<RakeTask> getTasks();
   
}

package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.util.Collection;

public interface IWorkspace extends IEventSource<IWorkspace> {
   
   public void setRoot(File rootFolder);
   public File getRoot();
   public Collection<RakeTask> getTasks();
   
}

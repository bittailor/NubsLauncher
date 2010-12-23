
package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.util.SortedMap;

public interface IRakeTaskImporter {

   public abstract SortedMap<String, RakeTask> importTasks(File workspaceRoot);

}

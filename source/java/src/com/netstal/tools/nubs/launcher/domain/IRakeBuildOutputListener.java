package com.netstal.tools.nubs.launcher.domain;

public interface IRakeBuildOutputListener {

   public void notifyTaskFailed(String taskName);
   public void notifyExecuteTask(String taskName);

}

package com.netstal.tools.nubs.launcher.domain;

public interface IRakeJobListener {

   public void notifyExecuteTask(String taskName);

   public void notifyTaskFailed(String taskName);

   public void notifyState(RakeJob.State state);

}

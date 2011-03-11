package com.netstal.tools.nubs.launcher.domain;

public interface IRakeJobRepository extends IEventSource<IRakeJobRepository> {

   public int size();
   public IRakeJob get(int index);
   public void add(IRakeJob job);
   public void remove(IRakeJob job);
   public IEventSource<IRakeJob> getJobsEventSource();

}     

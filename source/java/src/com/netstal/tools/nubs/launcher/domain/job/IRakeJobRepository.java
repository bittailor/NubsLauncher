package com.netstal.tools.nubs.launcher.domain.job;

import com.netstal.tools.nubs.launcher.domain.IEventSource;

public interface IRakeJobRepository extends IEventSource<IRakeJobRepository> {

   public int size();
   public IRakeJob get(int index);
   public void add(IRakeJob job);
   public void remove(IRakeJob job);
   public IEventSource<IRakeJob.Event> getJobsEventSource();
   public void clear(IRakeJob job);
   public void clearFinished();

}     

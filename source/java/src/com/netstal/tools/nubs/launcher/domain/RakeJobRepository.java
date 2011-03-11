package com.netstal.tools.nubs.launcher.domain;

import java.util.LinkedList;
import java.util.List;

public class RakeJobRepository extends EventSource<IRakeJobRepository> implements IRakeJobRepository, IEventListener<IRakeJob> {

   private List<IRakeJob> jobs;
   private EventSource<IRakeJob> jobsEventSource;
   
   public RakeJobRepository() {
      jobs = new LinkedList<IRakeJob>();
      jobsEventSource = new EventSource<IRakeJob>();
   }

   @Override
   public int size() {
      return jobs.size();
   }

   @Override
   public IRakeJob get(int index) {
      return jobs.get(index);
   }

   @Override
   public void add(IRakeJob job) {
      job.addListener(this);
      jobs.add(job);
      notifyEventListeners(this);
   }

   @Override
   public void remove(IRakeJob job) {
      if (jobs.remove(job)) {
         job.removeListener(this);
      }
      notifyEventListeners(this);
   }

   @Override
   public IEventSource<IRakeJob> getJobsEventSource() {
      return jobsEventSource;
   }

   @Override
   public void notifyEvent(IRakeJob source) {
      jobsEventSource.notifyEventListeners(source);    
   }
   
}

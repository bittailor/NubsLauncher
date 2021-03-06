package com.netstal.tools.nubs.launcher.domain.job;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.netstal.tools.nubs.launcher.domain.EventSource;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IEventSource;

public class RakeJobRepository extends EventSource<IRakeJobRepository> implements IRakeJobRepository, IEventListener<IRakeJob.Event> {

   private List<IRakeJob> jobs;
   private EventSource<IRakeJob.Event> jobsEventSource;
   
   public RakeJobRepository() {
      jobs = new LinkedList<IRakeJob>();
      jobsEventSource = new EventSource<IRakeJob.Event>();
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
      jobs.add(0,job);
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
   public void clear(IRakeJob job) {
      remove(job);
      job.dispose();
   }

   @Override
   public IEventSource<IRakeJob.Event> getJobsEventSource() {
      return jobsEventSource;
   }

   @Override
   public void notifyEvent(IRakeJob.Event source) {
      jobsEventSource.notifyEventListeners(source);    
   }

   @Override
   public void clearFinished() {
      for (IRakeJob job : new ArrayList<IRakeJob>(jobs)) {
         if (job.isFinished()) {
            jobs.remove(job);
            job.dispose();
         }
      }
      notifyEventListeners(this);
      
   }
   
}

package com.netstal.tools.nubs.launcher.ui;

import javax.swing.AbstractListModel;

import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;

public class JobListModel extends AbstractListModel {
  
   
   private IRakeJobRepository rakeJobRepository;
   private int oldSize;

   public JobListModel(IRakeJobRepository rakeJobRepository) {
      this.rakeJobRepository = rakeJobRepository;
      oldSize = getSize();
      rakeJobRepository.addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJobRepository>() {
         @Override
         public void notifyEvent(IRakeJobRepository source) {
            fireIntervalRemoved(JobListModel.this, 0, oldSize);
            fireIntervalAdded(JobListModel.this, 0, getSize());
            oldSize = getSize(); 
         }
      });
      rakeJobRepository.getJobsEventSource().addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJob>() {     
         @Override
         public void notifyEvent(final IRakeJob job) {
            fireContentsChanged(JobListModel.this, 0, getSize());                               
         }
      });
      
   }
   
   @Override
   public int getSize() {
      return rakeJobRepository.size();
   }

   @Override
   public Object getElementAt(int index) {
      return rakeJobRepository.get(index);
   }
   
}

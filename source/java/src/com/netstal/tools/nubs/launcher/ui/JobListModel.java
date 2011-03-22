package com.netstal.tools.nubs.launcher.ui;

import javax.swing.AbstractListModel;
import javax.swing.SwingUtilities;

import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.IRakeJobRepository;

public class JobListModel extends AbstractListModel {
  
   
   private IRakeJobRepository rakeJobRepository;
   private int oldSize;

   public JobListModel(IRakeJobRepository rakeJobRepository) {
      this.rakeJobRepository = rakeJobRepository;
      oldSize = getSize();
      rakeJobRepository.addListener(new IEventListener<IRakeJobRepository>() {
         @Override
         public void notifyEvent(IRakeJobRepository source) {
            fireIntervalRemoved(JobListModel.this, 0, oldSize);
            fireIntervalAdded(JobListModel.this, 0, getSize());
            oldSize = getSize(); 
         }
      });
      rakeJobRepository.getJobsEventSource().addListener(new IEventListener<IRakeJob>() {     
         @Override
         public void notifyEvent(final IRakeJob job) {
            SwingUtilities.invokeLater(new Runnable() {               
               @Override
               public void run() {
                  fireContentsChanged(JobListModel.this, 0, getSize());                             
               }
            });
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

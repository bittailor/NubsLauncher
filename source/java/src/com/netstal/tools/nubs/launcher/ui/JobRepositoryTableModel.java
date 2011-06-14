package com.netstal.tools.nubs.launcher.ui;

import javax.swing.table.AbstractTableModel;

import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;

public class JobRepositoryTableModel extends AbstractTableModel {
  
   private static final String[] NAMES = {"Command","State","Current Task","Auto Retry"};
   
   private IRakeJobRepository rakeJobRepository;

   public JobRepositoryTableModel(IRakeJobRepository rakeJobRepository) {
      this.rakeJobRepository = rakeJobRepository;
      attachListeners(rakeJobRepository);    
   }

   private void attachListeners(IRakeJobRepository rakeJobRepository) {
      rakeJobRepository.addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJobRepository>() {
         @Override
         public void notifyEvent(IRakeJobRepository source) {
            fireTableDataChanged(); 
         }
      });
      
      rakeJobRepository.getJobsEventSource().addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJob.Event>() {     
         @Override
         public void notifyEvent(final IRakeJob.Event event) {
            fireTableRowsUpdated(0, getRowCount());                               
         }
      });
   }
   
   @Override
   public String getColumnName(int column) {
      return NAMES[column];
   }

   @Override
   public int getRowCount() {
      return rakeJobRepository.size();
   }

   @Override
   public int getColumnCount() {
      return NAMES.length;
   }

   @Override
   public Object getValueAt(int rowIndex, int columnIndex) {
      return rakeJobRepository.get(rowIndex);        
   }
   
}

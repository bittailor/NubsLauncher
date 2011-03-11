package com.netstal.tools.nubs.launcher.ui;

import java.awt.Desktop;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.IRakeJob.State;
import com.netstal.tools.nubs.launcher.domain.IRakeJobRepository;

public class JobListModel extends AbstractListModel {
  
   private static Logger LOG = Logger.getLogger(JobListModel.class.getName());
   
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
            LOG.log(Level.INFO, "fireContentsChanged -> " + job.getCommand() + " --> " + job.getCurrentTask());
            System.out.println();
            SwingUtilities.invokeLater(new Runnable() {               
               @Override
               public void run() {
                  fireContentsChanged(JobListModel.this, 0, getSize());                             
               }
            });
            if (job.getState().equals(State.FAILED)) {
               SwingUtilities.invokeLater(new Runnable() {               
                  @Override
                  public void run() {
                     showRetryGui(job);
                  }

                  
               });
            }
            if (job.getState().equals(State.FINISHED_FAILURE) || job.getState().equals(State.FINISHED_EXCEPTION)) {
               SwingUtilities.invokeLater(new Runnable() {               
                  @Override
                  public void run() {
                     Desktop desktop = Desktop.getDesktop();
                     try {
                        desktop.open(job.getLogFile());
                     }
                     catch (Exception exception) {
                        LOG.log(Level.WARNING, "Could Not Launch log file editor", exception);
                     }
                  }

                  
               });
            }
         }
      });
      
   }
   
   private void showRetryGui(IRakeJob job) {
      Object[] options = {"Retry","Ignore","Fail"};
      int n = JOptionPane.showOptionDialog(null,
               "<html>" +
               "<b> Task " +  job.getCurrentTask() + " failed!</b>",
               "NUBS Launcher - Task Failed",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               new ImageIcon(NubsLauncherFrame.class.getResource("images/Rocket.png")),
               options,
               options[0]);
      if (n == 0) {
         job.retry();
      } else if (n == 1) {
         job.ignore();
      } else if (n == 2) {
         job.fail();
      }
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

package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.IRakeJobRepository;

public class JobPanel extends JPanel {

   private static Logger LOG = Logger.getLogger(JobPanel.class.getName());
   
   private IRakeJobRepository rakeJobRepository;
   private JToolBar toolBar;
   private JList jobs;

   private OpenLogAction openAction;

   private RetryAction retryAction;

   private ClearAction clearAction;   
   
   @Inject
   public JobPanel(IRakeJobRepository rakeJobRepository) {
      this.rakeJobRepository = rakeJobRepository;
      createUi();
   }

   private void createUi() {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createTitledBorder("Launched Rake Jobs"));
      jobs = new JList(new JobListModel(rakeJobRepository));
      jobs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jobs.setCellRenderer(new JobRenderer());
      
      JScrollPane scrollPane = new JScrollPane(jobs);
      add(scrollPane);

      toolBar = new JToolBar();
      add(toolBar,BorderLayout.SOUTH);
      
      openAction = new OpenLogAction();
      toolBar.add(openAction);
      retryAction = new RetryAction();
      toolBar.add(retryAction);
      clearAction = new ClearAction();
      toolBar.add(clearAction);
      
      
   }
   
   
   private class OpenLogAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public OpenLogAction() {
         super("Open Log");
         this.putValue(SHORT_DESCRIPTION, "Open Log");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            Desktop desktop = Desktop.getDesktop();
            try {
               desktop.open(job.getLogFile());
            }
            catch (Exception exception) {
               LOG.log(Level.WARNING, "Could Not Launch log file editor", exception);
            }           
         }
      }     
   }
   
   private class RetryAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public RetryAction() {
         super("Retry");
         this.putValue(SHORT_DESCRIPTION, "Retry");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            job.retry();          
         }
      }     
   }
   
   private class ClearAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public ClearAction() {
         super("Clear");
         this.putValue(SHORT_DESCRIPTION, "Clear Finished");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         rakeJobRepository.clearFinished();
      }     
   }
   
}

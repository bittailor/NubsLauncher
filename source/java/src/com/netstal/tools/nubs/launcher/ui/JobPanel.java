package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.*;

public class JobPanel extends JPanel {

   private static Logger LOG = Logger.getLogger(JobPanel.class.getName());
   
   private IRakeJobRepository rakeJobRepository;
   private IWorkspace workspace;
   private JToolBar toolBar;
   private JList jobs;

   private OpenLogAction openAction;

   private RetryAction retryAction;

   private ClearAction clearAction;   
   
   @Inject
   public JobPanel(IRakeJobRepository rakeJobRepository, IWorkspace workspace) {
      this.rakeJobRepository = rakeJobRepository;
      this.workspace = workspace;
      createUi();
   }

   private void createUi() {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createTitledBorder("Launched Rake Jobs"));
      jobs = new JList(new JobListModel(rakeJobRepository,workspace));
      jobs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jobs.setCellRenderer(new JobRenderer());
      
      JScrollPane scrollPane = new JScrollPane(jobs);
      add(scrollPane);

      toolBar = new JToolBar();
      toolBar.setFloatable(false);
      add(toolBar,BorderLayout.SOUTH);
      
      openAction = new OpenLogAction();
      toolBar.add(openAction);
      retryAction = new RetryAction();
      toolBar.add(retryAction);
      
      toolBar.add(Box.createHorizontalGlue());
      clearAction = new ClearAction();
      toolBar.add(clearAction);
      
      
   }
   
   
   private class OpenLogAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public OpenLogAction() {
         super("Open Log",new ImageIcon(NubsLauncherFrame.class.getResource("images/OpenLog.gif")));
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
         super("Clear Finished",new ImageIcon(NubsLauncherFrame.class.getResource("images/Clear.gif")));
         this.putValue(SHORT_DESCRIPTION, "Clear Finished");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         rakeJobRepository.clearFinished();
      }     
   }
   
}

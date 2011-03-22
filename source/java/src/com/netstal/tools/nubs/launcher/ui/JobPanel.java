package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;
import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobStateVisitor;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;

public class JobPanel extends JPanel {

   private static Logger LOG = Logger.getLogger(JobPanel.class.getName());
   
   private IRakeJobRepository rakeJobRepository;
   private IWorkspace workspace;
   private IConfiguration configuration;
   private JToolBar toolBar;
   private JList jobs;

   private OpenLogAction openAction;
   private RetryAction retryAction;
   private IgonreAction igonreAction;
   private FailAction failAction;
   private RemoveAction removeAction;

   private RemoveAllFinishedAction removeAllFinishedAction;



   
   @Inject
   public JobPanel(IRakeJobRepository rakeJobRepository, IWorkspace workspace, IConfiguration configuration) {
      this.rakeJobRepository = rakeJobRepository;
      this.workspace = workspace;
      this.configuration = configuration;
      createUi();
      createActions();
   }

   private void createActions() {
      jobs.addListSelectionListener(new ListSelectionListener() {        
         @Override
         public void valueChanged(ListSelectionEvent e) {
            jobsChanged();
         }
      });
      
      jobs.addMouseListener(new MouseAdapter() {
         
             @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount()>1) {
               openLogfile((IRakeJob)jobs.getSelectedValue());
            }
            
         }
      });
      
      rakeJobRepository.getJobsEventSource().addListener(new IEventListener<IRakeJob>() {       
         @Override
         public void notifyEvent(final IRakeJob job) {

            SwingUtilities.invokeLater(new Runnable() {               
               @Override
               public void run() {
                  jobsChanged();  
                  
                  if (job.getState().equals(Failed.INSTANCE)) {
                     showRetryGui(job);
                  }
               }                  
            });              
         }
      });
   }
   
   private void showRetryGui(IRakeJob job) {
      if (configuration.getFlag("notification.ShowRetryDialog")){
         Object[] options = {"Retry","Ignore","Fail"};
         int n = JOptionPane.showOptionDialog(null,
                  "<html>" +
                  "Task failed:<br/>"+
                  "<b>" +  job.getCurrentTask() + "</b><br/>",
                  "NUBS Launcher @ " + workspace.getRoot().getName(),
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
   }

   private void jobsChanged() {
      if (jobs.isSelectionEmpty()) {
         disableAll();
         return;
      }
      
      Object selectedValue = jobs.getSelectedValue();
      if (selectedValue instanceof IRakeJob) {
         IRakeJob job = (IRakeJob) selectedValue;
         openAction.setEnabled(true);
         
         job.getState().accept(new IJobStateVisitor() {
            
            private void finished() {
               retryAction.setEnabled(false);
               igonreAction.setEnabled(false);
               failAction.setEnabled(false);
               removeAction.setEnabled(true);              
            }
            
            
            @Override
            public void visit(FinishedExceptionally state) {
               finished();
            }
            
           

            @Override
            public void visit(FinishedFaultily state) {
               finished();
            }
            
            @Override
            public void visit(FinishedSucessfully state) {
               finished();
            }
            
            @Override
            public void visit(Failed state) {
               retryAction.setEnabled(true);
               igonreAction.setEnabled(true);
               failAction.setEnabled(true); 
               removeAction.setEnabled(false);
            }
            
            @Override
            public void visit(Building state) {
               retryAction.setEnabled(false);
               igonreAction.setEnabled(false);
               failAction.setEnabled(false); 
               removeAction.setEnabled(false);
               
            }
            
            @Override
            public void visit(Idle state) {
               retryAction.setEnabled(false);
               igonreAction.setEnabled(false);
               failAction.setEnabled(false); 
               removeAction.setEnabled(false);        
            }
         });
      } 
   }

   private void disableAll() {
      openAction.setEnabled(false);
      retryAction.setEnabled(false);
      igonreAction.setEnabled(false);
      failAction.setEnabled(false);
      removeAction.setEnabled(false);
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
      toolBar.setFloatable(false);
      add(toolBar,BorderLayout.SOUTH);
      
      openAction = new OpenLogAction();
      toolBar.add(openAction);
      retryAction = new RetryAction();
      toolBar.add(retryAction);
      igonreAction = new IgonreAction();
      toolBar.add(igonreAction);
      failAction = new FailAction();
      toolBar.add(failAction);
      removeAction = new RemoveAction();
      toolBar.add(removeAction);
      
      toolBar.add(Box.createHorizontalGlue());
      removeAllFinishedAction = new RemoveAllFinishedAction();
      toolBar.add(removeAllFinishedAction);
      
      disableAll();
      
   }
   
   
   private void openLogfile(IRakeJob job) {
      Desktop desktop = Desktop.getDesktop();
      try {
         desktop.open(job.getLogFile());
      }
      catch (Exception exception) {
         LOG.log(Level.WARNING, "Could Not Launch log file editor", exception);
      }
   }


   private class OpenLogAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public OpenLogAction() {
         super("Open Log",new ImageIcon(NubsLauncherFrame.class.getResource("images/OpenLog.gif")));
         this.putValue(SHORT_DESCRIPTION, "Open The Log File");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            openLogfile(job);           
         }
      }     
   }
   
   private class RetryAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public RetryAction() {
         super("Retry",new ImageIcon(NubsLauncherFrame.class.getResource("images/Retry.gif")));
         this.putValue(SHORT_DESCRIPTION, "Retry The Failed Task");
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
   
   private class IgonreAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public IgonreAction() {
         super("Ignore",new ImageIcon(NubsLauncherFrame.class.getResource("images/Ignore.gif")));
         this.putValue(SHORT_DESCRIPTION, "Ignore The Failed Task");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            job.ignore();          
         }
      }     
   }
   
   private class FailAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public FailAction() {
         super("Fail",new ImageIcon(NubsLauncherFrame.class.getResource("images/Fail.gif")));
         this.putValue(SHORT_DESCRIPTION, "Fail The Failed Task");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            job.fail();          
         }
      }     
   }
   
   private class RemoveAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public RemoveAction() {
         super("Remove",new ImageIcon(NubsLauncherFrame.class.getResource("images/Remove.gif")));
         this.putValue(SHORT_DESCRIPTION, "Remove This Job");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         rakeJobRepository.clear((IRakeJob)jobs.getSelectedValue());
      }     
   }
   
   private class RemoveAllFinishedAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public RemoveAllFinishedAction() {
         super("Remove All Finished Jobs",new ImageIcon(NubsLauncherFrame.class.getResource("images/RemoveAll.gif")));
         this.putValue(SHORT_DESCRIPTION, "Remove All Finished Jobs");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         rakeJobRepository.clearFinished();
      }     
   }
   
}

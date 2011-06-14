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
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
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
   private IRakeLauncher launcher;
   private IConfiguration configuration;
   private JobTailPanel jobTailPanel;
   private JToolBar toolBar;
   private JList jobs;

   private OpenLogAction openAction;
   private ToggleAutoRetryAction toggleAutoRetryAction;
   private RetryAction retryAction;
   private IgonreAction igonreAction;
   private FailAction failAction;
   private RemoveAction removeAction;
   private RelaunchAction relaunchAction;

   private RemoveAllFinishedAction removeAllFinishedAction;


   @Inject
   public JobPanel(IRakeJobRepository rakeJobRepository, 
                   IWorkspace workspace, 
                   IRakeLauncher launcher,
                   IConfiguration configuration) {
      this.rakeJobRepository = rakeJobRepository;
      this.workspace = workspace;
      this.launcher = launcher;
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
      
      jobs.getModel().addListDataListener(new ListDataListener() {
         
         @Override
         public void intervalRemoved(ListDataEvent e) {
         }
         
         @Override
         public void intervalAdded(ListDataEvent e) {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  if (jobs.getModel().getSize() > 0) {
                     jobs.setSelectedIndex(0);
                  }
               }
            });
         }
         
         @Override
         public void contentsChanged(ListDataEvent e) {   
         }
      });
      
      rakeJobRepository.getJobsEventSource().addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJob.Event>() {       
         @Override
         public void notifyEvent(final IRakeJob.Event event) {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  jobsChanged();  
                  if (event.stateChanged && event.job.getState().equals(Failed.INSTANCE)) {
                     showRetryGui(event.job);
                  }}
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
      if (jobs.getModel().getSize() == 0) {
         jobTailPanel.resetJob();
         disableAll();
         return;
      }
      
      if (jobs.isSelectionEmpty()) {
         jobTailPanel.resetJob();
         disableAll();
         return;
      }
      
      Object selectedValue = jobs.getSelectedValue();
      if (selectedValue instanceof IRakeJob) {
         IRakeJob job = (IRakeJob) selectedValue;
         jobTailPanel.setJob(job);
         openAction.setEnabled(true);
         toggleAutoRetryAction.setEnabled(true);
         
         job.getState().accept(new IJobStateVisitor() {
            
            private void finished() {
               retryAction.setEnabled(false);
               igonreAction.setEnabled(false);
               failAction.setEnabled(false);
               relaunchAction.setEnabled(true);
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
               relaunchAction.setEnabled(false);
               removeAction.setEnabled(false);
            }
            
            @Override
            public void visit(Building state) {
               retryAction.setEnabled(false);
               igonreAction.setEnabled(false);
               failAction.setEnabled(false); 
               relaunchAction.setEnabled(false);
               removeAction.setEnabled(false);
               
            }
            
            @Override
            public void visit(Idle state) {
               retryAction.setEnabled(false);
               igonreAction.setEnabled(false);
               failAction.setEnabled(false); 
               relaunchAction.setEnabled(false);
               removeAction.setEnabled(false);        
            }
         });
      } 
   }

   private void disableAll() {
      openAction.setEnabled(false);
      toggleAutoRetryAction.setEnabled(false);
      retryAction.setEnabled(false);
      igonreAction.setEnabled(false);
      failAction.setEnabled(false);
      relaunchAction.setEnabled(false);
      removeAction.setEnabled(false);
   }

   private void createUi() {
      setLayout(new BorderLayout());
      //setBorder(BorderFactory.createTitledBorder("Launched Rake Jobs"));
      jobs = new JList(new JobListModel(rakeJobRepository));
      jobs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jobs.setCellRenderer(new JobRenderer());
      
      JPanel scrollPanePanel = new JPanel(new BorderLayout()); 
      scrollPanePanel.setBorder(BorderFactory.createTitledBorder("Launched Rake Jobs"));
      JScrollPane scrollPane = new JScrollPane(jobs);
      scrollPanePanel.add(scrollPane);
      add(scrollPanePanel);

      JPanel south = new JPanel(new BorderLayout());
      add(south, BorderLayout.SOUTH);
      
      jobTailPanel = new JobTailPanel(configuration.getInteger("job.TailSize"));
      south.add(jobTailPanel, BorderLayout.CENTER);
      
      toolBar = new JToolBar();
      toolBar.setFloatable(false);
      south.add(toolBar, BorderLayout.NORTH);
      
      openAction = new OpenLogAction();
      toolBar.add(openAction);
      toggleAutoRetryAction = new ToggleAutoRetryAction();
      toolBar.add(toggleAutoRetryAction);
      retryAction = new RetryAction();
      toolBar.add(retryAction);
      igonreAction = new IgonreAction();
      toolBar.add(igonreAction);
      failAction = new FailAction();
      toolBar.add(failAction);
      relaunchAction = new RelaunchAction();
      toolBar.add(relaunchAction);
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
         super("Retry",new ImageIcon(NubsLauncherFrame.class.getResource("images/RetryFw.png")));
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
         super("Ignore",new ImageIcon(NubsLauncherFrame.class.getResource("images/IgnoreFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Ignore The Failed Task And Continue");
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
         super("Fail",new ImageIcon(NubsLauncherFrame.class.getResource("images/FailFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Quit The Job");
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
   
   private class RelaunchAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public RelaunchAction() {
         super("Relaunch",new ImageIcon(NubsLauncherFrame.class.getResource("images/Launch.png")));
         this.putValue(SHORT_DESCRIPTION, "Launch This Job Again");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            launcher.relaunch(job);
                      
         }
      }     
   }
   
   private class ToggleAutoRetryAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public ToggleAutoRetryAction() {
         super("AutoRetry",new ImageIcon(NubsLauncherFrame.class.getResource("images/AutoRetryFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Toggle Auto Retry");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = jobs.getSelectedValue();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            job.setAutoRetry(!job.isAutoRetry());         
         }
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

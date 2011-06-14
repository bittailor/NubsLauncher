package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableColumnModel;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;
import com.netstal.tools.nubs.launcher.ui.job.AbstractFailedJobDependentAction;
import com.netstal.tools.nubs.launcher.ui.job.AbstractFinishedJobDependentAction;
import com.netstal.tools.nubs.launcher.ui.job.AbstractJobDependentAction;
import com.netstal.tools.nubs.launcher.ui.job.IJobDependentAction;
import com.netstal.tools.nubs.launcher.ui.job.IJobSelectionListener;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderAutoRetry;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderCommand;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderCurrentState;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderState;

public class JobPanel extends JPanel {

   private static Logger LOG = Logger.getLogger(JobPanel.class.getName());
   
   private IRakeJobRepository rakeJobRepository;
   private IRakeLauncher launcher;
   private IConfiguration configuration;
   private JobTailPanel jobTailPanel;
   private JToolBar toolBar;
   private JTable jobs;
   
   private List<IJobSelectionListener> jobSelectionListeners;

   private RemoveAllFinishedAction removeAllFinishedAction;


   @Inject
   public JobPanel(IRakeJobRepository rakeJobRepository, 
                   IRakeLauncher launcher,
                   IConfiguration configuration) {
      this.rakeJobRepository = rakeJobRepository;
      this.launcher = launcher;
      this.configuration = configuration;
      jobSelectionListeners = new LinkedList<IJobSelectionListener>();
      createUi();
      createActions();
      jobsChanged();
   }

   private void createActions() {
      jobs.getSelectionModel().addListSelectionListener(new ListSelectionListener() {        
         @Override
         public void valueChanged(ListSelectionEvent e) {
            jobsChanged();
         }
      });
      
      jobs.addMouseListener(new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            if (e.getClickCount()>1) {
               
               OpenLogAction.openLogfile(getSelectedJob());
            }
            
         }
      });
      
      jobs.getModel().addTableModelListener(new TableModelListener() {
         
         @Override
         public void tableChanged(TableModelEvent e) {
            if (e.getType() == TableModelEvent.INSERT) {
               SwingUtilities.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     jobs.getSelectionModel().setSelectionInterval(0, 0);
                  }
               });
            }
            
         }
      });
       
      rakeJobRepository.getJobsEventSource().addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJob.Event>() {       
         @Override
         public void notifyEvent(final IRakeJob.Event event) {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  jobsChanged();
               }
            });
         }
      });
   }
   
   

   private void jobsChanged() {   
      IRakeJob job = getSelectedJob();
      for (IJobSelectionListener listener : jobSelectionListeners) {
         listener.newSelection(job);
      }
   }

   private void createUi() {
      setLayout(new BorderLayout());
      jobs = new JTable(new JobRepositoryTableModel(rakeJobRepository));
      jobs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jobs.setShowGrid(false);
      jobs.setShowVerticalLines(false);
      jobs.setShowHorizontalLines(true);
      jobs.setIntercellSpacing(new Dimension(0, 1));
      jobs.setRowHeight(20);
      
      TableColumnModel columnModel = jobs.getColumnModel();
      columnModel.getColumn(0).setCellRenderer(new RenderCommand());
      columnModel.getColumn(1).setCellRenderer(new RenderState());
      columnModel.getColumn(2).setCellRenderer(new RenderCurrentState());
      columnModel.getColumn(3).setCellRenderer(new RenderAutoRetry());
      
      JPanel scrollPanePanel = new JPanel(new BorderLayout()); 
      scrollPanePanel.setBorder(BorderFactory.createTitledBorder("Launched Rake Jobs"));
      JScrollPane scrollPane = new JScrollPane(jobs);
      scrollPanePanel.add(scrollPane);
      add(scrollPanePanel);

      JPanel south = new JPanel(new BorderLayout());
      add(south, BorderLayout.SOUTH);
      
      jobTailPanel = new JobTailPanel(configuration.getInteger("job.TailSize"));
      jobSelectionListeners.add(jobTailPanel);
      south.add(jobTailPanel, BorderLayout.CENTER);
      
      toolBar = new JToolBar();
      toolBar.setFloatable(false);
      south.add(toolBar, BorderLayout.NORTH);
      
      addJobDependendAction(new OpenLogAction());
      addJobDependendAction(new ToggleAutoRetryAction());
      addJobDependendAction(new RetryAction());
      addJobDependendAction(new IgonreAction());
      addJobDependendAction(new FailAction());
      addJobDependendAction(new RelaunchAction());
      addJobDependendAction(new RemoveAction());
      
      toolBar.add(Box.createHorizontalGlue());
      removeAllFinishedAction = new RemoveAllFinishedAction();
      toolBar.add(removeAllFinishedAction);
      
   }
   
   private void addJobDependendAction(IJobDependentAction action) {
      jobSelectionListeners.add(action);
      toolBar.add(action);
   }
   
   private IRakeJob getSelectedJob() {
      int selectedRow = jobs.getSelectedRow();
      
      if (selectedRow < 0) {
         return null;
      }
      if (selectedRow >= rakeJobRepository.size()) {
         return null;
      }
      
      return rakeJobRepository.get(selectedRow);
   }
    
   private static class OpenLogAction extends AbstractJobDependentAction {
      
      private static final long serialVersionUID = 1L;
      
      private static void openLogfile(IRakeJob job) {
         Desktop desktop = Desktop.getDesktop();
         try {
            desktop.open(job.getLogFile());
         }
         catch (Exception exception) {
            LOG.log(Level.WARNING, "Could Not Launch log file editor", exception);
         }
      }
      
      public OpenLogAction() {
         super("Open Log",new ImageIcon(NubsLauncherFrame.class.getResource("images/OpenLog.gif")));
         this.putValue(SHORT_DESCRIPTION, "Open The Log File");
      }

      @Override
      protected void actionPerformed(IRakeJob job) {
         openLogfile(job);         
      }
      
     
         
   }
         
   private static class RetryAction extends AbstractFailedJobDependentAction {
      private static final long serialVersionUID = 1L;

      public RetryAction() {
         super("Retry",new ImageIcon(NubsLauncherFrame.class.getResource("images/RetryFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Retry The Failed Task");
      }
      
      @Override
      protected void actionPerformed(IRakeJob job) {
         job.retry();          
      }
         
   }
   
   private static class IgonreAction extends AbstractFailedJobDependentAction {
      private static final long serialVersionUID = 1L;

      public IgonreAction() {
         super("Ignore",new ImageIcon(NubsLauncherFrame.class.getResource("images/IgnoreFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Ignore The Failed Task And Continue");
      }
      
      @Override
      protected void actionPerformed(IRakeJob job) {
         job.ignore();           
      }     
   }
   
   private class FailAction extends AbstractFailedJobDependentAction {
      private static final long serialVersionUID = 1L;

      public FailAction() {
         super("Fail",new ImageIcon(NubsLauncherFrame.class.getResource("images/FailFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Quit The Job");
      }
      
      @Override
      protected void actionPerformed(IRakeJob job) {
         job.fail();           
      }  
      
      @Override
      public void actionPerformed(ActionEvent e) {
         Object selectedValue = getSelectedJob();
         if (selectedValue instanceof IRakeJob) {
            IRakeJob job = (IRakeJob) selectedValue;
            job.fail();          
         }
      }     
   }
   
   private class RemoveAction extends AbstractFinishedJobDependentAction {
      private static final long serialVersionUID = 1L;

      public RemoveAction() {
         super("Remove",new ImageIcon(NubsLauncherFrame.class.getResource("images/Remove.gif")));
         this.putValue(SHORT_DESCRIPTION, "Remove This Job");
      }
      
      @Override
      protected void actionPerformed(IRakeJob job) {
         rakeJobRepository.clear(job);           
      }
             
   }
   
   private class RelaunchAction extends AbstractFinishedJobDependentAction {
      private static final long serialVersionUID = 1L;

      public RelaunchAction() {
         super("Relaunch",new ImageIcon(NubsLauncherFrame.class.getResource("images/Launch.png")));
         this.putValue(SHORT_DESCRIPTION, "Launch This Job Again");
      }
      
      @Override
      protected void actionPerformed(IRakeJob job) {
         launcher.relaunch(job);           
      }
     
   }
   
   
   private static class ToggleAutoRetryAction extends AbstractJobDependentAction {
      private static final long serialVersionUID = 1L;

      public ToggleAutoRetryAction() {
         super("AutoRetry",new ImageIcon(NubsLauncherFrame.class.getResource("images/AutoRetryFw.png")));
         this.putValue(SHORT_DESCRIPTION, "Toggle Auto Retry");
      }
      
      @Override
      protected void actionPerformed(IRakeJob job) {
         job.setAutoRetry(!job.isAutoRetry());           
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

package com.netstal.tools.nubs.launcher.ui.job;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
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
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;
import com.netstal.tools.nubs.launcher.ui.job.action.FailAction;
import com.netstal.tools.nubs.launcher.ui.job.action.IJobDependentAction;
import com.netstal.tools.nubs.launcher.ui.job.action.IgonreAction;
import com.netstal.tools.nubs.launcher.ui.job.action.OpenLogAction;
import com.netstal.tools.nubs.launcher.ui.job.action.RelaunchAction;
import com.netstal.tools.nubs.launcher.ui.job.action.RemoveAction;
import com.netstal.tools.nubs.launcher.ui.job.action.RemoveAllFinishedAction;
import com.netstal.tools.nubs.launcher.ui.job.action.RetryAction;
import com.netstal.tools.nubs.launcher.ui.job.action.ToggleAutoRetryAction;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderAutoRetry;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderCommand;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderCurrentTask;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderState;
import com.netstal.tools.nubs.launcher.ui.job.table.RenderRetryCounter;

public class JobPanel extends JPanel {
   
   private static final int TABLE_NUMBER_OF_ROWS_IN_VIEWPORT = 8;
   private static final int TABLE_ROW_HEIGHT = 20;
   
   private IRakeJobRepository rakeJobRepository;
   private IRakeLauncher launcher;
   private IConfiguration configuration;
   private JobTailPanel jobTailPanel;
   private JToolBar toolBar;
   private JTable jobs;
   
   private List<IJobSelectionListener> jobSelectionListeners;

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
               selectFirstJob();
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
      if(job == null && rakeJobRepository.size() > 0 ) {
         selectFirstJob();
      }
      
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
      jobs.setRowHeight(TABLE_ROW_HEIGHT);
      jobs.setPreferredScrollableViewportSize(new Dimension(0, TABLE_NUMBER_OF_ROWS_IN_VIEWPORT * TABLE_ROW_HEIGHT));
      
      TableColumnModel columnModel = jobs.getColumnModel();
      
      TableColumn column = columnModel.getColumn(0);
      column.setCellRenderer(new RenderCommand());      
      column.setPreferredWidth(370);
      
      column = columnModel.getColumn(1);
      column.setCellRenderer(new RenderState());
      column.setPreferredWidth(180);
      
      column = columnModel.getColumn(2);
      column.setCellRenderer(new RenderCurrentTask());
      column.setPreferredWidth(300);
      
      column = columnModel.getColumn(3);
      column.setCellRenderer(new RenderAutoRetry());
      column.setPreferredWidth(60);
      
      column = columnModel.getColumn(4);
      column.setCellRenderer(new RenderRetryCounter());
      column.setPreferredWidth(40);
      
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
      addJobDependendAction(new RelaunchAction(launcher));
      addJobDependendAction(new RemoveAction(rakeJobRepository));
      
      toolBar.add(Box.createHorizontalGlue());
      toolBar.add(new RemoveAllFinishedAction(rakeJobRepository));     
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
    
   private void selectFirstJob() {
      SwingUtilities.invokeLater(new Runnable() {
         @Override
         public void run() {
            if (jobs.getModel().getRowCount() > 0) {
               jobs.getSelectionModel().setSelectionInterval(0, 0);
            }
         }
      });
   }
}

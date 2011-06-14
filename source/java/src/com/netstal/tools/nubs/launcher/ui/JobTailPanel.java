package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.job.IJobSelectionListener;

public class JobTailPanel extends JPanel implements IEventListener<Collection<String>>, IJobSelectionListener {

   private JList list;
   private DefaultListModel listModel;
   private IRakeJob currentJob;

   public JobTailPanel(int tailSize) {      
      createUi(tailSize);     
   }
   
   public void setJob(IRakeJob job) {
      detachFromJob();
      currentJob = job;
      attachToJob();
   }
   
   public void resetJob() {
      detachFromJob();
      currentJob = null;
   } 

   private void attachToJob() {
      if (currentJob == null) {
         return;
      }
      notifyEvent(currentJob.getTailLog());
      currentJob.getTailLogEventSource().addListenerNotifyInSwingDispatchThread(this);
      
   }

   private void detachFromJob() {
      if (currentJob == null) {
         return;
      }
      currentJob.getTailLogEventSource().removeListener(this);
      listModel.clear();
   }

   private void createUi(int tailSize) {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createTitledBorder("Job Output"));
      listModel = new DefaultListModel();
      list = new JList(listModel);
      list.setVisibleRowCount(tailSize+2);
      JScrollPane scrollBar = new JScrollPane(list);
      scrollBar.setPreferredSize(new Dimension(scrollBar.getPreferredSize().width,scrollBar.getPreferredSize().height));
      add(scrollBar);
   }

   @Override
   public void notifyEvent(Collection<String> tail) {
      listModel.clear();
      for (String string : tail) {
         listModel.addElement(string);
      }
   }

   @Override
   public void newSelection(IRakeJob job) {
      if (job != null) {
         setJob(job);
         return;
      }
      resetJob();
   }

    
}

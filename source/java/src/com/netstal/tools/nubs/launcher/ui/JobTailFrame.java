package com.netstal.tools.nubs.launcher.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JScrollPane;

import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class JobTailFrame extends JFrame implements IEventListener<String> {

   private final int tailSize;
   private JList list;
   private DefaultListModel listModel;

   public JobTailFrame(final IRakeJob job, JFrame mainFrame ,IConfiguration configuration) {      
      this.tailSize = configuration.getInteger("job.TailFrameSize");
      
      
      
      createUi(mainFrame,job);
      createActions(job);
      
      pack();
      setVisible(true);
   }

   private void createUi(JFrame mainFrame, final IRakeJob job) {
      setIconImage(new ImageIcon(getClass().getResource("images/Rocket.png")).getImage());
      this.setTitle("Job Tail - " + job.getCommand().toString());
      setLocation(mainFrame);
      listModel = new DefaultListModel();
      list = new JList(listModel);
      list.setVisibleRowCount(tailSize+2);
      JScrollPane scrollBar = new JScrollPane(list);
      scrollBar.setPreferredSize(new Dimension(mainFrame.getSize().width,scrollBar.getPreferredSize().height));
      this.getContentPane().add(scrollBar);
   }

   private void createActions(final IRakeJob job) {
      job.getLogEventSource().addListenerNotifyInSwingDispatchThread(this);
      job.addListenerNotifyInSwingDispatchThread(new IEventListener<IRakeJob>() {
         
         @Override
         public void notifyEvent(IRakeJob job) {
            if (job.isDisposed()) {
               JobTailFrame.this.dispose();
            }
         }
      });
      
      this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);     
      this.addWindowListener(new WindowAdapter() {
         @Override
         public void windowClosed(WindowEvent e) {
            job.getLogEventSource().removeListener(JobTailFrame.this);           
         }
      });
      
   }

   private void setLocation(JFrame mainFrame) {
      int x = mainFrame.getLocation().x;
      int y = mainFrame.getLocation().y + mainFrame.getSize().height + 5;
      this.setLocation(new Point(x, y));
   }

   @Override
   public void notifyEvent(String line) {
      listModel.addElement(line);
      if (listModel.getSize() > tailSize) {
         listModel.remove(0);
      }
   }   
}

package com.netstal.tools.nubs.launcher.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
 
public class JobRenderer extends JPanel implements ListCellRenderer {
     
   private JLabel command;
   private JLabel state;
   private JLabel currentTask;
   private JLabel retry;
   
   public JobRenderer() {
      this.setLayout(new GridLayout(1, 0));
      
      command = new JLabel();
      add(command);
      
      state = new JLabel();
      add(state);
      
      currentTask = new JLabel();
      add(currentTask);
      
      retry = new JLabel();
      add(retry);
   }
   
   
   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      
      IRakeJob job = (IRakeJob) value;
      Color color = job.getState().accept(new ColorJobStateVisitor()).getColor();
      setBackground(color);
       
      if (isSelected) {
         setBorder(BorderFactory.createLineBorder(list.getSelectionBackground(),2));
      } else {
         setBorder(BorderFactory.createLineBorder(list.getBackground(),2));
      }
        
      String commandString = job.getCommand().toString();
      if (commandString.length() > 40) {
         commandString = commandString.substring(0, 40) + " ...";
      }
      
      command.setText(commandString);
      state.setText(job.getState().accept(new DisplayNameJobStateVisitor()).getName());
      currentTask.setText(job.getCurrentTask());
      if (job.isAutoRetry()) {
         retry.setText("Auto-Retry " + job.getCurrentNumberOfAutoRetries() + " of " + job.getMaximumNumberOfAutoRetries());
      } else {
         retry.setText("Auto-Retry is off");
      }
      
      return this;
   }

}

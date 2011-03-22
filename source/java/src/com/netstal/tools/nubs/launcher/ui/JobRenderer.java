package com.netstal.tools.nubs.launcher.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.netstal.tools.nubs.launcher.domain.IRakeJob;
 
public class JobRenderer extends JPanel implements ListCellRenderer {
   
   private static final Color BUILDING_COLOR = new Color(34, 176, 34); 
   
   
   private JLabel command;
   private JLabel state;
   private JLabel currentTask;
   
   public JobRenderer() {
      this.setLayout(new GridLayout(1, 0));
      
      command = new JLabel();
      add(command);
      
      state = new JLabel();
      add(state);
      
      currentTask = new JLabel();
      add(currentTask);
   }
   
   
   @Override
   public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
      
      IRakeJob job = (IRakeJob) value;
      setBackground(job.getState().getColor());
      /*
      switch (job.getState()) {
         case IDLE:                 setOpaque(false); setBackground(Color.LIGHT_GRAY);    break;
         case BUILDING:             setOpaque(true);  setBackground(BUILDING_COLOR);      break;
         case FAILED:               setOpaque(true);  setBackground(Color.ORANGE);        break;
         case FINISHED_FAILURE:     setOpaque(true);  setBackground(Color.RED);           break;
         case FINISHED_EXCEPTION:   setOpaque(true);  setBackground(Color.RED);           break;
         case FINISHED_SUCESSFULLY: setOpaque(false); setBackground(Color.LIGHT_GRAY);    break;
      }
      */
      
      if (isSelected) {
         setBorder(BorderFactory.createLineBorder(list.getSelectionBackground()));
      } else {
         setBorder(BorderFactory.createLineBorder(list.getBackground()));
      }
      
     
      
      String commandString = job.getCommand().toString();
      if (commandString.length() > 40) {
         commandString = commandString.substring(0, 40) + " ...";
      }
      
      command.setText(commandString);
      state.setText(job.getState().toString());
      currentTask.setText(job.getCurrentTask());
      
      return this;
   }

}

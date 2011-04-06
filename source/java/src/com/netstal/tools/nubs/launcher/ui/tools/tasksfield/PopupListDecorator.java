package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JList;

public class PopupListDecorator  {

   private JList popupList;
   private List<ActionListener> actionListeners;

   public PopupListDecorator(JList list) {
      this.popupList = list;
      this.actionListeners = new LinkedList<ActionListener>();
      list.addMouseListener(new MouseAdapter() {

         @Override
         public void mousePressed(MouseEvent e) {
            ActionEvent actionEvent = new ActionEvent(e.getSource(), e.getID(), "");
            for (ActionListener actionListener : actionListeners) {
               actionListener.actionPerformed(actionEvent);
            }
         }        
            
      });
      
      list.addMouseMotionListener(new MouseAdapter() {
         
         @Override
         public void mouseMoved(MouseEvent e) {
            int index = popupList.locationToIndex(e.getPoint());
            if (index >= 0) {
               popupList.setSelectedIndex(index);               
            }
         }

      });
   }
   
   public void addActionListener(ActionListener actionListener) {
      actionListeners.add(actionListener);
   }
   
   public boolean removeActionListener(ActionListener actionListener) {
      return actionListeners.remove(actionListener);
   }
   
}

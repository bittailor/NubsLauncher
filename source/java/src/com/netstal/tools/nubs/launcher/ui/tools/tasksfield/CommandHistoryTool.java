//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.ICommandHistory;

public class CommandHistoryTool extends AbstractTool {

   private ICommandHistory commandHistory;
   private JList historyList;
   private DefaultListModel historyModel;
   private JPopupMenu historyPopup;
   
   @Inject
   public CommandHistoryTool(ICommandHistory commandHistory) {
      super(KeyStroke.getKeyStroke(KeyEvent.VK_H, InputEvent.CTRL_MASK));
      this.commandHistory = commandHistory;
  
      historyModel = new DefaultListModel();
      historyList = new JList(historyModel);
      historyList.setFocusable(false);
      
      JScrollPane scrollPane = new JScrollPane(historyList);
      scrollPane.setBorder(null);
      scrollPane.getVerticalScrollBar().setFocusable(false);
      scrollPane.getHorizontalScrollBar().setFocusable(false);

      historyPopup = new JPopupMenu();
      historyPopup.setBorder(BorderFactory.createTitledBorder("History"));
      historyPopup.add(scrollPane);
      historyPopup.setFocusable(false);
      
   }

   @Override
   public void activate() {
      historyModel.clear();
      for (Command command : commandHistory) {
         historyModel.addElement(command);
      }
      showPopup();
   }
   
   @Override
   public void keyUp() {
      changeSelection(historyList.getSelectedIndex()-1);
   }

   @Override
   public void keyDown() {
      changeSelection(historyList.getSelectedIndex()+1);
   }
   
   private void changeSelection(int newIndex) {
      int indexInBound = Math.min(Math.max(0,newIndex),historyModel.getSize());
      historyList.setSelectedIndex(indexInBound);
      historyList.ensureIndexIsVisible(indexInBound);
   }

   @Override
   public void enter(ActionEvent event) {
      Object selectedValue = historyList.getSelectedValue();
      if (selectedValue == null) {
         return;
      }
      getTasksField().setText(selectedValue.toString());
      closePopup();
   }
   
   @Override
   public void escape(ActionEvent event) {
      closePopup();
   }
   
   private void showPopup() {
      historyPopup.setPreferredSize(new Dimension(getTasksField().getWidth(), getTasksField().getHeight()*10));
      historyPopup.show(getTasksField(), 0, getTasksField().getHeight());
      getTasksField().requestFocus();
   }
   
   private void closePopup() {
      historyPopup.setVisible(false);
   }

}

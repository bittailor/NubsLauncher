package com.netstal.tools.nubs.launcher.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListModel;

import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.ICommandHistory;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;

public class RakeTextField extends JPanel {
   
   private static final long serialVersionUID = 1L;
   private static final String KEY_DOWN = "Key Down";
   private static final String KEY_UP = "Key Up";
   private static final Object ESCAPE = "Escape";
 
   private IRakeTextFieldTool currentTool;
   
   private JTextField suggestTextField;
   private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
   private final IRakeTextFieldTool defaultTool;
   
   public RakeTextField(ListModel suggestListModel, ICommandHistory commandHistory, IWorkspace workspace) {
      defaultTool = new RakeRunnerTool(this,commandHistory,workspace);
      currentTool = defaultTool;
      createUi();
      createActions();
   }
   
   public void addTool(final IRakeTextFieldTool tool) {
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(tool.getKeyCode(), tool.getModifiers()),tool);
      suggestTextField.getActionMap().put(tool,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool = tool;
            tool.activate();
         }     
      });
   }
   
   
   public void addActionListener(ActionListener listener) {
      actionListeners.add(listener);
   }
   
   public void removeActionListener(ActionListener listener) {
      actionListeners.remove(listener);
   }
   
   public String getText() {
      return suggestTextField.getText();
   }
   
   private void createUi() {
      suggestTextField = new JTextField();
      suggestTextField.setPreferredSize(new Dimension(1000,suggestTextField.getPreferredSize().height));
      add(suggestTextField);  
   }

   private void createActions() {  
      addActionListener(new ActionListener() {      
         @Override
         public void actionPerformed(ActionEvent e) {
            currentTool.enter(e);
            currentTool = defaultTool;
         }
      });     
      
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),ESCAPE);
      suggestTextField.getActionMap().put(ESCAPE,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool.escape(actionEvent);
            currentTool = defaultTool;
         }     
      });
      
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),KEY_DOWN);
      suggestTextField.getActionMap().put(KEY_DOWN,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool.keyDown();
         }     
      });
      
      suggestTextField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),KEY_UP);
      suggestTextField.getActionMap().put(KEY_UP,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool.keyUp();
         }     
      });
        
      suggestTextField.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent event) {
            currentTool.enter(event);
            currentTool = defaultTool;
         }
      });
   }
     
   public void notifyActionListeners(ActionEvent event) {
      for (ActionListener listener : actionListeners) {
         listener.actionPerformed(event);
      }
   }

   /**
    * @param command
    */
   public void setText(Command command) {
      suggestTextField.setText(command.toString());
   }

   /**
    * @return
    */
   public JTextField getTextField() {
      return suggestTextField;
   }
   
}

package com.netstal.tools.nubs.launcher.ui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.ITool;
import com.netstal.tools.nubs.launcher.ui.tools.tasksfield.IToolsFactory;

public class RakeTasksField extends JPanel {
   
   private static final long serialVersionUID = 1L;
   private static final String KEY_DOWN = "Key Down";
   private static final String KEY_UP = "Key Up";
   private static final Object ESCAPE = "Escape";
 
   private ITool currentTool;
   private JTextField tasksField;
   private final ITool defaultTool;
   
   @Inject
   public RakeTasksField(IToolsFactory tasksFieldToolsFactory) {
      createUi();
      createActions();
      for (ITool tool : tasksFieldToolsFactory.createTools()) {
         addTool(tool);
      }
      defaultTool = tasksFieldToolsFactory.createDefaultTool();
      currentTool = defaultTool;
      defaultTool.initialize(tasksField);
   }
   
   private void addTool(final ITool tool) {
      tool.initialize(tasksField);
      tasksField.getActionMap().put(tool,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool = tool;
            tool.activate();
         }     
      });
   }
   
   private void createUi() {
      tasksField = new JTextField();
      tasksField.setPreferredSize(new Dimension(1000,tasksField.getPreferredSize().height));
      add(tasksField);  
   }

   private void createActions() {     
      tasksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,0),ESCAPE);
      tasksField.getActionMap().put(ESCAPE,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool.escape(actionEvent);
            currentTool = defaultTool;
         }     
      });
      
      tasksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,0),KEY_DOWN);
      tasksField.getActionMap().put(KEY_DOWN,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool.keyDown();
         }     
      });
      
      tasksField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_UP,0),KEY_UP);
      tasksField.getActionMap().put(KEY_UP,new AbstractAction() {
         private static final long serialVersionUID = 1L;
         @Override
         public void actionPerformed(ActionEvent actionEvent) {
            currentTool.keyUp();
         }     
      });
        
      tasksField.addActionListener(new ActionListener() {
         @Override
         public void actionPerformed(ActionEvent event) {
            currentTool.enter(event);
            currentTool = defaultTool;
         }
      });
   }
     
   public JTextField getTextField() {
      return tasksField;
   }
   
}

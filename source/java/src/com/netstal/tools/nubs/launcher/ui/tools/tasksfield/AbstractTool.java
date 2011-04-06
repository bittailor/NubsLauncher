//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import javax.swing.JTextField;
import javax.swing.KeyStroke;

public abstract class AbstractTool implements ITool {

   private final KeyStroke triggerKeyStroke;
   private JTextField tasksField;
   private IToolListener listener;
   
   public AbstractTool(KeyStroke triggerKeyStroke) {
      this.triggerKeyStroke = triggerKeyStroke;
   }

   @Override
   public void initialize(JTextField tasksTextField, IToolListener toolListener) {
      this.tasksField = tasksTextField;
      this.listener = toolListener;
      tasksField.getInputMap().put(triggerKeyStroke,this);
   }

   protected JTextField getTasksField() {
      return tasksField;
   }

   protected IToolListener getListener() {
      return listener;
   }
   
   
}

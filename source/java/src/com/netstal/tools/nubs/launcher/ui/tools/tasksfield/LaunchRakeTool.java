//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JTextField;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;

public final class LaunchRakeTool implements ITool {

   private static Logger LOG = Logger.getLogger(LaunchRakeTool.class.getName());
   
   private final IRakeLauncher launcher;
   private JTextField tasksField;
   
   @Inject
   public LaunchRakeTool(IRakeLauncher launcher) {
      this.launcher = launcher;
   }

   @Override
   public void initialize(JTextField tasksTextField) {
      this.tasksField = tasksTextField;
   }

   @Override
   public void activate() {
   }

   @Override
   public void keyUp() {
   }

   @Override
   public void keyDown() {
   }

   @Override
   public void enter(ActionEvent event) {
      try {
         Command command = new Command(tasksField.getText());
         launcher.launch(command);
      }
      catch (IOException exception) {
         LOG.log(Level.SEVERE, "Problem Launching Rake", exception);
      }      
   }

   @Override
   public void escape(ActionEvent event) {
   }
   
}

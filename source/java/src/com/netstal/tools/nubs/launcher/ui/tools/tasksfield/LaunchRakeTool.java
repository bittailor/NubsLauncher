package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;

public final class LaunchRakeTool implements ITool {
   
   private final IRakeLauncher launcher;
   private JTextField tasksField;
   private IToolListener listener;

   
   @Inject
   public LaunchRakeTool(IRakeLauncher launcher) {
      this.launcher = launcher;
   }

   @Override
   public void initialize(JTextField tasksTextField, IToolListener toolListener) {
      this.tasksField = tasksTextField;
      this.listener = toolListener;
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
      Command command = new Command(tasksField.getText());
      launcher.launch(command);
      listener.finished();
   }

   @Override
   public void escape(ActionEvent event) {
      listener.finished();
   }
   
}

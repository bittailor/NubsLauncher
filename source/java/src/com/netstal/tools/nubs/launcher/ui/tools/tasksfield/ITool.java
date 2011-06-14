package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.awt.event.ActionEvent;

import javax.swing.JTextField;

public interface ITool {
   
   public void initialize(JTextField tasksTextField, IToolListener toolListener);
   
   public void activate();
   public void keyUp();
   public void keyDown();
   public void enter(ActionEvent event);
   public void escape(ActionEvent event);

}

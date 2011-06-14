package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class LoadTasksPanel extends JPanel {

   private static final long serialVersionUID = 1L;

   public LoadTasksPanel() {
      super(new BorderLayout());
      Icon loadingImage = new ImageIcon(getClass().getResource("images/Loading.gif"));
      add(new JLabel("Loading Rake Tasks",loadingImage,SwingConstants.CENTER)); 
      setBackground(Color.WHITE);
   }  
}

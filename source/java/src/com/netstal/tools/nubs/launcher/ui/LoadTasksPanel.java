//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

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

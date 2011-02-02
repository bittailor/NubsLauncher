//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui;

import java.awt.event.ActionEvent;

public interface IRakeTextFieldTool {
   
   public int getKeyCode(); 
   public int getModifiers();
   
   public void activate();
   public void keyUp();
   public void keyDown();
   public void enter(ActionEvent event);
   public void escape(ActionEvent event);

}

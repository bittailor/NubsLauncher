//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui;

import java.awt.event.ActionEvent;
import java.io.IOException;

import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.ICommandHistory;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.infrastructure.RakeCmdLauncher;

/**
 * 
 */
public final class RakeRunnerTool implements IRakeTextFieldTool {
   private final RakeTextField suggestField;
   private final ICommandHistory commandHistory;
   private final IWorkspace workspace;
   
   public RakeRunnerTool(RakeTextField suggestField, ICommandHistory commandHistory, IWorkspace workspace) {
      this.suggestField = suggestField;
      this.commandHistory = commandHistory;
      this.workspace = workspace;
   }

   @Override
   public int getKeyCode() {
      return 0;
   }

   @Override
   public int getModifiers() {
      return 0;
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
      System.out.println("Execute " + suggestField.getText());
      
      try {
         Command command = new Command(suggestField.getText());
         commandHistory.push(command);
         new RakeCmdLauncher().launch(command,workspace.getRoot());
      }
      catch (IOException exception) {
         exception.printStackTrace();
      }      
   }

   @Override
   public void escape(ActionEvent event) {
   }

}

//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.io.IOException;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.infrastructure.IConsoleLauncher;


public class ConsoleRakeLauncher implements IRakeLauncher {

   private static final String RAKE = "rake ";
   private IConsoleLauncher consoleLauncher;
   private ICommandHistory commandHistory;
   private IWorkspace workspace;
   
   @Inject
   public ConsoleRakeLauncher(IConsoleLauncher consoleLauncher, ICommandHistory commandHistory, IWorkspace workspace) {
      this.consoleLauncher = consoleLauncher;
      this.commandHistory = commandHistory;
      this.workspace = workspace;
   }
   
   @Override
   public void launch(Command command) throws IOException {
      commandHistory.push(command);
      consoleLauncher.launch(RAKE+command.toString(), workspace.getRoot());
   }
   
}

//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;

import org.easymock.IMocksControl;
import org.junit.Test;

import com.netstal.tools.nubs.launcher.infrastructure.IConsoleLauncher;

public class ConsoleRakeLauncherTest {


   @Test
   public void testLaunch() throws IOException {
      
      Command command = new Command("just a test command");
      File directory = new File("workspaceRoot");

      IMocksControl control = createControl();
      
      IConsoleLauncher consoleLauncher = control.createMock("consoleLauncher",IConsoleLauncher.class);
      ICommandHistory commandHistory = control.createMock("commandHistory",ICommandHistory.class);
      IWorkspace workspace = control.createMock("workspace",IWorkspace.class);
      
      expect(workspace.getRoot()).andReturn(directory);
      commandHistory.push(command);
      consoleLauncher.launch(Command.RAKE+ " " + command.toString(), directory);
      
      control.replay();
      
      ConsoleRakeLauncher consoleRakeLauncher = new ConsoleRakeLauncher(consoleLauncher, commandHistory, workspace);
      
      consoleRakeLauncher.launch(command);
      
      control.verify();
   }

}

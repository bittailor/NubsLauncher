//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;

public interface IConsoleLauncher {

   public abstract void launch(String command, File directory) throws IOException;

}

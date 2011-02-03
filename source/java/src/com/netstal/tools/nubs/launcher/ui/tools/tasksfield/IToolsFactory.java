//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

public interface IToolsFactory {
   public ITool createDefaultTool();
   public Iterable<ITool> createTools();
}

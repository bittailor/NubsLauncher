//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * 
 */
public class ToolsFactory implements IToolsFactory {

   private Provider<LaunchRakeTool> launchRakeProvider;
   private Provider<SuggestTaskTool> suggestTaskProvider;
   private Provider<CommandHistoryTool> commandHistoryProvider ;
   
   @Inject
   public ToolsFactory(Provider<LaunchRakeTool> launchRakeProvider, Provider<SuggestTaskTool> suggestRakeTargetProvider, Provider<CommandHistoryTool> rakeCommandHistoryProvider) {
      this.launchRakeProvider = launchRakeProvider;
      this.suggestTaskProvider = suggestRakeTargetProvider;
      this.commandHistoryProvider = rakeCommandHistoryProvider;
   }
   
   @Override
   public ITool createDefaultTool() {
      return launchRakeProvider.get();
   }
   
   @Override   public Iterable<ITool> createTools() {
      List<ITool> tools = new ArrayList<ITool>();
      tools.add(suggestTaskProvider.get());
      tools.add(commandHistoryProvider.get());
      return tools;
   }

}

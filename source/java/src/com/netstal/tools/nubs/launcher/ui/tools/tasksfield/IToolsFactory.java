package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

public interface IToolsFactory {
   public ITool createDefaultTool();
   public Iterable<ITool> createTools();
}

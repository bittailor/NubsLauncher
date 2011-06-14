package com.netstal.tools.nubs.launcher.domain;

import java.util.List;


public interface IFilterChain extends IEventSource<IFilterChain> {

   public List<RakeTask> getFilteredTasks();
   
   public void setSuggestFilter(String text);
   
}

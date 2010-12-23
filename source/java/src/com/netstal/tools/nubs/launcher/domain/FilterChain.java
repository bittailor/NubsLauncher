//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.ArrayList;
import java.util.List;

import com.google.inject.Inject;

public class FilterChain extends EventSource<IFilterChain> implements IFilterChain {

   IWorkspace workspace;
   List<RakeTask> filteredTasks = new ArrayList<RakeTask>();
   private String suggestfilter = ".*";
   
   @Inject
   public FilterChain(IWorkspace workspace) {
      super();
      this.workspace = workspace;
      filteredTasks.addAll(workspace.getTasks());
      workspace.addListener(new IEventListener<IWorkspace>() {
         @Override
         public void notify(IWorkspace source) {
            filter();
         }
      });
   }

   @Override
   public void setSuggestFilter(String text) {
      suggestfilter = 
         text
         .trim()
         .replaceAll("([A-Z][a-z]*)", "$1[A-Za-z_]*")
         .replaceAll("(_[A-Za-z][a-z]*)", "$1[A-Za-z_]*")
         + ".*" ;
      filter();    
   }

   @Override
   public List<RakeTask> getFilteredTasks() {
      return filteredTasks;
   }

   private void filter() {
      filteredTasks.clear();
      for (RakeTask task : workspace.getTasks() ) {
         if (task.getName().matches(suggestfilter)) {
            filteredTasks.add(task);                     
         }
      }
      notify(this);
   }
}

//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui.tools.tasksfield;

import java.util.List;

import javax.swing.AbstractListModel;

import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IFilterChain;
import com.netstal.tools.nubs.launcher.domain.RakeTask;

public class SuggestListModel extends AbstractListModel {

   private static final long serialVersionUID = 1L;
   private IFilterChain filterChain;
   private int oldSize;
   
   public SuggestListModel(IFilterChain filterChain) {
      super();
      this.filterChain = filterChain;
      oldSize = getTasks().size();
      filterChain.addListener(new IEventListener<IFilterChain>() {
         @Override
         public void notify(IFilterChain source) {
            fireIntervalRemoved(this, 0, oldSize);
            fireIntervalAdded(this, 0, getTasks().size());
            oldSize = getTasks().size(); 
         }
      });
   }

   @Override
   public int getSize() {
      return getTasks().size();
   }

   @Override
   public Object getElementAt(int index) {
      return getTasks().get(index).getName();
   }
   
   private List<RakeTask> getTasks() {
      return filterChain.getFilteredTasks();
   }

}

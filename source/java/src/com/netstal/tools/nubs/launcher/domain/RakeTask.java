package com.netstal.tools.nubs.launcher.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class RakeTask implements Serializable {

   private final String name;
   private List<RakeTask> dependencies = new ArrayList<RakeTask>();
   private boolean dependencyCycleGuard;

   public RakeTask(String name) {
      this.name = name;
      dependencyCycleGuard = false;
   }
   
   public String getName() {
      return name;
   }

   public void addDependency(RakeTask task) {
      dependencies.add(task);
      
   }

   public void fillDependencySet(Set<RakeTask> dependecySet) {
      if (dependencyCycleGuard) {
         // TODO own exception
         throw new RuntimeException("Circular dependency: " + name);
      }
      dependecySet.add(this);
      dependencyCycleGuard = true;
      for (RakeTask rakeTask : dependencies) {
         rakeTask.fillDependencySet(dependecySet);
      }
      dependencyCycleGuard = false;
   }
   
}

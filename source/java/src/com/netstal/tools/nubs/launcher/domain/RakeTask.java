package com.netstal.tools.nubs.launcher.domain;

import java.util.ArrayList;
import java.util.List;

public class RakeTask {

   private final String name;
   private List<RakeTask> dependencies = new ArrayList<RakeTask>();

   public RakeTask(String name) {
      this.name = name;
   }
   
   public String getName() {
      return name;
   }

   public void addDependency(RakeTask task) {
      dependencies.add(task);
      
   }
   
}

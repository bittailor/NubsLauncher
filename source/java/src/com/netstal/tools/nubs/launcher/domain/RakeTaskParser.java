package com.netstal.tools.nubs.launcher.domain;

import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RakeTaskParser implements IRakeTaskParser {
   
   Pattern taskPattern = Pattern.compile("rake\\s(\\S+)");
   Pattern dependencyPattern = Pattern.compile("\\s+(\\S+)");
   
   private SortedMap<String,RakeTask> tasks = new TreeMap<String,RakeTask>();
   private RakeTask currentTask;
  
   @Override
   public SortedMap<String, RakeTask> getTasks() {
      return tasks;
   }
   
   @Override
   public void consumeLine(String line) {
      parseLine(line);
   }

   private void parseLine(String line) {
      if(matchTask(line)){
         return;
      }
      matchDependency(line);
   }
   
   private boolean matchTask(String line) {
      Matcher matcher = taskPattern.matcher(line);
      if (matcher.matches()) {
         currentTask = task(matcher.group(1));
         return true;
      }
      return false;
   }
   
   private boolean matchDependency(String line) {
      Matcher matcher = dependencyPattern.matcher(line);
      if (matcher.matches()) {
         currentTask.addDependency(task(matcher.group(1))); 
         return true;
      }
      return false;
   }
   
   private RakeTask task(String name) {
      if (tasks.containsKey(name)){
         return tasks.get(name);
      }
      RakeTask rakeTask = new RakeTask(name);
      tasks.put(rakeTask.getName(), rakeTask);
      return rakeTask;
   }

}

package com.netstal.tools.nubs.launcher.domain;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.inject.Inject;

public class RakeOutputParser implements IRakeBuildOutputParser {

      // --> Try build Fail_build_dpu_only again (y)es (n)o or just (i)gnore it and continue? [y|n|i]>
   private static Pattern RETRY_PATTERN = Pattern.compile("--> Try build (\\S+) again \\(y\\)es \\(n\\)o or just \\(i\\)gnore it and continue\\? \\[y\\|n\\|i\\]>");
      // ** Execute ProjectOne_build_ipc_only
   private static Pattern EXECUTE_TASK = Pattern.compile("\\*\\* Execute (\\S+)");
   
   private List<IRakeBuildOutputListener> listeners;
   
   @Inject
   public RakeOutputParser() {
      this.listeners = new LinkedList<IRakeBuildOutputListener>();
   }
   
   @Override
   public void addListener(IRakeBuildOutputListener listener) {
      listeners.add(listener);
   }
   
   @Override
   public void removeListener(IRakeBuildOutputListener listener) {
      listeners.remove(listener);
   }

   @Override
   public void consumeLine(String line) {
      Matcher matcher = RETRY_PATTERN.matcher(line);
      if (matcher.matches()) {
         retryDetected(matcher);
      }
      matcher = EXECUTE_TASK.matcher(line);
      if (matcher.matches()) {
         executeTaskDetected(matcher);
      }
   }


   private void executeTaskDetected(Matcher matcher) {
      String taskName = matcher.group(1);
      for (IRakeBuildOutputListener listener : listeners) {
         listener.notifyExecuteTask(taskName);         
      }
   }

   private void retryDetected(Matcher matcher) {
      String taskName = matcher.group(1);
      for (IRakeBuildOutputListener listener : listeners) {
         listener.notifyTaskFailed(taskName);
      }
      System.out.println("retryDetected : "+ taskName);
   }

}

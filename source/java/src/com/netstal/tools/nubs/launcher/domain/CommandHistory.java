//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.Iterator;
import java.util.LinkedList;

import com.google.inject.Inject;

public class CommandHistory implements ICommandHistory {

   public static final String KEY_MAX_SIZE = "commandHistory.MaxSize";
   
   private final int maxSize;
   private LinkedList<Command> history;
     
   @Inject
   public CommandHistory(IConfiguration configuration) {
      maxSize = configuration.getInteger(KEY_MAX_SIZE);
      history = new LinkedList<Command>();
   }

   @Override
   public void push(Command command) {
      history.remove(command);
      history.addFirst(command);
      if (history.size() > maxSize) {
         history.removeLast();
      }
   }

   @Override
   public Iterator<Command> iterator() {
      return history.iterator();
   }
   
   public int size() {
      return history.size();
   }

}

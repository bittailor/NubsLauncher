//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.Iterator;
import java.util.LinkedList;

public class CommandHistory implements ICommandHistory {

   // TODO - fsc - preferences - get history size from preferences.
   public static final int MAXIMAL_SIZE = 20;
   
   private LinkedList<Command> history = new LinkedList<Command>();
   
   @Override
   public void push(Command command) {
      history.remove(command);
      history.addFirst(command);
      if (history.size() > MAXIMAL_SIZE) {
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

//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.Iterator;
import java.util.LinkedList;

/**
 * 
 */
public class CommandHistory implements ICommandHistory {

   private LinkedList<Command> history = new LinkedList<Command>();
   
   @Override
   public void push(Command command) {
      history.remove(command);
      history.addFirst(command);
   }

   @Override
   public Command get(int index) {
      index = index % Math.max(1,history.size());
      if(index < 0) {
         index = history.size() + index;
      }
      try {
         return history.get(index);
      }
      catch (IndexOutOfBoundsException exception) {
         return new Command("");
      }
   }

   @Override
   public Iterator<Command> iterator() {
      return history.iterator();
   }

}

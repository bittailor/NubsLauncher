//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

public class CommandHistoryTest {

   private CommandHistory commandHistory;

   @Before
   public void setUp() {
      commandHistory = new CommandHistory();
   }

   @Test
   public void testMaximalHistorySize() {
      int mean = CommandHistory.MAXIMAL_SIZE / 2;
      
      for (int i = 0; i < mean ; i++) {
         commandHistory.push(createCommand(i));
      }
      
      assertEquals(mean, commandHistory.size());
      
      for (int i = mean ; i < CommandHistory.MAXIMAL_SIZE + 10 ; i++) {
         commandHistory.push(createCommand(i));
      }  
      assertEquals(CommandHistory.MAXIMAL_SIZE, commandHistory.size());
      
   }
   
   @Test
   public void testOrderOfHistory() {
      commandHistory.push(createCommand(1));
      commandHistory.push(createCommand(2));
      commandHistory.push(createCommand(3));
      commandHistory.push(createCommand(4));
      Iterator<Command> iterator = commandHistory.iterator();
      assertEquals(createCommand(4), iterator.next());
      assertEquals(createCommand(3), iterator.next());
      assertEquals(createCommand(2), iterator.next());
      assertEquals(createCommand(1), iterator.next());
   }
   
   @Test
   public void testMoveToFrontOnUse() {
      commandHistory.push(createCommand(1));
      commandHistory.push(createCommand(2));
      commandHistory.push(createCommand(3));
      commandHistory.push(createCommand(4));
      
      commandHistory.push(createCommand(2));
      
      assertEquals(4, commandHistory.size());
      
      Iterator<Command> iterator = commandHistory.iterator();
      assertEquals(createCommand(2), iterator.next());
      assertEquals(createCommand(4), iterator.next());
      assertEquals(createCommand(3), iterator.next());
      assertEquals(createCommand(1), iterator.next());
   }

   private Command createCommand(int i) {
      return new Command("C"+i);
   }
}

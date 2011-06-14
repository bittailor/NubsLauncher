package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.util.Iterator;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class CommandHistoryTest {

   private static final int MAXIMAL_SIZE = 20;
   private CommandHistory commandHistory;
   private IMocksControl control;
   private IConfiguration configuration;

   @Before
   public void setUp() {
      control = createControl();
      configuration = control.createMock("configuration",IConfiguration.class);
      
      expect(configuration.getInteger(CommandHistory.KEY_MAX_SIZE)).andReturn(MAXIMAL_SIZE).anyTimes();     
      
      control.replay();
      
      commandHistory = new CommandHistory(configuration);
   }

   @Test
   public void testMaximalHistorySize() {
      
      int mean = MAXIMAL_SIZE / 2;
      
      for (int i = 0; i < mean ; i++) {
         commandHistory.push(createCommand(i));
      }
      
      assertEquals(mean, commandHistory.size());
      
      for (int i = mean ; i < MAXIMAL_SIZE + 10 ; i++) {
         commandHistory.push(createCommand(i));
      }  
      assertEquals(MAXIMAL_SIZE, commandHistory.size());
      
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

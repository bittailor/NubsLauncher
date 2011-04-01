package com.netstal.tools.nubs.launcher.infrastructure;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class NotificationServiceTest {

   private IMocksControl control;
   private Emmiter emitterOne;
   private Emmiter emitterTwo;
   private IEmmiterListener listenerOne;
   private IEmmiterListener listenerTwo;
   private IEmmiterListener listenerThree;

   @Before
   public void setUp() throws Exception {
      control = createControl();
      
      emitterOne = new Emmiter(); 
      emitterTwo = new Emmiter(); 
      
      listenerOne = control.createMock("listenerOne", IEmmiterListener.class);
      listenerTwo = control.createMock("listenerTwo", IEmmiterListener.class);
      listenerThree = control.createMock("listenerThree", IEmmiterListener.class);
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testOneEmmiterTwoListener() {
      
      listenerOne.newLine("Call One");
      listenerTwo.newLine("Call One");
      
      control.replay();
      
      NotificationService notificationBus = new NotificationService();
      IEmmiterListener notificationDispatcher = notificationBus.getNotificationDispatcher(IEmmiterListener.class, emitterOne);
      
      notificationBus.addListener(IEmmiterListener.class, listenerOne, emitterOne);
      notificationBus.addListener(IEmmiterListener.class, listenerTwo, emitterOne);
      
      notificationDispatcher.newLine("Call One");
      
      control.verify();
      
   }
   
   @Test
   public void testWeakEmitterReference() {
      int notifyLoop = 1000;
      
      
      listenerOne.newLine("Bla");
      expectLastCall().times(notifyLoop);
      listenerTwo.newLine("Bla");
      expectLastCall().times(notifyLoop);
      
      control.replay();

      NotificationService notificationBus = new NotificationService();
      
      
      for (int i=0 ; i < notifyLoop ; i++) {
         Emmiter localEmitterOne = new Emmiter();
         IEmmiterListener notificationDispatcher = notificationBus.getNotificationDispatcher(IEmmiterListener.class, localEmitterOne);
         notificationBus.addListener(IEmmiterListener.class, listenerOne, localEmitterOne);
         notificationBus.addListener(IEmmiterListener.class, listenerTwo, localEmitterOne);
         notificationDispatcher.newLine("Bla");
         localEmitterOne = null;    
      }
      
      // we loop here because the gc is not really forced, therefore we requesting it a couple of times
      int loopCounter = 0;
      while(notificationBus.getListenerStorage(IEmmiterListener.class).qualifiedListeners.size() > 0) {
         System.gc();
         loopCounter++;
         assertTrue("emitter reference could not be cleard", loopCounter < 50 );
      }
      
      System.out.println(loopCounter);
  
      control.verify();
      
   }
   
   @Test
   public void testAddRemoveListener() {
      
      listenerOne.newLine("Call 2");
      listenerOne.newLine("Call 3");
      listenerOne.newLine("Call 4");
      
      listenerTwo.newLine("Call 3");
      listenerTwo.newLine("Call 4");
      listenerTwo.newLine("Call 5");
      listenerTwo.newLine("Call 6");
      
      listenerThree.newLine("Call 4");
      listenerThree.newLine("Call 5");
      
      control.replay();
      
      NotificationService notificationBus = new NotificationService();
      IEmmiterListener notificationDispatcher = notificationBus.getNotificationDispatcher(IEmmiterListener.class, emitterOne);
      
      notificationDispatcher.newLine("Call 1");
      
      notificationBus.addListener(IEmmiterListener.class, listenerOne, emitterOne);
      
      notificationDispatcher.newLine("Call 2");
      
      notificationBus.addListener(IEmmiterListener.class, listenerTwo, emitterOne);
      
      notificationDispatcher.newLine("Call 3");
      
      notificationBus.addListener(IEmmiterListener.class, listenerThree);
      
      notificationDispatcher.newLine("Call 4");
      
      assertTrue(notificationBus.removeListener(IEmmiterListener.class, listenerOne, emitterOne));
      
      notificationDispatcher.newLine("Call 5");
      
      assertFalse(notificationBus.removeListener(IEmmiterListener.class, listenerOne, emitterOne));
      assertTrue(notificationBus.removeListener(IEmmiterListener.class, listenerThree));
      
      notificationDispatcher.newLine("Call 6");
      
      assertFalse(notificationBus.removeListener(IEmmiterListener.class, listenerThree));
      assertTrue(notificationBus.removeListener(IEmmiterListener.class, listenerTwo, emitterOne));
      
      notificationDispatcher.newLine("Call 7");
      
      control.verify();

   }
   
   @Test
   public void testTwoEmmiterOneListener() {
      
      listenerOne.newLine("Call One");
      listenerTwo.newLine("Call Two");
      
      control.replay();
      
      NotificationService notificationBus = new NotificationService();
      
      notificationBus.addListener(IEmmiterListener.class, listenerOne, emitterOne);
      notificationBus.addListener(IEmmiterListener.class, listenerTwo, emitterTwo);
      
      notificationBus.getNotificationDispatcher(IEmmiterListener.class, emitterOne).newLine("Call One");
      notificationBus.getNotificationDispatcher(IEmmiterListener.class, emitterTwo).newLine("Call Two");
      
      control.verify();
      
   }

 

   private static interface IEmmiter {
   
   }
   
   private static interface IEmmiterListener extends IListener {
      void newLine(String message);
   }
   
   private static class Emmiter implements IEmmiter {
      
   }
   
   
}

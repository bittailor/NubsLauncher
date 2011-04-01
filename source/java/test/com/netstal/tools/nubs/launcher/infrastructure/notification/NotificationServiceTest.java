package com.netstal.tools.nubs.launcher.infrastructure.notification;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.netstal.tools.nubs.launcher.infrastructure.notification.IListener;
import com.netstal.tools.nubs.launcher.infrastructure.notification.NotificationService;

public class NotificationServiceTest {

   private IMocksControl control;
   private TestEmitter emitterOne;
   private TestEmitter emitterTwo;
   private ITestEmitterListener listenerOne;
   private ITestEmitterListener listenerTwo;
   private ITestEmitterListener listenerThree;

   @Before
   public void setUp() throws Exception {
      control = createControl();
      
      emitterOne = new TestEmitter(); 
      emitterTwo = new TestEmitter(); 
      
      listenerOne = control.createMock("listenerOne", ITestEmitterListener.class);
      listenerTwo = control.createMock("listenerTwo", ITestEmitterListener.class);
      listenerThree = control.createMock("listenerThree", ITestEmitterListener.class);
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
      ITestEmitterListener notificationDispatcher = notificationBus.getNotificationDispatcher(ITestEmitterListener.class, emitterOne);
      
      notificationBus.addListener(ITestEmitterListener.class, listenerOne, emitterOne);
      notificationBus.addListener(ITestEmitterListener.class, listenerTwo, emitterOne);
      
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
         TestEmitter localEmitterOne = new TestEmitter();
         ITestEmitterListener notificationDispatcher = notificationBus.getNotificationDispatcher(ITestEmitterListener.class, localEmitterOne);
         notificationBus.addListener(ITestEmitterListener.class, listenerOne, localEmitterOne);
         notificationBus.addListener(ITestEmitterListener.class, listenerTwo, localEmitterOne);
         notificationDispatcher.newLine("Bla");
         localEmitterOne = null;    
      }
      
      // we loop here because the gc is not really forced, therefore we requesting it a couple of times
      int loopCounter = 0;
      while(notificationBus.getListenerStorage(ITestEmitterListener.class).qualifiedListeners.size() > 0) {
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
      ITestEmitterListener notificationDispatcher = notificationBus.getNotificationDispatcher(ITestEmitterListener.class, emitterOne);
      
      notificationDispatcher.newLine("Call 1");
      
      notificationBus.addListener(ITestEmitterListener.class, listenerOne, emitterOne);
      
      notificationDispatcher.newLine("Call 2");
      
      notificationBus.addListener(ITestEmitterListener.class, listenerTwo, emitterOne);
      
      notificationDispatcher.newLine("Call 3");
      
      notificationBus.addListener(ITestEmitterListener.class, listenerThree);
      
      notificationDispatcher.newLine("Call 4");
      
      assertTrue(notificationBus.removeListener(ITestEmitterListener.class, listenerOne, emitterOne));
      
      notificationDispatcher.newLine("Call 5");
      
      assertFalse(notificationBus.removeListener(ITestEmitterListener.class, listenerOne, emitterOne));
      assertTrue(notificationBus.removeListener(ITestEmitterListener.class, listenerThree));
      
      notificationDispatcher.newLine("Call 6");
      
      assertFalse(notificationBus.removeListener(ITestEmitterListener.class, listenerThree));
      assertTrue(notificationBus.removeListener(ITestEmitterListener.class, listenerTwo, emitterOne));
      
      notificationDispatcher.newLine("Call 7");
      
      control.verify();

   }
   
   @Test
   public void testTwoEmmiterOneListener() {
      
      listenerOne.newLine("Call One");
      listenerTwo.newLine("Call Two");
      
      control.replay();
      
      NotificationService notificationBus = new NotificationService();
      
      notificationBus.addListener(ITestEmitterListener.class, listenerOne, emitterOne);
      notificationBus.addListener(ITestEmitterListener.class, listenerTwo, emitterTwo);
      
      notificationBus.getNotificationDispatcher(ITestEmitterListener.class, emitterOne).newLine("Call One");
      notificationBus.getNotificationDispatcher(ITestEmitterListener.class, emitterTwo).newLine("Call Two");
      
      control.verify();
      
   }

 

   private static interface ITestEmitter extends IEmitter {
   
   }
   
   private static interface ITestEmitterListener extends IListener {
      void newLine(String message);
   }
   
   private static class TestEmitter implements ITestEmitter {
      
   }
   
   
}

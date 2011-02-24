package com.netstal.tools.nubs.launcher.infrastructure;


import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expectLastCall;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ThreadWrapperTest {

   private Runnable runnable;
   private IMocksControl control;

   @Before
   public void setUp() throws Exception {
      control = createControl();
      runnable = control.createMock("runnable", Runnable.class);
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testGood() throws InterruptedException {
      runnable.run();
      
      control.replay();
      
      ThreadWrapper threadWrapper = new ThreadWrapper(runnable);
      
      threadWrapper.start();
      threadWrapper.join();
      
      control.verify();
   }
   
   @Test(expected=RuntimeException.class)
   public void testRuntimeException() throws InterruptedException {
      runnable.run();
      expectLastCall().andThrow(new RuntimeException());
      
      control.replay();
      
      ThreadWrapper threadWrapper = new ThreadWrapper(runnable);
      
      threadWrapper.start();
      threadWrapper.join();
      
      control.verify();
   }
   
   @Test(expected=Error.class)
   public void testRuntimeError() throws InterruptedException {
      runnable.run();
      expectLastCall().andThrow(new Error());
      
      control.replay();
      
      ThreadWrapper threadWrapper = new ThreadWrapper(runnable);
      
      threadWrapper.start();
      threadWrapper.join();
      
      control.verify();
   }

}

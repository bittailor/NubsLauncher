package com.netstal.tools.nubs.launcher.infrastructure;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expectLastCall;

import java.io.Closeable;
import java.io.IOException;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class StreamUtilityTest {

   private IMocksControl control;
   private Closeable closeable;


   @Before
   public void setUp() {
      control = createControl();
      closeable = control.createMock("closeable",Closeable.class);      
   }
   
   
   @Test
   public void testClose() throws IOException {
      closeable.close();
      control.replay();
      StreamUtility.close(closeable);
      control.verify();
   }
   
   @Test
   public void testCloseThrows() throws IOException {
      closeable.close();
      expectLastCall().andThrow(new IOException());
      control.replay();
      StreamUtility.close(closeable);
      control.verify();
   }

}

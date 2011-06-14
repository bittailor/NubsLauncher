package com.netstal.tools.nubs.launcher.infrastructure;

import static org.easymock.EasyMock.createControl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StreamPumperTest {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testRun() {
            
      String text = "line1\nline2\nline3\n";
      InputStream in = new ByteArrayInputStream(text.getBytes());
      IMocksControl control = createControl();
      ILineConsumer consumer = control.createMock("consumer", ILineConsumer.class);
      
      consumer.consumeLine("line1");
      consumer.consumeLine("line2");
      consumer.consumeLine("line3");
      
      control.replay();
      
      StreamPumper streamPumper = new StreamPumper(in, consumer);
      streamPumper.run();
      
      control.verify();
   }

   @Test
   public void testRunIoException() {
      
      InputStream in = new InputStream() {
         @Override
         public int read() throws IOException {
            throw new IOException("Just a test case");
         }
      };
      
      IMocksControl control = createControl();
      ILineConsumer consumer = control.createMock("consumer", ILineConsumer.class);
      
      control.replay();
      
      StreamPumper streamPumper = new StreamPumper(in, consumer);
      streamPumper.run();
      
      control.verify();
   }
   
}

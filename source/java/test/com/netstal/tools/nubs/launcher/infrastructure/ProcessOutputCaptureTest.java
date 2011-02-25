package com.netstal.tools.nubs.launcher.infrastructure;


import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessOutputCaptureTest {

   private IProcessBuilder processBuilder;
   private IMocksControl control;
   private Capture<ILineConsumer> lineConsumer;
   private IProcess process;

   @Before
   public void setUp() throws Exception {
      lineConsumer = new Capture<ILineConsumer>();
      control = createControl();
      processBuilder = control.createMock("processBuilder", IProcessBuilder.class);
      process = control.createMock("process", IProcess.class);
   }

   @After
   public void tearDown() throws Exception {
   }
   
   @Test
   public void testOneLine() throws IOException, InterruptedException { 
      expect(processBuilder.outputConsumer(capture(lineConsumer))).andReturn(processBuilder);
      expect(processBuilder.command("executable","--version")).andReturn(processBuilder);
      expect(processBuilder.start()).andReturn(process);
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            lineConsumer.getValue().consumeLine("3.4.5");
            return 0;
         }
      });
      
      control.replay();
      
      ProcessOutputCapture capture = new ProcessOutputCapture(processBuilder);
      //assertEquals("One", capture.)

      List<String> outputLines = capture.grab("executable","--version");
      assertEquals(1, outputLines.size());
      assertEquals("3.4.5", outputLines.get(0));
      
      control.verify();
     
   }
   
   @Test
   public void testThrowIOException() throws IOException, InterruptedException { 
      expect(processBuilder.outputConsumer(capture(lineConsumer))).andReturn(processBuilder);
      expect(processBuilder.command("executable","--version")).andReturn(processBuilder);
      expect(processBuilder.start()).andThrow(new IOException());
            
      control.replay();
      
      ProcessOutputCapture capture = new ProcessOutputCapture(processBuilder);
  
      List<String> outputLines = capture.grab("executable","--version");
      assertEquals(0, outputLines.size());
      
      control.verify();
     
   }
   
   @Test
   public void testThrowInterruptedException() throws IOException, InterruptedException { 
      expect(processBuilder.outputConsumer(capture(lineConsumer))).andReturn(processBuilder);
      expect(processBuilder.command("executable","--version")).andReturn(processBuilder);
      expect(processBuilder.start()).andReturn(process);
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            lineConsumer.getValue().consumeLine("3.4.5");
            throw new InterruptedException();
         }
      });
            
      control.replay();
      
      ProcessOutputCapture capture = new ProcessOutputCapture(processBuilder);
  
      List<String> outputLines = capture.grab("executable","--version");
      assertEquals(1, outputLines.size());
      assertEquals("3.4.5", outputLines.get(0));
      
      control.verify();
     
   }

}

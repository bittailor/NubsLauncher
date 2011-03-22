package com.netstal.tools.nubs.launcher.infrastructure;


import static org.easymock.EasyMock.createControl;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.PrintStream;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessWrapperTest {

   private ILineConsumer outputConsumer;
   private ILineConsumer errorConsumer;
   private IMocksControl control;
   private String classpath;

   @Before
   public void setUp() throws Exception {
      classpath = System.getProperty("java.class.path");
      control = createControl();
      outputConsumer = control.createMock("outputConsumer", ILineConsumer.class);
      errorConsumer = control.createMock("errorConsumer", ILineConsumer.class);
   }

   @After
   public void tearDown() throws Exception {
   
   }
   
   @Test
   public void testProcessOneLineOnOut() throws IOException, InterruptedException {
      outputConsumer.consumeLine("One");
      
      control.replay();
      
      ProcessBuilderWrapper processBuilder = new ProcessBuilderWrapper();
      processBuilder.command("java","-classpath",classpath,ScenarioPrinter.class.getName(),"oneLineToOut");
      processBuilder.outputConsumer(outputConsumer);
      IProcess process = processBuilder.start();
      process.waitFor();
      assertEquals(0,process.exitValue());
      
      control.verify();     
   }
   
   @Test
   public void testProcessMultipleLineToOut() throws IOException, InterruptedException {
      outputConsumer.consumeLine("1");
      outputConsumer.consumeLine("2");
      outputConsumer.consumeLine("3");
      
      control.replay();
      
      ProcessBuilderWrapper processBuilder = new ProcessBuilderWrapper();     
      processBuilder.command("java","-classpath",classpath,ScenarioPrinter.class.getName(),"multipleLineToOut");
      processBuilder.outputConsumer(outputConsumer);
      IProcess process = processBuilder.start();
      process.waitFor();
      assertEquals(0,process.exitValue());
      
      control.verify();     
   }
   
   @Test
   public void testProcessOutAndError() throws IOException, InterruptedException {
      outputConsumer.consumeLine("out1");
      outputConsumer.consumeLine("out2");
      errorConsumer.consumeLine("err1");
      errorConsumer.consumeLine("err2");
      
      control.replay();
      
      ProcessBuilderWrapper processBuilder = new ProcessBuilderWrapper();     
      processBuilder.command("java","-classpath",classpath,ScenarioPrinter.class.getName(),"outAndError");
      processBuilder.outputConsumer(outputConsumer);
      processBuilder.errorConsumer(errorConsumer);
      IProcess process = processBuilder.start();
      process.waitFor();
      assertEquals(0,process.exitValue());
      
      control.verify();     
   }
   
   @Test
   public void testProcessNoOutput() throws IOException, InterruptedException {
      control.replay();
      
      ProcessBuilderWrapper processBuilder = new ProcessBuilderWrapper();
      processBuilder.command("java","-classpath",classpath,ScenarioPrinter.class.getName(),"justReturn");
      IProcess process = processBuilder.start();
      process.waitFor();
      assertEquals(0,process.exitValue());
      
      control.verify();     
   }
   
   @Test
   public void testProcessIgnoreOutput() throws IOException, InterruptedException {
      control.replay();
      
      ProcessBuilderWrapper processBuilder = new ProcessBuilderWrapper();
      processBuilder.command("java","-classpath",classpath,ScenarioPrinter.class.getName(),"multipleLineToOut");
      IProcess process = processBuilder.start();
      process.waitFor();
      assertEquals(0,process.exitValue());
      
      control.verify();     
   }
   
   @Test
   public void testProcessInToOutput() throws IOException, InterruptedException {
      outputConsumer.consumeLine("just a line");
      
      control.replay();
      
      ProcessBuilderWrapper processBuilder = new ProcessBuilderWrapper();
      processBuilder.command("java","-classpath",classpath,ScenarioPrinter.class.getName(),"inToOutput");
      processBuilder.outputConsumer(outputConsumer);
      IProcess process = processBuilder.start();
      PrintStream out = new PrintStream(process.getOutputStream(),true);
      out.println("just a line"); 
      StreamUtility.close(out);
      process.waitFor();
      assertEquals(0,process.exitValue());
      
      control.verify();     
   }
 

}

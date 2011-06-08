package com.netstal.tools.nubs.launcher.domain.job;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;

import org.easymock.Capture;
import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.IRakeBuildOutputListener;
import com.netstal.tools.nubs.launcher.domain.IRakeBuildOutputParser;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.infrastructure.ILineConsumer;
import com.netstal.tools.nubs.launcher.infrastructure.IProcess;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;

public class RakeJobTest {

   private static final int MAX_NUMBER_OF_RETRIES = 3;
   private static final String COMMAND = "unittest";
   private IMocksControl control;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IRakeBuildOutputParser outputParser;
   private IWorkspace workspace;
   private IRakeLauncher launcher;
   private IConfiguration configuration;
   private IProcessBuilder processBuilder;
   private RakeJob rakeJob;
   private IProcess process;
   private Capture<IRakeBuildOutputListener> outputListener;
   private Capture<ILineConsumer> outputConsumer;
   private ByteArrayOutputStream outputBuffer;
   private PrintStream printStream;

   @Before
   public void setUp() throws Exception {
      
      control = createControl();
    
      @SuppressWarnings("unchecked")
      Provider<IProcessBuilder> localProcessBuilderProvider = control.createMock("processBuilderProvider", Provider.class); 
      processBuilderProvider = localProcessBuilderProvider; 
      
      outputParser = control.createMock("outputParser", IRakeBuildOutputParser.class);
      workspace = control.createMock("workspace", IWorkspace.class);
      launcher = control.createMock("launcher", IRakeLauncher.class);
      configuration = control.createMock("configuration", IConfiguration.class);
      processBuilder = control.createMock("processBuilder", IProcessBuilder.class);
      process = control.createMock("process", IProcess.class);
   
      outputListener = new Capture<IRakeBuildOutputListener>();
      outputConsumer = new Capture<ILineConsumer>();
      
      expect(configuration.getInteger("job.TailSize")).andReturn(10).anyTimes();
      expect(configuration.getInteger("job.MaximumNumberOfAutoRetries")).andReturn(MAX_NUMBER_OF_RETRIES).anyTimes();      
      
      File file = new File("unittest_dir");
      expect(workspace.getRoot()).andReturn(file);
      expect(processBuilder.directory(file)).andReturn(processBuilder);
         
      outputParser.addListener(capture(outputListener));
            
      expect(processBuilderProvider.get()).andReturn(processBuilder).anyTimes();
      
      expect(processBuilder.outputConsumer(capture(outputConsumer))).andReturn(processBuilder).anyTimes();
      expect(processBuilder.start()).andReturn(process).anyTimes();
      expect(processBuilder.command(anyObject(String.class),eq(COMMAND))).andReturn(processBuilder);
      
      
      outputBuffer = new ByteArrayOutputStream();
      printStream = new PrintStream(outputBuffer);
      
      expect(process.out()).andReturn(printStream).anyTimes();
      
      
   }

   @After
   public void tearDown() throws Exception {
      StreamUtility.close(printStream);
      rakeJob.dispose();
   }
   
   @Test
   public void testNoAutoRetry() throws InterruptedException {
      
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            IRakeBuildOutputListener listener = outputListener.getValue();
            listener.notifyExecuteTask("RetryTask");
            listener.notifyTaskFailed("RetryTask");
            assertEquals(Failed.INSTANCE, rakeJob.getState());
            return 0;
         }
      });
      
      control.replay();
      Command command = new Command(COMMAND);
      rakeJob = new RakeJob(processBuilderProvider, outputParser, workspace, launcher, configuration);
      rakeJob.setAutoRetry(false);
      assertEquals(0, rakeJob.getMaximumNumberOfAutoRetries());

      rakeJob.command(command).launch();
      
      control.verify();
      
   }

   @Test
   public void testAutoRetryOnce() throws InterruptedException {
      
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            IRakeBuildOutputListener listener = outputListener.getValue();
            listener.notifyExecuteTask("RetryTask");
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(1), outputBuffer.toString()); 
            assertEquals(Building.INSTANCE, rakeJob.getState());
            return 0;
         }
      });
      
      control.replay();
      Command command = new Command(COMMAND);
      rakeJob = new RakeJob(processBuilderProvider, outputParser, workspace, launcher, configuration);
      rakeJob.setAutoRetry(true);
      assertEquals(MAX_NUMBER_OF_RETRIES, rakeJob.getMaximumNumberOfAutoRetries());

      rakeJob.command(command).launch();
      
      control.verify();
      
   }
   
   @Test
   public void testAutoRetryManyTimes() throws InterruptedException {
      
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            IRakeBuildOutputListener listener = outputListener.getValue();
            listener.notifyExecuteTask("RetryTask");
            assertEquals(0, rakeJob.getCurrentNumberOfAutoRetries());
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(1), outputBuffer.toString());
            assertEquals(Building.INSTANCE, rakeJob.getState());
            assertEquals(1, rakeJob.getCurrentNumberOfAutoRetries());
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(2), outputBuffer.toString()); 
            assertEquals(Building.INSTANCE, rakeJob.getState());
            assertEquals(2, rakeJob.getCurrentNumberOfAutoRetries());
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(3), outputBuffer.toString()); 
            assertEquals(Building.INSTANCE, rakeJob.getState());
            assertEquals(3, rakeJob.getCurrentNumberOfAutoRetries());
            return 0;
         }
      });
      
      control.replay();
      Command command = new Command(COMMAND);
      rakeJob = new RakeJob(processBuilderProvider, outputParser, workspace, launcher, configuration);
      rakeJob.setAutoRetry(true);

      rakeJob.command(command).launch();
      
      control.verify();
      
   }
   
   @Test
   public void testAutoRetryTooManyTimes() throws InterruptedException {
      
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            IRakeBuildOutputListener listener = outputListener.getValue();
            listener.notifyExecuteTask("RetryTask");
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(1), outputBuffer.toString());           
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(2), outputBuffer.toString());           
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(3), outputBuffer.toString());
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(3), outputBuffer.toString());
            assertEquals(Failed.INSTANCE, rakeJob.getState());
            return 0;
         }
      });
      
      control.replay();
      Command command = new Command(COMMAND);
      rakeJob = new RakeJob(processBuilderProvider, outputParser, workspace, launcher, configuration);
      rakeJob.setAutoRetry(true);

      rakeJob.command(command).launch();
      
      control.verify();
      
   }
   
   @Test
   public void testAutoRetryResetCounterOnNewTask() throws InterruptedException {
      
      expect(process.waitFor()).andAnswer(new IAnswer<Integer>() {

         @Override
         public Integer answer() throws Throwable {
            IRakeBuildOutputListener listener = outputListener.getValue();
            listener.notifyExecuteTask("RetryTask");
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(1), outputBuffer.toString());           
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(2), outputBuffer.toString());           
            listener.notifyTaskFailed("RetryTask");
            assertEquals(getRetryString(3), outputBuffer.toString());
            assertEquals(3, rakeJob.getCurrentNumberOfAutoRetries());
            listener.notifyExecuteTask("NewTask");
            assertEquals(0, rakeJob.getCurrentNumberOfAutoRetries());
            listener.notifyTaskFailed("NewTask");
            assertEquals(getRetryString(4), outputBuffer.toString());
            assertEquals(Building.INSTANCE, rakeJob.getState());
            return 0;
         }
      });
      
      control.replay();
      Command command = new Command(COMMAND);
      rakeJob = new RakeJob(processBuilderProvider, outputParser, workspace, launcher, configuration);
      rakeJob.setAutoRetry(true);

      rakeJob.command(command).launch();
      
      control.verify();
      
   }

   private String getRetryString(int numberOfRetries) {
      ByteArrayOutputStream buffer = new ByteArrayOutputStream();
      PrintStream stream = new PrintStream(buffer);
      for (int i = 0; i < numberOfRetries; i++) {
         stream.println("y");
      }
      StreamUtility.close(stream);
      return buffer.toString();
   }
   

}

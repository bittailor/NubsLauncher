package com.netstal.tools.nubs.launcher.domain;


import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.infrastructure.IProcessBuilder;
import com.netstal.tools.nubs.launcher.infrastructure.ProcessBuilderWrapper;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;
import com.netstal.tools.nubs.launcher.infrastructure.StringUtility;
import com.netstal.tools.nubs.launcher.test.TestUtility;

public class RakeJobIntegrationTest {

   private IMocksControl control;
   private IWorkspace workspace;
   private IRakeBuildOutputParser outputParser;
   private Provider<IProcessBuilder> processBuilderProvider;
   private RakeJob rakeJob;

   @SuppressWarnings("unchecked")
   @Before
   public void setUp() throws Exception {
      control = createControl();
      
      workspace = control.createMock("workspace",IWorkspace.class);
      expect(workspace.getRoot())
         .andReturn(TestUtility.getRakeTestWorkspaceRoot())
         .anyTimes();
      
      processBuilderProvider = control.createMock("processBuilderProvider",Provider.class);
      expect(processBuilderProvider.get())
         .andAnswer(new IAnswer<IProcessBuilder>() {

            @Override
            public IProcessBuilder answer() throws Throwable {
               return new ProcessBuilderWrapper();
            }
         })
         .anyTimes();
      
      outputParser = new RakeOutputParser();
      
      rakeJob = new RakeJob(processBuilderProvider, outputParser, workspace);
      
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testJobWithParameterWithQuotes() throws IOException {
      testRakeJob("ShowVariableBjam bjam_targets=\"-j2 mingw\"","Output_Rake_JobWithParameterWithQuotes.txt");      
   }
   
   @Test
   public void testJobWithParameterWithoutQuotes() throws IOException {
      testRakeJob("ShowVariableIgnore ignore_deps=true ignore_deps=true","Output_Rake_JobWithParameterWithoutQuotes.txt");      
   }
   
   @Test
   public void testJobOneTask() throws IOException {
      testRakeJob("ProjectTwo","Output_Rake_JobOneTask.txt");      
   }
   
   @Test
   public void testJobMultipleTask() throws IOException {
      testRakeJob("ProjectOne_build_ipc_only ProjectTwo_build_dpu_only ProjectTwo_build_ipc_only","Output_Rake_JobMultipleTask.txt");      
   }
   
   @Test
   public void testJobMultipleTaskMultipleBlanks() throws IOException {
      testRakeJob(" ProjectOne_build_ipc_only  ProjectTwo_build_dpu_only   ProjectTwo_build_ipc_only   ","Output_Rake_JobMultipleTask.txt");      
   }
   
   @Test
   public void testJobdefaultTask() throws IOException {
      testRakeJob("","Output_Rake_JobDefaultTask.txt");      
   }
   
   @Test
   public void testJobFail() throws IOException {
      control.replay();

      Command command = new Command("FailFast retry=false");
      rakeJob
         .command(command)
         .launch();

      assertEquals(JobState.FINISHED_FAILURE, rakeJob.getState());
      
      control.verify();      
   }
   
   private void testRakeJob(String taskLine, String expectFileName) throws IOException {
      control.replay();
      
      Command command = new Command(taskLine);
      rakeJob
         .command(command)
         .launch();
      
      assertEquals(JobState.FINISHED_SUCESSFULLY, rakeJob.getState());
      assertRakeLog(expectFileName, rakeJob.getLogFile());
      
      control.verify();
   }

   private void assertRakeLog(String expectFileName, File logFile) throws IOException {
      assertEquals(rakeLogToString(expectFile(expectFileName)), rakeLogToString(logFile));
   }
   
   private String rakeLogToString(File file) throws IOException {
      List<String> lines = StreamUtility.readLines(file);
      if (lines.isEmpty()) {
         return "";
      }
      
      // the first line contains the invoke path of rake => remove it.
      lines.remove(0);
      
      return StringUtility.join("\n", lines);
   }

   private File expectFile(String filename) throws IOException {
      return new File(this.getClass().getResource(filename).getFile());
   }
   
   
   
}

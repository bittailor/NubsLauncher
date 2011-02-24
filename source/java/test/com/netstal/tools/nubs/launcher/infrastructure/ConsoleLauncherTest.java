package com.netstal.tools.nubs.launcher.infrastructure;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;

import java.io.File;
import java.io.IOException;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Provider;

public class ConsoleLauncherTest {

   private IMocksControl control;
   private Provider<IProcessBuilder> processBuilderProvider;
   private IProcessBuilder processBuilder;
   private IProcess process;

   @SuppressWarnings("unchecked")
   @Before
   public void setUp() throws Exception {
      control = createControl();
      processBuilderProvider = control.createMock("processBuilderProvider", Provider.class);
      processBuilder = control.createMock("processBuilder", IProcessBuilder.class);
      process = control.createMock("process", IProcess.class);
      
      expect(processBuilderProvider.get())
         .andReturn(processBuilder)
         .anyTimes();
   }

   @Test
   public void testLaunch() throws IOException {
      File directory = new File("testdir");
      String command = "myCommand";
      expect(processBuilder.command("cmd", "/C", "start", "cmd", "/K", "\""+command+"\"")).andReturn(processBuilder);
      expect(processBuilder.directory(directory)).andReturn(processBuilder);
      expect(processBuilder.start()).andReturn(process);
      control.replay();
      ConsoleLauncher launcher = new ConsoleLauncher(processBuilderProvider);
      launcher.launch(command,directory);  
      control.verify();
   }

}

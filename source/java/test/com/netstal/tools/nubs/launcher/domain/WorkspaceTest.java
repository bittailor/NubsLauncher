package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.netstal.tools.nubs.launcher.test.TestUtility;

public class WorkspaceTest {

   private IMocksControl control;
   private Workspace workspace;
   private IRakeTaskImporter importer;
   private File root;
   private IConfiguration configuration;
   private File configurationDirectory;

   @Before
   public void setUp() throws Exception {
	  root = TestUtility.getRakeTestWorkspaceRoot() ;
      
	  control = createControl();
     importer = control.createMock("importer", IRakeTaskImporter.class);
     configuration = control.createMock("configuration", IConfiguration.class);
    
     configurationDirectory = new File(System.getProperty("java.io.tmpdir"),"NubsUnitTest");
     if (!configurationDirectory.mkdirs()) {
        throw new Exception("Could not create temporary directory for tests");
     }
     
     expect(configuration.getConfigurationDirectory()).andReturn(configurationDirectory).anyTimes();
     expect(configuration.get(Workspace.WORKSPACE_CACHE_PATH)).andReturn("bin/nubslauncher/Tasks.cache").anyTimes();
   }
   
   @After
   public void tearDown() throws Exception {
      for (File file : configurationDirectory.listFiles()) {
         if (!file.delete()) {
            throw new Exception("Could not delete configuration file " + file.getName() + " after tests");
         }
      }
      if (!configurationDirectory.delete()) {
         throw new Exception("Could not delete temporary directory after tests");
      }
   }
   

   @Test
   public void testSetRoot() {
      SortedMap<String, RakeTask> tasks = new TreeMap<String, RakeTask>();
      expect(importer.importTasks(root)).andReturn(tasks).anyTimes();
      
      control.replay();
      workspace = new Workspace(importer,configuration);   
      workspace.setRoot(root);
      assertEquals(root, workspace.getRoot());
      control.verify(); 
   }
   
   @Test
   public void testSetRootCurrent() {
      SortedMap<String, RakeTask> tasks = new TreeMap<String, RakeTask>();
      expect(importer.importTasks(anyObject(File.class))).andReturn(tasks).anyTimes();
      
      control.replay();
      workspace = new Workspace(importer,configuration);  
      workspace.setRoot(new File("."));
      assertEquals(new File(System.getProperty("user.dir")), workspace.getRoot());
      control.verify(); 
   }

   @Test
   public void testGetTasks() {
      
      SortedMap<String, RakeTask> tasks = new TreeMap<String, RakeTask>();
      tasks.put("One", new RakeTask("One"));
      tasks.put("Two", new RakeTask("Two"));
      tasks.put("Three", new RakeTask("Three"));
      expect(importer.importTasks(root)).andReturn(tasks).anyTimes();
   
      control.replay();
      
      workspace = new Workspace(importer,configuration);   
      workspace.setRoot(root);
      workspace.loadTasks();
      assertEquals(tasks.values(), workspace.getTasks());
      
      control.verify();      
   }

}

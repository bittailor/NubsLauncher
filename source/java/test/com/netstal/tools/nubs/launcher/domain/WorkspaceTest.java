//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.SortedMap;
import java.util.TreeMap;

import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class WorkspaceTest {

   private IMocksControl control;
   private Workspace workspace;
   private IRakeTaskImporter importer;
   private File root;

   @Before
   public void setUp() throws Exception {
      root = new File("D:\\ws\\nms");
      control = createControl();
      importer = control.createMock("importer", IRakeTaskImporter.class);
      workspace = new Workspace(importer);   
   }

   @Test
   public void testSetRoot() {
      workspace.setRoot(root);
      assertEquals(root, workspace.getRoot());
   }
   
   @Test
   public void testSetRootCurrent() {
      workspace.setRoot(new File("."));
      assertEquals(new File(System.getProperty("user.dir")), workspace.getRoot());
   }

   @Test
   public void testGetTasks() {
      
      SortedMap<String, RakeTask> tasks = new TreeMap<String, RakeTask>();
      tasks.put("One", new RakeTask("One"));
      tasks.put("Two", new RakeTask("Two"));
      tasks.put("Three", new RakeTask("Three"));
      expect(importer.importTasks(root)).andReturn(tasks).anyTimes();
   
      control.replay();
      
      workspace.setRoot(root);
      assertEquals(tasks.values(), workspace.getTasks());
      
      control.verify();      
   }

}

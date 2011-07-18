package com.netstal.tools.nubs.launcher.domain;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RakeTaskTest {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testGetName() {
      String name = "Project_A_B_C";
      RakeTask task = new RakeTask(name);
      assertEquals(name, task.getName());
   }

   @Test(expected=CircularDependencyException.class)
   public void testCircularDependency() throws CircularDependencyException {
      RakeTask taskA = new RakeTask("A");
      RakeTask taskB = new RakeTask("B");
      RakeTask taskC = new RakeTask("C");
      
      taskA.addDependency(taskB);
      taskB.addDependency(taskC);
      taskC.addDependency(taskA);
      
      Set<RakeTask> dependecySet = new HashSet<RakeTask>();
      taskA.fillDependencySet(dependecySet);
      
   }

}

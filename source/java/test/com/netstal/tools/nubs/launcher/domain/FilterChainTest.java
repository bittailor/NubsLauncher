package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertArrayEquals;

import java.util.ArrayList;
import java.util.List;

import org.easymock.Capture;
import org.easymock.IMocksControl;
import org.junit.Before;
import org.junit.Test;

public class FilterChainTest {

   private IMocksControl control;
   private IWorkspace workspace;
   private FilterChain filterChain;
   private RakeTask projectOne;
   private RakeTask projectOne_build_dpu_only;
   private RakeTask projectTwo;
   private RakeTask projectTwo_build_dpu_only;
   private RakeTask applicationOne;
   private RakeTask applicationOne_generate;
   private RakeTask applicationTwo;
   private RakeTask applicationTwo_build;
   private Capture<IEventListener<IWorkspace>> listener;

   @Before
   public void setUp() {
      createTask();
      control = createControl();
      listener = new Capture<IEventListener<IWorkspace>>(); 
      workspace = control.createMock("workspace",IWorkspace.class);     
      workspace.addListener(capture(listener));
   }

   private List<RakeTask> createTasksList(RakeTask... rakeTasks) {
      List<RakeTask> tasks = new ArrayList<RakeTask>();
      for (RakeTask rakeTask : rakeTasks) {
         tasks.add(rakeTask);
      }
      return tasks;
   }
   
   private List<RakeTask> createAllTasksList() {
      List<RakeTask> tasks = new ArrayList<RakeTask>();
      tasks.add(projectOne);
      tasks.add(projectOne_build_dpu_only);
      tasks.add(projectTwo);
      tasks.add(projectTwo_build_dpu_only);
      tasks.add(applicationOne);
      tasks.add(applicationOne_generate);
      tasks.add(applicationTwo);
      tasks.add(applicationTwo_build);
      return tasks;
   }

   private void createTask() {
      projectOne = new RakeTask("ProjectOne");
      projectOne_build_dpu_only = new RakeTask("ProjectOne_build_dpu_only");
      projectTwo = new RakeTask("ProjectTwo");
      projectTwo_build_dpu_only = new RakeTask("ProjectTwo_build_dpu_only");
      applicationOne = new RakeTask("ApplicationOne");
      applicationOne_generate = new RakeTask("ApplicationOne_generate");
      applicationTwo = new RakeTask("ApplicationTwo");
      applicationTwo_build = new RakeTask("ApplicationTwo_build");
   }
   
   

   @Test
   public void testFilter_PrO() {
      expect(workspace.getTasks()).andReturn(createAllTasksList()).anyTimes();
      control.replay();
      
      filterChain = new FilterChain(workspace);
      filterChain.setSuggestFilter("PrO");
      
      assertArrayEquals(new RakeTask[]{projectOne,projectOne_build_dpu_only}, 
                        filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      control.verify();
   }
   
   @Test
   public void testFilter_PrT() {
      expect(workspace.getTasks()).andReturn(createAllTasksList()).anyTimes();
      control.replay();
      
      filterChain = new FilterChain(workspace);
      filterChain.setSuggestFilter("PrT");
      
      assertArrayEquals(new RakeTask[]{projectTwo,projectTwo_build_dpu_only}, 
                        filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      control.verify();
   }
   
   @Test
   public void testFilter_P() {
      expect(workspace.getTasks()).andReturn(createAllTasksList()).anyTimes();
      control.replay();
      
      filterChain = new FilterChain(workspace);
      filterChain.setSuggestFilter("P");
      
      assertArrayEquals(new RakeTask[]{projectOne, projectOne_build_dpu_only, projectTwo,projectTwo_build_dpu_only}, 
                        filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      control.verify();
   }
   
   @Test
   public void testFilter_A() {
      expect(workspace.getTasks()).andReturn(createAllTasksList()).anyTimes();
      control.replay();
      
      filterChain = new FilterChain(workspace);
      filterChain.setSuggestFilter("A");
      
      assertArrayEquals(new RakeTask[]{applicationOne, applicationOne_generate, applicationTwo, applicationTwo_build}, 
                        filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      control.verify();
   }
   
   @Test
   public void testFilter_A_g() {
      expect(workspace.getTasks()).andReturn(createAllTasksList()).anyTimes();
      control.replay();
      
      filterChain = new FilterChain(workspace);
      filterChain.setSuggestFilter("A_g");
      
      assertArrayEquals(new RakeTask[]{applicationOne_generate}, 
                        filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      control.verify();
   }
   
   @Test
   public void testRefilterOnWorkspaceChange() {
      expect(workspace.getTasks()).andReturn(createAllTasksList()).times(2);
      expect(workspace.getTasks()).andReturn(createTasksList(projectOne,projectTwo)).times(1);
      control.replay();
      
      filterChain = new FilterChain(workspace);
      filterChain.setSuggestFilter("PrO");
      
      assertArrayEquals(new RakeTask[]{projectOne,projectOne_build_dpu_only}, 
                        filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      listener.getValue().notifyEvent(workspace);
      
      assertArrayEquals(new RakeTask[]{projectOne}, 
               filterChain.getFilteredTasks().toArray(new RakeTask[]{}));
      
      control.verify();
   }

}

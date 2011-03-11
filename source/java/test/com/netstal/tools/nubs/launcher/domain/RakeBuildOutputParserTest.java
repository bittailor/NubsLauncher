package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.createControl;

import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RakeBuildOutputParserTest {

   private IRakeBuildOutputListener listener;
   private IMocksControl control;

   @Before
   public void setUp() throws Exception {
      control = createControl();
      listener = control.createMock("listener", IRakeBuildOutputListener.class);
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testConsumeLineNothing() {
      
      control.replay();
      
      IRakeBuildOutputParser outputParser = new RakeOutputParser();
      outputParser.addListener(listener);
      outputParser.consumeLine("Just some Line");
      outputParser.removeListener(listener);
      control.verify();
   }
   
   @Test
   public void testConsumeLineOK() {
      listener.notifyExecuteTask("ProjectOne");
      listener.notifyExecuteTask("ProjectTwo_build_ipc_only");
      listener.notifyExecuteTask("ProjectOne_generate");
      
      control.replay();
      
      IRakeBuildOutputParser outputParser = new RakeOutputParser();
      outputParser.addListener(listener);
      outputParser.consumeLine("** Execute ProjectOne");
      outputParser.consumeLine("** Execute ProjectTwo_build_ipc_only");
      outputParser.consumeLine("** Execute ProjectOne_generate");
      outputParser.removeListener(listener);
      control.verify();
   }
   
   @Test
   public void testConsumeLineTaksFailed() {
      
      listener.notifyTaskFailed("Fail_build_dpu_only");
      listener.notifyTaskFailed("ProjectOne");
      listener.notifyTaskFailed("AllComponentPlugins");
      
      control.replay();
      
      IRakeBuildOutputParser outputParser = new RakeOutputParser();
      outputParser.addListener(listener);
      outputParser.consumeLine("--> Try build Fail_build_dpu_only again (y)es (n)o or just (i)gnore it and continue? [y|n|i]>");
      outputParser.consumeLine("--> Try build ProjectOne again (y)es (n)o or just (i)gnore it and continue? [y|n|i]>");
      outputParser.consumeLine("--> Try build AllComponentPlugins again (y)es (n)o or just (i)gnore it and continue? [y|n|i]>");
      outputParser.removeListener(listener);
      control.verify();
   }

}

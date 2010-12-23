package com.netstal.tools.nubs.launcher;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

import org.junit.Test;

import com.netstal.tools.nubs.launcher.domain.RakeTask;
import com.netstal.tools.nubs.launcher.domain.RakeTaskParser;

public class RakeTaskParserTest {

   
   
   @Test
   public void testParseTasks() {     
      RakeTaskParser rakeTaskParser = new RakeTaskParser();
      parse(rakeTaskParser,"RakeTaskParserTest_TestFile_1.txt");
      Map<String, RakeTask> tasks = rakeTaskParser.getTasks();
      assertEquals(8, tasks.size());
      assertTrue(tasks.containsKey("ProjectOne"));
      assertTrue(tasks.containsKey("ProjectOne_generate"));
      assertTrue(tasks.containsKey("ProjectOne_build_ipc_only"));
      assertTrue(tasks.containsKey("ProjectTwo"));
      assertTrue(tasks.containsKey("ProjectTwo_generate"));
      assertTrue(tasks.containsKey("ProjectTwo_build_ipc_only"));
      assertTrue(tasks.containsKey("ProjectTwo_build_dpu_only"));
      assertTrue(tasks.containsKey("default"));
   }
   
   private void parse(RakeTaskParser rakeTaskParser, String string) {
      BufferedReader in = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(string)));
      String line;
      try {
         while((line = in.readLine())!=null) {
            rakeTaskParser.consumeLine(line);
         }
      }
      catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
   
   
   

}

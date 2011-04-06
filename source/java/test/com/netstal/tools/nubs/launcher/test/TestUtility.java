package com.netstal.tools.nubs.launcher.test;

import java.io.File;
import java.net.URL;

public class TestUtility {

   
   public static File getRakeTestWorkspaceRoot() {
      URL resource = TestUtility.class.getResource("../rake/unittest");
      File directory = new File(resource.getFile());
      return directory;
   }
   
}

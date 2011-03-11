package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.OutputStream;
import java.io.PrintStream;


public interface IProcess {
   
   public int waitFor() throws InterruptedException;
   public int exitValue();
   public PrintStream out();
   public OutputStream getOutputStream();
   
}

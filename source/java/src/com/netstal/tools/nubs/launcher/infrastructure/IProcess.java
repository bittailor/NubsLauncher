package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.OutputStream;


public interface IProcess {

   public OutputStream getOutputStream();
   
   public int waitFor() throws InterruptedException;

}

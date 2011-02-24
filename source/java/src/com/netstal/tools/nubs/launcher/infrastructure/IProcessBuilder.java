package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public interface IProcessBuilder {

   public IProcessBuilder command(String... command);

   public IProcessBuilder directory(File directory);

   public IProcessBuilder errorConsumer(ILineConsumer consumer);

   public IProcessBuilder outputConsumer(ILineConsumer consumer);

   public Map<String,String> environment();
   
   public IProcess start() throws IOException;

}

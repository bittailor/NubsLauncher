package com.netstal.tools.nubs.launcher.infrastructure;

public interface IProcessFactory {

   public IProcess create(ILineConsumer outputConsumer, String... command);
   
}

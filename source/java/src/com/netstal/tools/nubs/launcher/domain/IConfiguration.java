package com.netstal.tools.nubs.launcher.domain;

import java.io.File;

public interface IConfiguration {
   
   public String getRakeOsCommand();
   
   public File getConfigurationDirectory();
   
	public String get(String key);
   public boolean getFlag(String key);	
	
}

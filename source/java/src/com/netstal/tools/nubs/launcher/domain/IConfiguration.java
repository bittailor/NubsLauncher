package com.netstal.tools.nubs.launcher.domain;

import java.io.File;

public interface IConfiguration {
   
   public File getConfigurationDirectory();
   
	public String get(String key);
   public boolean getFlag(String key);
   public int getInteger(String key);	

	
}

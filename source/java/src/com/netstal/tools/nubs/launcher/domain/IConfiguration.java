package com.netstal.tools.nubs.launcher.domain;

public interface IConfiguration {
   
   public String getRakeOsCommand();
   
	public String get(String key);
   public boolean getFlag(String key);	
	
}

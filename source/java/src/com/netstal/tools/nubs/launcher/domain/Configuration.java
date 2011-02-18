package com.netstal.tools.nubs.launcher.domain;

import com.netstal.tools.nubs.launcher.application.NubsLauncher;

public class Configuration implements IConfiguration {

	@Override
	public String getRakeOsCommand() {
		if (isWindows()) {
			return "rake.bat";
		}
		return "rake";
	}
	
	@Override
	public String getVersion() {
	   String implementationVersion = NubsLauncher.class.getPackage().getImplementationVersion();
      if (implementationVersion == null) {
         implementationVersion = "(local build)";
      }
	   return implementationVersion;
	}

	@Override
	public String getInstallationServerPath() {
	   return "s:/Gruppe/TE/Verwaltung/Tools/NUBSLauncher";
	}
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf( "windows" ) >= 0;
	}

}

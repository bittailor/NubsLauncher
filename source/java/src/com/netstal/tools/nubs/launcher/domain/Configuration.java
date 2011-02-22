package com.netstal.tools.nubs.launcher.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.netstal.tools.nubs.launcher.infrastructure.Stream;

public class Configuration implements IConfiguration {

   private static Logger LOG = Logger.getLogger(Configuration.class.getName());
   
   private Properties properties;

   private Properties defaultProperties;
   
	public Configuration() {
	   ensureConfiguration();
	   loadProperties();
   }
	
	@Override
   public String getRakeOsCommand() {
      if (isWindows()) {
         return "rake.bat";
      }
      return "rake";
   }

   @Override
   public String get(String key) {
      return properties.getProperty(key);
   }
   
   @Override
   public boolean getFlag(String key) {
      return Boolean.parseBoolean(get(key));
   }

   private void ensureConfiguration() {
      File configurationDirectory = getConfigurationDirectory();
      if (!configurationDirectory.exists()) {
         if (!configurationDirectory.mkdirs()) {
            LOG.log(Level.SEVERE, "Could not create configuration directory ("+configurationDirectory+")"); 
         }
      }
      File userPropertiesFile = getUserPropertiesFile();
      if (!userPropertiesFile.exists()) {
         Properties userProperties = new Properties();
         FileOutputStream outputStream = null;
         try {
            outputStream = new FileOutputStream(userPropertiesFile);
            userProperties.store(outputStream," NUBS Launcher - User Properties");
         }
         catch (IOException e) {
           LOG.log(Level.SEVERE, "Could not create configuration file ("+userPropertiesFile.getAbsolutePath()+")",e); 
         } finally {
            Stream.close(outputStream);
         }
      }
   }

   private File getUserPropertiesFile() {
      File userPropertiesFile = new File(getConfigurationDirectory(),"user.properties");
      return userPropertiesFile;
   }
   
   private File getConfigurationDirectory() {
      return new File(System.getProperty("user.home"),".nubslauncher");
   }
  
   private void loadProperties() {
      loadDefaultProperties();   
      loadUserProperties();
   }

   private void loadUserProperties() {
      InputStream userInputStream = null; 
      properties = new Properties(defaultProperties);
      try {
         userInputStream = new FileInputStream(getUserPropertiesFile());
         properties.load(userInputStream);
      }
      catch (IOException e) {
         LOG.log(Level.WARNING,"Could not load user properties",e);
      }
      finally {
         Stream.close(userInputStream);
      }
   }


   private void loadDefaultProperties() {
      InputStream defaultInputStream = null;
      defaultProperties = new Properties();
	   try {
	      defaultInputStream = Configuration.class.getResourceAsStream("default.properties");
	      defaultProperties.load(defaultInputStream);
      }
      catch (IOException e) {
         LOG.log(Level.WARNING,"Could not load default properties",e);
      }
      finally {
         Stream.close(defaultInputStream);
      }
   }
   
   
   
	
	private boolean isWindows() {
		return System.getProperty("os.name").toLowerCase().indexOf( "windows" ) >= 0;
	}

}

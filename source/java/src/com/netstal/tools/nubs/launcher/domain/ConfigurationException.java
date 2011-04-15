package com.netstal.tools.nubs.launcher.domain;

public class ConfigurationException extends RuntimeException {

   public ConfigurationException(String message, Throwable cause) {
      super(message, cause);
   }

   public ConfigurationException(String message) {
      super(message);
   }   
}

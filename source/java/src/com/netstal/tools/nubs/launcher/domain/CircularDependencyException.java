package com.netstal.tools.nubs.launcher.domain;

public class CircularDependencyException extends Exception {

   public CircularDependencyException(String message) {
      super(message);
   }

}

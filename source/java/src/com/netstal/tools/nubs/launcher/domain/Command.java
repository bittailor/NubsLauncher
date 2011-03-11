//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

public class Command {
   
   // TODO fsc fix !
   private static final String RAKE = isWindows() ? "rake.bat" : "rake";
   
   private final String command;

   public Command(String command) {
      if (command==null) {
         throw new IllegalArgumentException("command can not be null");
      }
      this.command = command;
   }

   public String[] command() {
      return new String[]{RAKE,command.trim()}; 
   }
   
   public String rakeCmdLine() {
      return RAKE + " " + command; 
   }

   @Override
   public String toString() {
      return command;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + command.hashCode();
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      Command other = (Command) obj;
      if (!command.equals(other.command)) return false;
      return true;
   }
   
   private static boolean isWindows() {
      return Configuration.isWindows();
   }
   
}

//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.*;
import java.util.logging.*;

public class Command {
   
   private static Logger LOG = Logger.getLogger(Command.class.getName());
   
   // TODO fsc fix !
   public static final String RAKE = isWindows() ? "rake.bat" : "rake";
   
   private final String command;

   public Command(String command) {
      if (command==null) {
         throw new IllegalArgumentException("command can not be null");
      }
      this.command = command;
   }

   public String[] command() {
      List<String> commandParts = new LinkedList<String>();
      commandParts.add(RAKE);

     
      StringBuffer buffer = new StringBuffer(); 
      String[] split = command.trim().split(" ");
      for (String string : split) {
         if (buffer.length()==0) {
            if (evenQuotes(string)) {
               commandParts.add(doubleQuotes(string));
            } else {
               buffer.append(string);
            }
         } else {
            buffer.append(" ");
            buffer.append(string);
            if (evenQuotes(buffer.toString())) {
               commandParts.add(doubleQuotes(buffer.toString()));
               buffer = new StringBuffer();
            }
         }
         
      }  
      
      String[] commandArray = commandParts.toArray(new String[commandParts.size()]);
      
      LOG.log(Level.INFO,"commandArray : ");
      for (String string : commandArray) {
         LOG.log(Level.INFO,"  "+string);
      }
      
      
      return commandArray; 
   }

   private boolean evenQuotes(String string) {
      int count = countQuotes(string);
      return Math.abs(count)%2==0;
   }
   
   private int countQuotes(String string) {
      String quotes = string.replaceAll("[^\"]", "");
      return quotes.length();
   }
   
   private String doubleQuotes(String string) {
      return string.replaceAll("[\"]", "\"\"");
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

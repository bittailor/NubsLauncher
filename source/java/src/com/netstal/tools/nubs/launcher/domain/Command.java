//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.ArrayList;

import com.netstal.tools.nubs.launcher.infrastructure.OsPlatform;


public class Command {
   
   private final String command;

   public Command(String command) {
      if (command==null) {
         throw new IllegalArgumentException("command can not be null");
      }
      this.command = command;
   }

   public String[] command() {      
      return createCommandArray();
   }

   private String[] createCommandArray() {      
      String[] fragments = command.trim().split(" ");

      ArrayList<String> arguments = new ArrayList<String>(fragments.length);
      arguments.add(OsPlatform.getRakeOsCommand());

      StringBuilder buffer = new StringBuilder();
      for (String fragment : fragments) {
         if (buffer.length()==0) {
            if (containsOpenQuote(fragment)) {
               buffer.append(fragment);
            } else {
               insertArgument(arguments,fragment);
            }
         } else {
            buffer.append(" ").append(fragment);
            if (!containsOpenQuote(buffer.toString())) {
               insertArgument(arguments,buffer.toString());
               buffer = new StringBuilder();
            }
         }
      }
      String[] commandArray = arguments.toArray(new String[arguments.size()]);
      return commandArray;
   }

   
   
   private void insertArgument(ArrayList<String> arguments, String argument) {
      String unquoted = unquote(argument);
      if (!unquoted.isEmpty()) {
         arguments.add(unquoted);
      }     
   }

   private String unquote(String string) {
      return string.replace("\"", "");
   }

   private boolean containsOpenQuote(String string) {
      String quotes = string.replaceAll("[^\"]", "");
      int numberOfQuotes = quotes.length();
      return Math.abs(numberOfQuotes)%2==1;
   }

   public String rakeCmdLine() {
      return OsPlatform.getRakeOsCommand() + " " + command; 
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
}

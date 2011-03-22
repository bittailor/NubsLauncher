package com.netstal.tools.nubs.launcher.infrastructure;

import java.util.Collection;
import java.util.Iterator;


public class StringUtility {

   public static String join(String separator, String... strings) {
      StringBuffer buffer = new StringBuffer();
      for (int i = 0; i < strings.length; i++) {
         buffer.append(strings[i]);
         if (i < (strings.length - 1)) {
            buffer.append(separator);
         }
      }
      return buffer.toString();
   }
   
   public static String join(String separator, Collection<String> strings) {
      if (strings.isEmpty()) return "";
      Iterator<String> iterator = strings.iterator();
      StringBuffer buffer = new StringBuffer(iterator.next());
      while (iterator.hasNext()) {
         buffer.append(separator);
         buffer.append(iterator.next());
      }
      return buffer.toString();
   }
   
}

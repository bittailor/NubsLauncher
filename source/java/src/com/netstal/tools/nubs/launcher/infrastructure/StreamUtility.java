package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class StreamUtility {
   
   private StreamUtility() {}
   
   public static void close(Closeable closeable) {
      if (closeable!=null) {
         try {
            closeable.close();
         }
         catch (IOException e) {
         }
      }
   }
   
   public static List<String> readLines(InputStream input) throws IOException {
      List<String> buffer = new LinkedList<String>();
      BufferedReader reader = null;    
      try {
         reader = new BufferedReader(new InputStreamReader(input));
         String line;
         while((line = reader.readLine()) != null) {
            buffer.add(line);
         }
      } finally {
         StreamUtility.close(reader);
      }
      return buffer; 
   }
   
   public static List<String> readLines(File file) throws IOException {
      return readLines(new FileInputStream(file));
   }
   
   public static String readIntoString(InputStream input) throws IOException {
      return StringUtility.join("\n", readLines(input));
   }
   
   public static String readIntoString(File file) throws IOException {
      return StringUtility.join("\n", readLines(file));
   }
   
}

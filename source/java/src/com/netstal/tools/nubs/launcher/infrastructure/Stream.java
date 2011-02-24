package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.Closeable;
import java.io.IOException;

public class Stream {
   
   private Stream() {}
   
   public static void close(Closeable closeable) {
      if (closeable!=null) {
         try {
            closeable.close();
         }
         catch (IOException e) {
         }
      }
   }
}

package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamPumper implements Runnable {

   private final BufferedReader in;
   private I_LineConsumer consumer = null;

   private static final int SIZE = 1024;

   public StreamPumper(InputStream in, I_LineConsumer consumer) {
      this.in = new BufferedReader(new InputStreamReader(in), SIZE);
      this.consumer = consumer;
   }

   @Override
   public void run() {
      try {
         String line;
         while ((line=in.readLine()) != null) {
            consumeLine(line);
         }
      } catch (IOException e) {
         // do nothing
      } finally {
         try {
            in.close();
        } catch (IOException ignored) {
           // ignore
        }
      }
   }

   private void consumeLine(String line) {
      consumer.consumeLine(line);
   }
}
package com.netstal.tools.nubs.launcher.infrastructure;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class StreamPumper implements Runnable {

   private final BufferedReader in;
   private ILineConsumer consumer = null;

   private static final int SIZE = 1024;

   public StreamPumper(InputStream in, ILineConsumer consumer) {
      this.in = new BufferedReader(new InputStreamReader(in), SIZE);
      this.consumer = consumer;
   }

   @Override
   public void run() {
      work();
   }

   private void work() {
      try {
         String line;
         while ((line=in.readLine()) != null) {
            consumeLine(line);
         }
      } catch (IOException e) {
         // do nothing
      } finally {
         Stream.close(in);
      }
   }

   private void consumeLine(String line) {
      consumer.consumeLine(line);
   }
}
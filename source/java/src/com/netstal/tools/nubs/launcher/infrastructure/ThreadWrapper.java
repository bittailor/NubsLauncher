package com.netstal.tools.nubs.launcher.infrastructure;

import java.util.concurrent.atomic.AtomicReference;

public class ThreadWrapper {

   private AtomicReference<RuntimeException> runtimeExceptionReference;
   private AtomicReference<Error> errorReference;
   private Thread thread;
   
   public ThreadWrapper(final Runnable runnable) {
      runtimeExceptionReference = new AtomicReference<RuntimeException>();
      errorReference = new AtomicReference<Error>();
      thread = new Thread(new Runnable() {
         
         @Override
         public void run() {
            try {
               runnable.run();
            } catch (RuntimeException runtimeException) {
               runtimeExceptionReference.set(runtimeException);
            } catch (Error error) {
               errorReference.set(error);
            }
            
         }
      });
   }
   
   public void start() {
      thread.start();
   }


   public void join() throws InterruptedException  {
      thread.join();
      RuntimeException runtimeException = runtimeExceptionReference.get();
      if (runtimeException != null) {
         throw runtimeException;
      }
      Error error = errorReference.get();
      if (error != null) {
         throw error;
      }
   }

}

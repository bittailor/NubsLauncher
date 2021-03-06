package com.netstal.tools.nubs.launcher.infrastructure;

import java.util.concurrent.atomic.AtomicReference;

public class ThreadWrapper {

   private AtomicReference<RuntimeException> runtimeExceptionReference;
   private AtomicReference<Error> errorReference;
   private Thread thread;
   
   public ThreadWrapper(final Runnable runnable) {
      this(runnable,"ThreadWrapper-NoName");
   }
   
   public ThreadWrapper(final Runnable runnable, String name) {
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
      },name);
   }
      
   public void start() {
      thread.start();
   }


   public void join() throws InterruptedException  {
      join(0);
   }
   
   public void join(long millis) throws InterruptedException  {
      thread.join(millis);
      RuntimeException runtimeException = runtimeExceptionReference.get();
      if (runtimeException != null) {
         throw runtimeException;
      }
      Error error = errorReference.get();
      if (error != null) {
         throw error;
      }
   }

   public Thread getWrappedThread() {
      return thread;
   }
   
   

}

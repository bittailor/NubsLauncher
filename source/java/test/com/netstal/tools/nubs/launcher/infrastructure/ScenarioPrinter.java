package com.netstal.tools.nubs.launcher.infrastructure;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

public class ScenarioPrinter {

   public static void justReturn() {
      
   }
   
   public static void oneLineToOut() {
      System.out.println("One");
   }
   
   public static void multipleLineToOut() {
      System.out.println("1");
      System.out.println("2");
      System.out.println("3");
   }
   
   public static void outAndError() {
      System.out.println("out1");
      System.err.println("err1");
   
      System.out.println("out2");
      System.err.println("err2"); 
   }
   
   public static void inToOutput() {
      Scanner scanner = new Scanner(System.in);
      String line = scanner.nextLine();    
      System.out.println(line); 
   }
   
   
   public static void main(String[] args) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
      String methodName = args[0];
      Method method = ScenarioPrinter.class.getMethod(methodName);
      method.invoke(null);

   }

}

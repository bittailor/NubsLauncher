package com.netstal.tools.nubs.launcher.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.netstal.tools.nubs.launcher.infrastructure.OperatingSystem;

/**
 * 
 */
public class CommandTest {

   private String one;
   private String two;
   private Command commandOne;
   private Command commandTwo;
   private Command commandOneClone;
   private Command commandTwoClone;

   @Before
   public void setUp() {
      one = "Project_One Appl_A Appl_B";
      two = "Project_Two Appl_C flag_one=hallo flag_two=\"echo my flag\"";
      commandOne = new Command(one);
      commandTwo = new Command(two);
      commandOneClone = new Command(one);
      commandTwoClone = new Command(two);
   }
   
   @Test
   public void testHashCode() {
      assertTrue(commandOneClone.hashCode()==commandOne.hashCode());      
      assertTrue(commandTwoClone.hashCode()==commandTwo.hashCode());      
      assertFalse(commandTwo.hashCode()==commandOne.hashCode());      
   }
   
   @Test
   public void testCommand() {
      assertArrayEquals(commandArray("Project_One", "Appl_A", "Appl_B"),commandOne.command());       
      assertArrayEquals(commandArray("Project_Two", "Appl_C", "flag_one=hallo","flag_two=echo my flag"),commandTwo.command());       
   }
   
   @Test
   public void testGetTasks() {
      assertArrayEquals(taskList("Project_One", "Appl_A", "Appl_B"),commandOne.getTasks().toArray());       
      assertArrayEquals(taskList("Project_Two", "Appl_C"),commandTwo.getTasks().toArray());       
   }

   @Test
   public void testToString() {
      assertEquals(one, commandOne.toString());      
      assertEquals(two, commandTwo.toString());
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testConstructWithNull() {
      new Command(null);
   }

   @Test
   public void testEqualsObject() {
      assertEquals(commandOneClone, commandOne);
      assertEquals(commandOne, commandOne);
      assertFalse(commandOne.equals(null));
      assertFalse(commandOne.equals(new Object()));
      assertFalse(commandOneClone==commandOne);
      assertEquals(commandTwoClone, commandTwo);
      assertFalse(commandTwo.equals(commandOne));
   }
   
   private String[] commandArray(String... arguments) {
      String [] result = new String[arguments.length+1];
      result[0] = OperatingSystem.getRakeCommand();
      for (int i = 0; i < arguments.length; i++) {
         result[i+1] = arguments[i];
      }
      
      return result;
   }
   
   private String[] taskList(String... arguments) {
      String [] result = new String[arguments.length];
      for (int i = 0; i < arguments.length; i++) {
         result[i] = arguments[i];
      }  
      return result;
   }


}

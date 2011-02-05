//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

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
      one = "Project One";
      two = "Project Tw0";
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

}

package com.netstal.tools.nubs.launcher.infrastructure;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class StringUtilityTest {

   @Before
   public void setUp() throws Exception {
   }

   @After
   public void tearDown() throws Exception {
   }

   @Test
   public void testJoinStringStringArray() {
      assertEquals("AB-CD-EF", StringUtility.join("-", "AB","CD","EF"));
   }
   
   @Test
   public void testJoinStringStringArrayEmpty() {
      assertEquals("", StringUtility.join("-"));
   }
   
   @Test
   public void testJoinStringStringArrayOnlyOneElement() {
      assertEquals("One", StringUtility.join("-","One"));
   }

   @Test
   public void testJoinStringCollectionOfString() {
      assertEquals("AB-CD-EF", StringUtility.join("-",asCollection("AB","CD","EF")));
   }
   
   @Test
   public void testJoinStringCollectionOfStringEmpty() {
      assertEquals("", StringUtility.join("-",asCollection()));
   }
   
   @Test
   public void testJoinStringCollectionOfStringOneElement() {
      assertEquals("One", StringUtility.join("-",asCollection("One")));
   }
   
   Collection<String> asCollection(String... strings) {
      return Arrays.asList(strings);
   }
   
   

}

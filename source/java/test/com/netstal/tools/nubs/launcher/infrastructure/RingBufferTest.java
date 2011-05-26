package com.netstal.tools.nubs.launcher.infrastructure;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class RingBufferTest {

   private static final int CAPACITY = 5;
   private RingBuffer<Integer> ringBuffer;

   @Before
   public void setUp() throws Exception {
      ringBuffer = new RingBuffer<Integer>(CAPACITY);
   }

   @After
   public void tearDown() throws Exception {
      ringBuffer = null;
   }

   @Test
   public void testCapacity() {
      assertEquals(CAPACITY,ringBuffer.capacity());
   }
   
   @Test
   public void testSize() {
      assertEquals(0,ringBuffer.size());
      ringBuffer.add(1);
      assertEquals(1,ringBuffer.size());
      ringBuffer.add(2);
      assertEquals(2,ringBuffer.size());
      ringBuffer.add(3);
      assertEquals(3,ringBuffer.size());
      ringBuffer.add(4);
      assertEquals(4,ringBuffer.size());
      ringBuffer.add(5);
      assertEquals(5,ringBuffer.size());
      for(int i = 6 ; i < 20 ; i++ ) {
         ringBuffer.add(i);
         assertEquals(5,ringBuffer.size());
      }
   }

   @Test
   public void testIsEmpty() {
      assertTrue(ringBuffer.isEmpty());
      ringBuffer.add(1);
      assertFalse(ringBuffer.isEmpty());
   }

   @Test
   public void testContains() {
      assertFalse(ringBuffer.contains(1));
      ringBuffer.add(1);
      assertTrue(ringBuffer.contains(1));
   }
   
   @Test
   public void testContainsOverriden() {
      fill(0,20);
      assertTrue(ringBuffer.contains(19));
      assertTrue(ringBuffer.contains(18));
      assertTrue(ringBuffer.contains(17));
      assertTrue(ringBuffer.contains(16));
      assertTrue(ringBuffer.contains(15));
      assertFalse(ringBuffer.contains(14));
      assertFalse(ringBuffer.contains(13));
      assertFalse(ringBuffer.contains(1));
      assertFalse(ringBuffer.contains(0));
   }

   @Test
   public void testIterator() {
      assertFalse(ringBuffer.iterator().hasNext());
      fill(0,3);
      Iterator<Integer> iterator = ringBuffer.iterator();
      assertTrue(iterator.hasNext());
      assertEquals(0, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(1, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(2, iterator.next());
      assertFalse(iterator.hasNext()); 
   }
   
   @Test(expected=NoSuchElementException.class)
   public void testIteratorInvalidNext() {
      fill(0,3);
      Iterator<Integer> iterator = ringBuffer.iterator();
      iterator.next();
      iterator.next();
      iterator.next();
      iterator.next();
   }
   
   @Test(expected=UnsupportedOperationException.class)
   public void testIteratorRemove() {
      fill(0,3);
      Iterator<Integer> iterator = ringBuffer.iterator();
      iterator.remove();
   }
   
   @Test
   public void testIteratorOverriden() {
      fill(0,18);
      Iterator<Integer> iterator = ringBuffer.iterator();
      assertTrue(iterator.hasNext());
      assertEquals(13, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(14, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(15, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(16, iterator.next());
      assertTrue(iterator.hasNext());
      assertEquals(17, iterator.next());
      assertFalse(iterator.hasNext());
   }

   @Test
   public void testToArray() {
      assertArrayEquals(new Integer[0], ringBuffer.toArray());
      fill(0,3);
      assertArrayEquals(new Integer[]{0,1,2}, ringBuffer.toArray());
      fill(3,25);
      assertArrayEquals(new Integer[]{20,21,22,23,24}, ringBuffer.toArray());
   }

   @Test
   public void testToArrayTArray() {
      assertArrayEquals(new Integer[0], ringBuffer.toArray(new Integer[0]));
      fill(0,3);
      assertArrayEquals(new Integer[]{0,1,2}, ringBuffer.toArray(new Integer[0]));
      assertArrayEquals(new Integer[]{0,1,2}, ringBuffer.toArray(new Integer[3]));
      assertArrayEquals(new Integer[]{0,1,2,null,5,6,7}, ringBuffer.toArray(new Integer[]{1,2,3,4,5,6,7}));
   }

   @Test(expected=UnsupportedOperationException.class)
   public void testRemove() {
      ringBuffer.remove(99);
   }

   @Test
   public void testContainsAll() {
      fill(10,13);
      assertTrue(ringBuffer.containsAll(Arrays.asList(new Integer[]{10,11,12})));
      assertTrue(ringBuffer.containsAll(Arrays.asList(new Integer[]{10,12})));
      assertFalse(ringBuffer.containsAll(Arrays.asList(new Integer[]{10,11,12,13})));
      fill(20,30);
      assertTrue(ringBuffer.containsAll(Arrays.asList(new Integer[]{29,28,27,26,25})));
      assertFalse(ringBuffer.containsAll(Arrays.asList(new Integer[]{29,24})));
      assertFalse(ringBuffer.containsAll(Arrays.asList(new Integer[]{24})));
      assertTrue(ringBuffer.containsAll(Arrays.asList(new Integer[]{29})));
   }

   @Test
   public void testAddAll() {
      ringBuffer.addAll(Arrays.asList(new Integer[]{10,20,30}));
      assertEquals(3, ringBuffer.size());
      assertTrue(ringBuffer.containsAll(Arrays.asList(new Integer[]{10,20,30})));
      ringBuffer.addAll(Arrays.asList(new Integer[]{30,31,32,33,34,35,36,37,38,39}));
      assertEquals(5, ringBuffer.size());
      assertTrue(ringBuffer.containsAll(Arrays.asList(new Integer[]{35,36,37,38,39})));
      assertFalse(ringBuffer.contains(34));  
   }

   @Test(expected=UnsupportedOperationException.class)
   public void testRemoveAll() {
      ringBuffer.removeAll(Arrays.asList(new Integer[]{29,28,27,26,25}));
   }

   @Test(expected=UnsupportedOperationException.class)
   public void testRetainAll() {
      ringBuffer.retainAll(Arrays.asList(new Integer[]{29,28,27,26,25}));
   }

   @Test
   public void testClear() {
      fill(0,3);
      assertEquals(3,ringBuffer.size());
      assertTrue(ringBuffer.contains(2));
      ringBuffer.clear();
      assertEquals(0,ringBuffer.size());
      assertFalse(ringBuffer.contains(2));
   }
   
   private void fill(int from, int to) {
      for(int i = from ; i < to ; i++ ) {
         ringBuffer.add(i);    
      }
   }

}

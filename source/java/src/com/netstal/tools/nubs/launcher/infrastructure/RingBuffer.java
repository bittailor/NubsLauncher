package com.netstal.tools.nubs.launcher.infrastructure;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class RingBuffer<E> implements Collection<E> {

   private final Object[] buffer;
   private Strategie strategie;
   private int begin;
   private int end;
   
   
   public RingBuffer(int capacity) {
      buffer = new Object[capacity];
      strategie = new Filling();
      begin = 0;
      end = 0;
   }
   
   public int capacity() {
      return buffer.length;
   }

   @Override
   public int size() {
      return strategie.size();
   }

   @Override
   public boolean isEmpty() {
      return begin == end;
   }

   @Override
   public boolean contains(Object o) {
      for (Object object : buffer) {
         if(o.equals(object)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Iterator<E> iterator() {
      return new BufferIterator();
   }

   @Override
   public Object[] toArray() {
      Object[] array = new Object[size()];
      copyToArray(array);
      return array;
   }
   
   @Override
   public <T> T[] toArray(T[] a) {
      if (a.length >= size()) {
         copyToArray(a);
         if (a.length > size()) {
            a[size()] = null;
         }
         return a;
      }
      
      @SuppressWarnings("unchecked")
      T[] result = (T[])toArray();
      return result;
   }

   private void copyToArray(Object[] array) {
      int index = 0;
      for (Object object : this) {
         array[index] = object;
         index++;
      }
   }   

   @Override
   public boolean add(E e) {
      strategie.add(e);
      return true;
   }

   @Override
   public boolean remove(Object o) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      for (Object object : c) {
         if (!contains(object)) {
            return false;
         }
      }
      return true;
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      for (E e : c) {
         add(e);
      }
      return true;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void clear() {
      for (int i = 0; i < buffer.length; i++) {
         buffer[i] = null;
      }
      begin = 0;
      end = 0;
      strategie = new Filling();
   }
   
   private int nextLocation(int pointer) {
      return (pointer + 1) % buffer.length;
   }
   
   private interface Strategie {
      public void add(Object object);
      public int size();
   }
   
   private class Filling implements Strategie {
      @Override
      public void add(Object object) {
         buffer[end] = object;
         end = nextLocation(end);
         if (end == 0) {
            strategie = new Overriding(); 
         }
      }
      
      @Override
      public int size() {
         return end - begin;
      }
      
   }
   
   private class Overriding implements Strategie{
      
      @Override
      public void add(Object object) {
         buffer[end] = object;
         end = nextLocation(end);
         begin = nextLocation(begin);
      }
      
      @Override
      public int size() {
         return buffer.length;
      }
      
   }
   
   
   
   private class BufferIterator implements Iterator<E> {

      private int current;
      private int count;
      
      public BufferIterator() {
         current = begin;
         count = size();
      }
      
      
      @Override
      public boolean hasNext() {
         return count > 0;
      }

      @Override
      public E next() {
         if(!hasNext()) {
            throw new NoSuchElementException(); 
         }
         @SuppressWarnings("unchecked")
         E result = (E)buffer[current];
         current = nextLocation(current);
         count--;
         return result;
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }
      
   }

}

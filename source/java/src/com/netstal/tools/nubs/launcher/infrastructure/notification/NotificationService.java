package com.netstal.tools.nubs.launcher.infrastructure.notification;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.swing.SwingUtilities;



public class NotificationService implements INotificationService {

   protected static class ListenerEntry {
      public final IListener listener;
      public final boolean callInSwingThread;
      
      private ListenerEntry(IListener listener, boolean callInSwingThread) {
         this.listener = listener;
         this.callInSwingThread = callInSwingThread;  
      }
      
      private ListenerEntry(IListener listener) {
         this(listener,false); 
      }

      @Override
      public int hashCode() {
         return listener.hashCode();
      }

      @Override
      public boolean equals(Object object) {
         if (object instanceof ListenerEntry) {
            ListenerEntry other = (ListenerEntry) object;
            return listener.equals(other.listener);
         }
         return false;
      }
   }
   
   protected static class ListenerStorage {
      public Map<IEmitter,List<ListenerEntry>> qualifiedListeners = new  WeakHashMap<IEmitter, List<ListenerEntry>>();
      public List<ListenerEntry> unqualifiedListeners = new LinkedList<ListenerEntry>();
   }
   
   protected static class NotificationHandler implements InvocationHandler {
      
      private final List<List<ListenerEntry>> listOfListeners;
    
      private NotificationHandler(List<ListenerEntry> qualifiedListeners, List<ListenerEntry> unqualifiedListeners) {
         listOfListeners = new ArrayList<List<ListenerEntry>>(2);
         listOfListeners.add(qualifiedListeners);
         listOfListeners.add(unqualifiedListeners);
      }

      @Override
      public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
         int totalNumberOfListeners = 0;
         for (List<ListenerEntry> listeners : listOfListeners) {
            synchronized (listeners) {
               totalNumberOfListeners = totalNumberOfListeners + listeners.size();               
            }
         }
         
         if (totalNumberOfListeners == 0) {
            return null;
         }
         
         
         List<IListener> directListeners = new ArrayList<IListener>(totalNumberOfListeners); 
         final List<IListener> swingListeners = new ArrayList<IListener>(totalNumberOfListeners); 
         
         for (List<ListenerEntry> listeners : listOfListeners) {
            synchronized (listeners) {               
               for (ListenerEntry listenerEntry : listeners) {
                  if (listenerEntry.callInSwingThread) {
                     swingListeners.add(listenerEntry.listener);
                  } else {
                     directListeners.add(listenerEntry.listener);
                  }
               }
            }
         }
         
           
         Object returnValue = null;
         for (IListener listener : directListeners) {
            returnValue = method.invoke(listener, args);
         }
         
         SwingUtilities.invokeLater(new Runnable() {              
               @Override
               public void run() {
                  for (IListener listener : swingListeners) {
                     try {
                        method.invoke(listener, args);
                     }
                     catch (Throwable throwable) {
                        throw new RuntimeException("Exception during listener notification in swing thread", throwable);
                     }
                  }                  
               }
         });
            
         return returnValue;
      }
   }
   
   private Map<Class<?>,ListenerStorage> storageMap = new HashMap<Class<?>, NotificationService.ListenerStorage>(); 
   
   @Override
   public <T extends IListener> void addListener(Class<T> listenerInterface, T listener, IEmitter emitter) {
      List<ListenerEntry> listeners = get(listenerInterface,emitter);
      synchronized (listeners) {         
         listeners.add(new ListenerEntry(listener,false));
      }
   }
   
   @Override
   public <T extends IListener> void addListenerInSwingThread(Class<T> listenerInterface, T listener, IEmitter emitter) {
      List<ListenerEntry> listeners = get(listenerInterface,emitter);
      synchronized (listeners) {
         listeners.add(new ListenerEntry(listener,true));
      }
   }
   
   @Override
   public <T extends IListener> void addListener(Class<T> listenerInterface, T listener) {
      List<ListenerEntry> listeners = get(listenerInterface);
      synchronized (listeners) {         
         listeners.add(new ListenerEntry(listener,false));
      } 
   }
   
   @Override
   public <T extends IListener> void addListenerInSwingThread(Class<T> listenerInterface, T listener) {
      List<ListenerEntry> listeners = get(listenerInterface);
      synchronized (listeners) {
         listeners.add(new ListenerEntry(listener,true));
      }
   }
   
   @Override
   public <T extends IListener> boolean removeListener(Class<T> listenerInterface, T listener, IEmitter emitter) {
      List<ListenerEntry> listeners = lookup(listenerInterface,emitter);
      if (listener == null) {
         return false;
      }
      
      synchronized (listeners) {
         return listeners.remove(new ListenerEntry(listener));         
      }
   }
   
   @Override
   public <T extends IListener> boolean removeListener(Class<T> listenerInterface, T listener) {
      List<ListenerEntry> listeners = lookup(listenerInterface);
      if (listener == null) {
         return false;
      }
      
      synchronized (listeners) {
         return listeners.remove(new ListenerEntry(listener));
      }
   }
   
   @Override
   @SuppressWarnings("unchecked")
   public <T extends IListener> T getNotificationDispatcher(Class<T> listenerInterface, IEmitter emitter) {
      List<ListenerEntry> unqualifiedListeners = get(listenerInterface);      
      List<ListenerEntry> qualifiedListeners = get(listenerInterface,emitter);
          
      InvocationHandler handler = new NotificationHandler(qualifiedListeners,unqualifiedListeners);
      return (T) Proxy.newProxyInstance(listenerInterface.getClassLoader(), new Class[] { listenerInterface }, handler);
   }
   
   private <T> List<ListenerEntry> get(Class<T> listenerInterface, IEmitter emitter) {
      ListenerStorage listenerStorage = getListenerStorage(listenerInterface);
      
      synchronized (listenerStorage) {         
         List<ListenerEntry> listeners = listenerStorage.qualifiedListeners.get(emitter);
         if (listeners==null) {
            listeners = new LinkedList<ListenerEntry>();
            listenerStorage.qualifiedListeners.put(emitter, listeners);
         }
         
         return listeners;
      }
      
   }

   private <T> List<ListenerEntry> lookup(Class<T> listenerInterface, IEmitter emitter) {
      
      ListenerStorage listenerStorage;
      synchronized (storageMap) {
         listenerStorage = storageMap.get(listenerInterface);
         if (listenerStorage==null) {
            return null;
         }         
      }
               
      synchronized (listenerStorage) {
         List<ListenerEntry> listeners = listenerStorage.qualifiedListeners.get(emitter);
         if (listeners==null) {
            return null;
         }
         
         return listeners;
      }
   }
   
   private <T> List<ListenerEntry> get(Class<T> listenerInterface) {
      return getListenerStorage(listenerInterface).unqualifiedListeners;
   }
   
   private <T> List<ListenerEntry> lookup(Class<T> listenerInterface) {
      synchronized (storageMap) {
         ListenerStorage listenerStorage = storageMap.get(listenerInterface);
         if (listenerStorage==null) {
            return null;
         }    
         return listenerStorage.unqualifiedListeners;
      }
   }
   
   protected <T> ListenerStorage getListenerStorage(Class<T> listenerInterface) {
      synchronized (storageMap) {
         ListenerStorage listenerStorage = storageMap.get(listenerInterface);
         if (listenerStorage==null) {
            listenerStorage = new ListenerStorage();
            storageMap.put(listenerInterface, listenerStorage);
         }         
         return listenerStorage;
      }      
   }
      
}

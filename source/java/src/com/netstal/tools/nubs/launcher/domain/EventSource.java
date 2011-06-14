//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.LinkedList;
import java.util.List;

import javax.swing.SwingUtilities;

public class EventSource<Source> implements IEventSource<Source> {

   private List<IEventListener<Source>> listeners = new LinkedList<IEventListener<Source>>();
   private List<IEventListener<Source>> swingListeners = new LinkedList<IEventListener<Source>>();
   
   @Override
   public void addListener(IEventListener<Source> listener) {
      listeners.add(listener);
   }
   
   @Override
   public void addListenerNotifyInSwingDispatchThread(IEventListener<Source> listener) {
      swingListeners.add(listener);
   }
   
   @Override
   public void removeListener(IEventListener<Source> listener) {
      if (!listeners.remove(listener)) {
         swingListeners.remove(listener);
      }
   }
   
   public void notifyEventListeners(final Source source) {
      for (IEventListener<Source> listener : listeners) {
         listener.notifyEvent(source);
      }
      if (SwingUtilities.isEventDispatchThread()){
         for (IEventListener<Source> listener : swingListeners) {
            listener.notifyEvent(source);
         }
      } else {
         SwingUtilities.invokeLater(new Runnable() {               
            @Override
            public void run() {
               for (IEventListener<Source> listener : swingListeners) {
                  listener.notifyEvent(source);
               }
            }                  
         }); 
      }
   }
  
}

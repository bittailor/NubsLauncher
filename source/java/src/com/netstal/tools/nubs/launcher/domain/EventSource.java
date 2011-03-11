//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 */
public class EventSource<Source> implements IEventSource<Source> {

   private List<IEventListener<Source>> sources = new LinkedList<IEventListener<Source>>();
   
   @Override
   public void addListener(IEventListener<Source> listener) {
      sources.add(listener);
   }

   @Override
   public void removeListener(IEventListener<Source> listener) {
      sources.remove(listener);
   }
   
   public void notifyEventListeners(Source source) {
      for (IEventListener<Source> listener : sources) {
         listener.notifyEvent(source);
      }
   }
  
}

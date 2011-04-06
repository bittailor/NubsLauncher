//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;


public interface IEventSource<Source> {
   public void addListener(IEventListener<Source> listener);
   public void addListenerNotifyInSwingDispatchThread(IEventListener<Source> listener);
   public void removeListener(IEventListener<Source> listener);
}

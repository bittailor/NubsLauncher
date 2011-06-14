package com.netstal.tools.nubs.launcher.domain;


public interface IEventSource<Source> {
   public void addListener(IEventListener<Source> listener);
   public void addListenerNotifyInSwingDispatchThread(IEventListener<Source> listener);
   public void removeListener(IEventListener<Source> listener);
}

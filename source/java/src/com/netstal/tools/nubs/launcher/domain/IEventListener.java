package com.netstal.tools.nubs.launcher.domain;

public interface IEventListener<Source> {
   
   public void notifyEvent(Source source);
   
}

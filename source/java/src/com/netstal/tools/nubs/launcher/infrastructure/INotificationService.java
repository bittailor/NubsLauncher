
package com.netstal.tools.nubs.launcher.infrastructure;



public interface INotificationService {

   public <T extends IListener> void addListener(Class<T> listenerInterface, T listener, Object emitter);
   public <T extends IListener> void addListenerInSwingThread(Class<T> listenerInterface, T listener, Object emitter);

   public <T extends IListener> void addListener(Class<T> listenerInterface, T listener);
   public <T extends IListener> void addListenerInSwingThread(Class<T> listenerInterface, T listener);

   public <T extends IListener> boolean removeListener(Class<T> listenerInterface, T listener, Object emitter);

   public <T extends IListener> boolean removeListener(Class<T> listenerInterface, T listener);

   public <T extends IListener> T getNotificationDispatcher(Class<T> listenerInterface, Object emitter);


}

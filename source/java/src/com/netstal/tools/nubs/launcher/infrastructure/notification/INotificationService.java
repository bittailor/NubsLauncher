
package com.netstal.tools.nubs.launcher.infrastructure.notification;




public interface INotificationService {

   public <T extends IListener> void addListener(Class<T> listenerInterface, T listener, IEmitter emitter);
   public <T extends IListener> void addListenerInSwingThread(Class<T> listenerInterface, T listener, IEmitter emitter);

   public <T extends IListener> void addListener(Class<T> listenerInterface, T listener);
   public <T extends IListener> void addListenerInSwingThread(Class<T> listenerInterface, T listener);

   public <T extends IListener> boolean removeListener(Class<T> listenerInterface, T listener, IEmitter emitter);

   public <T extends IListener> boolean removeListener(Class<T> listenerInterface, T listener);

   public <T extends IListener> T getNotificationDispatcher(Class<T> listenerInterface, IEmitter emitter);


}

//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.domain;

import static org.easymock.EasyMock.createControl;
import static org.easymock.EasyMock.expectLastCall;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

import org.easymock.IAnswer;
import org.easymock.IMocksControl;
import org.junit.Test;

public class EventSourceTest {

   
   @Test
   public void testNotifyListener() {
      
      IMocksControl control = createControl();
      IEventListener<String> listener1 = createListenerMock(control);
      IEventListener<String> listener2 = createListenerMock(control);
      
      listener1.notifyEvent("1");
      listener1.notifyEvent("2");
      listener2.notifyEvent("2");
      listener2.notifyEvent("3");
      
      control.replay();
      EventSource<String> eventSource = new EventSource<String>();
      
      eventSource.addListener(listener1);
      eventSource.notifyEventListeners("1");
      eventSource.addListener(listener2);
      eventSource.notifyEventListeners("2");
      eventSource.removeListener(listener1);
      eventSource.notifyEventListeners("3");
      eventSource.removeListener(listener2);
      eventSource.notifyEventListeners("4");
      
      control.verify();
   }
   
   @Test
   public void testNotifySwingListeners() throws InterruptedException {
      IMocksControl control = createControl();
      final BlockingQueue<Boolean> inSwingThread = new ArrayBlockingQueue<Boolean>(1);
      IEventListener<String> listener = createListenerMock(control);
      
      listener.notifyEvent("Swing");
      expectLastCall().andAnswer(new IAnswer<Object>() {

         @Override
         public Object answer() throws Throwable {
            inSwingThread.put(SwingUtilities.isEventDispatchThread());
            return null;
         }
      });
      
      control.replay();
      
      EventSource<String> eventSource = new EventSource<String>();
      eventSource.addListenerNotifyInSwingDispatchThread(listener);
      
      eventSource.notifyEventListeners("Swing");
      
      assertTrue(inSwingThread.poll(1, TimeUnit.SECONDS));
      
      control.verify();  
   }

   @SuppressWarnings("unchecked")
   private IEventListener<String> createListenerMock(IMocksControl control) {
      return control.createMock(IEventListener.class);
   }

}

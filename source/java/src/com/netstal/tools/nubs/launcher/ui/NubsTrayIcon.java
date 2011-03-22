package com.netstal.tools.nubs.launcher.ui;

import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

public class NubsTrayIcon {

   private TrayIcon trayIcon;

   public NubsTrayIcon() {
      
      if (SystemTray.isSupported()) {
          
         SystemTray tray = SystemTray.getSystemTray();
          // load an image
          Image image = new ImageIcon(NubsLauncherFrame.class.getResource("images/Launch.png")).getImage();
          // create a action listener to listen for default action executed on the tray icon
          ActionListener listener = new ActionListener() {
              public void actionPerformed(ActionEvent actionEvent) {
                  // execute default action of the application
                  // ...
              }
          };
          // create a popup menu
          PopupMenu popup = new PopupMenu();
          // create menu item for the default action
          MenuItem defaultItem = new MenuItem("bla");
          defaultItem.addActionListener(listener);
          popup.add(defaultItem);
          /// ... add other items
          // construct a TrayIcon
          trayIcon = new TrayIcon(image, "Nubs Launcher", popup);
          // set the TrayIcon properties
          trayIcon.addActionListener(listener);
          // ...
          // add the tray image
          try {
              tray.add(trayIcon);
          } catch (AWTException e) {
              System.err.println(e);
          }
          // ...
      } else {
          // disable tray option in your application or
          // perform other actions
       
      }
      // ...
      
    
   }
   
   
   public void displayMessage(String caption, String text, TrayIcon.MessageType messageType) {
      if (trayIcon != null) {
         trayIcon.displayMessage(caption, text, messageType);
      }
   }
   
   
}

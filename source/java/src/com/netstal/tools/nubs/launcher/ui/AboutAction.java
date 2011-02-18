package com.netstal.tools.nubs.launcher.ui;

import java.awt.Component;
import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.net.URI;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class AboutAction extends AbstractAction {
   private static final long serialVersionUID = 1L;
   private Component component;
   private String version;

   public AboutAction(Component component, String version) {
      super("About", new ImageIcon(NubsLauncherFrame.class.getResource("images/About.png")));
      this.putValue(SHORT_DESCRIPTION, "About");
      this.component = component;
      this.version = version;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      Object[] options = {"Ok","-> F&E Wiki"};
      int n = JOptionPane.showOptionDialog(component,
               "<html>" +
               "<b>NUBS Launcher</b> Version " + version +
               "<br>"+
               "<br>"+
               "More Informationen @ F&E Wiki<html>",
               "About NUBS Launcher",
               JOptionPane.YES_NO_OPTION,
               JOptionPane.INFORMATION_MESSAGE,
               new ImageIcon(NubsLauncherFrame.class.getResource("images/Rocket.png")),
               options,
               options[0]);
      if(n==JOptionPane.NO_OPTION) {
         if (Desktop.isDesktopSupported()) {
            Desktop desktop = Desktop.getDesktop();
            try {
               desktop.browse(new URI("http://fuewiki.nmag.ch/FuEWiki/index.php5/NUBS_Launcher"));
            }
            catch (Exception exception) {
               // TODO Auto-generated catch block
               exception.printStackTrace();
            }
         }
      }
   }     
}

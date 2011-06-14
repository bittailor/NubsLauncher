package com.netstal.tools.nubs.launcher.ui.job.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.border.AbstractBorder;

public class RowSelectionBorder extends AbstractBorder {

   private Color color;
   private int size;
   
   public RowSelectionBorder(Color color, int size) {
      this.color = color;
      this.size = size;
   }

   @Override
   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      if (g instanceof Graphics2D) {
         Graphics2D graphics = (Graphics2D) g;            
         paintBorder(graphics, x, y, width, height);
      }
   }

   private void paintBorder(Graphics2D graphics, int x, int y, int width, int height) {
      Color oldColor = graphics.getColor();
      graphics.setColor(color);
      graphics.fillRect(x, y, width, size);
      graphics.fillRect(x, y - size + height, width, size);
      graphics.setColor(oldColor);
   }
}
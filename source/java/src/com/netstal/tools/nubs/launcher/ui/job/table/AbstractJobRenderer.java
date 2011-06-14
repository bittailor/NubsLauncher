package com.netstal.tools.nubs.launcher.ui.job.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.job.ColorJobStateVisitor;

public abstract class AbstractJobRenderer extends JLabel implements TableCellRenderer {
   
   @Override
   public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
      setOpaque(true);
      if (value instanceof IRakeJob) {
         IRakeJob job = (IRakeJob) value;
         renderJob(table, isSelected, job);
      }
      return this;         
   }

   private void renderJob(JTable table, boolean isSelected, IRakeJob job) {
      renderBackground(job);
      renderSelection(table, isSelected);
      renderJobCell(job);
   }

   private void renderSelection(JTable table, boolean isSelected) {
      if (isSelected) {
         setBorder(new  RowSelectionBorder(table.getSelectionBackground(),3));
      } else {
         setBorder(BorderFactory.createEmptyBorder());
      }
   }

   private void renderBackground(IRakeJob job) {
      Color color = job.getState().accept(new ColorJobStateVisitor()).getColor();
      setBackground(color);
   }

   protected abstract void renderJobCell(IRakeJob job);

}
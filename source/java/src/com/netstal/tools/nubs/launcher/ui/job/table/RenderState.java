package com.netstal.tools.nubs.launcher.ui.job.table;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.ui.DisplayNameJobStateVisitor;

public class RenderState extends AbstractJobRenderer {
   @Override
   public void renderJobCell(IRakeJob job) {
      setText(job.getState().accept(new DisplayNameJobStateVisitor()).getName());           
   }
}
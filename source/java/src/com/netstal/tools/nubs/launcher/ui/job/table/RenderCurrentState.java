package com.netstal.tools.nubs.launcher.ui.job.table;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class RenderCurrentState extends AbstractJobRenderer {
   @Override
   public void renderJobCell(IRakeJob job) {
      setText(job.getCurrentTask());           
   }
}
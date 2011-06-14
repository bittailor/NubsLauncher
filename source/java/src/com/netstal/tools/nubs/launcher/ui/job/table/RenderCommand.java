package com.netstal.tools.nubs.launcher.ui.job.table;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class RenderCommand extends AbstractJobRenderer {
   @Override
   public void renderJobCell(IRakeJob job) {
      setText(job.getCommand().toString());           
   }
}
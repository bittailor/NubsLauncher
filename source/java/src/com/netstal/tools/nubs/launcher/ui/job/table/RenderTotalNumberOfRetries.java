package com.netstal.tools.nubs.launcher.ui.job.table;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class RenderTotalNumberOfRetries extends AbstractJobRenderer {
   @Override
   public void renderJobCell(IRakeJob job) {
      setText(Integer.toString(job.getTotalNumberOfRetries()));           
   }
}
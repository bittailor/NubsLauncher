package com.netstal.tools.nubs.launcher.ui.job.table;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class RenderAutoRetry extends AbstractJobRenderer {
   @Override
   public void renderJobCell(IRakeJob job) {
      if (job.isAutoRetry()) {
         setText("" + job.getCurrentNumberOfAutoRetries() + " of " + job.getMaximumNumberOfAutoRetries());
      } else {
         setText("Off");
      }           
   }
}
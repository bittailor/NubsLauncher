package com.netstal.tools.nubs.launcher.ui.job.table;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class RenderCurrentTask extends AbstractJobRenderer {
   @Override
   public void renderJobCell(IRakeJob job) {
      setText(job.getCurrentTask() + " [ @" + job.getTaskCounter() + " of ~" + job.getFinalTaskCount() + " ]" );           
   }
}
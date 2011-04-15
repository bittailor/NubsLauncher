package com.netstal.tools.nubs.launcher.ui;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public interface IJobTailFrameFactory {
   public JobTailFrame create(IRakeJob job);
}

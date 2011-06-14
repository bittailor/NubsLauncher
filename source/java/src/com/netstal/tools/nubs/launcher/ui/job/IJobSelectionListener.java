package com.netstal.tools.nubs.launcher.ui.job;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public interface IJobSelectionListener {
   public void newSelection(IRakeJob job);
}
package com.netstal.tools.nubs.launcher.ui;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.domain.IConfiguration;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;

public class JobTailFrameFactory implements IJobTailFrameFactory {

   Provider<NubsLauncherFrame> mainFrame;
   private IConfiguration configuration;
   
   @Inject
   private JobTailFrameFactory(Provider<NubsLauncherFrame> mainFrame, IConfiguration configuration) {
      this.mainFrame = mainFrame;
      this.configuration = configuration;
   }

   @Override
   public JobTailFrame create(IRakeJob job) {
      NubsLauncherFrame frame = mainFrame.get();
      return new JobTailFrame(job, frame, configuration);
   }

}

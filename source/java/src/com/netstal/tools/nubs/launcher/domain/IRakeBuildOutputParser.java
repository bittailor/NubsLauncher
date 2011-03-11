package com.netstal.tools.nubs.launcher.domain;

import com.netstal.tools.nubs.launcher.infrastructure.ILineConsumer;

public interface IRakeBuildOutputParser extends ILineConsumer {

   public abstract void removeListener(IRakeBuildOutputListener listener);
   public abstract void addListener(IRakeBuildOutputListener listener);
   
}

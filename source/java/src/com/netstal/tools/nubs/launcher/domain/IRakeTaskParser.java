
package com.netstal.tools.nubs.launcher.domain;

import java.util.SortedMap;

import com.netstal.tools.nubs.launcher.infrastructure.ILineConsumer;



public interface IRakeTaskParser extends ILineConsumer {

   public abstract SortedMap<String, RakeTask> getTasks();

}

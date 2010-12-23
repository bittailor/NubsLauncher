
package com.netstal.tools.nubs.launcher.domain;

import java.util.SortedMap;

import com.netstal.tools.nubs.launcher.infrastructure.I_LineConsumer;



public interface IRakeTaskParser extends I_LineConsumer {

   public abstract SortedMap<String, RakeTask> getTasks();

}

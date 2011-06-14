package com.netstal.tools.nubs.launcher.ui.job;

import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobStateVisitor;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;

public class DisplayNameJobStateVisitor implements IJobStateVisitor {

   
   private String name;
   
   public String getName() {
      return name;
   }

   @Override
   public void visit(Idle state) {
      name = "Idle";
   }

   @Override
   public void visit(Building state) {
      name = "Building";
   }

   @Override
   public void visit(Failed state) {
      name = "Failed" ;
   }

   @Override
   public void visit(FinishedSucessfully state) {
      name = "Finished Sucessfully" ;
   }

   @Override
   public void visit(FinishedFaultily state) {
      name = "Finished With A Failure" ;
   }

   @Override
   public void visit(FinishedExceptionally state) {
      name = "Finished With An Exception" ;
   }

}

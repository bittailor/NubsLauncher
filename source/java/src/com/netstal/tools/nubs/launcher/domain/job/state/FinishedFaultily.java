package com.netstal.tools.nubs.launcher.domain.job.state;



public class FinishedFaultily implements IJobState {

   public static final FinishedFaultily INSTANCE = new FinishedFaultily(); 
   
   private FinishedFaultily() {
   }
   
   @Override
   public boolean isFinished() {
      return true;
   }
   
   @Override
   public <V extends IJobStateVisitor> V accept(V visitor) {
      visitor.visit(this);
      return visitor;
   }

}

package com.netstal.tools.nubs.launcher.domain.job.state;



public class FinishedExceptionally implements IJobState {

   public static final FinishedExceptionally INSTANCE = new FinishedExceptionally(); 
   
   private FinishedExceptionally() {
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

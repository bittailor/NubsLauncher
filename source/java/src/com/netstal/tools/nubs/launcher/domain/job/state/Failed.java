package com.netstal.tools.nubs.launcher.domain.job.state;



public class Failed implements IJobState {

   public static final Failed INSTANCE = new Failed(); 
   
   private Failed() {
   }
   
   @Override
   public boolean isFinished() {
      return false;
   }

   @Override
   public <V extends IJobStateVisitor> V accept(V visitor) {
      visitor.visit(this);
      return visitor;
   }

}

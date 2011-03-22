package com.netstal.tools.nubs.launcher.domain.job.state;



public class Idle implements IJobState {

   public static final Idle INSTANCE = new Idle(); 
   
   private Idle() {
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

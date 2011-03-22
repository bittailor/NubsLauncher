package com.netstal.tools.nubs.launcher.domain.job.state;



public class Building implements IJobState {

   public static final Building INSTANCE = new Building(); 
   
   private Building() {
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

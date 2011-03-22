package com.netstal.tools.nubs.launcher.domain.job.state;



public class FinishedSucessfully implements IJobState {

   public static final FinishedSucessfully INSTANCE = new FinishedSucessfully(); 

   private FinishedSucessfully() {
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

package com.netstal.tools.nubs.launcher.domain.job.state;


public interface IJobStateVisitor {
   
   public void visit(Idle state);
   public void visit(Building state);
   public void visit(Failed state);
   public void visit(FinishedSucessfully state);
   public void visit(FinishedFaultily state);
   public void visit(FinishedExceptionally state);
   
}

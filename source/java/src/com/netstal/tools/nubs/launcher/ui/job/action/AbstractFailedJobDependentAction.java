package com.netstal.tools.nubs.launcher.ui.job.action;

import javax.swing.Icon;

import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;

public abstract class AbstractFailedJobDependentAction extends AbstractJobDependentAction {

   public AbstractFailedJobDependentAction() {
      super();
   }

   public AbstractFailedJobDependentAction(String name, Icon icon) {
      super(name, icon);
   }

   public AbstractFailedJobDependentAction(String name) {
      super(name);
   }

   @Override
   public void visit(Idle state) {
      setEnabled(false);
   }

   @Override
   public void visit(Building state) {
      setEnabled(false);
   }

   @Override
   public void visit(Failed state) {
      setEnabled(true);
   }

   @Override
   public void visit(FinishedSucessfully state) {
      setEnabled(false);
   }

   @Override
   public void visit(FinishedFaultily state) {
      setEnabled(false);
   }

   @Override
   public void visit(FinishedExceptionally state) {
      setEnabled(false);
   }

  

}

package com.netstal.tools.nubs.launcher.ui.job;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobStateVisitor;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;

public abstract class AbstractJobDependentAction extends AbstractAction implements IJobDependentAction, IJobStateVisitor {

   private IRakeJob currentJob;
   
   public AbstractJobDependentAction() {
      super();
   }
   
   public AbstractJobDependentAction(String name) {
      super(name);
   }

   public AbstractJobDependentAction(String name, Icon icon) {
      super(name, icon);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      if (currentJob != null) {
         actionPerformed(currentJob);
      }
   }

   protected abstract void actionPerformed(IRakeJob job);

   @Override
   public void newSelection(IRakeJob job) {
      currentJob = job;
      setEnabled(currentJob != null);
      if (job != null) {
         job.getState().accept(this);         
      }
   }
   
   protected IRakeJob getCurrentJob() {
      return currentJob;
   }

   @Override
   public void visit(Idle state) {
   }

   @Override
   public void visit(Building state) {
   }

   @Override
   public void visit(Failed state) {
   }

   @Override
   public void visit(FinishedSucessfully state) {
   }

   @Override
   public void visit(FinishedFaultily state) {
   }

   @Override
   public void visit(FinishedExceptionally state) {
   }

}

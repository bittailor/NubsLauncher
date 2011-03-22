package com.netstal.tools.nubs.launcher.ui;

import java.awt.Color;

import com.netstal.tools.nubs.launcher.domain.job.state.Building;
import com.netstal.tools.nubs.launcher.domain.job.state.Failed;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedExceptionally;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedFaultily;
import com.netstal.tools.nubs.launcher.domain.job.state.FinishedSucessfully;
import com.netstal.tools.nubs.launcher.domain.job.state.IJobStateVisitor;
import com.netstal.tools.nubs.launcher.domain.job.state.Idle;

public class ColorJobStateVisitor implements IJobStateVisitor {

   private static final Color IDLE                    = new Color(  0,   0,   0,   0);
   private static final Color BUILDING                = new Color(  8, 159,   8, 255);
   // private static final Color FAILED                  = new Color(252, 185,   0, 255);
   private static final Color FAILED                  = new Color(198,  7,    7, 255);
   private static final Color FINISHED_SUCESSFULLY    = new Color(  8, 159,   8,  50);
   private static final Color FINISHED_FAULTILY       = new Color(198,   7,   7,  100);
   private static final Color FINISHED_EXCEPTIONALLY  = new Color(198,   7,   7,  100);
   
   private Color color;
   
   public Color getColor() {
      return color;
   }

   @Override
   public void visit(Idle state) {
      color = IDLE;
   }

   @Override
   public void visit(Building state) {
      color = BUILDING;
   }

   @Override
   public void visit(Failed state) {
      color = FAILED ;
   }

   @Override
   public void visit(FinishedSucessfully state) {
      color = FINISHED_SUCESSFULLY ;
   }

   @Override
   public void visit(FinishedFaultily state) {
      color = FINISHED_FAULTILY ;
   }

   @Override
   public void visit(FinishedExceptionally state) {
      color = FINISHED_EXCEPTIONALLY ;
   }

}

package com.netstal.tools.nubs.launcher.domain.job.state;


public interface IJobState {
   public <V extends IJobStateVisitor> V accept(V visitor);
   public boolean isFinished();  
}

/*
public enum JobState {
   IDLE                 (false,  new Color(  0,   0,   0,   0)),
   BUILDING             (false,  new Color(  8, 159,   8, 255)),
   FAILED               (false,  new Color(252, 185,   0, 255)),
   FINISHED_SUCESSFULLY (true,   new Color(  8, 159,   8,  50)),
   FINISHED_FAILURE     (true,   new Color(198,   7,   7,   0)),
   FINISHED_EXCEPTION   (true,   new Color(198,   7,   7,   0));
   
   private final boolean finished;
   private final Color color;

   private JobState(boolean finished, Color color) {
      this.finished = finished;
      this.color = color;
   }

   public boolean isFinished() {
      return finished;
   }
   
   public Color getColor() {
      return color;
   }
   
   
}

*/
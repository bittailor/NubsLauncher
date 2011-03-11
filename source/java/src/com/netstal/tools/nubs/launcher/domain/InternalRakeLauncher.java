package com.netstal.tools.nubs.launcher.domain;

import javax.swing.SwingWorker;

import com.google.inject.Inject;
import com.google.inject.Provider;

public class InternalRakeLauncher implements IRakeLauncher {

   private Provider<IRakeJob> rakeJobProvider;
   private IRakeJobRepository repository;
   
   @Inject
   public InternalRakeLauncher(Provider<IRakeJob> rakeJobProvider, IRakeJobRepository repository) {
      this.rakeJobProvider = rakeJobProvider;
      this.repository = repository;
   }



   @Override
   public void launch(final Command command) {
      // TODO fsc dont use swingworker here !
      
      SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {

         @Override
         protected Void doInBackground() throws Exception {
            IRakeJob rakeJob = rakeJobProvider.get();
            rakeJob.command(command);
            repository.add(rakeJob);
            rakeJob.launch();
            return null;
         }

      };
      worker.execute();
      
      
      
         
      

   }

}

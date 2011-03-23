package com.netstal.tools.nubs.launcher.domain;

import javax.swing.SwingWorker;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJob;
import com.netstal.tools.nubs.launcher.domain.job.IRakeJobRepository;

public class InternalRakeLauncher implements IRakeLauncher {

   private Provider<IRakeJob> rakeJobProvider;
   private ICommandHistory commandHistory;
   private IRakeJobRepository repository;
   
   @Inject
   public InternalRakeLauncher(Provider<IRakeJob> rakeJobProvider, ICommandHistory commandHistory, IRakeJobRepository repository) {
      this.rakeJobProvider = rakeJobProvider;
      this.commandHistory = commandHistory;
      this.repository = repository;
   }



   @Override
   public void launch(final Command command) {
      commandHistory.push(command);
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

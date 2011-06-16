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
      IRakeJob rakeJob = rakeJobProvider.get();
      rakeJob.command(command);
      launch(rakeJob);
   }

   @Override
   public void relaunch(IRakeJob job) {
      IRakeJob newJob = rakeJobProvider.get();
      newJob.command(job.getCommand());
      newJob.setAutoRetry(job.isAutoRetry());
      if (job.isFinished()) {
         repository.clear(job);
      }
      launch(newJob);
   }
   
   private void launch(final IRakeJob rakeJob) {    
      SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {
         @Override
         protected Void doInBackground() throws Exception {
            
            repository.add(rakeJob);
            rakeJob.launch();
            return null;
         }
      };
      worker.execute();
   }
   
}

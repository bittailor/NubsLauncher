package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IRakeJobRepository;

public class JobPanel extends JPanel {

   private IRakeJobRepository rakeJobRepository;   
   
   @Inject
   public JobPanel(IRakeJobRepository rakeJobRepository) {
      this.rakeJobRepository = rakeJobRepository;
      createUi();
   }

   private void createUi() {
      setLayout(new BorderLayout());
      setBorder(BorderFactory.createTitledBorder("Launched Rake Jobs"));
      JList jobs = new JList(new JobListModel(rakeJobRepository));
      jobs.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      jobs.setCellRenderer(new JobRenderer());
      
      JScrollPane scrollPane = new JScrollPane(jobs);
      add(scrollPane);

      
   }
   
   
}

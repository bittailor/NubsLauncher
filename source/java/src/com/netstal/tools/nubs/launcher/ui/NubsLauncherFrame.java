//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingWorker;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.IFilterChain;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.infrastructure.CmdLauncher;

public class NubsLauncherFrame extends JFrame {
 
   private static final long serialVersionUID = 1L;

   private static final String LOAD_LAYER = "Load";
   private static final String MAIN_LAYER = "Main";
  
   private static final String PREFIX = "NUBS - ";
   
   private IWorkspace workspace;
   private IFilterChain filterChain;

   private JPanel layersPanel;
   private CardLayout layersLayout;
   private JToolBar toolBar;
   private SuggestField suggestField;
   private JLabel workspaceLabel;
   private JLabel tasksLabel;

   private RunRakeAction runRakeAction;
   private ChangeWorkspaceAction changeWorkspaceAction;


   @Inject
   public NubsLauncherFrame(IWorkspace workspace, IFilterChain filterChain) {
      super(PREFIX);
      this.workspace = workspace;
      this.filterChain = filterChain;
      createUi();
      createActions();
      pack();
   }


   public void setWorkspaceDirectory(final File workspaceDirectory) {
      changeTitle(workspaceDirectory);     
      changeLayer(LOAD_LAYER);
      SwingWorker<Void,Void> worker = new SwingWorker<Void, Void>() {

         @Override
         protected Void doInBackground() throws Exception {
            workspace.setRoot(workspaceDirectory);
            return null;
         }

         @Override
         protected void done() {
            changeLayer(MAIN_LAYER);            
            updateLables();
         }
      };
      worker.execute();
   }
   
   public void selectWorkspaceDirectory() {
      changeWorkspaceAction.actionPerformed(new ActionEvent(this,0,""));
   }
   
   private void createUi() {
      setIconImage(new ImageIcon(getClass().getResource("images/Rocket.png")).getImage());      
      setDefaultCloseOperation(EXIT_ON_CLOSE);
      
      layersLayout = new CardLayout();
      layersPanel = new JPanel(layersLayout);
      getContentPane().add(layersPanel);
      
      JPanel rootPanel = new JPanel(new BorderLayout());
      layersPanel.add(rootPanel,MAIN_LAYER);
      
      toolBar = new JToolBar();
      rootPanel.add(toolBar,BorderLayout.NORTH);
      toolBar.setFloatable(false);
      runRakeAction = new RunRakeAction();
      toolBar.add(runRakeAction);
      changeWorkspaceAction = new ChangeWorkspaceAction();
      toolBar.add(changeWorkspaceAction);
      toolBar.add(new ReloadTaksAction());
      toolBar.add(Box.createHorizontalGlue());
      tasksLabel = new JLabel("-");
      toolBar.add(tasksLabel);
      workspaceLabel = new JLabel("-");
      toolBar.add(workspaceLabel);
      
      
      suggestField = new SuggestField(new SuggestListModel(filterChain));
      rootPanel.add(suggestField);
      
      
      LoadTasksPanel loadTasksScreen = new LoadTasksPanel();
      layersPanel.add(loadTasksScreen,LOAD_LAYER);
      
     
   }
   
   private void createActions() {
        
      suggestField.addSuggestListener(new ISuggestFieldListener() {     
         @Override
         public void changed(String newText) {
            filterChain.setSuggestFilter(newText);
         }
      });
      
      suggestField.addActionListener(new ActionListener() {
         
         @Override
         public void actionPerformed(ActionEvent e) {
            runRakeAction.actionPerformed(e);
         }
      });
   }

   private void changeLayer(String layer) {
      layersLayout.show(layersPanel,layer);
   }
   
   private void updateLables() {
      tasksLabel.setText(workspace.getTasks().size() +" Tasks @ ");
      workspaceLabel.setText(workspace.getRoot() + "  ");
   }
   
   private void changeTitle(File workspaceDirectory) {
      setTitle(PREFIX + workspaceDirectory.getName());
   }
   
   private class RunRakeAction extends AbstractAction {

      private static final long serialVersionUID = 1L;

      public RunRakeAction() {
         super("Run",new ImageIcon(NubsLauncherFrame.class.getResource("images/Launch.png")));
         this.putValue(SHORT_DESCRIPTION, "Launch Rake");
      }
      
      @Override
      public void actionPerformed(ActionEvent event) {
         System.out.println("Execute " + suggestField.getText());
         
         try {
            new CmdLauncher("rake "+suggestField.getText())
            .directory(workspace.getRoot())
            .launch();
         }
         catch (IOException exception) {
            exception.printStackTrace();
         }
         
         /*
         ProcessBuilder execute = new ProcessBuilder("cmd","/C","start","cmd","/K","\"rake "+suggestField.getText()+"\"");
         execute.directory(workspace.getRoot());
         try {
            execute.start();
         }
         catch (IOException exception) {
            exception.printStackTrace();
         }
         */
      }     
   }
   
   private class ChangeWorkspaceAction extends AbstractAction {

      private static final long serialVersionUID = 1L;
      private JFileChooser fileChooser;

      public ChangeWorkspaceAction() {
         super("Change Workspace",new ImageIcon(NubsLauncherFrame.class.getResource("images/ChangeWorkspace.gif")));
         this.putValue(SHORT_DESCRIPTION, "Change Workspace");
         fileChooser = new JFileChooser();
         fileChooser.setDialogTitle("Select Workspace");
         fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         fileChooser.setSelectedFile(workspace.getRoot());
         if (fileChooser.showOpenDialog(NubsLauncherFrame.this)==JFileChooser.APPROVE_OPTION) {
            setWorkspaceDirectory(fileChooser.getSelectedFile());
         }
      }     
   }
   
   private class ReloadTaksAction extends AbstractAction {
      private static final long serialVersionUID = 1L;

      public ReloadTaksAction() {
         super("Reload Tasks",new ImageIcon(NubsLauncherFrame.class.getResource("images/Reload.gif")));
         this.putValue(SHORT_DESCRIPTION, "Reload Tasks");
      }
      
      @Override
      public void actionPerformed(ActionEvent e) {
         setWorkspaceDirectory(workspace.getRoot());
      }     
   }
   
   
   
}

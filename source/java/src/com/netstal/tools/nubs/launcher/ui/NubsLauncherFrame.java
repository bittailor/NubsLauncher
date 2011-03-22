//*****************************************************************************
//
//  N E T S T A L   M A S C H I N E N   A G      8 7 5 2   N A E F E L S
//
//*****************************************************************************

package com.netstal.tools.nubs.launcher.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import com.google.inject.Inject;
import com.netstal.tools.nubs.launcher.domain.Command;
import com.netstal.tools.nubs.launcher.domain.IEventListener;
import com.netstal.tools.nubs.launcher.domain.IRakeLauncher;
import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.infrastructure.Version;

public class NubsLauncherFrame extends JFrame {
 
   private static final long serialVersionUID = 1L;
   private static Logger LOG = Logger.getLogger(NubsLauncherFrame.class.getName());

   private static final String LOAD_LAYER = "Load";
   private static final String MAIN_LAYER = "Main";
  
   private static final String PREFIX = "NUBS - ";
   
   private IWorkspace workspace;
   private IRakeLauncher launcher;
   
   
   private JPanel layersPanel;
   private CardLayout layersLayout;
   private JToolBar toolBar;
   private RakeTasksField suggestField;
   private JLabel workspaceLabel;
   private JLabel tasksLabel;

   private RunRakeAction runRakeAction;
   private ChangeWorkspaceAction changeWorkspaceAction;
   private JComponent launcherPanel;

   
   @Inject
   public NubsLauncherFrame(IWorkspace workspace, 
            RakeTasksField rakeTasksField, 
            IRakeLauncher launcher,
            @LauncherPanel JComponent launcherPanel) {
      super(PREFIX);
      this.workspace = workspace;
      this.launcher = launcher;
      this.suggestField = rakeTasksField;
      this.launcherPanel = launcherPanel;
      
      createUi();
      
      this.workspace.addListener(new IEventListener<IWorkspace>() {
         @Override
         public void notifyEvent(IWorkspace source) {
            SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                  notifyWorkspaceChanged();
               }
            });
         }
      });
      pack();
   }

   public void changeWorkspace(final File workspaceDirectory) {
      setFrameInfo("loading ...");
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
      
      JPanel topPanel = new JPanel(new BorderLayout());
      rootPanel.add(topPanel,BorderLayout.NORTH);
      
      toolBar = new JToolBar();
      topPanel.add(toolBar,BorderLayout.NORTH);
      toolBar.setFloatable(false);
      runRakeAction = new RunRakeAction();
      toolBar.add(runRakeAction);
      changeWorkspaceAction = new ChangeWorkspaceAction();
      toolBar.add(changeWorkspaceAction);
      toolBar.add(new ReloadTaksAction());
      toolBar.add(new AboutAction(this,Version.getVersion()));
      toolBar.add(Box.createHorizontalGlue());
      tasksLabel = new JLabel("-");
      toolBar.add(tasksLabel);
      workspaceLabel = new JLabel("-");
      toolBar.add(workspaceLabel);
     
      topPanel.add(suggestField,BorderLayout.SOUTH);
      
      rootPanel.add(launcherPanel);
      
      LoadTasksPanel loadTasksScreen = new LoadTasksPanel();
      layersPanel.add(loadTasksScreen,LOAD_LAYER);  
   }
   
   private void changeLayer(String layer) {
      layersLayout.show(layersPanel,layer);
   }
   
   private void notifyWorkspaceChanged() {
      tasksLabel.setText(workspace.getTasks().size() +" Tasks @ ");
      workspaceLabel.setText(workspace.getRoot() + "  ");
      setFrameInfo(workspace.getRoot().getName());
   }
   
   private void setFrameInfo(String info) {
      setTitle(PREFIX + info);
   }
    
   private class RunRakeAction extends AbstractAction {

      private static final long serialVersionUID = 1L;

      public RunRakeAction() {
         super("Run",new ImageIcon(NubsLauncherFrame.class.getResource("images/Launch.png")));
         this.putValue(SHORT_DESCRIPTION, "Launch Rake");
      }
      
      @Override
      public void actionPerformed(ActionEvent event) {
         LOG.log(Level.INFO, "Lauch Rake with " + suggestField.getTextField().getText());
         try {
            Command command = new Command(suggestField.getTextField().getText());
            launcher.launch(command);
         }
         catch (IOException exception) {
            LOG.log(Level.SEVERE, "Problem Launching Rake", exception);
         }
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
            changeWorkspace(fileChooser.getSelectedFile());
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
         changeWorkspace(workspace.getRoot());
      }     
   }

}

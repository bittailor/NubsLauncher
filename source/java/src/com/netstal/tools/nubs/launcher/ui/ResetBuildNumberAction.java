package com.netstal.tools.nubs.launcher.ui;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;

import com.netstal.tools.nubs.launcher.domain.IWorkspace;
import com.netstal.tools.nubs.launcher.infrastructure.StreamUtility;
import com.netstal.tools.nubs.launcher.infrastructure.StringUtility;

public class ResetBuildNumberAction extends AbstractAction {

   private static Logger LOG = Logger.getLogger(ResetBuildNumberAction.class.getName());
   
   private IWorkspace workspace;

   private static final String LAST_DEPLOYED_VERSION_PATH = StringUtility.join(File.separator, "bin","Version","VersionInfo.txt"); 
   private static final String BUILD_NUMBER_PATH = "buildnumber.txt"; 
   
   public ResetBuildNumberAction(IWorkspace workspace) {
      super("Reset Build Number",new ImageIcon(NubsLauncherFrame.class.getResource("images/BuildNumber.gif")));
      this.putValue(SHORT_DESCRIPTION, "Reset Build Number For Redeploy");
      this.workspace = workspace;
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      PrintWriter out = null;
      try {
         
         File lastDeployedVersionFile = new File(workspace.getRoot(),LAST_DEPLOYED_VERSION_PATH);
         if (!lastDeployedVersionFile.exists()) {
            LOG.log(Level.WARNING, "Last deployed version file " + lastDeployedVersionFile.getAbsolutePath() + " does not exist");
            return;
         }
         
         List<String> lines = StreamUtility.readLines(lastDeployedVersionFile);
         if (lines.isEmpty()) {
            LOG.log(Level.WARNING, "Last deployed version file " + lastDeployedVersionFile.getAbsolutePath() + "is empty");
            return;
         }
         String lastDeployedVersion = lines.get(0).trim();
         char buildnumber = lastDeployedVersion.charAt(lastDeployedVersion.length()-1);
         File buildNumberFile = new File(workspace.getRoot(), BUILD_NUMBER_PATH);
         out = new PrintWriter(buildNumberFile);
         out.println(buildnumber);
         LOG.log(Level.SEVERE, "Reset build number to " + buildnumber);
      }
      catch (IOException exception) {
         LOG.log(Level.INFO, "Problem reseting build number",exception);
      }
      finally {
         StreamUtility.close(out);
      }
   }
}

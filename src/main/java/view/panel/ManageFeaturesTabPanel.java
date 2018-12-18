package view.panel;

import java.awt.GridLayout;
import javax.swing.JPanel;

public class ManageFeaturesTabPanel extends JPanel {

  // private AddFeaturePanel addFeaturePanel = new AddFeaturePanel();
  private AddFeatureWithAgentImagePanel addFeatureWithAgentImagePanel =
      new AddFeatureWithAgentImagePanel();
  private ManageFeaturesPanel manageFeaturesPanel = new ManageFeaturesPanel();

  public ManageFeaturesTabPanel() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(addFeatureWithAgentImagePanel);
    add(manageFeaturesPanel);
  }

  /**
   * @return the addFeatureWithAgentImagePanel
   */
  public AddFeatureWithAgentImagePanel getAddFeatureWithAgentImagePanel() {
    return addFeatureWithAgentImagePanel;
  }

  /**
   * @param addFeatureWithAgentImagePanel the addFeatureWithAgentImagePanel to set
   */
  public void setAddFeatureWithAgentImagePanel(
      AddFeatureWithAgentImagePanel addFeatureWithAgentImagePanel) {
    this.addFeatureWithAgentImagePanel = addFeatureWithAgentImagePanel;
  }


  // /**
  // * @return the addFeaturePanel
  // */
  // public AddFeaturePanel getAddFeaturePanel() {
  // return addFeaturePanel;
  // }
  //
  // /**
  // * @param addFeaturePanel the addFeaturePanel to set
  // */
  // public void setAddFeaturePanel(AddFeaturePanel addFeaturePanel) {
  // this.addFeaturePanel = addFeaturePanel;
  // }

  /**
   * @return the manageFeaturesPanel
   */
  public ManageFeaturesPanel getManageFeaturesPanel() {
    return manageFeaturesPanel;
  }

  /**
   * @param manageFeaturesPanel the manageFeaturesPanel to set
   */
  public void setManageFeaturesPanel(ManageFeaturesPanel manageFeaturesPanel) {
    this.manageFeaturesPanel = manageFeaturesPanel;
  }

}

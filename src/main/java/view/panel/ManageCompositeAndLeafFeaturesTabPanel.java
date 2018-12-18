package view.panel;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ManageCompositeAndLeafFeaturesTabPanel extends JPanel {

  private AddCompositeFeaturePanel addCompositeFeaturePanel = new AddCompositeFeaturePanel();
  private AddLeafFeaturePanel addLeafFeaturePanel = new AddLeafFeaturePanel();
  private AgentInformationPanel agentImagePanel = new AgentInformationPanel();
  private ManageFeaturesPanel manageFeaturesPanel = new ManageFeaturesPanel();

  public ManageCompositeAndLeafFeaturesTabPanel() {
    setLayout(new GridLayout(2, 2, 0, 0));
    add(addCompositeFeaturePanel);
    add(addLeafFeaturePanel);
    add(agentImagePanel);
    add(manageFeaturesPanel);

    addCompositeFeaturePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    addLeafFeaturePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    agentImagePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    manageFeaturesPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
  }

  /**
   * @return the addCompositeFeaturePanel
   */
  public AddCompositeFeaturePanel getAddCompositeFeaturePanel() {
    return addCompositeFeaturePanel;
  }

  /**
   * @param addCompositeFeaturePanel the addCompositeFeaturePanel to set
   */
  public void setAddCompositeFeaturePanel(AddCompositeFeaturePanel addCompositeFeaturePanel) {
    this.addCompositeFeaturePanel = addCompositeFeaturePanel;
  }

  /**
   * @return the addLeafFeaturePanel
   */
  public AddLeafFeaturePanel getAddLeafFeaturePanel() {
    return addLeafFeaturePanel;
  }

  /**
   * @param addLeafFeaturePanel the addLeafFeaturePanel to set
   */
  public void setAddLeafFeaturePanel(AddLeafFeaturePanel addLeafFeaturePanel) {
    this.addLeafFeaturePanel = addLeafFeaturePanel;
  }

  /**
   * @return the agentImagePanel
   */
  public AgentInformationPanel getAgentImagePanel() {
    return agentImagePanel;
  }

  /**
   * @param agentImagePanel the agentImagePanel to set
   */
  public void setAgentImagePanel(AgentInformationPanel agentImagePanel) {
    this.agentImagePanel = agentImagePanel;
  }

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

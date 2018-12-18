package view.panel;

import java.awt.GridLayout;
import javax.swing.JPanel;

public class AddFeatureWithAgentImagePanel extends JPanel {
  private AddFeaturePanel addFeaturePanel = new AddFeaturePanel();
  private AgentInformationPanel agentImage = new AgentInformationPanel();

  public AddFeatureWithAgentImagePanel() {
    setLayout(new GridLayout(2, 0, 0, 0));
    add(addFeaturePanel);
    add(agentImage);
  }

  /**
   * @return the addFeaturePanel
   */
  public AddFeaturePanel getAddFeaturePanel() {
    return addFeaturePanel;
  }

  /**
   * @param addFeaturePanel the addFeaturePanel to set
   */
  public void setAddFeaturePanel(AddFeaturePanel addFeaturePanel) {
    this.addFeaturePanel = addFeaturePanel;
  }

  /**
   * @return the agentImage
   */
  public AgentInformationPanel getAgentImage() {
    return agentImage;
  }

  /**
   * @param agentImage the agentImage to set
   */
  public void setAgentImage(AgentInformationPanel agentImage) {
    this.agentImage = agentImage;
  }

}

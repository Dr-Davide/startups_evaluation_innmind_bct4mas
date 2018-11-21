package view.panel;

import java.awt.GridLayout;
import javax.swing.JPanel;

public class AddServiceWithAgentImagePanel extends JPanel {
  private AddServicePanel addServicePanel = new AddServicePanel();
  private AgentInformationPanel agentImage = new AgentInformationPanel();

  public AddServiceWithAgentImagePanel() {
    setLayout(new GridLayout(2, 0, 0, 0));
    add(addServicePanel);
    add(agentImage);
  }

  /**
   * @return the addServicePanel
   */
  public AddServicePanel getAddServicePanel() {
    return addServicePanel;
  }

  /**
   * @param addServicePanel the addServicePanel to set
   */
  public void setAddServicePanel(AddServicePanel addServicePanel) {
    this.addServicePanel = addServicePanel;
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

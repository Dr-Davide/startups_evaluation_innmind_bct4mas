package view.panel;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ManageCompositeAndLeafServicesTabPanel extends JPanel {

  private AddCompositeServicePanel addCompositeServicePanel = new AddCompositeServicePanel();
  private AddLeafServicePanel addLeafServicePanel = new AddLeafServicePanel();
  private AgentInformationPanel agentImagePanel = new AgentInformationPanel();
  private ManageServicesPanel manageServicesPanel = new ManageServicesPanel();

  public ManageCompositeAndLeafServicesTabPanel() {
    setLayout(new GridLayout(2, 2, 0, 0));
    add(addCompositeServicePanel);
    add(addLeafServicePanel);
    add(agentImagePanel);
    add(manageServicesPanel);

    addCompositeServicePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    addLeafServicePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    agentImagePanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    manageServicesPanel.setBorder(BorderFactory.createCompoundBorder(
        BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
  }

  /**
   * @return the addCompositeServicePanel
   */
  public AddCompositeServicePanel getAddCompositeServicePanel() {
    return addCompositeServicePanel;
  }

  /**
   * @param addCompositeServicePanel the addCompositeServicePanel to set
   */
  public void setAddCompositeServicePanel(AddCompositeServicePanel addCompositeServicePanel) {
    this.addCompositeServicePanel = addCompositeServicePanel;
  }

  /**
   * @return the addLeafServicePanel
   */
  public AddLeafServicePanel getAddLeafServicePanel() {
    return addLeafServicePanel;
  }

  /**
   * @param addLeafServicePanel the addLeafServicePanel to set
   */
  public void setAddLeafServicePanel(AddLeafServicePanel addLeafServicePanel) {
    this.addLeafServicePanel = addLeafServicePanel;
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
   * @return the manageServicesPanel
   */
  public ManageServicesPanel getManageServicesPanel() {
    return manageServicesPanel;
  }

  /**
   * @param manageServicesPanel the manageServicesPanel to set
   */
  public void setManageServicesPanel(ManageServicesPanel manageServicesPanel) {
    this.manageServicesPanel = manageServicesPanel;
  }
}

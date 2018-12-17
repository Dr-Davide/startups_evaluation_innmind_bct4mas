package view.panel;

import java.awt.GridLayout;
import javax.swing.JPanel;

public class ManageServicesTabPanel extends JPanel {

  // private AddServicePanel addServicePanel = new AddServicePanel();
  private AddServiceWithAgentImagePanel addServiceWithAgentImagePanel =
      new AddServiceWithAgentImagePanel();
  private ManageServicesPanel manageServicesPanel = new ManageServicesPanel();

  public ManageServicesTabPanel() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(addServiceWithAgentImagePanel);
    add(manageServicesPanel);
  }

  /**
   * @return the addServiceWithAgentImagePanel
   */
  public AddServiceWithAgentImagePanel getAddServiceWithAgentImagePanel() {
    return addServiceWithAgentImagePanel;
  }

  /**
   * @param addServiceWithAgentImagePanel the addServiceWithAgentImagePanel to set
   */
  public void setAddServiceWithAgentImagePanel(
      AddServiceWithAgentImagePanel addServiceWithAgentImagePanel) {
    this.addServiceWithAgentImagePanel = addServiceWithAgentImagePanel;
  }


  // /**
  // * @return the addServicePanel
  // */
  // public AddServicePanel getAddServicePanel() {
  // return addServicePanel;
  // }
  //
  // /**
  // * @param addServicePanel the addServicePanel to set
  // */
  // public void setAddServicePanel(AddServicePanel addServicePanel) {
  // this.addServicePanel = addServicePanel;
  // }

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

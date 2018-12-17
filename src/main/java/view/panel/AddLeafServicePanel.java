package view.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddLeafServicePanel extends JPanel {


  private JButton buttonAddService;
  private CouplePanel panelServiceName = new CouplePanel("Name:  ");
  private CouplePanel panelServiceDescription = new CouplePanel("Description:  ");
  private CouplePanel panelServiceCost = new CouplePanel("Cost:  ");
  private CouplePanel panelServiceTime = new CouplePanel("Time:  ");
  private final JLabel lblAddService = new JLabel("Add Leaf Service");

  /**
   * Create the panel.
   */
  public AddLeafServicePanel() {
    // setBorder(new EmptyBorder(10, 10, 10, 10));

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {23, 36, 0, 26, 26, 26, 26, 40, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    GridBagConstraints gbc_lblAddService = new GridBagConstraints();
    gbc_lblAddService.anchor = GridBagConstraints.NORTH;
    gbc_lblAddService.gridwidth = 3;
    gbc_lblAddService.insets = new Insets(0, 0, 5, 5);
    gbc_lblAddService.gridx = 1;
    gbc_lblAddService.gridy = 1;
    lblAddService.setFont(new Font("Dialog", Font.BOLD, 16));
    // lblAddService.setFont(new Font("Ubuntu Light", Font.BOLD, 16));
    add(lblAddService, gbc_lblAddService);
    GridBagConstraints gbc_panelServiceName = new GridBagConstraints();
    gbc_panelServiceName.gridwidth = 3;
    gbc_panelServiceName.fill = GridBagConstraints.BOTH;
    gbc_panelServiceName.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceName.gridx = 1;
    gbc_panelServiceName.gridy = 3;
    panelServiceName.setLbl("Name: ");
    add(panelServiceName, gbc_panelServiceName);
    GridBagConstraints gbc_panelServiceDescription = new GridBagConstraints();
    gbc_panelServiceDescription.gridwidth = 3;
    gbc_panelServiceDescription.fill = GridBagConstraints.BOTH;
    gbc_panelServiceDescription.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceDescription.gridx = 1;
    gbc_panelServiceDescription.gridy = 4;
    add(panelServiceDescription, gbc_panelServiceDescription);
    GridBagConstraints gbc_panelServiceCost = new GridBagConstraints();
    gbc_panelServiceCost.gridwidth = 3;
    gbc_panelServiceCost.fill = GridBagConstraints.BOTH;
    gbc_panelServiceCost.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceCost.gridx = 1;
    gbc_panelServiceCost.gridy = 5;
    add(panelServiceCost, gbc_panelServiceCost);
    GridBagConstraints gbc_panelServiceTime = new GridBagConstraints();
    gbc_panelServiceTime.gridwidth = 3;
    gbc_panelServiceTime.fill = GridBagConstraints.BOTH;
    gbc_panelServiceTime.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceTime.gridx = 1;
    gbc_panelServiceTime.gridy = 6;
    add(panelServiceTime, gbc_panelServiceTime);

    buttonAddService = new JButton("Add Leaf Service");
    GridBagConstraints gbc_buttonAddService = new GridBagConstraints();
    gbc_buttonAddService.insets = new Insets(0, 0, 5, 5);
    gbc_buttonAddService.fill = GridBagConstraints.VERTICAL;
    gbc_buttonAddService.gridx = 2;
    gbc_buttonAddService.gridy = 8;
    add(buttonAddService, gbc_buttonAddService);
  }

  /**
   * @return the buttonAddService
   */
  public JButton getButtonAddService() {
    return buttonAddService;
  }

  /**
   * @param buttonAddService the btnAddService to set
   */
  public void setButtonAddService(JButton btnAddService) {
    this.buttonAddService = btnAddService;
  }

  /**
   * @return the panelServiceName
   */
  public CouplePanel getPanelServiceName() {
    return panelServiceName;
  }

  /**
   * @param panelServiceName the panelServiceName to set
   */
  public void setPanelServiceName(CouplePanel panelServiceName) {
    this.panelServiceName = panelServiceName;
  }

  /**
   * @return the panelServiceDescription
   */
  public CouplePanel getPanelServiceDescription() {
    return panelServiceDescription;
  }

  /**
   * @param panelServiceDescription the panelServiceDescription to set
   */
  public void setPanelServiceDescription(CouplePanel panelServiceDescription) {
    this.panelServiceDescription = panelServiceDescription;
  }

  /**
   * @return the panelServiceCost
   */
  public CouplePanel getPanelServiceCost() {
    return panelServiceCost;
  }

  /**
   * @param panelServiceCost the panelServiceCost to set
   */
  public void setPanelServiceCost(CouplePanel panelServiceCost) {
    this.panelServiceCost = panelServiceCost;
  }

  /**
   * @return the panelServiceTime
   */
  public CouplePanel getPanelServiceTime() {
    return panelServiceTime;
  }

  /**
   * @param panelServiceTime the panelServiceTime to set
   */
  public void setPanelServiceTime(CouplePanel panelServiceTime) {
    this.panelServiceTime = panelServiceTime;
  }


}

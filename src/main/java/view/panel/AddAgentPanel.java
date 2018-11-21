package view.panel;

import javax.swing.*;
import java.awt.*;

public class AddAgentPanel extends JPanel {

    private static final long serialVersionUID = 8513389855919940996L;
    private CouplePanel panelAgentName = new CouplePanel("Agent Name");
  private CouplePanel panelAgentType = new CouplePanel("Agent Type");
  // TODO: Per ora andare a prendere valori da configJade.json
  private String[] agentNames = {"main", "a1", "a2", "a3"};
    private String[] agentTypes = {"BCAgent"};
  private JComboBox comboBoxAgentNames;
  private JComboBox comboBoxAgentTypes;

  private CouplePanel panelExpirationCertificate = new CouplePanel("Expiration eCert: ");
  private JButton buttonAddAgent;
  private JLabel lblName;
  private JLabel lblType;

  /**
   * Create the panel.
   */
  public AddAgentPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {24, 51, 24, 53, 19, 31, 0};
    gridBagLayout.columnWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);


    // INSERT NAME BY LIST

    // lblName = new JLabel("Name:");
    // GridBagConstraints gbc_lblName = new GridBagConstraints();
    // gbc_lblName.anchor = GridBagConstraints.EAST;
    // gbc_lblName.insets = new Insets(0, 0, 5, 5);
    // gbc_lblName.gridx = 1;
    // gbc_lblName.gridy = 1;
    // add(lblName, gbc_lblName);

    // comboBoxAgentNames = new JComboBox(agentNames);
    // GridBagConstraints gbc_comboBoxAgentNames = new GridBagConstraints();
    // gbc_comboBoxAgentNames.gridwidth = 4;
    // gbc_comboBoxAgentNames.insets = new Insets(0, 0, 5, 0);
    // gbc_comboBoxAgentNames.gridx = 4;
    // gbc_comboBoxAgentNames.gridy = 1;
    // add(comboBoxAgentNames, gbc_comboBoxAgentNames);

    GridBagConstraints gbc_panelAgentName = new GridBagConstraints();
    gbc_panelAgentName.gridwidth = 6;
    gbc_panelAgentName.fill = GridBagConstraints.BOTH;
    gbc_panelAgentName.insets = new Insets(0, 0, 5, 5);
    gbc_panelAgentName.gridx = 1;
    gbc_panelAgentName.gridy = 1;
    panelAgentName.setLbl("Name: ");
    add(panelAgentName, gbc_panelAgentName);

    lblType = new JLabel("Type:");
    GridBagConstraints gbc_lblType = new GridBagConstraints();
    gbc_lblType.anchor = GridBagConstraints.WEST;
    gbc_lblType.insets = new Insets(0, 0, 5, 5);
    gbc_lblType.gridx = 1;
    gbc_lblType.gridy = 2;
    add(lblType, gbc_lblType);
    comboBoxAgentTypes = new JComboBox(agentTypes);
    GridBagConstraints gbc_comboBoxAgentTypes = new GridBagConstraints();
    gbc_comboBoxAgentTypes.gridwidth = 3;
    gbc_comboBoxAgentTypes.insets = new Insets(0, 0, 5, 0);
    gbc_comboBoxAgentTypes.gridx = 4;
    gbc_comboBoxAgentTypes.gridy = 2;
    add(comboBoxAgentTypes, gbc_comboBoxAgentTypes);
    GridBagConstraints gbc_panelExpirationCertificate = new GridBagConstraints();
    gbc_panelExpirationCertificate.anchor = GridBagConstraints.WEST;
    gbc_panelExpirationCertificate.insets = new Insets(0, 0, 5, 0);
    gbc_panelExpirationCertificate.gridwidth = 7;
    gbc_panelExpirationCertificate.gridx = 1;
    gbc_panelExpirationCertificate.gridy = 3;
    add(panelExpirationCertificate, gbc_panelExpirationCertificate);

    buttonAddAgent = new JButton("Add Agent");
    GridBagConstraints gbc_buttonAddAgent = new GridBagConstraints();
    gbc_buttonAddAgent.gridwidth = 5;
    gbc_buttonAddAgent.insets = new Insets(0, 0, 5, 5);
    gbc_buttonAddAgent.fill = GridBagConstraints.BOTH;
    gbc_buttonAddAgent.gridx = 2;
    gbc_buttonAddAgent.gridy = 4;
    add(buttonAddAgent, gbc_buttonAddAgent);

  }

  /**
   * @return the panelAgentName
   */
  public CouplePanel getPanelAgentName() {
    return panelAgentName;
  }

  /**
   * @param panelAgentName the panelAgentName to set
   */
  public void setPanelAgentName(CouplePanel panelAgentName) {
    this.panelAgentName = panelAgentName;
  }

  /**
   * @return the panelAgentType
   */
  public CouplePanel getPanelAgentType() {
    return panelAgentType;
  }

  /**
   * @param panelAgentType the panelAgentType to set
   */
  public void setPanelAgentType(CouplePanel panelAgentType) {
    this.panelAgentType = panelAgentType;
  }

  /**
   * @return the agentNames
   */
  public String[] getAgentNames() {
    return agentNames;
  }

  /**
   * @param agentNames the agentNames to set
   */
  public void setAgentNames(String[] agentNames) {
    this.agentNames = agentNames;
  }

  /**
   * @return the agentTypes
   */
  public String[] getAgentTypes() {
    return agentTypes;
  }

  /**
   * @param agentTypes the agentTypes to set
   */
  public void setAgentTypes(String[] agentTypes) {
    this.agentTypes = agentTypes;
  }

  /**
   * @return the comboBoxAgentNames
   */
  public JComboBox getComboBoxAgentNames() {
    return comboBoxAgentNames;
  }

  /**
   * @param comboBoxAgentNames the comboBoxAgentNames to set
   */
  public void setComboBoxAgentNames(JComboBox comboBoxAgentNames) {
    this.comboBoxAgentNames = comboBoxAgentNames;
  }

  /**
   * @return the comboBoxAgentTypes
   */
  public JComboBox getComboBoxAgentTypes() {
    return comboBoxAgentTypes;
  }

  /**
   * @param comboBoxAgentTypes the comboBoxAgentTypes to set
   */
  public void setComboBoxAgentTypes(JComboBox comboBoxAgentTypes) {
    this.comboBoxAgentTypes = comboBoxAgentTypes;
  }

  /**
   * @return the panelExpirationCertificate
   */
  public CouplePanel getPanelExpirationCertificate() {
    return panelExpirationCertificate;
  }

  /**
   * @param panelExpirationCertificate the panelExpirationCertificate to set
   */
  public void setPanelExpirationCertificate(CouplePanel panelExpirationCertificate) {
    this.panelExpirationCertificate = panelExpirationCertificate;
  }

  /**
   * @return the buttonAddAgent
   */
  public JButton getButtonAddAgent() {
    return buttonAddAgent;
  }

  /**
   * @param buttonAddAgent the buttonAddAgent to set
   */
  public void setButtonAddAgent(JButton buttonAddAgent) {
    this.buttonAddAgent = buttonAddAgent;
  }

}

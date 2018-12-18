package view.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class AddFeaturePanel extends JPanel {

  private JButton buttonAddFeature;
  private CouplePanel panelFeatureName = new CouplePanel("Name:  ");
  private CouplePanel panelFeatureDescription = new CouplePanel("Description:  ");
  private CouplePanel panelFeatureCost = new CouplePanel("Cost:  ");
  private CouplePanel panelFeatureTime = new CouplePanel("Time:  ");
  private final JLabel lblAddFeature = new JLabel("Add Feature");

  /**
   * Create the panel.
   */
  public AddFeaturePanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {23, 36, 0, 26, 26, 26, 26, 40, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    GridBagConstraints gbc_lblAddFeature = new GridBagConstraints();
    gbc_lblAddFeature.anchor = GridBagConstraints.NORTH;
    gbc_lblAddFeature.gridwidth = 3;
    gbc_lblAddFeature.insets = new Insets(0, 0, 5, 5);
    gbc_lblAddFeature.gridx = 1;
    gbc_lblAddFeature.gridy = 1;
    lblAddFeature.setFont(new Font("Dialog", Font.BOLD, 16));
    // lblAddFeature.setFont(new Font("Ubuntu Light", Font.BOLD, 16));
    add(lblAddFeature, gbc_lblAddFeature);
    GridBagConstraints gbc_panelFeatureName = new GridBagConstraints();
    gbc_panelFeatureName.gridwidth = 3;
    gbc_panelFeatureName.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureName.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureName.gridx = 1;
    gbc_panelFeatureName.gridy = 3;
    panelFeatureName.setLbl("Name: ");
    add(panelFeatureName, gbc_panelFeatureName);
    GridBagConstraints gbc_panelFeatureDescription = new GridBagConstraints();
    gbc_panelFeatureDescription.gridwidth = 3;
    gbc_panelFeatureDescription.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureDescription.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureDescription.gridx = 1;
    gbc_panelFeatureDescription.gridy = 4;
    add(panelFeatureDescription, gbc_panelFeatureDescription);
    GridBagConstraints gbc_panelFeatureCost = new GridBagConstraints();
    gbc_panelFeatureCost.gridwidth = 3;
    gbc_panelFeatureCost.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureCost.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureCost.gridx = 1;
    gbc_panelFeatureCost.gridy = 5;
    add(panelFeatureCost, gbc_panelFeatureCost);
    GridBagConstraints gbc_panelFeatureTime = new GridBagConstraints();
    gbc_panelFeatureTime.gridwidth = 3;
    gbc_panelFeatureTime.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureTime.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureTime.gridx = 1;
    gbc_panelFeatureTime.gridy = 6;
    add(panelFeatureTime, gbc_panelFeatureTime);

    buttonAddFeature = new JButton("Add Feature");
    GridBagConstraints gbc_buttonAddFeature = new GridBagConstraints();
    gbc_buttonAddFeature.insets = new Insets(0, 0, 5, 5);
    gbc_buttonAddFeature.fill = GridBagConstraints.VERTICAL;
    gbc_buttonAddFeature.gridx = 2;
    gbc_buttonAddFeature.gridy = 8;
    add(buttonAddFeature, gbc_buttonAddFeature);
  }

  /**
   * @return the buttonAddFeature
   */
  public JButton getButtonAddFeature() {
    return buttonAddFeature;
  }

  /**
   * @param buttonAddFeature the btnAddFeature to set
   */
  public void setButtonAddFeature(JButton btnAddFeature) {
    this.buttonAddFeature = btnAddFeature;
  }

  /**
   * @return the panelFeatureName
   */
  public CouplePanel getPanelFeatureName() {
    return panelFeatureName;
  }

  /**
   * @param panelFeatureName the panelFeatureName to set
   */
  public void setPanelFeatureName(CouplePanel panelFeatureName) {
    this.panelFeatureName = panelFeatureName;
  }

  /**
   * @return the panelFeatureDescription
   */
  public CouplePanel getPanelFeatureDescription() {
    return panelFeatureDescription;
  }

  /**
   * @param panelFeatureDescription the panelFeatureDescription to set
   */
  public void setPanelFeatureDescription(CouplePanel panelFeatureDescription) {
    this.panelFeatureDescription = panelFeatureDescription;
  }

  /**
   * @return the panelFeatureCost
   */
  public CouplePanel getPanelFeatureCost() {
    return panelFeatureCost;
  }

  /**
   * @param panelFeatureCost the panelFeatureCost to set
   */
  public void setPanelFeatureCost(CouplePanel panelFeatureCost) {
    this.panelFeatureCost = panelFeatureCost;
  }

  /**
   * @return the panelFeatureTime
   */
  public CouplePanel getPanelFeatureTime() {
    return panelFeatureTime;
  }

  /**
   * @param panelFeatureTime the panelFeatureTime to set
   */
  public void setPanelFeatureTime(CouplePanel panelFeatureTime) {
    this.panelFeatureTime = panelFeatureTime;
  }

}

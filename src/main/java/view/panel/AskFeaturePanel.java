package view.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

public class AskFeaturePanel extends JPanel {

  private static final long serialVersionUID = -2188053278698149460L;
  private CouplePanel panelFeatureName = new CouplePanel("Feature Name:  ");
  private JLabel selectHeuristicLabel = new JLabel("Order By: ");
  private HeuristicRadioPanel selectHeuristicPanel = new HeuristicRadioPanel();
  private JButton buttonGetFeature;
  private final JLabel lblSearchFeature = new JLabel("Search Feature");

  /**
   * Create the panel.
   */
  AskFeaturePanel() {
    Border loweredbevel = BorderFactory.createLoweredBevelBorder();
    selectHeuristicPanel.setBorder(loweredbevel);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {50, 24, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    GridBagConstraints gbc_lblSearchFeature = new GridBagConstraints();
    gbc_lblSearchFeature.anchor = GridBagConstraints.NORTH;
    gbc_lblSearchFeature.gridwidth = 3;
    gbc_lblSearchFeature.insets = new Insets(0, 0, 5, 5);
    gbc_lblSearchFeature.gridx = 1;
    gbc_lblSearchFeature.gridy = 0;
    lblSearchFeature.setFont(new Font("Dialog", Font.BOLD, 16));
    add(lblSearchFeature, gbc_lblSearchFeature);

    GridBagConstraints gbc_panelFeatureName = new GridBagConstraints();
    gbc_panelFeatureName.gridwidth = 3;
    gbc_panelFeatureName.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureName.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureName.gridx = 1;
    gbc_panelFeatureName.gridy = 1;
    add(panelFeatureName, gbc_panelFeatureName);
    GridBagConstraints gbc_selectHeuristicLabel = new GridBagConstraints();
    gbc_selectHeuristicLabel.anchor = GridBagConstraints.WEST;
    gbc_selectHeuristicLabel.gridwidth = 3;
    gbc_selectHeuristicLabel.fill = GridBagConstraints.VERTICAL;
    gbc_selectHeuristicLabel.insets = new Insets(0, 0, 5, 5);
    gbc_selectHeuristicLabel.gridx = 1;
    gbc_selectHeuristicLabel.gridy = 2;
    add(selectHeuristicLabel, gbc_selectHeuristicLabel);

    GridBagConstraints gbc_selectHeuristicPanel = new GridBagConstraints();
    gbc_selectHeuristicPanel.gridwidth = 3;
    gbc_selectHeuristicPanel.fill = GridBagConstraints.BOTH;
    gbc_selectHeuristicPanel.insets = new Insets(0, 0, 5, 5);
    gbc_selectHeuristicPanel.gridx = 1;
    gbc_selectHeuristicPanel.gridy = 3;
    add(selectHeuristicPanel, gbc_selectHeuristicPanel);

    buttonGetFeature = new JButton("Search");
    GridBagConstraints gbc_buttonAskFeature = new GridBagConstraints();
    gbc_buttonAskFeature.anchor = GridBagConstraints.SOUTH;
    gbc_buttonAskFeature.insets = new Insets(0, 0, 0, 5);
    gbc_buttonAskFeature.gridx = 2;
    gbc_buttonAskFeature.gridy = 4;
    add(buttonGetFeature, gbc_buttonAskFeature);
  }

  /**
   * @return the butttnAskFeature
   */
  public JButton getButtonGetFeature() {
    return buttonGetFeature;
  }

  /**
   * @param buttonAskFeature the butttnAskFeature to set
   */
  public void setButtonGetFeature(JButton buttonAskFeature) {
    buttonGetFeature = buttonAskFeature;
  }

  /**
   * @return the selectHeuristicPanel
   */
  public HeuristicRadioPanel getSelectHeuristicPanel() {
    return selectHeuristicPanel;
  }

  /**
   * @param selectHeuristicPanel the selectHeuristicPanel to set
   */
  public void setSelectHeuristicPanel(HeuristicRadioPanel selectHeuristicPanel) {
    this.selectHeuristicPanel = selectHeuristicPanel;
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

}

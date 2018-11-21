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

public class AskServicePanel extends JPanel {

  private static final long serialVersionUID = -2188053278698149460L;
  private CouplePanel panelServiceName = new CouplePanel("Service Name:  ");
  private JLabel selectHeuristicLabel = new JLabel("Order By: ");
  private HeuristicRadioPanel selectHeuristicPanel = new HeuristicRadioPanel();
  private JButton buttonGetService;
  private final JLabel lblSearchService = new JLabel("Search Service");

  /**
   * Create the panel.
   */
  AskServicePanel() {
    Border loweredbevel = BorderFactory.createLoweredBevelBorder();
    selectHeuristicPanel.setBorder(loweredbevel);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {50, 24, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    GridBagConstraints gbc_lblSearchService = new GridBagConstraints();
    gbc_lblSearchService.anchor = GridBagConstraints.NORTH;
    gbc_lblSearchService.gridwidth = 3;
    gbc_lblSearchService.insets = new Insets(0, 0, 5, 5);
    gbc_lblSearchService.gridx = 1;
    gbc_lblSearchService.gridy = 0;
    lblSearchService.setFont(new Font("Dialog", Font.BOLD, 16));
    add(lblSearchService, gbc_lblSearchService);

    GridBagConstraints gbc_panelServiceName = new GridBagConstraints();
    gbc_panelServiceName.gridwidth = 3;
    gbc_panelServiceName.fill = GridBagConstraints.BOTH;
    gbc_panelServiceName.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceName.gridx = 1;
    gbc_panelServiceName.gridy = 1;
    add(panelServiceName, gbc_panelServiceName);
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

    buttonGetService = new JButton("Search");
    GridBagConstraints gbc_buttonAskService = new GridBagConstraints();
    gbc_buttonAskService.anchor = GridBagConstraints.SOUTH;
    gbc_buttonAskService.insets = new Insets(0, 0, 0, 5);
    gbc_buttonAskService.gridx = 2;
    gbc_buttonAskService.gridy = 4;
    add(buttonGetService, gbc_buttonAskService);
  }

  /**
   * @return the butttnAskService
   */
  public JButton getButtonGetService() {
    return buttonGetService;
  }

  /**
   * @param buttonAskService the butttnAskService to set
   */
  public void setButtonGetService(JButton buttonAskService) {
    buttonGetService = buttonAskService;
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

}

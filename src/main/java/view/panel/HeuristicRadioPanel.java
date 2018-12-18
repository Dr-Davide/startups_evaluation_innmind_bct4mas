package view.panel;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class HeuristicRadioPanel extends JPanel {

  private ButtonGroup buttonGroup = new ButtonGroup();
  private JRadioButton costHeuristicRadioButton;
  private JRadioButton timeHeuristicRadioButton;
  private JRadioButton reputationHeuristicRadioButton;

  /**
   * Create the panel.
   */
  public HeuristicRadioPanel() {
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {100, 0};
    gridBagLayout.rowHeights = new int[] {50, 50, 50, 0};
    gridBagLayout.columnWeights = new double[] {1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    costHeuristicRadioButton = new JRadioButton("Cost");
    GridBagConstraints gbc_costHeuristicRadioButton = new GridBagConstraints();
    gbc_costHeuristicRadioButton.fill = GridBagConstraints.VERTICAL;
    gbc_costHeuristicRadioButton.insets = new Insets(0, 0, 5, 0);
    gbc_costHeuristicRadioButton.gridx = 0;
    gbc_costHeuristicRadioButton.gridy = 0;
    add(costHeuristicRadioButton, gbc_costHeuristicRadioButton);

    buttonGroup.add(costHeuristicRadioButton);

    timeHeuristicRadioButton = new JRadioButton("Time");
    GridBagConstraints gbc_timeHeuristicRadioButton = new GridBagConstraints();
    gbc_timeHeuristicRadioButton.fill = GridBagConstraints.VERTICAL;
    gbc_timeHeuristicRadioButton.insets = new Insets(0, 0, 5, 0);
    gbc_timeHeuristicRadioButton.gridx = 0;
    gbc_timeHeuristicRadioButton.gridy = 1;
    add(timeHeuristicRadioButton, gbc_timeHeuristicRadioButton);
    buttonGroup.add(timeHeuristicRadioButton);

    reputationHeuristicRadioButton = new JRadioButton("InnMindReputation");
    GridBagConstraints gbc_reputationHeuristicRadioButton = new GridBagConstraints();
    gbc_reputationHeuristicRadioButton.fill = GridBagConstraints.VERTICAL;
    gbc_reputationHeuristicRadioButton.gridx = 0;
    gbc_reputationHeuristicRadioButton.gridy = 2;
    add(reputationHeuristicRadioButton, gbc_reputationHeuristicRadioButton);
    buttonGroup.add(reputationHeuristicRadioButton);
  }

  /**
   * @return the buttonGroup
   */
  public ButtonGroup getButtonGroup() {
    return buttonGroup;
  }

  /**
   * @param buttonGroup the buttonGroup to set
   */
  public void setButtonGroup(ButtonGroup buttonGroup) {
    this.buttonGroup = buttonGroup;
  }

  /**
   * @return the costHeuristicRadioButton
   */
  public JRadioButton getCostHeuristicRadioButton() {
    return costHeuristicRadioButton;
  }

  /**
   * @param costHeuristicRadioButton the costHeuristicRadioButton to set
   */
  public void setCostHeuristicRadioButton(JRadioButton costHeuristicRadioButton) {
    this.costHeuristicRadioButton = costHeuristicRadioButton;
  }

  /**
   * @return the timeHeuristicRadioButton
   */
  public JRadioButton getTimeHeuristicRadioButton() {
    return timeHeuristicRadioButton;
  }

  /**
   * @param timeHeuristicRadioButton the timeHeuristicRadioButton to set
   */
  public void setTimeHeuristicRadioButton(JRadioButton timeHeuristicRadioButton) {
    this.timeHeuristicRadioButton = timeHeuristicRadioButton;
  }

  /**
   * @return the reputationHeuristicRadioButton
   */
  public JRadioButton getReputationHeuristicRadioButton() {
    return reputationHeuristicRadioButton;
  }

  /**
   * @param reputationHeuristicRadioButton the reputationHeuristicRadioButton to set
   */
  public void setReputationHeuristicRadioButton(JRadioButton reputationHeuristicRadioButton) {
    this.reputationHeuristicRadioButton = reputationHeuristicRadioButton;
  }

}

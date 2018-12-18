package view.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AskFeatureWithAgentInformationPanel extends JPanel {
  private static final long serialVersionUID = -6006578213568007270L;
  private AskFeaturePanel askFeaturePanel = new AskFeaturePanel();
  private AgentInformationPanel agentInformationPanel = new AgentInformationPanel();

  AskFeatureWithAgentInformationPanel() {
    setLayout(new GridLayout(2, 0, 0, 0));
    add(askFeaturePanel);
    add(agentInformationPanel);
    askFeaturePanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
    
    askFeaturePanel.setBorder(BorderFactory
        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
            new EmptyBorder(0, 0, 0, 0)));
    agentInformationPanel.setBorder(BorderFactory
        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
            new EmptyBorder(10, 10, 10, 10)));

    agentInformationPanel.getAgentInformationLabel().setText("Expert Information");
    agentInformationPanel.getAgentNameLabel().setText("Selected Agent Name");
  }

  /**
   * @return the askFeaturePanel
   */
  public AskFeaturePanel getAskFeaturePanel() {
    return askFeaturePanel;
  }

  /**
   * @param askFeaturePanel the askFeaturePanel to set
   */
  public void setAskFeaturePanel(AskFeaturePanel askFeaturePanel) {
    this.askFeaturePanel = askFeaturePanel;
  }

  /**
   * @return the agentInformationPanel
   */
  public AgentInformationPanel getAgentInformationPanel() {
    return agentInformationPanel;
  }

  /**
   * @param agentInformationPanel the agentInformationPanel to set
   */
  public void setAgentInformationPanel(AgentInformationPanel agentInformationPanel) {
    this.agentInformationPanel = agentInformationPanel;
  }
}

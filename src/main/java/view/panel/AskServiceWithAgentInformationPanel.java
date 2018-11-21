package view.panel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AskServiceWithAgentInformationPanel extends JPanel {
  private static final long serialVersionUID = -6006578213568007270L;
  private AskServicePanel askServicePanel = new AskServicePanel();
  private AgentInformationPanel agentInformationPanel = new AgentInformationPanel();

  AskServiceWithAgentInformationPanel() {
    setLayout(new GridLayout(2, 0, 0, 0));
    add(askServicePanel);
    add(agentInformationPanel);
    askServicePanel.setPreferredSize(new Dimension(getWidth(), getHeight()));
    
    askServicePanel.setBorder(BorderFactory
        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
            new EmptyBorder(0, 0, 0, 0)));
    agentInformationPanel.setBorder(BorderFactory
        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
            new EmptyBorder(10, 10, 10, 10)));

    agentInformationPanel.getAgentInformationLabel().setText("Service Provider Agent Information");
    agentInformationPanel.getAgentNameLabel().setText("Selected Agent Name");
  }

  /**
   * @return the askServicePanel
   */
  public AskServicePanel getAskServicePanel() {
    return askServicePanel;
  }

  /**
   * @param askServicePanel the askServicePanel to set
   */
  public void setAskServicePanel(AskServicePanel askServicePanel) {
    this.askServicePanel = askServicePanel;
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

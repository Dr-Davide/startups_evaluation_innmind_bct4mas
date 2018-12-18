package view.panel;

import javax.swing.*;
import java.awt.*;

public class AskFeatureTabPanel extends JPanel {
  private static final long serialVersionUID = -8538671824756689445L;
  private AskFeatureWithAgentInformationPanel askFeatureWithAgentInformationPanel =
      new AskFeatureWithAgentInformationPanel();
  private SearchFeatureResultPanel searchFeatureResultPanel = new SearchFeatureResultPanel();

  public AskFeatureTabPanel() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(askFeatureWithAgentInformationPanel);
    add(searchFeatureResultPanel);

    //    askFeatureWithAgentInformationPanel.setBorder(BorderFactory
    //        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
    //            new EmptyBorder(10, 10, 10, 10)));
    //    searchFeatureResultPanel.setBorder(BorderFactory
    //        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
    //            new EmptyBorder(10, 10, 10, 10)));
  }

  /**
   * @return the askFeatureWithAgentInformationPanel
   */
  public AskFeatureWithAgentInformationPanel getAskFeatureWithAgentInformationPanel() {
    return askFeatureWithAgentInformationPanel;
  }

  /**
   * @param askFeatureWithAgentInformationPanel the askFeatureWithAgentInformationPanel to set
   */
  public void setAskFeatureWithAgentInformationPanel(
      AskFeatureWithAgentInformationPanel askFeatureWithAgentInformationPanel) {
    this.askFeatureWithAgentInformationPanel = askFeatureWithAgentInformationPanel;
  }

  /**
   * @return the searchFeatureResultPanel
   */
  public SearchFeatureResultPanel getSearchFeatureResultPanel() {
    return searchFeatureResultPanel;
  }

  /**
   * @param searchFeatureResultPanel the searchFeatureResultPanel to set
   */
  public void setSearchFeatureResultPanel(SearchFeatureResultPanel searchFeatureResultPanel) {
    this.searchFeatureResultPanel = searchFeatureResultPanel;
  }
}

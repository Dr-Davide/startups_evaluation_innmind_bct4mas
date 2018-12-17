package view.panel;

import javax.swing.*;
import java.awt.*;

public class AskServiceTabPanel extends JPanel {
  private static final long serialVersionUID = -8538671824756689445L;
  private AskServiceWithAgentInformationPanel askServiceWithAgentInformationPanel =
      new AskServiceWithAgentInformationPanel();
  private SearchServiceResultPanel searchServiceResultPanel = new SearchServiceResultPanel();

  public AskServiceTabPanel() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(askServiceWithAgentInformationPanel);
    add(searchServiceResultPanel);

    //    askServiceWithAgentInformationPanel.setBorder(BorderFactory
    //        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
    //            new EmptyBorder(10, 10, 10, 10)));
    //    searchServiceResultPanel.setBorder(BorderFactory
    //        .createCompoundBorder(BorderFactory.createLineBorder(Color.black),
    //            new EmptyBorder(10, 10, 10, 10)));
  }

  /**
   * @return the askServiceWithAgentInformationPanel
   */
  public AskServiceWithAgentInformationPanel getAskServiceWithAgentInformationPanel() {
    return askServiceWithAgentInformationPanel;
  }

  /**
   * @param askServiceWithAgentInformationPanel the askServiceWithAgentInformationPanel to set
   */
  public void setAskServiceWithAgentInformationPanel(
      AskServiceWithAgentInformationPanel askServiceWithAgentInformationPanel) {
    this.askServiceWithAgentInformationPanel = askServiceWithAgentInformationPanel;
  }

  /**
   * @return the searchServiceResultPanel
   */
  public SearchServiceResultPanel getSearchServiceResultPanel() {
    return searchServiceResultPanel;
  }

  /**
   * @param searchServiceResultPanel the searchServiceResultPanel to set
   */
  public void setSearchServiceResultPanel(SearchServiceResultPanel searchServiceResultPanel) {
    this.searchServiceResultPanel = searchServiceResultPanel;
  }
}

package view.panel;

import javax.swing.*;
import java.awt.*;

public class AskServiceTabPanelOLD extends JPanel {
  private static final long serialVersionUID = -8538671824756689445L;
  private AskServicePanel askServicePanel = new AskServicePanel();
  private SearchServiceResultPanel searchServiceResultPanel = new SearchServiceResultPanel();

  public AskServiceTabPanelOLD() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(askServicePanel);
    add(searchServiceResultPanel);
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

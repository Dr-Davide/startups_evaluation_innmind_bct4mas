package view.panel;

import javax.swing.*;
import java.awt.*;

public class AskFeatureTabPanelOLD extends JPanel {
  private static final long serialVersionUID = -8538671824756689445L;
  private AskFeaturePanel askFeaturePanel = new AskFeaturePanel();
  private SearchFeatureResultPanel searchFeatureResultPanel = new SearchFeatureResultPanel();

  public AskFeatureTabPanelOLD() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(askFeaturePanel);
    add(searchFeatureResultPanel);
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

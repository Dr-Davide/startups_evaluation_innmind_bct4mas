package view.panel;

import java.awt.GridLayout;
import javax.swing.JPanel;

public class MessagesTabPanel extends JPanel {
  private InBoxMessagesPanel inBoxMessagesPanel = new InBoxMessagesPanel();
  private WriteMessagePanel writeMessagePanel = new WriteMessagePanel();

  /**
   * @return the inBoxMessagesPanel
   */
  public InBoxMessagesPanel getInBoxMessagesPanel() {
    return inBoxMessagesPanel;
  }

  /**
   * @param inBoxMessagesPanel the inBoxMessagesPanel to set
   */
  public void setInBoxMessagesPanel(InBoxMessagesPanel inBoxMessagesPanel) {
    this.inBoxMessagesPanel = inBoxMessagesPanel;
  }

  /**
   * @return the writeMessagePanel
   */
  public WriteMessagePanel getWriteMessagePanel() {
    return writeMessagePanel;
  }

  /**
   * @param writeMessagePanel the writeMessagePanel to set
   */
  public void setWriteMessagePanel(WriteMessagePanel writeMessagePanel) {
    this.writeMessagePanel = writeMessagePanel;
  }

  public MessagesTabPanel() {
    setLayout(new GridLayout(0, 2, 0, 0));
    add(inBoxMessagesPanel);
    add(writeMessagePanel);
  }
}

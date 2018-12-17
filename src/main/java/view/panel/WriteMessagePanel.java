package view.panel;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;

public class WriteMessagePanel extends JPanel {

  private JButton btnSendMessage;
  private CouplePanel panelMessageTo = new CouplePanel("To:  ");
  private CouplePanel panelMessageSubject = new CouplePanel("Subject:  ");
  private final JLabel lblWriteTitle = new JLabel("Write");
  private final JLabel lblBody = new JLabel("Body:");
  private final JTextPane textPane = new JTextPane();

  /**
   * Create the panel.
   */
  public WriteMessagePanel() {

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {23, 36, 30, 26, 26, 26, 50, 40, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 1.0, 0.0, 1.0, 1.0, Double.MIN_VALUE};
    gridBagLayout.rowWeights =
        new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);

    GridBagConstraints gbc_lblAddService = new GridBagConstraints();
    gbc_lblAddService.anchor = GridBagConstraints.NORTH;
    gbc_lblAddService.gridwidth = 3;
    gbc_lblAddService.insets = new Insets(0, 0, 5, 5);
    gbc_lblAddService.gridx = 1;
    gbc_lblAddService.gridy = 1;
    lblWriteTitle.setFont(new Font("Dialog", Font.BOLD, 16));
    // lblAddService.setFont(new Font("Ubuntu Light", Font.BOLD, 16));
    add(lblWriteTitle, gbc_lblAddService);
    GridBagConstraints gbc_panelServiceName = new GridBagConstraints();
    gbc_panelServiceName.gridwidth = 3;
    gbc_panelServiceName.fill = GridBagConstraints.BOTH;
    gbc_panelServiceName.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceName.gridx = 1;
    gbc_panelServiceName.gridy = 3;
    add(panelMessageTo, gbc_panelServiceName);
    GridBagConstraints gbc_panelServiceDescription = new GridBagConstraints();
    gbc_panelServiceDescription.gridwidth = 3;
    gbc_panelServiceDescription.fill = GridBagConstraints.BOTH;
    gbc_panelServiceDescription.insets = new Insets(0, 0, 5, 5);
    gbc_panelServiceDescription.gridx = 1;
    gbc_panelServiceDescription.gridy = 4;
    add(panelMessageSubject, gbc_panelServiceDescription);

    GridBagConstraints gbc_lblBody = new GridBagConstraints();
    gbc_lblBody.anchor = GridBagConstraints.WEST;
    gbc_lblBody.insets = new Insets(0, 0, 5, 5);
    gbc_lblBody.gridx = 1;
    gbc_lblBody.gridy = 5;
    add(lblBody, gbc_lblBody);

    GridBagConstraints gbc_textPane = new GridBagConstraints();
    gbc_textPane.gridheight = 2;
    gbc_textPane.gridwidth = 2;
    gbc_textPane.insets = new Insets(0, 0, 5, 5);
    gbc_textPane.fill = GridBagConstraints.BOTH;
    gbc_textPane.gridx = 2;
    gbc_textPane.gridy = 5;
    add(textPane, gbc_textPane);

    btnSendMessage = new JButton("Send");
    GridBagConstraints gbc_buttonAddService = new GridBagConstraints();
    gbc_buttonAddService.insets = new Insets(0, 0, 5, 5);
    gbc_buttonAddService.fill = GridBagConstraints.VERTICAL;
    gbc_buttonAddService.gridx = 2;
    gbc_buttonAddService.gridy = 8;
    add(btnSendMessage, gbc_buttonAddService);

  }

  /**
   * @return the btnSendMessage
   */
  public JButton getBtnSendMessage() {
    return btnSendMessage;
  }

  /**
   * @param btnSendMessage the btnSendMessage to set
   */
  public void setBtnSendMessage(JButton btnSendMessage) {
    this.btnSendMessage = btnSendMessage;
  }

  /**
   * @return the panelMessageTo
   */
  public CouplePanel getPanelMessageTo() {
    return panelMessageTo;
  }

  /**
   * @param panelMessageTo the panelMessageTo to set
   */
  public void setPanelMessageTo(CouplePanel panelMessageTo) {
    this.panelMessageTo = panelMessageTo;
  }

  /**
   * @return the panelMessageSubject
   */
  public CouplePanel getPanelMessageSubject() {
    return panelMessageSubject;
  }

  /**
   * @param panelMessageSubject the panelMessageSubject to set
   */
  public void setPanelMessageSubject(CouplePanel panelMessageSubject) {
    this.panelMessageSubject = panelMessageSubject;
  }

  /**
   * @return the lblWriteTitle
   */
  public JLabel getLblWriteTitle() {
    return lblWriteTitle;
  }

}


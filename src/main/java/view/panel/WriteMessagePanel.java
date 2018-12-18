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

    GridBagConstraints gbc_lblAddFeature = new GridBagConstraints();
    gbc_lblAddFeature.anchor = GridBagConstraints.NORTH;
    gbc_lblAddFeature.gridwidth = 3;
    gbc_lblAddFeature.insets = new Insets(0, 0, 5, 5);
    gbc_lblAddFeature.gridx = 1;
    gbc_lblAddFeature.gridy = 1;
    lblWriteTitle.setFont(new Font("Dialog", Font.BOLD, 16));
    // lblAddFeature.setFont(new Font("Ubuntu Light", Font.BOLD, 16));
    add(lblWriteTitle, gbc_lblAddFeature);
    GridBagConstraints gbc_panelFeatureName = new GridBagConstraints();
    gbc_panelFeatureName.gridwidth = 3;
    gbc_panelFeatureName.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureName.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureName.gridx = 1;
    gbc_panelFeatureName.gridy = 3;
    add(panelMessageTo, gbc_panelFeatureName);
    GridBagConstraints gbc_panelFeatureDescription = new GridBagConstraints();
    gbc_panelFeatureDescription.gridwidth = 3;
    gbc_panelFeatureDescription.fill = GridBagConstraints.BOTH;
    gbc_panelFeatureDescription.insets = new Insets(0, 0, 5, 5);
    gbc_panelFeatureDescription.gridx = 1;
    gbc_panelFeatureDescription.gridy = 4;
    add(panelMessageSubject, gbc_panelFeatureDescription);

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
    GridBagConstraints gbc_buttonAddFeature = new GridBagConstraints();
    gbc_buttonAddFeature.insets = new Insets(0, 0, 5, 5);
    gbc_buttonAddFeature.fill = GridBagConstraints.VERTICAL;
    gbc_buttonAddFeature.gridx = 2;
    gbc_buttonAddFeature.gridy = 8;
    add(btnSendMessage, gbc_buttonAddFeature);

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


package view.panel;

import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class CouplePanel extends JPanel {

  private JTextField textField;
  private String lbl;

  /**
   * Create the panel.
   */
  public CouplePanel(String lblIn) {
    lbl = lblIn;
    setLayout(new GridLayout(1, 2));

    JLabel lblNewLabel = new JLabel(lbl);
    // lblNewLabel.setFont(new Font("Ubuntu Light", Font.BOLD, 12));
    add(lblNewLabel);

    textField = new JTextField();
    add(textField);
    textField.setColumns(10);

  }

  /**
   * @return the textField
   */
  public JTextField getTextField() {
    return textField;
  }

  /**
   * @param textField the textField to set
   */
  public void setTextField(JTextField textField) {
    this.textField = textField;
  }

  /**
   * @return the lbl
   */
  public String getLbl() {
    return lbl;
  }

  /**
   * @param lbl the lbl to set
   */
  public void setLbl(String lbl) {
    this.lbl = lbl;
  }

}

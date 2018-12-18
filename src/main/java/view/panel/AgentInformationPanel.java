package view.panel;

import org.apache.log4j.Logger;
import start.JadeJson2Pojo;
import start.StartClass;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class AgentInformationPanel extends JPanel {
  private static final Logger log = Logger.getLogger(AgentInformationPanel.class);

  private static final long serialVersionUID = -3461158901128190982L;
  private BufferedImage agentImage;
  private JLabel agentNameLabel;
  private JLabel agentInformationLabel;
  private ImageIcon imageIcon;
  private JLabel imageLabel;
  private GridBagConstraints gbc_imageLabel = new GridBagConstraints();
  private Border raisedBevelBorder = BorderFactory.createRaisedBevelBorder();
  private Border loweredbevelBorder = BorderFactory.createLoweredBevelBorder();
  // This creates a nice frame.
  private Border compoundBorder =
      BorderFactory.createCompoundBorder(raisedBevelBorder, loweredbevelBorder);

  AgentInformationPanel(){
    this("");
  }

  AgentInformationPanel(String imagePath){
    {

      if (imagePath.equals("")){
        JadeJson2Pojo jadeJson2Pojo = new JadeJson2Pojo();

        try {
          jadeJson2Pojo = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        }
        imagePath = jadeJson2Pojo.getAgentImagePath();
      }



      // agentImage = ImageIO.read(new File());
      try {
        InputStream inputStream = StartClass.getInputStreamPublic(imagePath);

        agentImage = ImageIO.read(inputStream);
        //            agentImage = ImageIO.read(new File(jadeJson2Pojo.getAgentImagePath())); // "resources/images/agentFigure.png"
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      imageIcon = new ImageIcon(agentImage);


      GridBagLayout gridBagLayout = new GridBagLayout();
      gridBagLayout.columnWidths = new int[] {0, 0, 0, 0};
      gridBagLayout.rowHeights = new int[] {0, 0, 0, 0};
      gridBagLayout.columnWeights = new double[] {0.0, 1.0, 0.0, Double.MIN_VALUE};
      gridBagLayout.rowWeights = new double[] {1.0, 1.0, 1.0, Double.MIN_VALUE};
      setLayout(gridBagLayout);

      agentInformationLabel = new JLabel("Agent Information");
      agentInformationLabel.setFont(new Font("Dialog", Font.BOLD, 16));
      GridBagConstraints gbc_lblAgentInformation = new GridBagConstraints();
      gbc_lblAgentInformation.insets = new Insets(0, 0, 5, 5);
      gbc_lblAgentInformation.gridx = 1;
      gbc_lblAgentInformation.gridy = 0;
      add(agentInformationLabel, gbc_lblAgentInformation);

      imageLabel = new JLabel(imageIcon);
      imageLabel.setBorder(compoundBorder);
      // gbc_imageLabel = new GridBagConstraints();
      gbc_imageLabel.insets = new Insets(5, 5, 5, 5);
      gbc_imageLabel.gridx = 1;
      gbc_imageLabel.gridy = 1;
      add(imageLabel, gbc_imageLabel);

      agentNameLabel = new JLabel("Agent Name");
      agentNameLabel.setFont(new Font("Dialog", Font.BOLD, 12));
      GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
      gbc_lblNewLabel.insets = new Insets(0, 0, 0, 5);
      gbc_lblNewLabel.gridx = 1;
      gbc_lblNewLabel.gridy = 2;
      add(agentNameLabel, gbc_lblNewLabel);

    }
  }

  /**
   * @return the agentImage
   */
  public BufferedImage getAgentImage() {
    return agentImage;
  }

  /**
   * @param bufferedImage the agentImage to set
   */
  public void setAgentImage(BufferedImage bufferedImage) {
    remove(imageLabel);
    agentImage = bufferedImage;

    // Rescale image
    Image imageScaledInstance = agentImage.getScaledInstance((getWidth() / 10) * 7,
        (getHeight() / 10) * 7, java.awt.Image.SCALE_SMOOTH);

    imageIcon = new ImageIcon(imageScaledInstance);

    imageLabel = new JLabel(imageIcon);

    imageLabel.setBorder(compoundBorder);
    GridBagConstraints gbc_imageLabel = new GridBagConstraints();
    gbc_imageLabel.insets = new Insets(5, 5, 5, 5);
    gbc_imageLabel.gridx = 1;
    gbc_imageLabel.gridy = 1;
    add(imageLabel, gbc_imageLabel);
  }

  /**
   * @return the agentNameLabel
   */
  public JLabel getAgentNameLabel() {
    return agentNameLabel;
  }

  /**
   * @param lblNewLabel the agentNameLabel to set
   */
  public void setAgentNameLabel(JLabel lblNewLabel) {
    agentNameLabel = lblNewLabel;
  }

  /**
   * @return the agentInformationLabel
   */
  JLabel getAgentInformationLabel() {
    return agentInformationLabel;
  }

  /**
   * @param lblNewLabel the agentInformationLabel to set
   */
  public void setAgentInformationLabel(JLabel lblNewLabel) {
    agentInformationLabel = lblNewLabel;
  }



}

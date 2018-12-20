package view.panel;

import javax.swing.*;
import java.awt.*;

public class AddCompositeFeatureStartupPanel extends JPanel {
    private static final long serialVersionUID = 3878035954720786152L;
    private CouplePanel panelFeatureName = new CouplePanel("Name:  ");
    private final JLabel lblAddFeature = new JLabel("Add Composite Feature");
    private final SelectLeafFeaturesStartupPanel selectLeafFeaturesPanel = new SelectLeafFeaturesStartupPanel();



    private final JButton btnAddCompositeFeature = new JButton("Add Composite Feature");

    /**
     * Create the panel.
     */
    AddCompositeFeatureStartupPanel() {
        // setBorder(new EmptyBorder(10, 10, 10, 10));
        GridBagLayout gridBagLayout = new GridBagLayout();
        gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0};
        gridBagLayout.rowHeights = new int[] {23, 36, 0, 26, 26, 26, 26, 40, 0, 0, 0};
        gridBagLayout.columnWeights = new double[] {1.0, 1.0, 1.0, 1.0, 1.0, Double.MIN_VALUE};
        gridBagLayout.rowWeights =
                new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        setLayout(gridBagLayout);

        GridBagConstraints gbc_lblAddFeature = new GridBagConstraints();
        gbc_lblAddFeature.anchor = GridBagConstraints.NORTH;
        gbc_lblAddFeature.gridwidth = 3;
        gbc_lblAddFeature.insets = new Insets(0, 0, 5, 5);
        gbc_lblAddFeature.gridx = 1;
        gbc_lblAddFeature.gridy = 1;
        lblAddFeature.setFont(new Font("Dialog", Font.BOLD, 16));
        // lblAddFeature.setFont(new Font("Ubuntu Light", Font.BOLD, 16));
        add(lblAddFeature, gbc_lblAddFeature);

        GridBagConstraints gbc_panelFeatureName = new GridBagConstraints();
        gbc_panelFeatureName.gridwidth = 3;
        gbc_panelFeatureName.fill = GridBagConstraints.BOTH;
        gbc_panelFeatureName.insets = new Insets(0, 0, 5, 5);
        gbc_panelFeatureName.gridx = 1;
        gbc_panelFeatureName.gridy = 3;
        panelFeatureName.setLbl("Name: ");
        add(panelFeatureName, gbc_panelFeatureName);

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.gridwidth = 3;
        gbc_panel.insets = new Insets(0, 0, 0, 0);
        gbc_panel.fill = GridBagConstraints.BOTH;
        gbc_panel.gridx = 1;
        gbc_panel.gridy = 8;
        add(selectLeafFeaturesPanel, gbc_panel);

        GridBagConstraints gbc_btnAddCompositeFeature = new GridBagConstraints();
        gbc_btnAddCompositeFeature.insets = new Insets(0, 0, 0, 5);
        gbc_btnAddCompositeFeature.gridx = 2;
        gbc_btnAddCompositeFeature.gridy = 9;
        add(btnAddCompositeFeature, gbc_btnAddCompositeFeature);
    }


    /**
     * @return the panelFeatureName
     */
    public CouplePanel getPanelFeatureName() {
        return panelFeatureName;
    }

    /**
     * @param panelFeatureName the panelFeatureName to set
     */
    public void setPanelFeatureName(CouplePanel panelFeatureName) {
        this.panelFeatureName = panelFeatureName;
    }


    /**
     * @return the selectLeafFeaturesPanel
     */
    public SelectLeafFeaturesStartupPanel getSelectLeafFeaturesPanel() {
        return selectLeafFeaturesPanel;
    }

    public JButton getBtnAddCompositeFeature() {
        return btnAddCompositeFeature;
    }

}

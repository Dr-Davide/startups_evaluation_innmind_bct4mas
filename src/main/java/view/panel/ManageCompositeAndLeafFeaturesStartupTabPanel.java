package view.panel;

import agents.BCAgent;
import start.JadeJson2Pojo;
import start.StartClass;

import java.awt.Color;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class ManageCompositeAndLeafFeaturesStartupTabPanel extends JPanel {

    private AddCompositeFeatureStartupPanel addCompositeFeaturePanel = new AddCompositeFeatureStartupPanel();
    private AddLeafFeatureStartupPanel addLeafFeaturePanel = new AddLeafFeatureStartupPanel();
    private AgentInformationPanel agentImagePanel;
    private ManageFeaturesStartupPanel manageFeaturesPanel = new ManageFeaturesStartupPanel();

    public ManageCompositeAndLeafFeaturesStartupTabPanel(String agentType) {
        // SET AGENT IMAGE
        agentImagePanel = new AgentInformationPanel(agentType);

        setLayout(new GridLayout(2, 2, 0, 0));
        add(addCompositeFeaturePanel);
        add(addLeafFeaturePanel);
        add(agentImagePanel);
        add(manageFeaturesPanel);

        addCompositeFeaturePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
        addLeafFeaturePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
        agentImagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
        manageFeaturesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    }

    public ManageCompositeAndLeafFeaturesStartupTabPanel() {
        agentImagePanel = new AgentInformationPanel();
        setLayout(new GridLayout(2, 2, 0, 0));
        add(addCompositeFeaturePanel);
        add(addLeafFeaturePanel);
        add(agentImagePanel);
        add(manageFeaturesPanel);

        addCompositeFeaturePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
        addLeafFeaturePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
        agentImagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
        manageFeaturesPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.black), new EmptyBorder(10, 10, 10, 10)));
    }

    /**
     * @return the addCompositeFeaturePanel
     */
    public AddCompositeFeatureStartupPanel getAddCompositeFeaturePanel() {
        return addCompositeFeaturePanel;
    }

    /**
     * @param addCompositeFeaturePanel the addCompositeFeaturePanel to set
     */
    public void setAddCompositeFeaturePanel(AddCompositeFeatureStartupPanel addCompositeFeaturePanel) {
        this.addCompositeFeaturePanel = addCompositeFeaturePanel;
    }

    /**
     * @return the addLeafFeaturePanel
     */
    public AddLeafFeatureStartupPanel getAddLeafFeaturePanel() {
        return addLeafFeaturePanel;
    }

    /**
     * @param addLeafFeaturePanel the addLeafFeaturePanel to set
     */
    public void setAddLeafFeaturePanel(AddLeafFeatureStartupPanel addLeafFeaturePanel) {
        this.addLeafFeaturePanel = addLeafFeaturePanel;
    }

    /**
     * @return the agentImagePanel
     */
    public AgentInformationPanel getAgentImagePanel() {
        return agentImagePanel;
    }

    /**
     * @param agentImagePanel the agentImagePanel to set
     */
    public void setAgentImagePanel(AgentInformationPanel agentImagePanel) {
        this.agentImagePanel = agentImagePanel;
    }

    /**
     * @return the manageFeaturesPanel
     */
    public ManageFeaturesStartupPanel getManageFeaturesPanel() {
        return manageFeaturesPanel;
    }

    /**
     * @param manageFeaturesPanel the manageFeaturesPanel to set
     */
    public void setManageFeaturesPanel(ManageFeaturesStartupPanel manageFeaturesPanel) {
        this.manageFeaturesPanel = manageFeaturesPanel;
    }
}

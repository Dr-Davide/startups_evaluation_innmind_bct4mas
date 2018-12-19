package view;

import agents.BCAgent;
import controllers.CheckerController;
import controllers.RangeQueriesController;
import logic.Heuristic;
import messages.BCMessage;
import model.StructFeatureRequest;
import model.pojo.InnMindReputation;
import org.apache.log4j.Logger;
import start.JadeJson2Pojo;
import start.StartClass;
import view.panel.AskFeatureTabPanel;
import view.panel.ManageCompositeAndLeafFeaturesStartupTabPanel;
import view.panel.ManageCompositeAndLeafFeaturesTabPanel;
import view.panel.MessagesTabPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class StartupAgentGui extends BCAgentGui {

    private static final Logger log = Logger.getLogger(BCAgentGui.class);


    private static final long serialVersionUID = -8399308930154655548L;
    private BCAgent bcAgent;
    //  private ManageFeaturesTabPanel manageFeaturesTabPanel = new ManageFeaturesTabPanel();
    private AskFeatureTabPanel askFeatureTabPanel = new AskFeatureTabPanel();
    private MessagesTabPanel messagesTabPanel = new MessagesTabPanel();
    private ManageCompositeAndLeafFeaturesStartupTabPanel manageCompositeAndLeafFeaturesTabPanel;
    private JComponent panel;

    /**
     *
     * BCAgentGui @param agent
     */
    public StartupAgentGui(BCAgent agent) {

//        super(agent.getLocalName());
        super(true, agent);

        JadeJson2Pojo jadeJson2Pojo = new JadeJson2Pojo();

        try {
            jadeJson2Pojo = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        String agentImagePath = jadeJson2Pojo.getNoAvatarStartupImagePath();

        manageCompositeAndLeafFeaturesTabPanel = new ManageCompositeAndLeafFeaturesStartupTabPanel(agentImagePath);

        bcAgent = agent;

        // JComponent panel = this.buildTabbedPane();
        panel = buildTabbedPane();
        getContentPane().add(panel, BorderLayout.CENTER);
        pack();
        setVisible(true);
        setTitle("Startup - Welcome agent " + agent.getLocalName().toString() + "!");
        //    manageFeaturesTabPanel.getAddFeatureWithAgentImagePanel().getAgentImage().getAgentNameLabel()
        //        .setText(agent.getLocalName());
        manageCompositeAndLeafFeaturesTabPanel.getAgentImagePanel().getAgentNameLabel()
                .setText(agent.getLocalName());


        // ADD LEAF SERVICE ACTION IN NEW MANAGE SERVICES (MANAGE COMPOSITE AND LEAF SERVICES)
        manageCompositeAndLeafFeaturesTabPanel.getAddLeafFeaturePanel().getButtonAddFeature()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            // GET FORM DATA
                            String serviceName = manageCompositeAndLeafFeaturesTabPanel.getAddLeafFeaturePanel()
                                    .getPanelFeatureName().getTextField().getText().trim();
//                            String serviceDescription =
//                                    manageCompositeAndLeafFeaturesTabPanel.getAddLeafFeaturePanel()
//                                            .getPanelFeatureDescription().getTextField().getText().trim();
//                            String serviceCost = manageCompositeAndLeafFeaturesTabPanel.getAddLeafFeaturePanel()
//                                    .getPanelFeatureCost().getTextField().getText().trim();
//                            String serviceTime = manageCompositeAndLeafFeaturesTabPanel.getAddLeafFeaturePanel()
//                                    .getPanelFeatureTime().getTextField().getText().trim();

                            String serviceDescription = "";
                            String serviceCost = "";
                            String serviceTime = "";
                            // TRIGGER the Behaviour
                            bcAgent
                                    .addLeafFeatureTrigger(serviceName, serviceDescription, serviceCost, serviceTime);
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(StartupAgentGui.this,
                                    "Failed loading the service : " + e.getMessage(), "Error: ",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

        // ADD COMPOSITE SERVICE ACTION IN NEW MANAGE SERVICES (MANAGE COMPOSITE AND LEAF SERVICES)
        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel().getBtnAddCompositeFeature()
                .addActionListener(new ActionListener() {

                    @Override public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            if (manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                    .getSelectLeafFeaturesPanel().atLeastTwoSelectedInTableModel()) {

                                ArrayList<Integer> selectedRowIndexes =
                                        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                                .getSelectLeafFeaturesPanel().getRowIndexesSelectedInTableModel();
                                log.info("SELECTED ROW INDEXES SIZE: " + selectedRowIndexes.size());

                                // GET FORM DATA
                                String serviceName =
                                        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                                .getPanelFeatureName().getTextField().getText().trim();
                                String serviceDescription =
                                        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                                .getPanelFeatureDescription().getTextField().getText().trim();

                                String serviceComposition =
                                        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                                .getSelectLeafFeaturesPanel()
                                                .getFeatureCompositionString(selectedRowIndexes);
                                //                String serviceComposition = "s1,s2";
                                log.info("SERVICE COMPOSITION: " + serviceComposition);
                                String serviceCost =
                                        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                                .getPanelFeatureCost().getTextField().getText().trim();
                                String serviceTime =
                                        manageCompositeAndLeafFeaturesTabPanel.getAddCompositeFeaturePanel()
                                                .getPanelFeatureTime().getTextField().getText().trim();

                                // TRIGGER the Behaviour
                                bcAgent
                                        .addCompositeFeatureTrigger(serviceName, serviceDescription, serviceComposition,
                                                serviceCost, serviceTime);
                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this,
                                        "Select at least Two Records in the list of leaf services", "Selection Error",
                                        JOptionPane.INFORMATION_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(StartupAgentGui.this,
                                    "Failed loading the service : " + e.getMessage(), "Error: ",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

        // DELETE SERVICE from the List of my Features (Delete FeatureRelationAgent)
        manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel().getBtnDeleteSelection()
                .addActionListener(new ActionListener() {

                    @Override public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            if (manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel()
                                    .onlyOneSelectedInTableModel()) {

                                ArrayList<Integer> selectedRowIndexes =
                                        manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel()
                                                .getRowIndexesSelectedInTableModel();

                                // GET TABLE DATA (hidden service id)
                                String selectedFeatureId =
                                        manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel()
                                                .getFeatureId(selectedRowIndexes);

                                String agentId = bcAgent.getMyName();

                                int deleteConfirmation = JOptionPane.showConfirmDialog(StartupAgentGui.this,
                                        "Are you sure that you want to delete the service ID: " + selectedFeatureId
                                                + " of the agent ID: " + agentId + "?", "Confirm Deletion of Feature",
                                        JOptionPane.YES_NO_OPTION);
                                if (deleteConfirmation == JOptionPane.YES_OPTION) {
                                    // TRIGGER the Delete Behaviour
                                    bcAgent.deleteFeatureRelationAgentTrigger(selectedFeatureId, agentId);

                                } else {
                                    JOptionPane.showMessageDialog(StartupAgentGui.this,
                                            "Aborted the deletion of  the service ID: " + selectedFeatureId
                                                    + " of the agent ID: " + agentId, "Abort Delete Action",
                                            JOptionPane.INFORMATION_MESSAGE);
                                }


                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this, "Select only one record to Delete",
                                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(StartupAgentGui.this,
                                    "Failed deleting the service : " + e.getMessage(), "Delete Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });

        // UPDATE SERVICE from the List of my Features (Update FeatureRelationAgent)
        manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel().getBtnModifyFeature()
                .addActionListener(new ActionListener() {

                    @Override public void actionPerformed(ActionEvent actionEvent) {
                        try {
                            if (manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel()
                                    .onlyOneSelectedInTableModel()) {

                                ArrayList<Integer> selectedRowIndexes =
                                        manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel()
                                                .getRowIndexesSelectedInTableModel();
                                log.info("SELECTED ROW INDEXES SIZE: " + selectedRowIndexes.size());

                                // GET TABLE DATA (hidden service id)
                                String selectedFeatureId =
                                        manageCompositeAndLeafFeaturesTabPanel.getManageFeaturesPanel()
                                                .getFeatureId(selectedRowIndexes);

                                String agentId = bcAgent.getMyName();
                                //                String serviceComposition = "s1,s2";
                                log.info("SELECTED SERVICE ID: " + selectedFeatureId);


                                String[] options = new String[3];
                                //                options[0] = FeatureRelationAgent.COST;
                                //                options[1] = FeatureRelationAgent.TIME;
                                //                options[2] = FeatureRelationAgent.DESCRIPTION;

                                options[0] = "Cost";
                                options[1] = "Time";
                                options[2] = "Description";

                                int modifyConfirmation = JOptionPane
                                        .showOptionDialog(StartupAgentGui.this, "Select which field you want to modifiy",
                                                "Select Field to Modify", 0, JOptionPane.INFORMATION_MESSAGE, null, options,
                                                null);
                                switch (modifyConfirmation) {
                                    case 0:
                                        // TRIGGER the Modify Cost Behaviour
                                        String newCostValue = JOptionPane
                                                .showInputDialog(StartupAgentGui.this, "Please insert the new Cost Value",
                                                        "Modify Cost",
                                                        JOptionPane.INFORMATION_MESSAGE);
                                        bcAgent.updateFeatureRelationAgentCostTrigger(selectedFeatureId, agentId,
                                                newCostValue);
                                        break;
                                    case 1:
                                        // TRIGGER the Modify Time Behaviour
                                        String newTimeValue = JOptionPane
                                                .showInputDialog(StartupAgentGui.this, "Please insert the new Time Value",
                                                        "Modify Time", JOptionPane.INFORMATION_MESSAGE);
                                        bcAgent.updateFeatureRelationAgentTimeTrigger(selectedFeatureId, agentId,
                                                newTimeValue);
                                        break;
                                    case 2:
                                        // TRIGGER the Modify Time Behaviour
                                        String newDescriptionValue = JOptionPane
                                                .showInputDialog(StartupAgentGui.this, "Please insert the new Description Value",
                                                        "Modify Description", JOptionPane.INFORMATION_MESSAGE);
                                        bcAgent.updateFeatureRelationAgentDescriptionTrigger(selectedFeatureId, agentId,
                                                newDescriptionValue);
                                        break;
                                    default: // should be unreachable
                                        IllegalStateException illegalStateException = new IllegalStateException(
                                                "Wrong field to update, it's not in the expected ones");
                                        log.error(illegalStateException);
                                        throw illegalStateException;
                                }


                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this, "Select only one record to Delete",
                                        "Selection Error", JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(StartupAgentGui.this,
                                    "Failed deleting the service : " + e.getMessage(), "Delete Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });



        // GET SERVICE LIST ACTION (SEARCH SERVICE)
        askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                .getButtonGetFeature()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {

                            String serviceName =
                                    askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                            .getPanelFeatureName()
                                            .getTextField().getText().trim();
                            String selectedHeuristic;
                            boolean selectedCostHeuristic =
                                    askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                            .getSelectHeuristicPanel().getCostHeuristicRadioButton().isSelected();
                            if (selectedCostHeuristic) {
                                selectedHeuristic = Heuristic.COST;
                            } else {
                                boolean selectedTimeHeuristic =
                                        askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                                .getSelectHeuristicPanel().getTimeHeuristicRadioButton().isSelected();
                                if (selectedTimeHeuristic) {
                                    selectedHeuristic = Heuristic.TIME;
                                } else {
                                    boolean selectedReputationHeuristic =
                                            askFeatureTabPanel.getAskFeatureWithAgentInformationPanel()
                                                    .getAskFeaturePanel()
                                                    .getSelectHeuristicPanel().getReputationHeuristicRadioButton().isSelected();
                                    if (selectedReputationHeuristic) {
                                        selectedHeuristic = Heuristic.REPUTATION;
                                    } else {
                                        selectedHeuristic = Heuristic.COST; // DEFAULT CASE
                                    }

                                }
                            }

                            // TODO: Aggiungere verifica se Startup agent ha reputazione per il servizio
                            // TODO: Uso name as ID
                            String agentId = bcAgent.getMyName();
                            String featureId = serviceName;

                            if (CheckerController.isReputationAlreadyInLedger(bcAgent,agentId, featureId, InnMindReputation.STARTUP_ROLE)) {
                                bcAgent.getFeaturesListTrigger(serviceName, selectedHeuristic);
                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this,
                                        "The Startup can't look for this feature: " + featureId, "403: the client is not authorized",
                                        JOptionPane.ERROR_MESSAGE);
                            }
//                            bcAgent.getFeaturesListTrigger(serviceName, selectedHeuristic);

                        } catch (Exception getException) {
                            JOptionPane.showMessageDialog(StartupAgentGui.this,
                                    "Failed looking for the service: " + getException.getMessage(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    }

                    /**
                     *
                     * @param askFeatureTabPanel
                     * @return
                     */
                    private boolean selectedHeuristic(AskFeatureTabPanel askFeatureTabPanel) {
                        return askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                .getSelectHeuristicPanel().getCostHeuristicRadioButton().isSelected()
                                || askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                .getSelectHeuristicPanel()
                                .getTimeHeuristicRadioButton().isSelected();
                    }
                });

        // PUT THE AGENT INFORMATION OF THE SELECTED RECORD IN THE RESULT LIST (JTABLE) IN THE AGENT INFORMATION PANEL
        JTable table = askFeatureTabPanel.getSearchFeatureResultPanel().getTable();
        table.getSelectionModel().addListSelectionListener(event -> {
            // put the agentId in the AgentInformationPanel Label
            // TODO: Temporary fix to GUI error in refresh (IndexOutOfBoundException: -1
            if (table.getSelectedRow() == -1) {
                askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAgentInformationPanel()
                        .getAgentNameLabel().setText(table.getValueAt(0, 2).toString());
            } else {
                // put agent name in FeatureAgentInformationPanel
                askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAgentInformationPanel()
                        .getAgentNameLabel().setText(table.getValueAt(table.getSelectedRow(), 2).toString());
                // TODO: put service description in FeatureAgentInformationPanel
                //        askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAgentInformationPanel()
                //            .getFeatureDescriptionLabel()
                //            .setText(table.getValueAt(table.getSelectedRow(), 5).toString());
            }
            JadeJson2Pojo jadeJson2Pojo2 = new JadeJson2Pojo();

            try {
                jadeJson2Pojo2 = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            // agentImage = ImageIO.read(new File());
            try {
                // TODO: Temporary fix to GUI error in refresh (IndexOutOfBoundException: -1
                if (table.getSelectedRow() == -1) {
                    log.info("HOTEL IMAGE PATH: " + jadeJson2Pojo2.getAgentImagesHotelsPath().get(0));
                    askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAgentInformationPanel()
                            .setAgentImage(
                                    ImageIO.read(new File(jadeJson2Pojo2.getAgentImagesHotelsPath().get(0))));
                } else {
                    log.info("HOTEL IMAGE PATH: " + jadeJson2Pojo2.getAgentImagesHotelsPath()
                            .get(table.getSelectedRow()));
//                    InputStream inputStream = StartClass.getInputStreamPublic(
//                            jadeJson2Pojo2.getAgentImagesHotelsPath().get(table.getSelectedRow()));
                    InputStream inputStream = StartClass.getInputStreamPublic(
                            jadeJson2Pojo2.getNoAvatarExpertImagePath());
                    BufferedImage agentImage = ImageIO.read(inputStream);
                    //          askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAgentInformationPanel()
                    //              .setAgentImage(ImageIO.read(
                    //                  new File(jadeJson2Pojo.getAgentImagesHotelsPath().get(table.getSelectedRow()))));
                    askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAgentInformationPanel()
                            .setAgentImage(agentImage);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });

        // ASK SERVICE ACTION
        askFeatureTabPanel.getSearchFeatureResultPanel().getButtonAskFeatureSelection()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        try {

                            if (askFeatureTabPanel.getSearchFeatureResultPanel().onlyOneSelectedInTableModel()) {

                                // GET INPUT DATA
                                int selectedRowIndex = askFeatureTabPanel.getSearchFeatureResultPanel()
                                        .getRowIndexSelectedInTableModel();

                                // TODO: ADD SERVICE ID

                                String serviceName = (String) askFeatureTabPanel.getSearchFeatureResultPanel()
                                        .getRowFeature(selectedRowIndex);
                                String agentName = (String) askFeatureTabPanel.getSearchFeatureResultPanel()
                                        .getRowAgent(selectedRowIndex);
                                Integer cost = Integer.parseInt((String) askFeatureTabPanel
                                        .getSearchFeatureResultPanel().getRowCost(selectedRowIndex));
                                Integer time = Integer.parseInt((String) askFeatureTabPanel
                                        .getSearchFeatureResultPanel().getRowTime(selectedRowIndex));
                                Float reputation = Float.parseFloat((String) askFeatureTabPanel
                                        .getSearchFeatureResultPanel().getRowReputation(selectedRowIndex));
                                String serviceId = (String) askFeatureTabPanel.getSearchFeatureResultPanel()
                                        .getRowFeatureId(selectedRowIndex);

                                StructFeatureRequest selectedStructAgent =
                                        new StructFeatureRequest(agentName, cost, time, reputation);
                                ArrayList<StructFeatureRequest> singleStructAgent = new ArrayList<>();


                                // TODO: REQUEST SERVICE TO AGENT

                                //                bcAgent.askSelectedFeatureTrigger(serviceName, selectedStructAgent);
                                bcAgent.askSelectedFeatureTrigger(serviceId, serviceName, selectedStructAgent);


                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this, "Select only One Record in the list",
                                        "Selection Error", JOptionPane.INFORMATION_MESSAGE);

                            }

                        } catch (Exception askException) {
                            JOptionPane.showMessageDialog(StartupAgentGui.this,
                                    "Failed looking for the service: " + askException.getMessage(), "Error Message",
                                    JOptionPane.ERROR_MESSAGE);
                        }

                    }

                    /**
                     *
                     * @param askFeatureTabPanel
                     * @return
                     */
                    private boolean selectedHeuristic(AskFeatureTabPanel askFeatureTabPanel) {
                        return askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                .getSelectHeuristicPanel().getCostHeuristicRadioButton().isSelected()
                                || askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getAskFeaturePanel()
                                .getSelectHeuristicPanel()
                                .getTimeHeuristicRadioButton().isSelected();
                        // return askFeatureTabPanel.getAskFeatureWithAgentInformationPanel().getSelectHeuristicPanel()
                        // .getButtonGroup().getSelection().isSelected();
                    }
                });


        // ACCEPT SERVICE EXECUTION

        messagesTabPanel.getInBoxMessagesPanel().getBtnAcceptSelection()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (messagesTabPanel.getInBoxMessagesPanel().onlyOneSelectedInTableModel()) {

                            // GET INPUT DATA
                            int selectedRowIndex =
                                    messagesTabPanel.getInBoxMessagesPanel().getRowIndexSelectedInTableModel();

                            String agentName = (String) messagesTabPanel.getInBoxMessagesPanel()
                                    .getRowAgentName(selectedRowIndex);
                            String messageType = (String) messagesTabPanel.getInBoxMessagesPanel()
                                    .getRowMessageType(selectedRowIndex);
                            String messageBody = (String) messagesTabPanel.getInBoxMessagesPanel()
                                    .getRowMessageBody(selectedRowIndex);


                            // serviceId è messageBody
                            // demanderAgentId è agentName
                            // TODO: USARE ID ORA È IL NOME
                            String serviceId = messageBody;
                            String demanderAgentId = agentName;

                            if (messageType.equals(BCMessage.SERVICE_EXECUTION)) {
                                bcAgent.executeFeatureTrigger(serviceId, demanderAgentId);
                                // Cancellare Record dalla lista
                                bcAgent.deleteMessageTrigger(selectedRowIndex);
                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this,
                                        "You can do the \"Accept\" only in response to a service execution request",
                                        "Selection Error", JOptionPane.INFORMATION_MESSAGE);
                            }

                        } else {
                            JOptionPane.showMessageDialog(StartupAgentGui.this, "Select only One Record in the list",
                                    "Selection Error", JOptionPane.INFORMATION_MESSAGE);
                        }

                    }
                });

        // DENY SERVICE EXECUTION

        messagesTabPanel.getInBoxMessagesPanel().getBtnDenySelection()
                .addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (messagesTabPanel.getInBoxMessagesPanel().onlyOneSelectedInTableModel()) {

                            // GET INPUT DATA
                            int selectedRowIndex =
                                    messagesTabPanel.getInBoxMessagesPanel().getRowIndexSelectedInTableModel();

                            String agentName = (String) messagesTabPanel.getInBoxMessagesPanel()
                                    .getRowAgentName(selectedRowIndex);
                            String messageType = (String) messagesTabPanel.getInBoxMessagesPanel()
                                    .getRowMessageType(selectedRowIndex);
                            String messageBody = (String) messagesTabPanel.getInBoxMessagesPanel()
                                    .getRowMessageBody(selectedRowIndex);


                            // serviceId è messageBody
                            // demanderAgentId è agentName
                            // TODO: USARE ID
                            String serviceId = messageBody;
                            String demanderAgentId = agentName;

                            if (messageType.equals(BCMessage.SERVICE_EXECUTION)) {
                                // TODO: Informare il Demander del diniego
                                bcAgent.denyFeatureExecutionTrigger(serviceId, demanderAgentId);

                                // Cancellare Record dalla lista
                                bcAgent.deleteMessageTrigger(selectedRowIndex);
                            } else {
                                JOptionPane.showMessageDialog(StartupAgentGui.this,
                                        "You can do the \"Denial\" only in response to a service execution request",
                                        "Selection Error", JOptionPane.INFORMATION_MESSAGE);
                            }

                        } else {
                            JOptionPane.showMessageDialog(StartupAgentGui.this, "Select only One Record in the list",
                                    "Selection Error", JOptionPane.INFORMATION_MESSAGE);
                        }

                    }
                });


        // Make the agent terminate when the user closes
        // the GUI using the button on the upper right corner
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                bcAgent.doDelete();
            }
        });

        setResizable(true);
    }

    /**
     * Build and return the JTabbedPane (I vari tab della finestra)
     *
     * @return tabbedPane
     */
    private JComponent buildTabbedPane() {

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.putClientProperty("jgoodies.noContentBorder", Boolean.TRUE);


        tabbedPane.addTab("Manage Features", manageCompositeAndLeafFeaturesTabPanel);
        //    tabbedPane.addTab("Manage Features", manageFeaturesTabPanel);
        GridBagLayout gridBagLayout =
                (GridBagLayout) askFeatureTabPanel.getAskFeatureWithAgentInformationPanel()
                        .getAskFeaturePanel().getLayout();
        gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
        gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        tabbedPane.addTab("Ask Evaluation", askFeatureTabPanel);
        tabbedPane.addTab("Messages", messagesTabPanel);




        return tabbedPane;
    }

    public String getDifferentId(String oldFeatureId) {
        String newFeatureId;
        String showInputDialogMessage =
                "Feature ID: " + oldFeatureId + " already used. Insert a valid ID for the service";
        String showInputDialogTitle = "Feature Naming Conflict";

        //    newFeatureId = JOptionPane
        //        .showInputDialog(bcAgent.expertAgentGui.getPanel(), showInputDialogMessage,
        //            showInputDialogTitle, JOptionPane.QUESTION_MESSAGE);
        newFeatureId = (String) JOptionPane
                .showInputDialog(bcAgent.bcAgentGui.getPanel(), showInputDialogMessage,
                        showInputDialogTitle, JOptionPane.QUESTION_MESSAGE, null, null, oldFeatureId);

        return newFeatureId;
    }

    public String getExecuterEvaluation(String serviceId) {
        String executerEvaluation;
        String showInputDialogMessage =
                "Agent " + bcAgent.getLocalName() + ", please evaluate the QoS of service: " + serviceId
                        + " as the Feature Executer Role in the transaction";
        String showInputDialogTitle = "Executer Feature Evaluation";

        executerEvaluation = getEvaluation(showInputDialogMessage, showInputDialogTitle);

        return executerEvaluation;
    }

    public String getDemanderEvaluation(String serviceId) {
        String demanderEvaluation;
        String showInputDialogMessage =
                "Agent " + bcAgent.getLocalName().toString() + ", please evaluate the QoS of service: "
                        + serviceId + " as the Feature Demander Role in the transaction";
        String showInputDialogTitle = "Demander Feature Evaluation";

        demanderEvaluation = getEvaluation(showInputDialogMessage, showInputDialogTitle);

        return demanderEvaluation;
    }

    /**
     * Show the Input Dialog to permit the user to insert the evaluation
     *
     * @param showInputDialogMessage
     * @param showInputDialogTitle
     * @return
     */
    public String getEvaluation(String showInputDialogMessage, String showInputDialogTitle) {
        JadeJson2Pojo jadeJson2Pojo = new JadeJson2Pojo();
        try {
            jadeJson2Pojo = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        URL imageURL = StartClass.getURLPublic(jadeJson2Pojo.getEvaluateImagePath());
        ImageIcon icon = new ImageIcon(imageURL);

        //    ImageIcon icon = new ImageIcon(jadeJson2Pojo.getEvaluateImagePath());

        Image img = icon.getImage();
        Integer newWidth = img.getWidth(null) / 5;
        Integer newHeight = img.getHeight(null) / 5;
        BufferedImage bi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        // g.drawImage(img, 0, 0, null);
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        ImageIcon newIcon = new ImageIcon(bi);
        // String[] evaluationOptions = {"0.0", "1.0", "1.5", "2.0", "2.5", "3.0", "3.5", "4.0", "4.5",
        // "5.0", "5.5", "6.0", "6.5", "7.0", "7.5", "8.0", "8.5", "9.0", "9.5", "10.0"};
        String[] evaluationOptions =
                {"0.0", "1.0", "2.0", "3.0", "4.0", "5.0", "6.0", "7.0", "8.0", "9.0", "10.0"};

        return (String) JOptionPane
                .showInputDialog(bcAgent.bcAgentGui.getPanel(), showInputDialogMessage,
                        showInputDialogTitle, JOptionPane.QUESTION_MESSAGE, newIcon, evaluationOptions,
                        evaluationOptions[6]);
    }

    private void showOkMessage(String showInputDialogMessage, String showInputDialogTitle) {
        JadeJson2Pojo jadeJson2Pojo = new JadeJson2Pojo();
        try {
            jadeJson2Pojo = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        URL imageURL = StartClass.getURLPublic(jadeJson2Pojo.getOkIconPath());
        //    BufferedImage agentImage = ImageIO.read(inputStream);
        ImageIcon icon = new ImageIcon(imageURL);
        //    ImageIcon icon = new ImageIcon(jadeJson2Pojo.getOkIconPath());
        log.info(jadeJson2Pojo.getOkIconPath());

        Image img = icon.getImage();
        Integer newWidth = img.getWidth(null) / 6;
        Integer newHeight = img.getHeight(null) / 6;
        BufferedImage bi = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics g = bi.createGraphics();
        // g.drawImage(img, 0, 0, null);
        g.drawImage(img, 0, 0, newWidth, newHeight, null);
        ImageIcon newIcon = new ImageIcon(bi);

        JOptionPane.showMessageDialog(bcAgent.bcAgentGui.getPanel(), showInputDialogMessage,
                showInputDialogTitle, JOptionPane.INFORMATION_MESSAGE, newIcon);
    }

    public void getFeatureCompletedMessage(String executerAgentId, String executedFeatureId) {
        String showInputDialogMessage =
                "Feature: " + executedFeatureId + " by executer Agent: " + executerAgentId + " Completed";
        String showInputDialogTitle = "Feature Execution Completed";
        showOkMessage(showInputDialogMessage, showInputDialogTitle);
    }

    public void showGui() {
        // DIMENSIONS
        //    pack();  // means automatic dimensions
        // setSize() overwrites pack()
        setSize(850, 780);

        // POSITION: NOTHING MEANS UP-LEFT
        // example if you want to change position
        //    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        //    int centerX = (int) screenSize.getWidth() / 2;
        //    int centerY = (int) screenSize.getHeight() / 2;
        //     setLocation(centerX - getWidth() / 2, centerY - getHeight() / 2);

        super.setVisible(true);
    }

    /**
     * @return the askFeatureTabPanel
     */
    public AskFeatureTabPanel getAskFeatureTabPanel() {
        return askFeatureTabPanel;
    }

    /**
     * @param askFeatureTabPanel the askFeatureTabPanel to set
     */
    public void setAskFeatureTabPanel(AskFeatureTabPanel askFeatureTabPanel) {
        this.askFeatureTabPanel = askFeatureTabPanel;
    }

    /**
     * @return the messagesTabPanel
     */
    public MessagesTabPanel getMessagesTabPanel() {
        return messagesTabPanel;
    }

    /**
     * @param messagesTabPanel the messagesTabPanel to set
     */
    public void setMessagesTabPanel(MessagesTabPanel messagesTabPanel) {
        this.messagesTabPanel = messagesTabPanel;
    }

    /**
     * @return the panel
     */
    public JComponent getPanel() {
        return panel;
    }

    /**
     * @param panel the panel to set
     */
    public void setPanel(JComponent panel) {
        this.panel = panel;
    }

    /**
     * @return the manageCompositeAndLeafFeaturesTabPanel
     */
    public ManageCompositeAndLeafFeaturesStartupTabPanel getManageCompositeAndLeafFeaturesStartupTabPanel() {
        return manageCompositeAndLeafFeaturesTabPanel;
    }

    /**
     * @param manageCompositeAndLeafFeaturesTabPanel the manageCompositeAndLeafFeaturesTabPanel to set
     */
    public void setManageCompositeAndLeafFeaturesStartupTabPanel(
            ManageCompositeAndLeafFeaturesStartupTabPanel manageCompositeAndLeafFeaturesTabPanel) {
        this.manageCompositeAndLeafFeaturesTabPanel = manageCompositeAndLeafFeaturesTabPanel;
    }
}
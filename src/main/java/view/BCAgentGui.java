/*****************************************************************
 * JADE - Java Agent DEvelopment Framework is a framework to develop multi-agent systems in
 * compliance with the FIPA specifications. Copyright (C) 2000 CSELT S.p.A.
 * 
 * GNU Lesser General Public License
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation, version 2.1 of
 * the License.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA
 * 02111-1307, USA.
 *****************************************************************/
package view;

import agents.BCAgent;
import logic.Heuristic;
import messages.BCMessage;
import model.StructServiceRequest;
import org.apache.log4j.Logger;
import start.JadeJson2Pojo;
import start.StartClass;
import view.panel.AskServiceTabPanel;
import view.panel.ManageCompositeAndLeafServicesTabPanel;
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

/**
 * Class to create user interface
 *
 * @author Valerio Mattioli @ HES-SO (valeriomattioli580@gmail.com)
 */
public class BCAgentGui extends JFrame {
  private static final Logger log = Logger.getLogger(BCAgentGui.class);


  private static final long serialVersionUID = -8399308930154655548L;
  private BCAgent bcAgent;
  //  private ManageServicesTabPanel manageServicesTabPanel = new ManageServicesTabPanel();
  private AskServiceTabPanel askServiceTabPanel = new AskServiceTabPanel();
  private MessagesTabPanel messagesTabPanel = new MessagesTabPanel();
  private ManageCompositeAndLeafServicesTabPanel manageCompositeAndLeafServicesTabPanel =
      new ManageCompositeAndLeafServicesTabPanel();
  private JComponent panel;

  /**
   *
   * BCAgentGui @param agent
   */
  public BCAgentGui(BCAgent agent) {

    super(agent.getLocalName());

    bcAgent = agent;

    // JComponent panel = this.buildTabbedPane();
    panel = buildTabbedPane();
    getContentPane().add(panel, BorderLayout.CENTER);
    pack();
    setVisible(true);
    setTitle("BCAgent - Welcome agent " + agent.getLocalName().toString() + "!");
    //    manageServicesTabPanel.getAddServiceWithAgentImagePanel().getAgentImage().getAgentNameLabel()
    //        .setText(agent.getLocalName());
    manageCompositeAndLeafServicesTabPanel.getAgentImagePanel().getAgentNameLabel()
        .setText(agent.getLocalName());


    // ADD LEAF SERVICE ACTION IN NEW MANAGE SERVICES (MANAGE COMPOSITE AND LEAF SERVICES)
    manageCompositeAndLeafServicesTabPanel.getAddLeafServicePanel().getButtonAddService()
        .addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent actionEvent) {
            try {
              // GET FORM DATA
              String serviceName = manageCompositeAndLeafServicesTabPanel.getAddLeafServicePanel()
                  .getPanelServiceName().getTextField().getText().trim();
              String serviceDescription =
                  manageCompositeAndLeafServicesTabPanel.getAddLeafServicePanel()
                      .getPanelServiceDescription().getTextField().getText().trim();
              String serviceCost = manageCompositeAndLeafServicesTabPanel.getAddLeafServicePanel()
                  .getPanelServiceCost().getTextField().getText().trim();
              String serviceTime = manageCompositeAndLeafServicesTabPanel.getAddLeafServicePanel()
                  .getPanelServiceTime().getTextField().getText().trim();

              // TRIGGER the Behaviour
              bcAgent
                  .addLeafServiceTrigger(serviceName, serviceDescription, serviceCost, serviceTime);
            } catch (Exception e) {
              JOptionPane.showMessageDialog(BCAgentGui.this,
                  "Failed loading the service : " + e.getMessage(), "Error: ",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });

    // ADD COMPOSITE SERVICE ACTION IN NEW MANAGE SERVICES (MANAGE COMPOSITE AND LEAF SERVICES)
    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel().getBtnAddCompositeService()
        .addActionListener(new ActionListener() {

          @Override public void actionPerformed(ActionEvent actionEvent) {
            try {
              if (manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                  .getSelectLeafServicesPanel().atLeastTwoSelectedInTableModel()) {

                ArrayList<Integer> selectedRowIndexes =
                    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                        .getSelectLeafServicesPanel().getRowIndexesSelectedInTableModel();
                log.info("SELECTED ROW INDEXES SIZE: " + selectedRowIndexes.size());

                // GET FORM DATA
                String serviceName =
                    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                        .getPanelServiceName().getTextField().getText().trim();
                String serviceDescription =
                    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                        .getPanelServiceDescription().getTextField().getText().trim();

                String serviceComposition =
                    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                        .getSelectLeafServicesPanel()
                        .getServiceCompositionString(selectedRowIndexes);
                //                String serviceComposition = "s1,s2";
                log.info("SERVICE COMPOSITION: " + serviceComposition);
                String serviceCost =
                    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                        .getPanelServiceCost().getTextField().getText().trim();
                String serviceTime =
                    manageCompositeAndLeafServicesTabPanel.getAddCompositeServicePanel()
                        .getPanelServiceTime().getTextField().getText().trim();

                // TRIGGER the Behaviour
                bcAgent
                    .addCompositeServiceTrigger(serviceName, serviceDescription, serviceComposition,
                        serviceCost, serviceTime);
              } else {
                JOptionPane.showMessageDialog(BCAgentGui.this,
                    "Select at least Two Records in the list of leaf services", "Selection Error",
                    JOptionPane.INFORMATION_MESSAGE);
              }
            } catch (Exception e) {
              JOptionPane.showMessageDialog(BCAgentGui.this,
                  "Failed loading the service : " + e.getMessage(), "Error: ",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });

    // DELETE SERVICE from the List of my Services (Delete ServiceRelationAgent)
    manageCompositeAndLeafServicesTabPanel.getManageServicesPanel().getBtnDeleteSelection()
        .addActionListener(new ActionListener() {

          @Override public void actionPerformed(ActionEvent actionEvent) {
            try {
              if (manageCompositeAndLeafServicesTabPanel.getManageServicesPanel()
                  .onlyOneSelectedInTableModel()) {

                ArrayList<Integer> selectedRowIndexes =
                    manageCompositeAndLeafServicesTabPanel.getManageServicesPanel()
                        .getRowIndexesSelectedInTableModel();

                // GET TABLE DATA (hidden service id)
                String selectedServiceId =
                    manageCompositeAndLeafServicesTabPanel.getManageServicesPanel()
                        .getServiceId(selectedRowIndexes);

                String agentId = bcAgent.getMyName();

                int deleteConfirmation = JOptionPane.showConfirmDialog(BCAgentGui.this,
                    "Are you sure that you want to delete the service ID: " + selectedServiceId
                        + " of the agent ID: " + agentId + "?", "Confirm Deletion of Service",
                    JOptionPane.YES_NO_OPTION);
                if (deleteConfirmation == JOptionPane.YES_OPTION) {
                  // TRIGGER the Delete Behaviour
                  bcAgent.deleteServiceRelationAgentTrigger(selectedServiceId, agentId);

                } else {
                  JOptionPane.showMessageDialog(BCAgentGui.this,
                      "Aborted the deletion of  the service ID: " + selectedServiceId
                          + " of the agent ID: " + agentId, "Abort Delete Action",
                      JOptionPane.INFORMATION_MESSAGE);
                }


              } else {
                JOptionPane.showMessageDialog(BCAgentGui.this, "Select only one record to Delete",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
              }
            } catch (Exception e) {
              JOptionPane.showMessageDialog(BCAgentGui.this,
                  "Failed deleting the service : " + e.getMessage(), "Delete Error",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });

    // UPDATE SERVICE from the List of my Services (Update ServiceRelationAgent)
    manageCompositeAndLeafServicesTabPanel.getManageServicesPanel().getBtnModifyService()
        .addActionListener(new ActionListener() {

          @Override public void actionPerformed(ActionEvent actionEvent) {
            try {
              if (manageCompositeAndLeafServicesTabPanel.getManageServicesPanel()
                  .onlyOneSelectedInTableModel()) {

                ArrayList<Integer> selectedRowIndexes =
                    manageCompositeAndLeafServicesTabPanel.getManageServicesPanel()
                        .getRowIndexesSelectedInTableModel();
                log.info("SELECTED ROW INDEXES SIZE: " + selectedRowIndexes.size());

                // GET TABLE DATA (hidden service id)
                String selectedServiceId =
                    manageCompositeAndLeafServicesTabPanel.getManageServicesPanel()
                        .getServiceId(selectedRowIndexes);

                String agentId = bcAgent.getMyName();
                //                String serviceComposition = "s1,s2";
                log.info("SELECTED SERVICE ID: " + selectedServiceId);


                String[] options = new String[3];
                //                options[0] = ServiceRelationAgent.COST;
                //                options[1] = ServiceRelationAgent.TIME;
                //                options[2] = ServiceRelationAgent.DESCRIPTION;

                options[0] = "Cost";
                options[1] = "Time";
                options[2] = "Description";

                int modifyConfirmation = JOptionPane
                    .showOptionDialog(BCAgentGui.this, "Select which field you want to modifiy",
                        "Select Field to Modify", 0, JOptionPane.INFORMATION_MESSAGE, null, options,
                        null);
                switch (modifyConfirmation) {
                  case 0:
                    // TRIGGER the Modify Cost Behaviour
                    String newCostValue = JOptionPane
                        .showInputDialog(BCAgentGui.this, "Please insert the new Cost Value",
                            "Modify Cost",
                        JOptionPane.INFORMATION_MESSAGE);
                    bcAgent.updateServiceRelationAgentCostTrigger(selectedServiceId, agentId,
                        newCostValue);
                    break;
                  case 1:
                    // TRIGGER the Modify Time Behaviour
                    String newTimeValue = JOptionPane
                        .showInputDialog(BCAgentGui.this, "Please insert the new Time Value",
                            "Modify Time", JOptionPane.INFORMATION_MESSAGE);
                    bcAgent.updateServiceRelationAgentTimeTrigger(selectedServiceId, agentId,
                        newTimeValue);
                    break;
                  case 2:
                    // TRIGGER the Modify Time Behaviour
                    String newDescriptionValue = JOptionPane
                        .showInputDialog(BCAgentGui.this, "Please insert the new Description Value",
                            "Modify Description", JOptionPane.INFORMATION_MESSAGE);
                    bcAgent.updateServiceRelationAgentDescriptionTrigger(selectedServiceId, agentId,
                        newDescriptionValue);
                    break;
                  default: // should be unreachable
                    IllegalStateException illegalStateException = new IllegalStateException(
                        "Wrong field to update, it's not in the expected ones");
                    log.error(illegalStateException);
                    throw illegalStateException;
                }


              } else {
                JOptionPane.showMessageDialog(BCAgentGui.this, "Select only one record to Delete",
                    "Selection Error", JOptionPane.WARNING_MESSAGE);
              }
            } catch (Exception e) {
              JOptionPane.showMessageDialog(BCAgentGui.this,
                  "Failed deleting the service : " + e.getMessage(), "Delete Error",
                  JOptionPane.ERROR_MESSAGE);
            }
          }
        });



    // GET SERVICE LIST ACTION (SEARCH SERVICE)
    askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
        .getButtonGetService()
        .addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            try {

              String serviceName =
                  askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                      .getPanelServiceName()
                  .getTextField().getText().trim();
              String selectedHeuristic;
              boolean selectedCostHeuristic =
                  askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                  .getSelectHeuristicPanel().getCostHeuristicRadioButton().isSelected();
              if (selectedCostHeuristic) {
                selectedHeuristic = Heuristic.COST;
              } else {
                boolean selectedTimeHeuristic =
                    askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                    .getSelectHeuristicPanel().getTimeHeuristicRadioButton().isSelected();
                if (selectedTimeHeuristic) {
                  selectedHeuristic = Heuristic.TIME;
                } else {
                  boolean selectedReputationHeuristic =
                      askServiceTabPanel.getAskServiceWithAgentInformationPanel()
                          .getAskServicePanel()
                      .getSelectHeuristicPanel().getReputationHeuristicRadioButton().isSelected();
                  if (selectedReputationHeuristic) {
                    selectedHeuristic = Heuristic.REPUTATION;
                  } else {
                    selectedHeuristic = Heuristic.COST; // DEFAULT CASE
                  }

                }
              }

              bcAgent.getServicesListTrigger(serviceName, selectedHeuristic);

            } catch (Exception getException) {
              JOptionPane.showMessageDialog(BCAgentGui.this,
                  "Failed looking for the service: " + getException.getMessage(), "Error",
                  JOptionPane.ERROR_MESSAGE);
            }

          }

          /**
           *
           * @param askServiceTabPanel
           * @return
           */
          private boolean selectedHeuristic(AskServiceTabPanel askServiceTabPanel) {
            return askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                .getSelectHeuristicPanel().getCostHeuristicRadioButton().isSelected()
                || askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                .getSelectHeuristicPanel()
                    .getTimeHeuristicRadioButton().isSelected();
          }
        });

    // PUT THE AGENT INFORMATION OF THE SELECTED RECORD IN THE RESULT LIST (JTABLE) IN THE AGENT INFORMATION PANEL
    JTable table = askServiceTabPanel.getSearchServiceResultPanel().getTable();
    table.getSelectionModel().addListSelectionListener(event -> {
      // put the agentId in the AgentInformationPanel Label
      // TODO: Temporary fix to GUI error in refresh (IndexOutOfBoundException: -1
      if (table.getSelectedRow() == -1) {
        askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAgentInformationPanel()
            .getAgentNameLabel().setText(table.getValueAt(0, 2).toString());
      } else {
        // put agent name in ServiceAgentInformationPanel
        askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAgentInformationPanel()
            .getAgentNameLabel().setText(table.getValueAt(table.getSelectedRow(), 2).toString());
        // TODO: put service description in ServiceAgentInformationPanel
        //        askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAgentInformationPanel()
        //            .getServiceDescriptionLabel()
        //            .setText(table.getValueAt(table.getSelectedRow(), 5).toString());
      }
      JadeJson2Pojo jadeJson2Pojo = new JadeJson2Pojo();

      try {
        jadeJson2Pojo = StartClass.getJadeJsonConfig(StartClass.JADE_CONFIG_FILE);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }


      // agentImage = ImageIO.read(new File());
      try {
        // TODO: Temporary fix to GUI error in refresh (IndexOutOfBoundException: -1
        if (table.getSelectedRow() == -1) {
          log.info("HOTEL IMAGE PATH: " + jadeJson2Pojo.getAgentImagesHotelsPath().get(0));
          askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAgentInformationPanel()
              .setAgentImage(
                  ImageIO.read(new File(jadeJson2Pojo.getAgentImagesHotelsPath().get(0))));
        } else {
          log.info("HOTEL IMAGE PATH: " + jadeJson2Pojo.getAgentImagesHotelsPath()
              .get(table.getSelectedRow()));
          InputStream inputStream = StartClass.getInputStreamPublic(
              jadeJson2Pojo.getAgentImagesHotelsPath().get(table.getSelectedRow()));
          BufferedImage agentImage = ImageIO.read(inputStream);
          //          askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAgentInformationPanel()
          //              .setAgentImage(ImageIO.read(
          //                  new File(jadeJson2Pojo.getAgentImagesHotelsPath().get(table.getSelectedRow()))));
          askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAgentInformationPanel()
              .setAgentImage(agentImage);
        }
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    });

    // ASK SERVICE ACTION
    askServiceTabPanel.getSearchServiceResultPanel().getButtonAskServiceSelection()
        .addActionListener(new ActionListener() {

          @Override
          public void actionPerformed(ActionEvent e) {
            try {

              if (askServiceTabPanel.getSearchServiceResultPanel().onlyOneSelectedInTableModel()) {

                // GET INPUT DATA
                int selectedRowIndex = askServiceTabPanel.getSearchServiceResultPanel()
                    .getRowIndexSelectedInTableModel();

                // TODO: ADD SERVICE ID

                String serviceName = (String) askServiceTabPanel.getSearchServiceResultPanel()
                    .getRowService(selectedRowIndex);
                String agentName = (String) askServiceTabPanel.getSearchServiceResultPanel()
                    .getRowAgent(selectedRowIndex);
                Integer cost = Integer.parseInt((String) askServiceTabPanel
                    .getSearchServiceResultPanel().getRowCost(selectedRowIndex));
                Integer time = Integer.parseInt((String) askServiceTabPanel
                    .getSearchServiceResultPanel().getRowTime(selectedRowIndex));
                Float reputation = Float.parseFloat((String) askServiceTabPanel
                    .getSearchServiceResultPanel().getRowReputation(selectedRowIndex));
                String serviceId = (String) askServiceTabPanel.getSearchServiceResultPanel()
                    .getRowServiceId(selectedRowIndex);

                StructServiceRequest selectedStructAgent =
                    new StructServiceRequest(agentName, cost, time, reputation);
                ArrayList<StructServiceRequest> singleStructAgent = new ArrayList<>();


                // TODO: REQUEST SERVICE TO AGENT

                //                bcAgent.askSelectedServiceTrigger(serviceName, selectedStructAgent);
                bcAgent.askSelectedServiceTrigger(serviceId, serviceName, selectedStructAgent);


              } else {
                JOptionPane.showMessageDialog(BCAgentGui.this, "Select only One Record in the list",
                    "Selection Error", JOptionPane.INFORMATION_MESSAGE);

              }

            } catch (Exception askException) {
              JOptionPane.showMessageDialog(BCAgentGui.this,
                  "Failed looking for the service: " + askException.getMessage(), "Error Message",
                  JOptionPane.ERROR_MESSAGE);
            }

          }

          /**
           *
           * @param askServiceTabPanel
           * @return
           */
          private boolean selectedHeuristic(AskServiceTabPanel askServiceTabPanel) {
            return askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                .getSelectHeuristicPanel().getCostHeuristicRadioButton().isSelected()
                || askServiceTabPanel.getAskServiceWithAgentInformationPanel().getAskServicePanel()
                .getSelectHeuristicPanel()
                    .getTimeHeuristicRadioButton().isSelected();
            // return askServiceTabPanel.getAskServiceWithAgentInformationPanel().getSelectHeuristicPanel()
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
                bcAgent.executeServiceTrigger(serviceId, demanderAgentId);
                // Cancellare Record dalla lista
                bcAgent.deleteMessageTrigger(selectedRowIndex);
              } else {
                JOptionPane.showMessageDialog(BCAgentGui.this,
                    "You can do the \"Accept\" only in response to a service execution request",
                    "Selection Error", JOptionPane.INFORMATION_MESSAGE);
              }

            } else {
              JOptionPane.showMessageDialog(BCAgentGui.this, "Select only One Record in the list",
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
                bcAgent.denyServiceExecutionTrigger(serviceId, demanderAgentId);

                // Cancellare Record dalla lista
                bcAgent.deleteMessageTrigger(selectedRowIndex);
              } else {
                JOptionPane.showMessageDialog(BCAgentGui.this,
                    "You can do the \"Denial\" only in response to a service execution request",
                    "Selection Error", JOptionPane.INFORMATION_MESSAGE);
              }

            } else {
              JOptionPane.showMessageDialog(BCAgentGui.this, "Select only One Record in the list",
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


    tabbedPane.addTab("Manage Services", manageCompositeAndLeafServicesTabPanel);
    //    tabbedPane.addTab("Manage Services", manageServicesTabPanel);
    GridBagLayout gridBagLayout =
        (GridBagLayout) askServiceTabPanel.getAskServiceWithAgentInformationPanel()
            .getAskServicePanel().getLayout();
    gridBagLayout.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
    gridBagLayout.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    tabbedPane.addTab("Ask Service", askServiceTabPanel);
    tabbedPane.addTab("Messages", messagesTabPanel);




    return tabbedPane;
  }

  public String getDifferentId(String oldServiceId) {
    String newServiceId;
    String showInputDialogMessage =
        "Service ID: " + oldServiceId + " already used. Insert a valid ID for the service";
    String showInputDialogTitle = "Service Naming Conflict";

    //    newServiceId = JOptionPane
    //        .showInputDialog(bcAgent.bcAgentGui.getPanel(), showInputDialogMessage,
    //            showInputDialogTitle, JOptionPane.QUESTION_MESSAGE);
    newServiceId = (String) JOptionPane
        .showInputDialog(bcAgent.bcAgentGui.getPanel(), showInputDialogMessage,
            showInputDialogTitle, JOptionPane.QUESTION_MESSAGE, null, null, oldServiceId);

    return newServiceId;
  }

  public String getExecuterEvaluation(String serviceId) {
    String executerEvaluation;
    String showInputDialogMessage =
        "Agent " + bcAgent.getLocalName() + ", please evaluate the QoS of service: " + serviceId
            + " as the Service Executer Role in the transaction";
    String showInputDialogTitle = "Executer Service Evaluation";

    executerEvaluation = getEvaluation(showInputDialogMessage, showInputDialogTitle);

    return executerEvaluation;
  }

  public String getDemanderEvaluation(String serviceId) {
    String demanderEvaluation;
    String showInputDialogMessage =
        "Agent " + bcAgent.getLocalName().toString() + ", please evaluate the QoS of service: "
            + serviceId + " as the Service Demander Role in the transaction";
    String showInputDialogTitle = "Demander Service Evaluation";

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

  public void getServiceCompletedMessage(String executerAgentId, String executedServiceId) {
    String showInputDialogMessage =
        "Service: " + executedServiceId + " by executer Agent: " + executerAgentId + " Completed";
    String showInputDialogTitle = "Service Execution Completed";
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
   * @return the askServiceTabPanel
   */
  public AskServiceTabPanel getAskServiceTabPanel() {
    return askServiceTabPanel;
  }

  /**
   * @param askServiceTabPanel the askServiceTabPanel to set
   */
  public void setAskServiceTabPanel(AskServiceTabPanel askServiceTabPanel) {
    this.askServiceTabPanel = askServiceTabPanel;
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
   * @return the manageCompositeAndLeafServicesTabPanel
   */
  public ManageCompositeAndLeafServicesTabPanel getManageCompositeAndLeafServicesTabPanel() {
    return manageCompositeAndLeafServicesTabPanel;
  }

  /**
   * @param manageCompositeAndLeafServicesTabPanel the manageCompositeAndLeafServicesTabPanel to set
   */
  public void setManageCompositeAndLeafServicesTabPanel(
      ManageCompositeAndLeafServicesTabPanel manageCompositeAndLeafServicesTabPanel) {
    this.manageCompositeAndLeafServicesTabPanel = manageCompositeAndLeafServicesTabPanel;
  }
}

package view.panel;

import model.pojo.Agent;
import model.pojo.Reputation;
import model.pojo.Service;
import model.pojo.ServiceRelationAgent;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class SearchServiceResultPanel extends JPanel {

  private static final long serialVersionUID = 7166992678562598177L;
  private JButton buttonAskServiceSelection;
  private ArrayList<ServiceRelationAgent> searchServiceResult = new ArrayList<>();
  private ArrayList<Service> services = new ArrayList<>();
  private ArrayList<Agent> agents = new ArrayList<>();
  private ArrayList<Reputation> reputations = new ArrayList<>();
  private EditableTableModel tableModel;
  private JTable table;



  /**
   * Create the panel.
   */
  SearchServiceResultPanel() {

    // TODO: Usare:
    // 1) Service Name and Description (query by serviceid)
    // 2) Agent Name (query by agentid)
    // 3) Reputation Value ( query by reputationid)


    String[] columnTitles = {"Selected", "Service", "Agent", "Cost", "Time", "Ex.Rep."};

    Object[][] tableModelData =
        fillTableModelData(searchServiceResult, agents, services, reputations);

    tableModel = new EditableTableModel(columnTitles, tableModelData);



    // LAYOUT AUTO-GENERATED CODE (by WindowBuilderEditor)
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0};
    gridBagLayout.rowHeights = new int[] {42, 150, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 0.0, 1.0};
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);



    JLabel lblNewLabel = new JLabel("Results");
    lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 16));
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.gridwidth = 3;
    gbc_lblNewLabel.insets = new Insets(5, 5, 5, 5);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);
    table = new JTable(tableModel);
    table.setDefaultRenderer(String.class, centerRenderer);
    table.createDefaultColumnsFromModel();
    JScrollPane scrollPane = new JScrollPane(table);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.gridwidth = 3;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.insets = new Insets(5, 5, 0, 5);
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 1;
    add(scrollPane, gbc_scrollPane);

    buttonAskServiceSelection = new JButton("Ask");
    GridBagConstraints gbc_btnModifyService = new GridBagConstraints();
    gbc_btnModifyService.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnModifyService.insets = new Insets(5, 5, 5, 5);
    gbc_btnModifyService.gridx = 1;
    gbc_btnModifyService.gridy = 3;
    add(buttonAskServiceSelection, gbc_btnModifyService);

  }

  /**
   * update the data in the TableModel from the agentServiceList (ArrayList<StructService>)
   */
  public void updateTableModelData() {
    Object[][] tableModelData =
        fillTableModelData(searchServiceResult, agents, services, reputations);
    tableModel.setData(tableModelData);
  }

  /**
   * fill the TableModel Data with the ArrayList<StructService>: Basically does the parsing from the
   * ArrayList<StructService> to Object[][] structure needed by TableModel as Data
   * 
   * @param serviceRelationAgentList
   * @return the TableModel's Data
   */
  private Object[][] fillTableModelData(ArrayList<ServiceRelationAgent> serviceRelationAgentList,
      ArrayList<Agent> agentList, ArrayList<Service> serviceList,
      ArrayList<Reputation> reputationList) {
    int row = serviceRelationAgentList.size();
    Object[][] data = new Object[row][8];
    for (int i = 0; i < row; i++) {
      data[i][0] = new Boolean(false);
      // data[i][1] = serviceRelationAgentList.get(i).getRelationId();
      data[i][1] = serviceList.get(i).getName();
      data[i][2] = agentList.get(i).getName();
      data[i][3] = serviceRelationAgentList.get(i).getCost();
      data[i][4] = serviceRelationAgentList.get(i).getTime();
      data[i][5] = reputationList.get(i).getValue();
      data[i][6] = serviceList.get(i).getServiceId();
      data[i][7] = serviceRelationAgentList.get(i).getDescription();
    }
    return data;
  }

  /**
   * Return True if only one record is selected
   * 
   * @return
   */
  public boolean onlyOneSelectedInTableModel() {
    boolean isOnlyOneSelected = false;
    int selectedCount = 0;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if ((boolean) tableModel.getValueAt(i, 0)) {
        selectedCount++;
      }
    }

    if (selectedCount == 1) {
      isOnlyOneSelected = true;
    }
    return isOnlyOneSelected;
  }

  /**
   * Return the index (implies onlyOneSelectedInTableModel == true)
   * 
   * @return
   */
  public int getRowIndexSelectedInTableModel() {
    int index = 0;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if ((boolean) tableModel.getValueAt(i, 0)) {
        index = i;
      }
    }

    return index;
  }

  public Object getRowService(int row) {
    return tableModel.getValueAt(row, 1);
  }

  public Object getRowAgent(int row) {
    return tableModel.getValueAt(row, 2);
  }

  public Object getRowCost(int row) {
    return tableModel.getValueAt(row, 3);
  }

  public Object getRowTime(int row) {
    return tableModel.getValueAt(row, 4);
  }

  public Object getRowReputation(int row) {
    return tableModel.getValueAt(row, 5);
  }

  public Object getRowServiceId(int row) {
    return tableModel.getValueAt(row, 6);
  }

  public Object getServiceDescription(int row) {
    return searchServiceResult.get(row).getDescription();
  }

  /**
   * @return the btnAskServiceSelection
   */
  public JButton getButtonAskServiceSelection() {
    return buttonAskServiceSelection;
  }

  /**
   * @param btnAskServiceSelection the btnAskServiceSelection to set
   */
  public void setButtonAskServiceSelection(JButton btnAskServiceSelection) {
    buttonAskServiceSelection = btnAskServiceSelection;
  }

  /**
   * @return the searchServiceResult
   */
  public ArrayList<ServiceRelationAgent> getSearchServiceResult() {
    return searchServiceResult;
  }

  /**
   * @param searchServiceResult the searchServiceResult to set
   */
  public void setSearchServiceResult(ArrayList<ServiceRelationAgent> searchServiceResult) {
    this.searchServiceResult = searchServiceResult;
  }

  /**
   * @return the services
   */
  public ArrayList<Service> getServices() {
    return services;
  }

  /**
   * @param services the services to set
   */
  public void setServices(ArrayList<Service> services) {
    this.services = services;
  }

  /**
   * @return the agents
   */
  public ArrayList<Agent> getAgents() {
    return agents;
  }

  /**
   * @param agents the agents to set
   */
  public void setAgents(ArrayList<Agent> agents) {
    this.agents = agents;
  }

  /**
   * @return the reputations
   */
  public ArrayList<Reputation> getReputations() {
    return reputations;
  }

  /**
   * @param reputations the reputations to set
   */
  public void setReputations(ArrayList<Reputation> reputations) {
    this.reputations = reputations;
  }

  /**
   * @return the tableModel
   */
  public EditableTableModel getTableModel() {
    return tableModel;
  }

  /**
   * @param tableModel the tableModel to set
   */
  public void setTableModel(EditableTableModel tableModel) {
    this.tableModel = tableModel;
  }

  /**
   * @return the table
   */
  public JTable getTable() {
    return table;
  }

  /**
   * @param table the table to set
   */
  public void setTable(JTable table) {
    this.table = table;
  }

}


package view.panel;

import model.pojo.Agent;
import model.pojo.Feature;
import model.pojo.FeatureRelationAgent;
import model.pojo.InnMindReputation;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class SearchFeatureResultPanel extends JPanel {

  private static final long serialVersionUID = 7166992678562598177L;
  private JButton buttonAskFeatureSelection;
  private ArrayList<FeatureRelationAgent> searchFeatureResult = new ArrayList<>();
  private ArrayList<Feature> features = new ArrayList<>();
  private ArrayList<Agent> agents = new ArrayList<>();
  private ArrayList<InnMindReputation> innMindReputations = new ArrayList<>();
  private EditableTableModel tableModel;
  private JTable table;



  /**
   * Create the panel.
   */
  SearchFeatureResultPanel() {

    // TODO: Usare:
    // 1) Feature Name and Description (query by serviceid)
    // 2) Agent Name (query by agentid)
    // 3) InnMindReputation Value ( query by reputationid)


    String[] columnTitles = {"Selected", "Feature", "Agent", "Cost", "Time", "Ex.Rep."};

    Object[][] tableModelData =
        fillTableModelData(searchFeatureResult, agents, features, innMindReputations);

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



    JLabel lblNewLabel = new JLabel("List of Available Experts");
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

    buttonAskFeatureSelection = new JButton("Ask");
    GridBagConstraints gbc_btnModifyFeature = new GridBagConstraints();
    gbc_btnModifyFeature.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnModifyFeature.insets = new Insets(5, 5, 5, 5);
    gbc_btnModifyFeature.gridx = 1;
    gbc_btnModifyFeature.gridy = 3;
    add(buttonAskFeatureSelection, gbc_btnModifyFeature);

  }

  /**
   * update the data in the TableModel from the agentFeatureList (ArrayList<StructFeature>)
   */
  public void updateTableModelData() {
    Object[][] tableModelData =
        fillTableModelData(searchFeatureResult, agents, features, innMindReputations);
    tableModel.setData(tableModelData);
  }

  /**
   * fill the TableModel Data with the ArrayList<StructFeature>: Basically does the parsing from the
   * ArrayList<StructFeature> to Object[][] structure needed by TableModel as Data
   * 
   * @param featureRelationAgentList
   * @return the TableModel's Data
   */
  private Object[][] fillTableModelData(ArrayList<FeatureRelationAgent> featureRelationAgentList,
      ArrayList<Agent> agentList, ArrayList<Feature> featureList,
      ArrayList<InnMindReputation> innMindReputationList) {
    int row = featureRelationAgentList.size();
    Object[][] data = new Object[row][8];
    for (int i = 0; i < row; i++) {
      data[i][0] = new Boolean(false);
      // data[i][1] = featureRelationAgentList.get(i).getRelationId();
      data[i][1] = featureList.get(i).getName();
      data[i][2] = agentList.get(i).getName();
      data[i][3] = featureRelationAgentList.get(i).getCost();
      data[i][4] = featureRelationAgentList.get(i).getTime();
      data[i][5] = innMindReputationList.get(i).getValue();
      data[i][6] = featureList.get(i).getFeatureId();
      data[i][7] = featureRelationAgentList.get(i).getDescription();
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

  public Object getRowFeature(int row) {
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

  public Object getRowFeatureId(int row) {
    return tableModel.getValueAt(row, 6);
  }

  public Object getFeatureDescription(int row) {
    return searchFeatureResult.get(row).getDescription();
  }

  /**
   * @return the btnAskFeatureSelection
   */
  public JButton getButtonAskFeatureSelection() {
    return buttonAskFeatureSelection;
  }

  /**
   * @param btnAskFeatureSelection the btnAskFeatureSelection to set
   */
  public void setButtonAskFeatureSelection(JButton btnAskFeatureSelection) {
    buttonAskFeatureSelection = btnAskFeatureSelection;
  }

  /**
   * @return the searchFeatureResult
   */
  public ArrayList<FeatureRelationAgent> getSearchFeatureResult() {
    return searchFeatureResult;
  }

  /**
   * @param searchFeatureResult the searchFeatureResult to set
   */
  public void setSearchFeatureResult(ArrayList<FeatureRelationAgent> searchFeatureResult) {
    this.searchFeatureResult = searchFeatureResult;
  }

  /**
   * @return the features
   */
  public ArrayList<Feature> getFeatures() {
    return features;
  }

  /**
   * @param features the features to set
   */
  public void setFeatures(ArrayList<Feature> features) {
    this.features = features;
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
   * @return the innMindReputations
   */
  public ArrayList<InnMindReputation> getInnMindReputations() {
    return innMindReputations;
  }

  /**
   * @param innMindReputations the innMindReputations to set
   */
  public void setInnMindReputations(ArrayList<InnMindReputation> innMindReputations) {
    this.innMindReputations = innMindReputations;
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


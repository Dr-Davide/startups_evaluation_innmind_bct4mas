package view.panel;

import model.FeatureView;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.ArrayList;

public class SelectLeafFeaturesPanel extends JPanel {
  private static final Logger log = Logger.getLogger(SelectLeafFeaturesPanel.class);

  private static final long serialVersionUID = -4500050339629852514L;
  private ArrayList<FeatureView> agentFeatureList = new ArrayList<>();
  private EditableTableModel tableModel;



  /**
   * Create the panel.
   */
  SelectLeafFeaturesPanel() {

    String[] columnTitles = {"Selected", "Id", "Name", "Description", "Cost", "Time", "InnMindReputation"};

    Object[][] tableModelData = fillTableModelData(agentFeatureList);

    tableModel = new EditableTableModel(columnTitles, tableModelData);


    // LAYOUT AUTO-GENERATED CODE (by WindowBuilderEditor)

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {42, 150, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0};
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);



    JLabel lblNewLabel = new JLabel("List of my Leaf Features");
    lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 12));
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.gridwidth = 5;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);
    JTable table = new JTable(tableModel);
    table.setDefaultRenderer(String.class, centerRenderer);
    table.createDefaultColumnsFromModel();
    JScrollPane scrollPane = new JScrollPane(table);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.gridwidth = 5;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.insets = new Insets(0, 0, 5, 0);
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 1;
    add(scrollPane, gbc_scrollPane);

  }

  /**
   * update the data in the TableModel from the agentFeatureList (ArrayList<StructFeature>)
   */
  public void updateTableModelData() {
    Object[][] tableModelData = fillTableModelData(agentFeatureList);
    tableModel.setData(tableModelData);
  }



  /**
   * fill the TableModel Data with the ArrayList<StructFeature>: Basically does the parsing from the
   * ArrayList<StructFeature> to Object[][] structure needed by TableModel as Data
   *
   * @param featureViewList
   * @return the TableModel's Data
   */
  private Object[][] fillTableModelData(ArrayList<FeatureView> featureViewList) {
    int row = featureViewList.size();
    int leafFeatures = 0;
    // TODO: funziona ma migliorare efficienza
    for (int i = 0; i < row; i++) {
      if (featureViewList.get(i).getFeatureComposition().isEmpty()) {
        leafFeatures++;
      }
    }
    Object[][] data = new Object[leafFeatures][7];
    int j = 0;
    for (int i = 0; i < row; i++) {
      if (featureViewList.get(i).getFeatureComposition().isEmpty()) {
        data[j][0] = new Boolean(false);
        data[j][1] = featureViewList.get(i).getFeatureId();
        data[j][2] = featureViewList.get(i).getName();
        data[j][3] = featureViewList.get(i).getDescription();
        data[j][4] = featureViewList.get(i).getCost();
        data[j][5] = featureViewList.get(i).getTime();
        data[j][6] = featureViewList.get(i).getReputation();
        j++;
      }
    }
    return data;
  }

  /**
   * Return True if at least two records are selected
   * 
   * @return
   */
  public boolean atLeastTwoSelectedInTableModel() {
    boolean atLeastOneSelected = false;
    int selectedCount = 0;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if ((boolean) tableModel.getValueAt(i, 0)) {
        selectedCount++;
      }
    }

    if (selectedCount >= 2) {
      atLeastOneSelected = true;
    }
    return atLeastOneSelected;
  }

  /**
   * Return the selected indexes
   *
   * @return
   */
  public ArrayList<Integer> getRowIndexesSelectedInTableModel() {
    ArrayList<Integer> indexes = new ArrayList<>();
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if ((boolean) tableModel.getValueAt(i, 0)) {
        indexes.add(i);
      }
    }
    return indexes;
  }

  /**
   * Return the FeatureComposition as String if null->"" if JsonArray->"element-1, ... ,element-N"
   *
   * @param indexes
   * @return
   */
  public String getFeatureCompositionString(ArrayList<Integer> indexes) {
    String serviceCompositionString = "";
    for (int i = 0; i < indexes.size(); i++) {
      String id = tableModel.getValueAt(indexes.get(i), 1).toString();
      if (i == 0) {
        serviceCompositionString = serviceCompositionString + id;
      } else {
        serviceCompositionString = serviceCompositionString + "," + id;
      }
    }
    return serviceCompositionString;
  }



  /**
   * @return the agentFeatureList
   */
  public ArrayList<FeatureView> getAgentFeatureList() {
    return agentFeatureList;
  }



  /**
   * @param agentFeatureList the agentFeatureList to set
   */
  public void setAgentFeatureList(ArrayList<FeatureView> agentFeatureList) {
    this.agentFeatureList = agentFeatureList;
  }



}

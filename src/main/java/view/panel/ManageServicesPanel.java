package view.panel;

import model.ServiceView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class ManageServicesPanel extends JPanel {

  private static final long serialVersionUID = -4093410100114329247L;
  private JButton btnDeleteSelection;
  private JButton btnModifyService;
  private ArrayList<ServiceView> agentServiceList = new ArrayList<>();
  private EditableTableModel tableModel;



  /**
   * Create the panel.
   */
  ManageServicesPanel() {

    String[] columnTitles =
        {"Selected", "Name", "Description", "Cost", "Time", "Reputation", "Composition"};

    Object[][] tableModelData = fillTableModelData(agentServiceList);

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



    JLabel lblNewLabel = new JLabel("List of my Services");
    lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 16));
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

    btnModifyService = new JButton("Modify");
    btnModifyService.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {}
    });
    GridBagConstraints gbc_btnModifyService = new GridBagConstraints();
    gbc_btnModifyService.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnModifyService.insets = new Insets(5, 5, 5, 5);
    gbc_btnModifyService.gridx = 1;
    gbc_btnModifyService.gridy = 3;
    add(btnModifyService, gbc_btnModifyService);

    btnDeleteSelection = new JButton("Delete");
    GridBagConstraints gbc_btnDeleteSelection = new GridBagConstraints();
    gbc_btnDeleteSelection.fill = GridBagConstraints.BOTH;
    gbc_btnDeleteSelection.insets = new Insets(5, 5, 5, 5);
    gbc_btnDeleteSelection.gridx = 3;
    gbc_btnDeleteSelection.gridy = 3;
    add(btnDeleteSelection, gbc_btnDeleteSelection);

  }

  /**
   * update the data in the TableModel from the agentServiceList (ArrayList<StructService>)
   */
  public void updateTableModelData() {
    Object[][] tableModelData = fillTableModelData(agentServiceList);
    tableModel.setData(tableModelData);
  }



  /**
   * fill the TableModel Data with the ArrayList<StructService>: Basically does the parsing from the
   * ArrayList<StructService> to Object[][] structure needed by TableModel as Data
   *
   * @param serviceViewList
   * @return the TableModel's Data
   */
  private Object[][] fillTableModelData(ArrayList<ServiceView> serviceViewList) {
    int row = serviceViewList.size();
    Object[][] data = new Object[row][8];
    for (int i = 0; i < row; i++) {
      data[i][0] = new Boolean(false);
      data[i][1] = serviceViewList.get(i).getName();
      data[i][2] = serviceViewList.get(i).getDescription();
      data[i][3] = serviceViewList.get(i).getCost();
      data[i][4] = serviceViewList.get(i).getTime();
      data[i][5] = serviceViewList.get(i).getReputation();
      data[i][6] = serviceViewList.get(i).getServiceComposition();
      data[i][7] = serviceViewList.get(i).getServiceId();
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
   * Return True if only one record is selected
   * 
   * @return
   */
  public boolean onlyOneSelectedInTableModel() {
    boolean onlyOneSelected = false;
    int selectedCount = 0;
    for (int i = 0; i < tableModel.getRowCount(); i++) {
      if ((boolean) tableModel.getValueAt(i, 0)) {
        selectedCount++;
      }
    }

    if (selectedCount == 1) {
      onlyOneSelected = true;
    }
    return onlyOneSelected;
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
   * Return the ServiceComposition as String if null->"" if JsonArray->"element-1, ... ,element-N"
   *
   * @param indexes
   * @return
   */
  public String getServiceId(ArrayList<Integer> indexes) {
    String serviceId = "";
    for (int i = 0; i < indexes.size(); i++) {
      serviceId = tableModel.getValueAt(indexes.get(i), 7).toString();
    }
    return serviceId;
  }


  /**
   * @return the btnDeleteSelection
   */
  public JButton getBtnDeleteSelection() {
    return btnDeleteSelection;
  }



  /**
   * @param btnDeleteSelection the btnDeleteSelection to set
   */
  public void setBtnDeleteSelection(JButton btnDeleteSelection) {
    this.btnDeleteSelection = btnDeleteSelection;
  }



  /**
   * @return the btnModifyService
   */
  public JButton getBtnModifyService() {
    return btnModifyService;
  }



  /**
   * @param btnModifyService the btnModifyService to set
   */
  public void setBtnModifyService(JButton btnModifyService) {
    this.btnModifyService = btnModifyService;
  }



  /**
   * @return the agentServiceList
   */
  public ArrayList<ServiceView> getAgentServiceList() {
    return agentServiceList;
  }



  /**
   * @param agentServiceList the agentServiceList to set
   */
  public void setAgentServiceList(ArrayList<ServiceView> agentServiceList) {
    this.agentServiceList = agentServiceList;
  }



}


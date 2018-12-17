package view.panel;

import model.ServiceView;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.ArrayList;

public class ManageAgentPanel extends JPanel {

  private static final long serialVersionUID = 8121092562779998102L;
  private JButton buttonUpAgent = null;
  private JButton btnEnableAgent;
  private JButton buttonDownAgent = null;
  private JButton btnEliminateAgent;
  private JList<String> agentList = null;
  private ArrayList<ServiceView> dataEntries = new ArrayList<>();
  private Object[][] data;

  /**
   * Create the panel.
   */
  public ManageAgentPanel() {

    agentList = getAgentList();

    String[] columnTitles = {"Selected", "AgentID", "Name", "State", "eCert Expire"};
    Object[][] dataEntries = {{new Boolean(false), "AGT00001", "agent1", "UP", "2018/12/25"},
        {new Boolean(false), "AGT00002", "agent2", "UP", "2019/1/6"},
        {new Boolean(false), "AGT00003", "agent3", "DOWN", "2018/10/2"},};
    //// PROVA DINAMICO (REMEMBER: JAVA È UNA MERDA)
    //
    // StructService s1 = new StructService("123412", "service1", "descr 1", "3", "6", "8");
    // ArrayList<String> st1 = new ArrayList<String>();
    // st1.add(s1.getServiceId());
    // st1.add(s1.getName());
    // // dataEntries.add(st1);
    // StructService s2 = new StructService("123672", "service2", "descr 2", "1", "3", "5");
    // ArrayList<String> st2 = new ArrayList<String>();
    // st2.add(s2.getServiceId());
    // st2.add(s2.getName());
    // // dataEntries.add(st2);
    // ArrayList<StructService> services = new ArrayList<StructService>();
    // services.add(s1);
    // services.add(s2);
    //
    // int row = services.size();
    // Object[][] data = new Object[row][6];
    // for (int i = 0; i < row; i++) {
    // data[i][0] = new Boolean(false);
    // data[i][1] = services.get(i).getName();
    // data[i][1] = services.get(i).getDescription();
    // data[i][1] = services.get(i).getCost();
    // data[i][1] = services.get(i).getTime();
    // data[i][1] = services.get(i).getReputation();
    // }

    TableModel tableModel = new EditableTableModel(columnTitles, dataEntries);
    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 160, 0, 0, 0, 114, -2};
    gridBagLayout.rowHeights = new int[] {30, 100, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0};
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
    JTable table = new JTable(tableModel);
    table.setDefaultRenderer(String.class, centerRenderer);
    table.createDefaultColumnsFromModel();


    // table.getColumn(2).setCellRenderer(centerRenderer);

    JLabel lblNewLabel = new JLabel("List of Agents");
    lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 16));
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.gridwidth = 5;
    gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
    gbc_lblNewLabel.gridx = 1;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);
    JScrollPane scrollPane = new JScrollPane(table);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.gridwidth = 5;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
    gbc_scrollPane.gridx = 1;
    gbc_scrollPane.gridy = 1;
    add(scrollPane, gbc_scrollPane);

    JButton btnDisableAgent = new JButton("Disable Agent");
    GridBagConstraints gbc_btnDisableAgent = new GridBagConstraints();
    gbc_btnDisableAgent.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnDisableAgent.insets = new Insets(0, 0, 0, 5);
    gbc_btnDisableAgent.gridx = 1;
    gbc_btnDisableAgent.gridy = 3;
    add(btnDisableAgent, gbc_btnDisableAgent);

    btnEnableAgent = new JButton("Enable Agent");
    GridBagConstraints gbc_btnEnableAgent = new GridBagConstraints();
    gbc_btnEnableAgent.fill = GridBagConstraints.BOTH;
    gbc_btnEnableAgent.insets = new Insets(0, 0, 0, 5);
    gbc_btnEnableAgent.gridx = 3;
    gbc_btnEnableAgent.gridy = 3;
    add(btnEnableAgent, gbc_btnEnableAgent);

    btnEliminateAgent = new JButton("Eliminate Agent");
    GridBagConstraints gbc_btnEliminateAgent = new GridBagConstraints();
    gbc_btnEliminateAgent.insets = new Insets(0, 0, 0, 5);
    gbc_btnEliminateAgent.fill = GridBagConstraints.BOTH;
    gbc_btnEliminateAgent.gridx = 5;
    gbc_btnEliminateAgent.gridy = 3;
    add(btnEliminateAgent, gbc_btnEliminateAgent);

  }

  private JList<String> getAgentList() {
    String[] items = {"A", "B", "C", "D"};
    JList list = new JList(items);

    int start = 0;
    int end = list.getModel().getSize() - 1;
    if (end >= 0) {
      list.setSelectionInterval(start, end);
    }
    return list;
  }

  /**
   * @return the buttonUpAgent
   */
  public JButton getButtonUpAgent() {
    return btnEnableAgent;
  }

  /**
   * @param buttonUpAgent the buttonUpAgent to set
   */
  public void setButtonUpAgent(JButton buttonUpAgent) {
    btnEnableAgent = buttonUpAgent;
  }

  /**
   * @return the buttonDownAgent
   */
  public JButton getButtonDownAgent() {
    return btnEliminateAgent;
  }

  /**
   * @param buttonDownAgent the buttonDownAgent to set
   */
  public void setButtonDownAgent(JButton buttonDownAgent) {
    btnEliminateAgent = buttonDownAgent;
  }

  /**
   * @param agentList the agentList to set
   */
  public void setAgentList(JList<String> agentList) {
    this.agentList = agentList;
  }

}

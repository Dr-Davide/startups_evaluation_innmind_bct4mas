package view.panel;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InBoxMessagesPanel extends JPanel {

  private static final long serialVersionUID = 5025176373966197605L;
  private JButton btnDenySelection;
  private JButton btnAcceptSelection;
  private JButton btnReplyMsg;
  private JButton btnDeleteSelection;
  private EditableTableModel tableModel;


  /**
   * Create the panel.
   */
  InBoxMessagesPanel() {

    String[] columnTitles = {"Selected", "Sender", "MSG Type", "Subject", "Body", "Time"};
    Object[][] dataEntries = {};
    tableModel = new EditableTableModel(columnTitles, dataEntries);


    // LAYOUT AUTO-GENERATED CODE (by WindowBuilderEditor)

    GridBagLayout gridBagLayout = new GridBagLayout();
    gridBagLayout.columnWidths = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
    gridBagLayout.rowHeights = new int[] {42, 150, 0, 0, 0, 0};
    gridBagLayout.columnWeights = new double[] {1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0, 0.0, 1.0};
    gridBagLayout.rowWeights = new double[] {0.0, 1.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
    setLayout(gridBagLayout);
    DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
    centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);



    JLabel lblNewLabel = new JLabel("InBox");
    lblNewLabel.setFont(new Font("Dialog", Font.BOLD, 16));
    GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
    gbc_lblNewLabel.gridwidth = 9;
    gbc_lblNewLabel.insets = new Insets(5, 0, 5, 0);
    gbc_lblNewLabel.gridx = 0;
    gbc_lblNewLabel.gridy = 0;
    add(lblNewLabel, gbc_lblNewLabel);
    JTable table = new JTable(tableModel);
    table.setDefaultRenderer(String.class, centerRenderer);
    table.createDefaultColumnsFromModel();
    JScrollPane scrollPane = new JScrollPane(table);
    GridBagConstraints gbc_scrollPane = new GridBagConstraints();
    gbc_scrollPane.gridwidth = 9;
    gbc_scrollPane.fill = GridBagConstraints.BOTH;
    gbc_scrollPane.insets = new Insets(0, 5, 0, 5);
    gbc_scrollPane.gridx = 0;
    gbc_scrollPane.gridy = 1;
    add(scrollPane, gbc_scrollPane);

    btnReplyMsg = new JButton("Reply");
    btnReplyMsg.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {}
    });

    btnDenySelection = new JButton("Deny");
    GridBagConstraints gbc_btnDeny = new GridBagConstraints();
    gbc_btnDeny.insets = new Insets(5, 5, 5, 5);
    gbc_btnDeny.gridx = 1;
    gbc_btnDeny.gridy = 3;
    add(btnDenySelection, gbc_btnDeny);

    btnAcceptSelection = new JButton("Accept");
    btnAcceptSelection.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {}
    });

    GridBagConstraints gbc_btnAccept = new GridBagConstraints();
    gbc_btnAccept.insets = new Insets(5, 5, 5, 5);
    gbc_btnAccept.gridx = 3;
    gbc_btnAccept.gridy = 3;
    add(btnAcceptSelection, gbc_btnAccept);

    GridBagConstraints gbc_btnReply = new GridBagConstraints();
    gbc_btnReply.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnReply.insets = new Insets(5, 5, 5, 5);
    gbc_btnReply.gridx = 5;
    gbc_btnReply.gridy = 3;
    add(btnReplyMsg, gbc_btnReply);

    btnDeleteSelection = new JButton("Delete");
    GridBagConstraints gbc_btnDelete = new GridBagConstraints();
    gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
    gbc_btnDelete.insets = new Insets(5, 5, 5, 5);
    gbc_btnDelete.gridx = 7;
    gbc_btnDelete.gridy = 3;
    add(btnDeleteSelection, gbc_btnDelete);

  }


  /**
   * Add the message in the InBoxMessages
   * 
   * @throws UnreadableException
   */
  public void addMessageInTableModel(ACLMessage message) throws UnreadableException {
    int columnCount = tableModel.getColumnCount();
    Object[][] oldTableModelData = tableModel.getDataEntries();
    Object[][] newTableModelData =
        addNewMessageInTableModelData(message, oldTableModelData, columnCount);
    tableModel.setData(newTableModelData);
  }

  /**
   * Delete the message in the InBoxMessages
   * 
   * @throws UnreadableException
   */
  public void deleteMessageInTableModel(Integer deletingMessageIndex) throws UnreadableException {
    int columnCount = tableModel.getColumnCount();
    Object[][] oldTableModelData = tableModel.getDataEntries();
    Object[][] newTableModelData =
        deleteMessageInTableModelData(deletingMessageIndex, oldTableModelData, columnCount);
    tableModel.setData(newTableModelData);
  }

  /**
   * Add the new arriving message in the tail of the queue of the inbox messages
   * 
   * @param message
   * @param oldTableModelData
   * @param columnCount
   * @return
   * @throws UnreadableException
   */
  private Object[][] addNewMessageInTableModelData(ACLMessage message, Object[][] oldTableModelData,
      int columnCount) throws UnreadableException {

    String[] messageContentArray = parseMessageContent(message.getContent());


    int oldRowCount = oldTableModelData.length;
    int newRowCount = oldRowCount + 1;
    Object[][] newTableModelData = new Object[newRowCount][columnCount];

    // fill with old value
    for (int i = 0; i < oldRowCount; i++) {
      newTableModelData[i][0] = new Boolean(false);
      newTableModelData[i][1] = oldTableModelData[i][1];
      newTableModelData[i][2] = oldTableModelData[i][2];
      newTableModelData[i][3] = oldTableModelData[i][3];
      newTableModelData[i][4] = oldTableModelData[i][4];
      newTableModelData[i][5] = oldTableModelData[i][5];
    }

    // add the new value in the tail
    newTableModelData[oldRowCount][0] = new Boolean(false); // Selected
    newTableModelData[oldRowCount][1] = message.getSender().getLocalName(); // Agent Name
    newTableModelData[oldRowCount][2] = messageContentArray[0]; // Message Type
    newTableModelData[oldRowCount][3] = messageContentArray[1]; // Message Object
    newTableModelData[oldRowCount][4] = messageContentArray[2]; // Message Body
    newTableModelData[oldRowCount][5] = messageContentArray[3]; // Timestamp

    return newTableModelData;
  }

  /**
   * Delete the executed Message from the table
   * 
   * @param message
   * @param oldTableModelData
   * @param columnCount
   * @return
   * @throws UnreadableException
   */
  private Object[][] deleteMessageInTableModelData(Integer executedMessageIndex,
      Object[][] oldTableModelData, int columnCount) throws UnreadableException {

    boolean ignoredDeletingMessageRow = false;
    int oldRowCount = oldTableModelData.length;
    int newRowCount = oldRowCount - 1;
    Object[][] newTableModelData = new Object[newRowCount][columnCount];

    // fill with old value
    for (int i = 0; i < oldRowCount; i++) {
      if (executedMessageIndex.equals(i)) {
        ignoredDeletingMessageRow = true;
      } else {
        if (ignoredDeletingMessageRow) {
          newTableModelData[i - 1][0] = new Boolean(false);
          newTableModelData[i - 1][1] = oldTableModelData[i][1];
          newTableModelData[i - 1][2] = oldTableModelData[i][2];
          newTableModelData[i - 1][3] = oldTableModelData[i][3];
          newTableModelData[i - 1][4] = oldTableModelData[i][4];
          newTableModelData[i - 1][5] = oldTableModelData[i][5];
        } else {
          newTableModelData[i][0] = new Boolean(false);
          newTableModelData[i][1] = oldTableModelData[i][1];
          newTableModelData[i][2] = oldTableModelData[i][2];
          newTableModelData[i][3] = oldTableModelData[i][3];
          newTableModelData[i][4] = oldTableModelData[i][4];
          newTableModelData[i][5] = oldTableModelData[i][5];
        }
      }
    }
    return newTableModelData;
  }


  // TODO: Refactoring generalizzazione classe con questi metodi
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


  // TODO: Refactoring generalizzazione classe con questi metodi
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

  public Object getRowAgentName(int row) {
    return tableModel.getValueAt(row, 1);
  }

  public Object getRowMessageType(int row) {
    return tableModel.getValueAt(row, 2);
  }

  public Object getRowMessageObject(int row) {
    return tableModel.getValueAt(row, 3);
  }

  public Object getRowMessageBody(int row) {
    return tableModel.getValueAt(row, 4);
  }

  public Object getRowTimestamp(int row) {
    return tableModel.getValueAt(row, 5);
  }

  /**
   * parse[0] -> messageType parse[1] -> messageObject parse[2] -> messageBody parse[3] ->
   * requestTimestamp
   * 
   * @param messageContent
   * @return
   */
  private String[] parseMessageContent(String messageContent) {
    String[] splittedContent = messageContent.split("%");
    return splittedContent;
  }


  /**
   * @return the btnDenySelection
   */
  public JButton getBtnDenySelection() {
    return btnDenySelection;
  }


  /**
   * @param btnDenySelection the btnDenySelection to set
   */
  public void setBtnDenySelection(JButton btnDenySelection) {
    this.btnDenySelection = btnDenySelection;
  }


  /**
   * @return the btnAcceptSelection
   */
  public JButton getBtnAcceptSelection() {
    return btnAcceptSelection;
  }


  /**
   * @param btnAcceptSelection the btnAcceptSelection to set
   */
  public void setBtnAcceptSelection(JButton btnAcceptSelection) {
    this.btnAcceptSelection = btnAcceptSelection;
  }


  /**
   * @return the btnReplyMsg
   */
  public JButton getBtnReplyMsg() {
    return btnReplyMsg;
  }


  /**
   * @param btnReplyMsg the btnReplyMsg to set
   */
  public void setBtnReplyMsg(JButton btnReplyMsg) {
    this.btnReplyMsg = btnReplyMsg;
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

}


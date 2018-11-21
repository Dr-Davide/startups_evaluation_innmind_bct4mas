package view.panel;

import javax.swing.table.AbstractTableModel;

public class EditableTableModel extends AbstractTableModel {

  private static final long serialVersionUID = -2751681806580088357L;
  private String[] columnTitles;

  // TODO: Refactor in ArrayList
  // ArrayList<T> dataEntries = new ArrayList<T>();
  private Object[][] dataEntries;

  int rowCount;

  EditableTableModel(String[] columnTitles, Object[][] dataEntries) {
    this.columnTitles = columnTitles;
    this.dataEntries = dataEntries;
  }

  /**
   * This method receives the new data vector, and update the table
   * 
   * @param data with new values
   */
  public void setData(Object[][] data) {
    dataEntries = data; // Save data..
    fireTableDataChanged(); // Update table
  }

  @Override
  public int getRowCount() {
    return dataEntries.length;
  }

  @Override
  public int getColumnCount() {
    return columnTitles.length;
  }

  @Override
  public Object getValueAt(int row, int column) {
    return dataEntries[row][column];
  }

  @Override
  public String getColumnName(int column) {
    return columnTitles[column];
  }

  @Override
  public Class<?> getColumnClass(int column) {
    return getValueAt(0, column).getClass();
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    switch (column) {
      case 0:
        return true;
      default:
        return false;
    }
  }

  @Override
  public void setValueAt(Object value, int row, int column) {
    // TODO: FIX
    dataEntries[row][column] = value;
  }

  /**
   * @return the columnTitles
   */
  public String[] getColumnTitles() {
    return columnTitles;
  }

  /**
   * @param columnTitles the columnTitles to set
   */
  public void setColumnTitles(String[] columnTitles) {
    this.columnTitles = columnTitles;
  }

  /**
   * @return the dataEntries
   */
  Object[][] getDataEntries() {
    return dataEntries;
  }

  /**
   * @param dataEntries the dataEntries to set
   */
  public void setDataEntries(Object[][] dataEntries) {
    this.dataEntries = dataEntries;
  }
}

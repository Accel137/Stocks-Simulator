package view.gui;

import java.util.Vector;
import javax.swing.table.DefaultTableModel;

/**
 * Modified Table Model.
 */
class StockTableModel extends DefaultTableModel {

  boolean isEditable;

  private StockTableModel(Vector<String> col, boolean isEditable) {
    super(null, col);
    this.isEditable = isEditable;
  }

  @Override
  public boolean isCellEditable(int row, int column) {
    if (isEditable) {
      return super.isCellEditable(row, column);
    } else {
      return false;
    }
  }

  public static StockTableModel updateData(
      Vector<Vector<Object>> data,
      Vector<String> col,
      boolean isEditable
  ) {
    StockTableModel stockTableModel = new StockTableModel(col, isEditable);
    stockTableModel.setDataVector(data, col);
    return stockTableModel;
  }
}

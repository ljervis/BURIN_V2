package rootPackage;

import java.awt.Color;
import java.awt.Component;
import java.util.Arrays;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	
	final String partCol = "Part Number";
	final String inStockCol = "Qty In Stock";
	final String totalCol = "Total Qty Needed";
	final String remainingCol = "Qty Remaining";
	String[] permCols;
	
	public CustomTableCellRenderer() {
		super();
		setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		permCols = new String[] {partCol, inStockCol, totalCol, remainingCol}; 
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
		Object valueAt = table.getModel().getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
		setOpaque(true);
		cell.setBackground(null);
		if (valueAt != null) {
			int val = Integer.parseInt(valueAt.toString());
	        if(val < 0) {
	        	cell.setBackground(Color.orange);
	        }
	 		else if(table.convertColumnIndexToModel(column) > 1 && table.convertColumnIndexToModel(column) < (table.getColumnCount()-2)) {
	 			if(val != 0) {
	 				cell.setBackground(Color.lightGray);
	 			}
	 		}
	    }
		return cell;
	}

}

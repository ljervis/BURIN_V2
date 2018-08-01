package rootPackage;

import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Luke <a href="mailto:lukejervis14@gmail.com">lukejervis14@gmail.com</a>
 *
 */
public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Creates a cell renderer to be used for table cell rendering. 
	 * @see DataTable
	 */
	public CustomTableCellRenderer() {
		super();
		setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
	}
	
	/**
	 * {@inheritDoc}
	 * Retrieves the cell to be rendered and value of that cell by converting indices from view 
	 * coordinates to the underlying model. This allows for manipulation of the table by the 
	 * user without upsetting the cells rendering. 
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
		Object valueAt = table.getModel().getValueAt(table.convertRowIndexToModel(row), table.convertColumnIndexToModel(column));
		setOpaque(true);
		
		setCellBackground(table, valueAt, isSelected, cell, column);
		
		return cell;
	}
	
	/**
	 * Handles the background colors for the tables cell renderer
	 * @param table The JTable being rendered
	 * @param valueAt The value (Integer) contained at a cell
	 * @param isSelected True if the cell has been selected by the user
	 * @param cell The cell that is being rendered 
	 * @param column The column containing the cell in the view coordinates
	 */
	public void setCellBackground(JTable table, Object valueAt, boolean isSelected, Component cell, int column) {
		cell.setBackground(null);
		if (valueAt != null) {
			int val = Integer.parseInt(valueAt.toString());	// Convert the cells value to an int (all values in the table are originally type Integer)
	        if(val < 0) {
	        	cell.setBackground(Color.orange);	// Only deficit values will be negative and given an orange background
	        }
	        //	This sets cells in the columns containing work order quantity numbers that are positive to light grey 
	 		else if(table.convertColumnIndexToModel(column) > 1 && table.convertColumnIndexToModel(column) < (table.getColumnCount()-2)) {
	 			if(val != 0) {
	 				cell.setBackground(Color.lightGray); 
	 			}
	 		}
	        if(isSelected) {
	        	cell.setBackground(SystemColor.inactiveCaption);
	        }
	    }
	}

}

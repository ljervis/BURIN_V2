package rootPackage;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class CustomTableCellRenderer extends DefaultTableCellRenderer {
	
	public CustomTableCellRenderer() {
		super();
		setHorizontalAlignment(DefaultTableCellRenderer.LEFT);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		Integer val = (Integer)value;
		if(value == null) {
			value = "";
			return cell;
		}
		if(val.intValue() < 0) {
			cell.setBackground(Color.red);
		}
		else if(!table.getColumnName(column).equals("Part Number")) {
			if(val.intValue() != 0) {
				cell.setBackground(Color.yellow);
			}
		}
		else {
			cell.setBackground(null);
		}
		return cell;
	}

}

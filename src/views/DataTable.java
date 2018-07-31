package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rootPackage.CustomTableCellRenderer;
import rootPackage.InventoryWorkbook;
import rootPackage.Pair;
import rootPackage.WorkOrderWorkbook;

public class DataTable {
	
	// Swing components
	JScrollPane tableScrollPane;
	JTable table;
	DefaultTableModel tableModel;
	Set<Integer> currentPartList;
	CustomTableCellRenderer renderer;
	
	private TableRowSorter<TableModel> sorter;
	InventoryWorkbook invWB;
	
	/**
	 * Constructor to create and add the table in a scroll pane to the window. 
	 * The table is not populated with a table model until the user adds a work order 
	 * 
	 */
	public DataTable(InventoryWorkbook inv) {
		
		invWB = inv;
		
		table = new JTable();
//		table.setGridColor(SystemColor.text);
		table.setRowHeight(25);
		table.setFont(new Font("Times New Roman", Font.PLAIN, 25));
		
		
		Border panelBorder = BorderFactory.createCompoundBorder(BorderFactory.createRaisedBevelBorder(), BorderFactory.createLoweredBevelBorder());
		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setBorder(panelBorder);
	}
	
	public JScrollPane getTable() {
		return tableScrollPane;
	}
	
	/**
	 * This is executed each time a work order is added or removed from the work order list. 
	 * Scans all the work orders in the work order list for parts to display and populates the table by comparing quantities with the inventory work book
	 */
	public void refreshTable(ArrayList<WorkOrderWorkbook> workOrderWBList) {
		
		currentPartList = new TreeSet<Integer>();	// Reset the part list
		Vector<String> columnNames = new Vector<String>();

		columnNames.add("Part Number"); 
		columnNames.add("Qty In Stock");
		for(WorkOrderWorkbook n : workOrderWBList) { 
			columnNames.add(n.getMultiplicity() + " x " + n.getWorkbookName());	// Add each workbook that is in the work book list as a column in the table
			currentPartList.addAll(n.getPartList().keySet());	// Add all parts in each workbook to the part list set from their hash map
		}
		columnNames.add("Total Qty Needed"); 
		columnNames.add("Qty Remaining");
		
		tableModel = new DefaultTableModel(populateTableData(workOrderWBList), columnNames) {
			@Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
			
			// Allows the table columns to be sorted correctly by returning Integers
			// This will need to be changed if the table is to support any other objects 
			@Override
			public Class<?> getColumnClass(int column){
				return Integer.class;
			}
		};
		table.setModel(tableModel);
		JTableHeader tableHeader = table.getTableHeader();
//		tableHeader.setReorderingAllowed(false);
		tableHeader.setBackground(SystemColor.inactiveCaption);
		tableHeader.setFont(new Font("Times New Roman", Font.BOLD, 24));
		tableHeader.setForeground(new Color(199, 21, 133));
		
		sorter = new TableRowSorter<TableModel>(tableModel);
		renderer = new CustomTableCellRenderer();
		table.setDefaultRenderer(Integer.class, renderer);
		table.setRowSorter(sorter);
	}
	
	/**
	 * Use the currentPartList and scan the inventory work book and each work order in the work 
	 * order list to populate the data that will be added to the table. 
	 * 
	 * @return a vector of vectors that will be used in the table model
	 */
	public Vector<Vector<Integer>> populateTableData(ArrayList<WorkOrderWorkbook> workOrderWBList){
		Vector<Vector<Integer>> tableData = new Vector<Vector<Integer>>();
		String missingParts = "";
		// Create a row for each part in the part list set
		for(Integer i : currentPartList) {
			Vector<Integer> row = new Vector<Integer>();
			row.add(i);
			Pair invPair = invWB.getPartList().get(i);
					
			if(invPair != null) { 
				Integer invQty = invPair.first;
				row.add(invQty); 
			} 
			else { 
				missingParts += i.toString() + ", ";
//				System.out.println(i.toString() + " not found in inventory workbook");
				continue;	// If the part was not found in the inventory workbook this row will not be added to the table
			}
					
			int totalQtyNeeded = 0;
					
			for(WorkOrderWorkbook n : workOrderWBList) {
				Pair part = n.getPartList().get(i);
				if(part != null) { 
					row.add(part.first);	// Add the number of parts needed for each work order from their hash maps 
					totalQtyNeeded += part.first.intValue();
				}
				else { row.add(new Integer(0)); }	// Add 0 parts if the workbooks hash map does not contain the part number 
			}
			row.add(new Integer(totalQtyNeeded));	// Add the total number of parts needed across all work orders
			row.add(new Integer(row.get(1).intValue() - totalQtyNeeded)); // Add the remaining parts in inventory after all parts needed have been removed
			tableData.add(row);
		}
		if(!missingParts.equals("")) {
			missingParts = "The following parts were not found in the inventory workbook and will not be shown:\n" + missingParts;
			JOptionPane.showMessageDialog(new JFrame(), missingParts.substring(0, missingParts.lastIndexOf(",")), "Warning", JOptionPane.WARNING_MESSAGE);
		}
		return tableData;
	}
	
	/**
	 * Adds a row filter to the table so that only parts with a stock qty < 0 are shown 
	 */
	public void filterInStock() {
		if(tableModel == null) {return;} // table model has yet to be set up
		RowFilter<TableModel, Object> rf = null;
		try {
			rf = RowFilter.numberFilter(ComparisonType.AFTER, -1, tableModel.getColumnCount()-1);
		}catch(Exception e) {
			e.printStackTrace();
		}
		sorter.setRowFilter(rf);
	}
	
	/**
	 * Adds a row filter to the table so that only parts with a stock qty > -1 are shown 
	 */
	public void filterOutOfStock() {
		if(tableModel == null) {return;} // table model has yet to be set up
		RowFilter<TableModel, Object> rf = null;
		try {
			rf = RowFilter.numberFilter(ComparisonType.BEFORE, 0, tableModel.getColumnCount()-1);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(sorter != null) {
			sorter.setRowFilter(rf);
		}
	}
	
	/**
	 * Removes the current row filter to show all values in the table model 
	 */
	public void removeRowFilter() {
		if(tableModel == null) {return;} // table model has yet to be set up
		Font boldItalicFont = new Font("Serif", Font.BOLD, 26);
		if(sorter != null) {
			sorter.setRowFilter(null);
		}
	}
	
	public DefaultTableModel getTableModel() {
		return tableModel;
	}
	
	/**
	 * Updates the inventory workbook with the current tables values. Negative quantities are shows as 0 in the workbook. 
	 * Will only update if there are parts in the table model
	 */
	public void updateInventoryWorkbook() {
		if(tableModel == null) {
			JOptionPane.showMessageDialog(new JFrame(), "Please add a work order before updating the inventory work book!", "Warning", JOptionPane.WARNING_MESSAGE);
		}
		else {
			ArrayList<Pair> inventoryUpdates = new ArrayList<Pair>();
			int rowCount = tableModel.getRowCount();
			int colCount = tableModel.getColumnCount();
			for(int x = 0; x < rowCount; x++) {
				Integer part = (Integer)tableModel.getValueAt(x, 0);
				Integer qty = (Integer)tableModel.getValueAt(x, colCount-1);
				Integer qtyAdj = qty.intValue() >= 0 ? qty : new Integer(0);	// If the qty is negative then add a 0 
				Pair i = new Pair(part, qtyAdj);
				inventoryUpdates.add(i);
			}
			
			if(inventoryUpdates.size() > 0) {
				invWB.updateInventoryWorkbook(inventoryUpdates);
			}
		}
	}
}

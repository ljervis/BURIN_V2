package views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowFilter;
import javax.swing.RowFilter.ComparisonType;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import rootPackage.InventoryWorkbook;
import rootPackage.WorkOrderWorkbook;

public class DataTable {
	
	// Swing components
	JScrollPane tableScrollPane;
	JTable table;
	DefaultTableModel tableModel;
	Set<Integer> currentPartList;
	
	private TableRowSorter<TableModel> sorter;
	InventoryWorkbook invWB;
	
	/**
	 * Constructor to create and add the table in a scroll pane with default column names to the window. 
	 * The table is not editable by the user but rows can be selected. 
	 * 
	 */
	public DataTable(InventoryWorkbook inv) {
		
		invWB = inv;
		
		String[] columnNames = new String[] {"Part Number","Qty In Stock", "Total Qty Needed", "Qty Remaining"};	// The default column names
		
		tableModel = new DefaultTableModel(columnNames, 0) {
		    @Override
		    public boolean isCellEditable(int row, int column) {
		       //all cells false
		       return false;
		    }
		};
		
		table = new JTable(tableModel);
		table.setBackground(SystemColor.activeCaptionBorder);
		table.setFillsViewportHeight(true);
		
		tableScrollPane = new JScrollPane(table);
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
		};
		table.setModel(tableModel);
		sorter = new TableRowSorter<TableModel>(tableModel);
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
		
		// Create a row for each part in the part list set
		for(Integer i : currentPartList) {
			Vector<Integer> row = new Vector<Integer>();
			row.add(i);
			Integer invQty = invWB.getPartList().get(i);
					
			if(invQty != null) { 
				row.add(invWB.getPartList().get(i)); 
			} 
			else { 
				System.out.println(i.toString() + " not found in inventory workbook");
				continue; 
			}
					
			int totalQtyNeeded = 0;
					
			for(WorkOrderWorkbook n : workOrderWBList) {
				Integer part = n.getPartList().get(i);
				if(part != null) { 
					row.add(part);	// Add the number of parts needed for each work order from their hash maps 
					totalQtyNeeded += part.intValue();
				}
				else { row.add(new Integer(0)); }	// Add 0 parts if the workbooks hash map does not contain the part number 
			}
			row.add(new Integer(totalQtyNeeded));	// Add the total number of parts needed across all work orders
			row.add(new Integer(row.get(1).intValue() - totalQtyNeeded)); // Add the remaining parts in inventory after all parts needed have been removed
			tableData.add(row);
		}
		return tableData;
	}
	
	/**
	 * Adds a row filter to the table so that only parts with a stock qty < 0 are shown 
	 */
	public void filterInStock() {
		RowFilter<TableModel, Object> rf = null;
		try {
			rf = RowFilter.numberFilter(ComparisonType.AFTER, -1, tableModel.getColumnCount()-1);
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(sorter != null) {
			sorter.setRowFilter(rf);
		}
	}
	
	/**
	 * Adds a row filter to the table so that only parts with a stock qty > -1 are shown 
	 */
	public void filterOutOfStock() {
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
		if(sorter != null) {
			sorter.setRowFilter(null);
		}
	}

}

package views;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.SystemColor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
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
import javax.swing.UIManager;

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
		table.setBackground(SystemColor.activeCaption);
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
			
			// Allows the table columns to be sorted correctly by returning Integers 
			@Override
			public Class<?> getColumnClass(int column){
	              Class<?> returnValue;
	              if ((column >= 0) && (column < getColumnCount())) 
	              {
	                  returnValue = getValueAt(0, column).getClass();
	              } 
	              else 
	              {
	                 returnValue = Object.class;
	              }

	              return returnValue;
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
		String missingParts = "";
		// Create a row for each part in the part list set
		for(Integer i : currentPartList) {
			Vector<Integer> row = new Vector<Integer>();
			row.add(i);
			Integer invQty = invWB.getPartList().get(i);
					
			if(invQty != null) { 
				row.add(invWB.getPartList().get(i)); 
			} 
			else { 
				missingParts += i.toString() + ", ";
				System.out.println(i.toString() + " not found in inventory workbook");
				continue;	// If the part was not found in the inventory workbook this row will not be added to the table
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
	
	/**
	 * Updates the inventory workbook with the current tables values. Negative quantities are shows as 0 in the workbook
	 */
	public void updateInventoryWorkbook() {
		ArrayList<InventoryContainer> inventoryUpdates = new ArrayList<InventoryContainer>();
		int rowCount = tableModel.getRowCount();
		int colCount = tableModel.getColumnCount();
		for(int x = 0; x < rowCount; x++) {
			Integer part = (Integer)tableModel.getValueAt(x, 0);
			Integer qty = (Integer)tableModel.getValueAt(x, colCount-1);
			int qtyAdj = qty.intValue() >= 0 ? qty.intValue() : 0;
			InventoryContainer i = new InventoryContainer(part.intValue(), qtyAdj);
			inventoryUpdates.add(i);
		}
		// Testing 
		Iterator<InventoryContainer> iter = inventoryUpdates.iterator();
		while(iter.hasNext()){
			InventoryContainer curr = iter.next();
			System.out.println(curr.part + "\t" + curr.qty);
		}
	}
	
	// Object to hold the part and qty values for updating the inventory 
	public class InventoryContainer{
		public int part;
		public int qty;
		
		public InventoryContainer(int p, int q) {
			part = p;
			qty = q;
		}
	}

}

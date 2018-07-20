package views;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import rootPackage.InventoryWorkbook;
import rootPackage.WorkOrderWorkbook;

import javax.swing.JTable;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Explorator {

	private JFrame exploratorFrame;
	private JPanel contentPane;
	private JList<String> workOrderList;
	private JScrollPane listScroller;
	private DefaultTableModel tableModel;
	private JTable table;
	
	private InventoryWorkbook invWB;	// The inventory workbook to be processed
	private ArrayList<WorkOrderWorkbook> workOrderWBList;	// 
	private File[] workOrderFiles;
	private ArrayList<String> workOrderNames;
	private String workOrderDirectory; 
	private Set<Integer> currentPartList;
	
	/**
	 * Constructor for the explorator window that allows for work order manipulation and part stock visualization
	 * 
	 * @param wb the InventoryWorkbook that will be used for processing 
	 * @see InventoryWorkbook
	 */
	public Explorator(InventoryWorkbook wb) {
		// This will need to be changed to the permanent work order folder before shipping 
		workOrderDirectory = "C:\\Users\\luke\\eclipse-workspace\\BURIN_V2\\src\\Files\\WorkOrders";
		
		invWB = wb;
		workOrderWBList = new ArrayList<WorkOrderWorkbook>();
		workOrderNames = new ArrayList<String>();
		
		setUpWindow();
		exploratorFrame.setVisible(true);
	}

	
	/**
	 * Create the window
	 */
	public void setUpWindow() {
		
		exploratorFrame = new JFrame("Burin - Exploratron 3000");
		exploratorFrame.setBounds(300, 300, 1000, 1000);
		exploratorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (!getWorkOrders(workOrderDirectory)) { 
			errorMessage("No work orders found in *** folder. Please add the work orders you would like to process and restart the program");
			System.exit(0);
		}
		
		populateWorkOrderList();
		addTable();
		exploratorFrame.setContentPane(contentPane);
	}
	
	public void addTable() {
		
		String[] columnNames = new String[] {"Part Number","Qty In Stock", "Total Qty Needed", "Qty Remaining"};
		
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
		
		JScrollPane scrollPane = new JScrollPane(table);
		contentPane.add(scrollPane);
	}
	
	/*
	 * Retrieves the names of all files in the given directory. Returns true if at least
	 * one file was present in the folder and false if no files were found
	 */
	public boolean getWorkOrders(String dir) {
		
		boolean workOrdersPresent = false;
		try {
			File workOrderDir = new File(dir);
			workOrderFiles = workOrderDir.listFiles();
			
			for(File f : workOrderFiles) {
				if(f.isFile()) {
					workOrderNames.add(f.getName());
					workOrdersPresent = true;
				}
			}
		}catch(NullPointerException e) {
			errorMessage("Work order folder not found. Please make sure a folder with the name \"Work Orders\" is in the Burin folder and restart the program.");
			System.exit(0);
		}
		return workOrdersPresent;
	}
	
	/*
	 * Create and add the work order list to the window.
	 * The work order list should will have at least one element. 
	 * The list items can be selected with a mouse click and a pop up menu will be shown for that selection
	 */
	public void populateWorkOrderList() {
		
		
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.LINE_AXIS));
		
		DefaultListModel<String> listModel = new DefaultListModel<String>();
		for(String name : workOrderNames) {
			listModel.addElement(name);
		}
		
		workOrderList = new JList<String>(listModel);
		workOrderList.addListSelectionListener(new ListSelectionListener() {
			@Override 
			public void valueChanged(ListSelectionEvent e) {
				if (e.getValueIsAdjusting() == false) {
					if(workOrderList.getSelectedIndex() != -1) {
						displayPopUpMenu(workOrderList.getSelectedIndex(), workOrderList.getSelectedValue());
					}
				}
			}
		});
		workOrderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workOrderList.setLayoutOrientation(JList.VERTICAL);	
		
		listScroller = new JScrollPane(workOrderList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
		contentPane.add(listScroller);
		contentPane.add(Box.createHorizontalGlue());
	}
	
	/*
	 * Show pop up menu for adding and removing the selected work order from the table.
	 */
	public void displayPopUpMenu(int listIndex, String listValue) {
		
		JPopupMenu popup = new JPopupMenu("Edit");
		JMenuItem addItem = new JMenuItem("Add Work Order");
		JMenuItem removeItem = new JMenuItem("Remove Work Order");
		popup.add(addItem);
		popup.add(removeItem);
		Point listIndexLocation = workOrderList.indexToLocation(listIndex);
		if(listIndexLocation != null) {
			Rectangle cellBounds = workOrderList.getCellBounds(listIndex, listIndex);
			popup.show(workOrderList, listIndexLocation.x, listIndexLocation.y+cellBounds.height);
		}
		
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWorkOrder(listValue);
			}
		});
	}
	
	/*
	 * Adds the selected work order to the array list by searching for the work order name in the work order folder.
	 * Refreshes the table to reflect the additional work order
	 */
	public void addWorkOrder(String workOrderName) {
		for(File f : workOrderFiles) {
			if(f.getName().equals(workOrderName)) {
				try {
					// This will need to be changed in the future to reflect the permanent location
					String workOrderPath = ".\\src\\Files\\WorkOrders\\"+ workOrderName;
					workOrderWBList.add(new WorkOrderWorkbook(workOrderPath, 1));
					refreshTable();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/*
	 * Refresh the table. This will update any of the work orders that have been added or removed from the array list
	 */
	public void refreshTable() {
		
		currentPartList = new TreeSet<Integer>();
		Vector<String> columnNames = new Vector<String>();
		Vector<Vector<Integer>> tableData = new Vector<Vector<Integer>>();
		columnNames.add("Part Number"); columnNames.add("Qty In Stock");
		for(WorkOrderWorkbook n : workOrderWBList) { 
			columnNames.add(n.getWorkbookName()); 
			currentPartList.addAll(n.getPartList().keySet());
		}
		columnNames.add("Total Qty Needed"); columnNames.add("Qty Remaining");
		
		for(Integer i : currentPartList) {
			Vector<Integer> row = new Vector<Integer>();
			row.add(i);
			Integer invQty = invWB.getPartList().get(i);
			if(invQty != null) { row.add(invWB.getPartList().get(i)); } 
			else { 
				System.out.println(i.toString() + " not found in inventory workbook");
				continue; 
			}
			int totalQtyNeeded = 0;
			for(WorkOrderWorkbook n : workOrderWBList) {
				Integer part = n.getPartList().get(i);
				if(part != null) { 
					row.add(part); 
					totalQtyNeeded += part.intValue();
				}
				else { row.add(new Integer(0)); }
			}
			row.add(new Integer(totalQtyNeeded));
			row.add(new Integer(row.get(1).intValue() - totalQtyNeeded));
			tableData.add(row);
		}
		
		tableModel = new DefaultTableModel(tableData, columnNames);
		table.setModel(tableModel);
		
		System.out.println(currentPartList);
	}
	
	/*
	 * Display an error message to the user 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	

}

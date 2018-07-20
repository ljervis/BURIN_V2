package views;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;
import java.util.ArrayList;

import javax.swing.AbstractListModel;
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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import rootPackage.InventoryWorkbook;
import rootPackage.WorkOrderWorkbook;

import javax.swing.JTable;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Explorator {

	private JFrame exploratorFrame;
	private JPanel workOrderListPane;
	private JList<String> workOrderList;
	private JScrollPane listScroller;
	private DefaultTableModel tableModel;

	InventoryWorkbook invWB;
	ArrayList<WorkOrderWorkbook> workOrderWBList;
	File[] workOrderFiles;
	ArrayList<String> workOrderNames;
	private JTable table;
	String workOrderDirectory; 
	
	/*
	 * Constructor
	 */
	public Explorator(InventoryWorkbook wb) {
		workOrderDirectory = "C:\\Users\\luke\\eclipse-workspace\\BURIN_V2\\src\\Files\\WorkOrders";
		invWB = wb;
		workOrderWBList = new ArrayList<WorkOrderWorkbook>();
		setUpWindow();
		exploratorFrame.setVisible(true);
	}

	
	/*
	 * Create the window
	 */
	public void setUpWindow() {
		
		exploratorFrame = new JFrame("Burin - Exploratron 3000");
		exploratorFrame.setBounds(300, 300, 1000, 1000);
		exploratorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		getWorkOrders(workOrderDirectory);
		populateWorkOrderList();
		addTable();
		exploratorFrame.setContentPane(workOrderListPane);
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
		table.setBackground(SystemColor.control);
		table.setFillsViewportHeight(true);
		
		JScrollPane scrollPane = new JScrollPane(table);
		workOrderListPane.add(scrollPane);
	}
	
	/*
	 * Retrieves the names of all files in the given directory. Returns true if at least
	 * one file was present in the folder and false if no files were found
	 */
	public boolean getWorkOrders(String dir) {
		
		boolean workOrdersPresent = false;
		File workOrderDir = new File(dir);
		
		workOrderFiles = workOrderDir.listFiles();
		workOrderNames = new ArrayList<String>();
		
		for(File f : workOrderFiles) {
			if(f.isFile()) {
				workOrderNames.add(f.getName());
				workOrdersPresent = true;
			}
		}
		return workOrdersPresent;
	}
	
	/*
	 * Create and add the work order list to the window
	 */
	public void populateWorkOrderList() {
		
		
		workOrderListPane = new JPanel();
		workOrderListPane.setLayout(new BoxLayout(workOrderListPane, BoxLayout.LINE_AXIS));
		
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
						displayPopUpMenu(workOrderList.getSelectedIndex());
					}
				}
			}
		});
		workOrderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workOrderList.setLayoutOrientation(JList.VERTICAL);	
		
		listScroller = new JScrollPane(workOrderList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
		workOrderListPane.add(listScroller);
		workOrderListPane.add(Box.createHorizontalGlue());
	}
	
	/*
	 * Show pop up menu for adding and removing work orders from the table
	 */
	public void displayPopUpMenu(int listIndex) {
		
		JPopupMenu popup = new JPopupMenu("Edit");
		JMenuItem addItem = new JMenuItem("Add Work Order");
		JMenuItem removeItem = new JMenuItem("Remove Work Order");
		popup.add(addItem);
		popup.add(removeItem);
		Point listIndexLocation = workOrderList.indexToLocation(listIndex);
		Rectangle cellBounds = workOrderList.getCellBounds(listIndex, listIndex);
		popup.show(workOrderList, listIndexLocation.x, listIndexLocation.y+cellBounds.height);
		
		addItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWorkOrder(listIndex);
			}
		});
	}
	
	/*
	 * Add a work order to the table
	 */
	public void addWorkOrder(int listIndex) {
		String workOrderName = workOrderList.getSelectedValue();
		for(File f : workOrderFiles) {
			if(f.getName().equals(workOrderName)) {
				try {
					String workOrderPath = ".\\src\\Files\\WorkOrders\\"+ workOrderName;
					workOrderWBList.add(new WorkOrderWorkbook(workOrderPath, 1));
					refreshTable();
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void refreshTable() {
		for(WorkOrderWorkbook w : workOrderWBList) {
			tableModel.addColumn(w.getWorkbookName());
			table.setModel(tableModel);
		}
	}
	
	/*
	 * Display an error message to the user 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	

}

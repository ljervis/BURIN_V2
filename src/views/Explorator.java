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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import rootPackage.InventoryWorkbook;
import javax.swing.JTable;
import java.awt.Insets;
import javax.swing.BoxLayout;
import java.awt.SystemColor;

public class Explorator {

	private JFrame exploratorFrame;
	private JPanel workOrderListPane;
	private JList<String> workOrderList;
	private JScrollPane listScroller;

	InventoryWorkbook invWB;
	File[] workOrderFiles;
	ArrayList<String> workOrderNames;
	private JTable table;
	
	/*
	 * Constructor
	 */
	public Explorator(InventoryWorkbook wb) {
		invWB = wb;
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
		
		getWorkOrders("C:\\Users\\luke\\eclipse-workspace\\BURIN_V2\\src\\Files\\WorkOrders");
		populateWorkOrderList();
		addTable();
		exploratorFrame.setContentPane(workOrderListPane);
	}
	
	public void addTable() {
		
		String[] columnNames = new String[] {"Part Number","Qty In Stock", "Qty Needed", "Qty Remaining"};
		
		DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0) {

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
		workOrderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workOrderList.setLayoutOrientation(JList.VERTICAL);	
		workOrderList.setVisibleRowCount(-1);
		
		listScroller = new JScrollPane(workOrderList);
		
		workOrderListPane.add(listScroller);
		workOrderListPane.add(Box.createHorizontalGlue());
	}
	
	/*
	 * Display an error message to the user 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	

}

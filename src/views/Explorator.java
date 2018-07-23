package views;

import java.io.File;
import java.util.ArrayList;
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
import rootPackage.InventoryWorkbook;
import rootPackage.WorkOrderWorkbook;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Explorator {

	// Swing components
	private JFrame exploratorFrame;
	private JPanel contentPane;
	private JList<String> workOrderList;
	private JScrollPane listScroller;
	private JPopupMenu popup;
	private JMenuItem addItem;
	private JMenuItem removeItem;
	
	private InventoryWorkbook invWB;	// The inventory workbook to be processed
	private ArrayList<WorkOrderWorkbook> workOrderWBList;	//	List of all work orders that are currently being processed
	private File[] workOrderFiles; //	Files in the work order folder
	private ArrayList<String> workOrderNames;	// The names of all work orders in the work order folder
	private String workOrderDirectory;	// The path of the work order folder
	private DataTable dataTable;
	private Options optionsPane;
	
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
	 * Creates the explorator window and gets the work orders in the work order folder.
	 * If no work orders are found the program will exit.
	 * If there are work orders it will add them to the work order list and add the table to the window. 
	 */
	public void setUpWindow() {
		
		exploratorFrame = new JFrame("Burin - Explorator");
		exploratorFrame.setBounds(300, 300, 1000, 1000);
		exploratorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (!getWorkOrders(workOrderDirectory)) { 
			errorMessage("No work orders found in *** folder. Please add the work orders you would like to process and restart the program");
			System.exit(0);
		}
		
		populateWorkOrderList();
		dataTable = new DataTable(invWB);
		contentPane.add(dataTable.getTable());
		optionsPane = new Options(dataTable);
		contentPane.add(optionsPane.getOptionsPane());
		exploratorFrame.setContentPane(contentPane);
	}
	
	/**
	 * Retrieves the names of all files in the given directory. Returns true if at least
	 * one file was present in the folder and false if no files were found.
	 * 
	 * @param dir the path to the work order directory to be searched
	 * @exception e thrown if the folder was not found
	 */
	public boolean getWorkOrders(String dir) {
		
		boolean workOrdersPresent = false;
		try {
			File workOrderDir = new File(dir);
			workOrderFiles = workOrderDir.listFiles();
			
			for(File f : workOrderFiles) {
				if(f.isFile()) {
					workOrderNames.add(f.getName().substring(0, f.getName().lastIndexOf(".")));
					workOrdersPresent = true;
				}
			}
		}catch(NullPointerException e) {
			errorMessage("Work order folder not found. Please make sure a folder with the name \"Work Orders\" is in the Burin folder and restart the program.");
			System.exit(0);
		}
		return workOrdersPresent;
	}
	
	/**
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
		
		workOrderList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseEvent) {
				int index = workOrderList.locationToIndex(mouseEvent.getPoint());
				if(index >= 0) {
					String selection = workOrderList.getModel().getElementAt(index);
					displayPopUpMenu(index, selection);
				}
			}
		});
		
		// The cell rendered changes the labels 
		workOrderList.setCellRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			      Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			      if(isSelected) {
		    		  setBackground(Color.LIGHT_GRAY);
		    	  }
			      for(WorkOrderWorkbook w : workOrderWBList) {
			    	  if(w.getWorkbookName().equals(value)) {
			    		  setText(value + " x " + w.getMultiplicity()); // This changes the text but not the value of the selection
			    		  setBackground(Color.GRAY);
			    	  }
			      }
                  return c;
             }
		});
		
		workOrderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workOrderList.setLayoutOrientation(JList.VERTICAL);	
		
		listScroller = new JScrollPane(workOrderList, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			
		contentPane.add(listScroller);
		contentPane.add(Box.createHorizontalGlue());
	}
	
	/**
	 * @return true if the name is in the work order list, false otherwise 
	 */
	public boolean inWorkOrderList(String name) {
		for(WorkOrderWorkbook w : workOrderWBList) {
			if(w.getWorkbookName().equals(name)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Show pop up menu for adding and removing the selected work order from the table. The popup will allow the user to 
	 * either add a work order if it has not been added, or remove a work order that was previously added 
	 * 
	 * @param listIndex the index of the selection on the work order list
	 * @param listValue the name of the work order selected on the list
	 */
	public void displayPopUpMenu(int listIndex, String listValue) {
		
		popup = new JPopupMenu("Edit");
		addItem = new JMenuItem("Add Work Order");
		removeItem = new JMenuItem("Remove Work Order");

		if(inWorkOrderList(listValue)) {
			addItem.setEnabled(false);
		}
		else {
			removeItem.setEnabled(false);
		}
		
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
		
		removeItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeWorkOrder(listValue);
			}
		});
	}
	
	/**
	 * Removes the selected work order from the work order list and refreshes the table to reflect the removal
	 * 
	 */
	public void removeWorkOrder(String workOrderName) {
		for(int x = 0; x < workOrderWBList.size(); x++) {
			if(workOrderWBList.get(x).getWorkbookName().equals(workOrderName)) {
				workOrderWBList.remove(x);
			}
		}
		dataTable.refreshTable(workOrderWBList);
	}
	
	/**
	 * Adds the selected work order to the work order list by searching for selected work orders name in the work order folder.
	 * Refreshes the table to reflect the additional work order.
	 * 
	 * @param workOrderName the name of the work order selected on the list
	 * @exception e there was a problem loading the work order
	 */
	public void addWorkOrder(String workOrderName) {
		for(File f : workOrderFiles) {
			if(f.getName().equals(workOrderName + ".xlsx")) {
				try {
					
					Integer[] range = new Integer[20];
					for(int i = 0; i < 20; i++) {
						range[i] = new Integer(i+1);
					}
					
					// Prompt the user for the number of work orders to process 
					Integer mult = (Integer) JOptionPane.showInputDialog(exploratorFrame, workOrderName + 
							"\n How many work orders would you like to process?",
							"Work Order Selection", JOptionPane.PLAIN_MESSAGE, null, range, range[0]);
					
					// This will need to be changed in the future to reflect the permanent location
					String workOrderPath = ".\\src\\Files\\WorkOrders\\"+ workOrderName + ".xlsx";
					workOrderWBList.add(new WorkOrderWorkbook(workOrderPath, mult.intValue()));
					dataTable.refreshTable(workOrderWBList);
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Display an error message to the user 
	 * 
	 * @param message the error message to display
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	

}

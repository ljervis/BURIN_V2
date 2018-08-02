package views;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.io.FileOutputStream;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.border.EtchedBorder;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import rootPackage.WorkOrderWorkbook;
import rootPackage.ShortageSheet;
import rootPackage.OrderStock;

public class MenuBar {
	
	// Swing Components
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu viewMenu;
	private JMenu seperator;
	private ButtonGroup viewBtnGroup;
	
	ArrayList<WorkOrderWorkbook> workOrderWBList;
	DataTable table;
	
	public MenuBar(DataTable table, ArrayList<WorkOrderWorkbook> list) {
		workOrderWBList = list;
		this.table = table;
		setUpMenu();
	}
	
	public void setUpMenu() {
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		seperator  = new JMenu("|");
		viewMenu = new JMenu("View Options");
		
		seperator.setEnabled(false);
		
		menuBar.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		fileMenu.setFont(new Font("Serif", Font.PLAIN, 18));
		seperator.setFont(new Font("Serif", Font.PLAIN, 18));
		viewMenu.setFont(new Font("Serif", Font.PLAIN, 18));
		
		viewBtnGroup = new ButtonGroup();
		JRadioButtonMenuItem inStockOnly = new JRadioButtonMenuItem("Show In Stock");
		JRadioButtonMenuItem outOfStockOnly = new JRadioButtonMenuItem("Show Out Of Stock");
		JRadioButtonMenuItem allStock = new JRadioButtonMenuItem("Show All");
        allStock.setSelected(true);
        
        inStockOnly.setFont(new Font("Serif", Font.PLAIN, 18));
        outOfStockOnly.setFont(new Font("Serif", Font.PLAIN, 18));
        allStock.setFont(new Font("Serif", Font.PLAIN, 18));
        
		viewBtnGroup.add(allStock);
		viewBtnGroup.add(inStockOnly);
        viewBtnGroup.add(outOfStockOnly);
        viewMenu.add(allStock);
        viewMenu.add(inStockOnly);
        viewMenu.add(outOfStockOnly);
        
        inStockOnly.addItemListener((ItemEvent e) -> {
        	if(e.getStateChange() == ItemEvent.SELECTED) {
        		table.filterInStock();
        	}
        });
        
        outOfStockOnly.addItemListener((ItemEvent e) -> {
        	if(e.getStateChange() == ItemEvent.SELECTED) {
        		table.filterOutOfStock();
        	}
        });
        
        allStock.addItemListener((ItemEvent e) -> {
        	if(e.getStateChange() == ItemEvent.SELECTED) {
        		table.removeRowFilter();
        	}
        });
        
        JMenuItem reportBtn = new JMenuItem("Create Reports");
        JSeparator itemSeperator = new JSeparator();
        JMenuItem invUpdateBtn = new JMenuItem("Update Inventory");
        reportBtn.setToolTipText("Create an Excel workbook with a \"Pick List\" and a \"Shortage List\"");
        invUpdateBtn.setToolTipText("Update the inventory workbook with the currently loaded work orders");
        
        reportBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        invUpdateBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        
        reportBtn.addActionListener((ActionEvent e) -> {
        	if(workOrderWBList == null || workOrderWBList.isEmpty() == true) {
        		JOptionPane.showMessageDialog(new JFrame(), "Please add a work order to the table before creating a report!", "Error", JOptionPane.ERROR_MESSAGE);
        	}
        	else {
        		saveLocationReport();
        	}
        });
        
        invUpdateBtn.addActionListener((ActionEvent e) -> {
        	
        	saveLocationInventory();
        });
        
        fileMenu.add(reportBtn);
        fileMenu.add(itemSeperator);
        fileMenu.add(invUpdateBtn);
        
        menuBar.add(fileMenu);
        menuBar.add(seperator);
        menuBar.add(viewMenu);
		
	}
	
	public void saveLocationInventory() {
		
		String[] options = {"Update Current File", "Create New File"};
		
		int n = JOptionPane.showOptionDialog(new JFrame(),
			    "Would you like to update the current inventory workbook or create a new inventory workbook copy?",
			    "Options",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,     //do not use a custom Icon
			    options,  //the titles of buttons
			    options[0]); //default button title
		
		if(n == 1) {
			String fileName = (String) JOptionPane.showInputDialog(new JFrame(), "A new inventory workbook will be be created and\n"
					+ "updated to reflect the changes shown in the table.\n"
					+ "Enter a name for the new inventory workbook file \nand press OK to continue.", "Create New File", JOptionPane.PLAIN_MESSAGE, null, null, null);
			if(fileName != null && (!"".equals(fileName)))   
			{
			    table.updateInventoryWorkbook(fileName);
			}
		}
		else {
			table.updateInventoryWorkbook();
		}
	}
	
	public void saveLocationReport() {
		String fileName = (String) JOptionPane.showInputDialog(new JFrame(), "An excel file containing a \"Pick List\" and \"Shortage List\"\n will be created based on "
    			+ "the work orders added to the table, Enter a name for\n the report and press OK to continue.", "Warning", JOptionPane.PLAIN_MESSAGE, null, null, null);
		if(fileName != null && (!"".equals(fileName)))   
		{
		    createReport(fileName);
		    JOptionPane.showMessageDialog(new JFrame(), "Report Created!");
		}
	}
	
	public void createReport(String fileName) {
		Workbook reportWB = new XSSFWorkbook();
		OrderStock pickList = new OrderStock(table, reportWB);
		ShortageSheet shortageList = new ShortageSheet(table, reportWB, workOrderWBList);
		pickList.createList();
		shortageList.createList();
		try {
			FileOutputStream fileout = new FileOutputStream(fileName + ".xlsx");
			reportWB.write(fileout);
		}catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(), "There was an error writing the report to the file", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	
	public JMenuBar getMenu() {
		return menuBar;
	}
	
	public ButtonGroup getButtonGroup() {
		return viewBtnGroup;
	}

}

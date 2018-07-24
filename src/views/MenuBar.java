package views;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.ButtonGroup;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

public class MenuBar {
	
	// Swing Components
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu viewMenu;
	private ButtonGroup viewBtnGroup;
	
	DataTable table;
	
	public MenuBar(DataTable table) {
		this.table = table;
		setUpMenu();
	}
	
	public void setUpMenu() {
		
		menuBar = new JMenuBar();
		fileMenu = new JMenu("File");
		viewMenu = new JMenu("Table View Options");
		
		viewBtnGroup = new ButtonGroup();
		JRadioButtonMenuItem inStockOnly = new JRadioButtonMenuItem("Show In Stock");
		JRadioButtonMenuItem outOfStockOnly = new JRadioButtonMenuItem("Show Out Of Stock");
		JRadioButtonMenuItem allStock = new JRadioButtonMenuItem("Show All");
        allStock.setSelected(true);
        
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
        JMenuItem invUpdateBtn = new JMenuItem("Update Inventory");
        reportBtn.setToolTipText("Create an Excel workbook with in-stock and out-of-stock part sheets");
        invUpdateBtn.setToolTipText("Update the inventory workbook with the currently loaded work orders");
        
        reportBtn.addActionListener((ActionEvent e) -> {
        	
        });
        
        invUpdateBtn.addActionListener((ActionEvent e) -> {
        	
        });
        
        fileMenu.add(reportBtn);
        fileMenu.add(invUpdateBtn);
        
        menuBar.add(fileMenu);
        menuBar.add(viewMenu);
		
	}
	
	public JMenuBar getMenu() {
		return menuBar;
	}

}

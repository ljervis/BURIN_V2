package views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

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

public class MenuBar {
	
	// Swing Components
	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu viewMenu;
	private JMenu seperator;
	private ButtonGroup viewBtnGroup;
	
	DataTable table;
	
	public MenuBar(DataTable table) {
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
        reportBtn.setToolTipText("Create an Excel workbook with in-stock and out-of-stock part sheets");
        invUpdateBtn.setToolTipText("Update the inventory workbook with the currently loaded work orders");
        
        reportBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        invUpdateBtn.setFont(new Font("Serif", Font.PLAIN, 18));
        
        reportBtn.addActionListener((ActionEvent e) -> {
        	
        });
        
        invUpdateBtn.addActionListener((ActionEvent e) -> {
        	int choice = JOptionPane.showConfirmDialog(new JFrame(),
        			"The inventory workbook will be updated to reflect the changes shown in the table. "
        			+ "Are you sure you want to proceed?", "Warning", JOptionPane.YES_NO_OPTION);
        	if(choice == 0) {
        		table.updateInventoryWorkbook();
        	}
        });
        
        fileMenu.add(reportBtn);
        fileMenu.add(itemSeperator);
        fileMenu.add(invUpdateBtn);
        
        menuBar.add(fileMenu);
        menuBar.add(seperator);
        menuBar.add(viewMenu);
		
	}
	
	public JMenuBar getMenu() {
		return menuBar;
	}

}

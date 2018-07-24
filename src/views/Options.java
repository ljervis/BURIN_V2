package views;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;

public class Options {
	
	// Swing Components 
	JButton updateInvBtn;
	JButton reportBtn;
	JButton inStockFilterBtn;
	JButton outOfStockFilterBtn;
	JPanel optionsPane;
	boolean inStockFilter;
	boolean outOfStockFilter;
	
	DataTable table;

	/**
	 * Create the panel.
	 */
	public Options(DataTable table) {
		this.table = table;
		optionsPane = new JPanel();
		optionsPane.setLayout(new BoxLayout(optionsPane,BoxLayout.Y_AXIS));
		inStockFilter = false;
		outOfStockFilter = false;
		setUpButtons();
	}
	
	public JPanel getOptionsPane() {
		return optionsPane;
	}
	
	public void setUpButtons() {
		updateInvBtn = new JButton("Update Inventory Work Book");
		reportBtn = new JButton("Create Reports");
		inStockFilterBtn = new JButton("Show Parts In Stock Only");
		outOfStockFilterBtn = new JButton("Show Parts Out Of Stock Only");
		
		optionsPane.add(inStockFilterBtn);
		optionsPane.add(outOfStockFilterBtn);
		optionsPane.add(reportBtn);
		optionsPane.add(updateInvBtn);
		
		inStockFilterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!inStockFilter) {
					table.filterInStock();
					inStockFilterBtn.setBackground(Color.BLUE);
					inStockFilter = true;
				}
				else {
					table.removeRowFilter();
					inStockFilterBtn.setBackground(null);
					inStockFilter = false;
				}
			}
		});
		
		outOfStockFilterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(!outOfStockFilter) {
					table.filterOutOfStock();
					outOfStockFilterBtn.setBackground(Color.BLUE);
					outOfStockFilter = true;
				}
				else {
					table.removeRowFilter();
					outOfStockFilterBtn.setBackground(null);
					outOfStockFilter = false;
				}
			}
		});
	}

}

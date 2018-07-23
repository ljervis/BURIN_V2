package views;

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
	
	DataTable table;

	/**
	 * Create the panel.
	 */
	public Options(DataTable table) {
		this.table = table;
		optionsPane = new JPanel();
		optionsPane.setLayout(new BoxLayout(optionsPane,BoxLayout.Y_AXIS));
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
				table.filterInStock();
			}
		});
		
		outOfStockFilterBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				table.filterOutOfStock();
			}
		});
	}

}

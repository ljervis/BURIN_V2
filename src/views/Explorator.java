package views;

import javax.swing.JFrame;

import rootPackage.InventoryWorkbook;

public class Explorator {

	InventoryWorkbook invWB;
	private JFrame frame;

	/*
	 * Constructor
	 */
	public Explorator(InventoryWorkbook wb) {
		invWB = wb;
		setUpWindow();
		frame.setVisible(true);
	}

	
	/*
	 * Create the window
	 */
	public void setUpWindow() {
		
		frame = new JFrame();
		frame.setBounds(300, 300, 1000, 1000);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	

}

package views;

import java.awt.Dimension;

import javax.swing.AbstractListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import rootPackage.InventoryWorkbook;

public class Explorator {

	InventoryWorkbook invWB;
	private JFrame exploratorFrame;
	private JPanel contentPane;
	private JList workOrderList;
	private JScrollPane listScroller;

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
		
		exploratorFrame = new JFrame("Burin Exploratron 3000");
		exploratorFrame.setBounds(300, 300, 1000, 1000);
		exploratorFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		String[] exampleData = {"item1", "item2", "item3"};
		
		workOrderList = new JList(exampleData);
		workOrderList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		workOrderList.setLayoutOrientation(JList.VERTICAL);
		workOrderList.setVisibleRowCount(-1);
		
		listScroller = new JScrollPane(workOrderList);
		listScroller.setPreferredSize(new Dimension(250, 80));
		
		contentPane.add(listScroller);
		
		exploratorFrame.setContentPane(contentPane);
		
		
	}
	
	

}

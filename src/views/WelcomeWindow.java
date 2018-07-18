package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.GraphicsConfiguration;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import rootPackage.InventoryWorkbook;

public class WelcomeWindow {

	// Swing Components
	private JFrame windowFrame;
	private JPanel contentPane;
	private JLabel textLabel;
	private JTextPane dragDropArea;
	
	private File inventoryFile;
	private InventoryWorkbook inventoryWB;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
	        public void run() {
	        	try {
					WelcomeWindow window = new WelcomeWindow();
					window.windowFrame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }
	    });
		
	}

	/**
	 * Constructor
	 */
	public WelcomeWindow() {
		setUpWindow();
	}
	
	/*
	 * Create the window
	 */
	public void setUpWindow() {
		
		windowFrame = new JFrame();
		windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowFrame.setTitle("Burin");
		windowFrame.setBounds(300, 300, 600, 600);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(10, 10, 10, 10));
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
		
		textLabel = new JLabel("please select the inventory workbook by dragging and dropping the excel file below");
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		dragDropArea = new JTextPane();
		dragDropArea.setAlignmentX(Component.CENTER_ALIGNMENT);
		dragDropArea.setBackground(Color.CYAN);
		dragDropArea.setTransferHandler(new FileDropHandler());

		contentPane.add(textLabel);
		contentPane.add(dragDropArea);
		dragDropArea.setAlignmentX(Component.CENTER_ALIGNMENT);

		windowFrame.setContentPane(contentPane);
	}
	
	/*
	 * Import the inventory file specified by the user
	 */
	public void importInventoryWB() {
		try {
			inventoryWB = new InventoryWorkbook(inventoryFile.getAbsolutePath());
			inventoryWB.setSheet(0);
			if(inventoryWB.isValid()) {
				openExplorator();
			}
			else {
				inventoryWB.closeWB();
				inventoryWB = null;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Open the explorator window and close the welcome window
	 */
	public void openExplorator() {
		Explorator e = new Explorator(inventoryWB);
    	windowFrame.dispose();
    	windowFrame.setVisible(false);
	}
	
	/*
	 * Display an error message to the user 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/*
	 * Display error message dialogs if more that one file was selected, a directory was selected, or the file does not have a ".xlsx" extension
	 */
	public boolean checkValidFile(List<File> fileList) {
		if(fileList.size() != 1) {
			errorMessage("Multiple files detected. Please upload one file only.");
			return false;
		}
		if(fileList.get(0).isDirectory()) {
			errorMessage("Folder detected. Please enter an excel file.");
			return false;
		}
		String extension = "";
		int i = fileList.get(0).getName().lastIndexOf('.');
		if (i > 0) {
		    extension = fileList.get(0).getName().substring(i+1);
		}
		if(!extension.equalsIgnoreCase("xlsx")) {
			errorMessage("Incorrect file extention detected. Please upload file with \".xlsx\" extension.");
			return false;
		}
		return true;
	}
	
	/*
	 * Handler for drag and drop file feature
	 */
	final class FileDropHandler extends TransferHandler {
	    @Override
	    public boolean canImport(TransferHandler.TransferSupport support) {
	        for (DataFlavor flavor : support.getDataFlavors()) {
	            if (flavor.isFlavorJavaFileListType()) {
	                return true;
	            }
	        }
	        return false;
	    }

	    @Override
	    @SuppressWarnings("unchecked")
	    public boolean importData(TransferHandler.TransferSupport support) {
	        if (!this.canImport(support))
	            return false;

	        List<File> files;
	        try {
	            files = (List<File>) support.getTransferable()
	                    .getTransferData(DataFlavor.javaFileListFlavor);
	        } catch (UnsupportedFlavorException | IOException ex) {
	            // should never happen (or JDK is buggy)
	            return false;
	        }
	        if(checkValidFile(files)) {
	        	inventoryFile = files.get(0);
	        	importInventoryWB();
	        }
	        return true;
	    }
	}

}

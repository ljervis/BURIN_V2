package views;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.TransferHandler;
import rootPackage.InventoryWorkbook;
import java.awt.SystemColor;

public class WelcomeWindow {

	// Swing Components
	private JFrame windowFrame;
	private JPanel contentPane;
	private JLabel textLabel;
	private ImageIcon fileIcon;
	
	private File inventoryFile;
	private InventoryWorkbook inventoryWB;
	
	/**
	 * The main method that launches the program. Swing GUI objects are run on the event dispatch thread. 
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
	 * Constructor for the initial window that is displayed when the program starts
	 */
	public WelcomeWindow() {
		setUpWindow();
	}
	
	/**
	 * Creates a window with a JPanel that has a drag and drop file transfer handler
	 */
	public void setUpWindow() {
		
		windowFrame = new JFrame();
		windowFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		windowFrame.setTitle("Burin - Inventory Workbook Selection");
		windowFrame.setBounds(300, 300, 600, 600);
		
		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.X_AXIS));
		contentPane.setTransferHandler(new FileDropHandler());
		
		textLabel = new JLabel("Drag and drop the inventory workbook here");
		Font boldItalicFont = new Font("Serif", Font.BOLD+Font.ITALIC, 24);
		textLabel.setFont(boldItalicFont);
		
		contentPane.add(Box.createHorizontalGlue());
		contentPane.add(textLabel);
		contentPane.add(Box.createHorizontalGlue());

		windowFrame.setContentPane(contentPane);
	}
	
	/**
	 * Finds and processes the inventory work book specified by the user if the workbook is valid and open the explorator window. 
	 * 
	 * @exception an unknown error occurred when opening the file path. Check the stack trace for more information
	 */
	public void importInventoryWB() {
		
		try {
			inventoryWB = new InventoryWorkbook(inventoryFile.getAbsolutePath());
			inventoryWB.setSheet(0);
			if(inventoryWB.isValid()) { // run checks to make sure the file given is a valid inventory work book
				inventoryWB.read();
				openExplorator();
			}
			else {
				inventoryWB.closeWB();
				inventoryWB = null;
			}
		}catch(Exception e) {
			e.printStackTrace();
			errorMessage("A problem occured when trying to open the inventory work book. Please make sure the file is closed and restart the program.");
			System.exit(0);
		}
	}
	
	/**
	 * Open the explorator window and close the welcome window
	 */
	public void openExplorator() {
		Explorator e = new Explorator(inventoryWB);
    	windowFrame.dispose();
    	windowFrame.setVisible(false);
	}
	
	/**
	 * Display an error message to the user 
	 * @param message the error message to display
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Try to check whether the transfered file is valid. 
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
	
	/**
	 * File handler for dragging and dropping the inventory work book file
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

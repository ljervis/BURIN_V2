package views;

import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.TransferHandler;

public class WelcomeWindow extends JFrame {

	private JPanel contentPane;
	private JButton inventoryButton;
	private JLabel textLabel;
	private JTextPane dragDropArea;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WelcomeWindow frame = new WelcomeWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame
	 */
	public WelcomeWindow() {
		setUpWindow();
	}
	
	public void setUpWindow() {
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(300, 300, 600, 600);
		
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
		
		textLabel = new JLabel("please select the inventory workbook by dragging and dropping the excel file below");
		textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
//		inventoryButton = new JButton("Choose File");
//		inventoryButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		dragDropArea = new JTextPane();
		dragDropArea.setAlignmentX(CENTER_ALIGNMENT);
		dragDropArea.setBackground(Color.CYAN);
		dragDropArea.setTransferHandler(new FileDropHandler());

		contentPane.add(textLabel);
//		contentPane.add(inventoryButton);
		contentPane.add(dragDropArea);
		dragDropArea.setAlignmentX(CENTER_ALIGNMENT);

		setContentPane(contentPane);
	}
	
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

	        for (File file: files) {
	            System.out.println(file.getAbsolutePath());
	        }
	        return true;
	    }
	}

}

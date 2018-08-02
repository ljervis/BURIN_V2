package rootPackage;

import java.awt.Dimension;
import java.awt.SystemColor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;


/**
 * @author Luke <a href="mailto:lukejervis14@gmail.com">lukejervis14@gmail.com</a>
 *
 */
public class InventoryWorkbook implements WorkbookInterface {

	/**
	 * Private fields used in this class 
	 */
	private Workbook workbook;
	private Sheet sheet;
	private HashMap<Integer, Pair> partList;
	private int partNumStartRow;
	private String workbookFileName;
	private String updateMessage;
	
	/**
	 * Final fields used in this class to validate the inventory workbook file
	 */
	final String IH = "inventory work book";
	final String IP = "part #";
		
	/**
	 * Creates an object to control the life cycle and data contained in the 
	 * inventory workbook excel file. Initializes most private variables
	 * @param file	The file name of the inventory workbook 
	 * @throws Exception Creation of the workbook with the workbook factory can 
	 * throw IOException, InvalidFormatException, or EncryptedDocumentException
	 */
	public InventoryWorkbook(String file) throws Exception {
		
		workbookFileName = file;
		InputStream inp = new FileInputStream(file);
//		File inp = new File(file);
		workbook = WorkbookFactory.create(inp);
		updateMessage = "";
		
		System.out.println(file);
	}
	
	/**
	 * Getter method for the inventory workbook file
	 * @return The inventory workbook file path
	 */
	public String getInventoryFile() {
		return workbookFileName;
	}
	
	/**
	 * Close the inventory workbook 
	 * @throws IOException
	 */
	public void closeWB() throws IOException {
		workbook.close();
	}
	
	/**
	 * One of the inventory workbook validation methods. Checks to see if 
	 * the words "inventory work book" are contained in cell A1 of the sheet. 
	 * @return True if the words were found and false otherwise
	 */
	public boolean checkHeader() {
		
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);
		if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(IH)) {return true;}
		return false;
	}
	
	/**
	 * One of the inventory workbook validation methods.Checks to see if the 
	 * word "part#" is in column A. Makes the assumption that checkHeader() 
	 * already returned true
	 * @return Row index where "part#" was found or -1 if not present
	 * @see checkHeader 
	 */
	public int checkPartNum() {
		
		for(Row row : sheet) {
			if(row != null) {
				Cell cell = row.getCell(0);
				if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(IP)) {return row.getRowNum();}
			}
		}
		return -1;
	}
	
	/**
	 * String cell check
	 * @param cell The cell to be checked
	 * @return True if the cell is of type String, false otherwise 
	 */
	public boolean checkStringCellValid(Cell cell) {
		
		if(cell == null || cell.getCellTypeEnum() != CellType.STRING) {return false;}
		return true;
	}
	
	/**
	 * Numeric cell check
	 * @param cell The cell to be checked
	 * @return True if the cell is of type Numeric, false otherwise
	 */
	public boolean checkNumericCellValid(Cell cell) {
		
		if(cell == null || cell.getCellTypeEnum() != CellType.NUMERIC) {return false;}
		return true;
	}
	
	/**
	 * Debugging function to print the partList field to the console
	 */
	public void printPartList() {
		int count = 0;
		for(Integer key : partList.keySet()) {
			count++;
			String part = key.toString();
			String qty = partList.get(key).first.toString();
			String row = partList.get(key).second.toString();
			System.out.println(part + " " + qty + " " + row + " " + count);
		}
	}
	
	/**
	 * Updates the inventory workbook with values contained in the parameter ArrayList
	 * and write them to the file. Assume that there inv.size() > 0. 
	 * Assume that all part numbers in inv can be found in the inventory workbook, 
	 * Otherwise they would not have been populated in the data table.
	 * 
	 * @param invUpdate list of part numbers and the new quantity 
	 * @param fileName The file name of the newly created inventory workbook
	 */
	public void updateInventoryWorkbook(ArrayList<Pair> invUpdate, String fileName) {
		Iterator<Pair> iter = invUpdate.iterator();
		while(iter.hasNext()) {
			Pair invUpdatePair = iter.next();
			Pair partListPair = partList.get(invUpdatePair.first); // Get the part # entry in the inventory part list with the part # from the invUpdate
			Row row = sheet.getRow(partListPair.second); // Get this part #'s row in the inventory work book that was previously saved
			Cell cell = row.getCell(1); // Get the quantity cell for this part # 
			cell.setCellValue(invUpdatePair.second.intValue()); // Update the inventory workbook with the new quantity as reflected on the table  
			
			partListPair.first = invUpdatePair.second; // Update the part list with the new qty value
			
			updateMessage += invUpdatePair.first.toString() + " | " + invUpdatePair.second.toString() + "\n";
		}
		
		try {
			
			writeInventoryWorkbook(fileName + ".xlsx");
			showInventoryUpdateMessage(updateMessage);
			System.exit(0);
		}catch(IOException e) {
			errorMessage("The inventory workbook could not be updated, " + "please review the documentation and try again");
			System.exit(1);
		}
	}
	
	/**
	 * Updates the inventory workbook with values contained in the parameter ArrayList
	 * and write them to the file. Assume that there inv.size() > 0. 
	 * Assume that all part numbers in inv can be found in the inventory workbook, 
	 * Otherwise they would not have been populated in the data table.
	 * 
	 * @param invUpdate list of part numbers and the new quantity 
	 */
	public void updateInventoryWorkbook(ArrayList<Pair> invUpdate) {
		Iterator<Pair> iter = invUpdate.iterator();
		while(iter.hasNext()) {
			Pair invUpdatePair = iter.next();
			Pair partListPair = partList.get(invUpdatePair.first); // Get the part # entry in the inventory part list with the part # from the invUpdate
			Row row = sheet.getRow(partListPair.second); // Get this part #'s row in the inventory work book that was previously saved
			Cell cell = row.getCell(1); // Get the quantity cell for this part # 
			cell.setCellValue(invUpdatePair.second.intValue()); // Update the inventory workbook with the new quantity as reflected on the table  
			
			partListPair.first = invUpdatePair.second; // Update the part list with the new qty value
			
			updateMessage += invUpdatePair.first.toString() + " | " + invUpdatePair.second.toString() + "\n";
		}
		
		try {
			
			writeInventoryWorkbook(workbookFileName);
			showInventoryUpdateMessage(updateMessage);
			System.exit(0);
		}catch(IOException e) {
			errorMessage("The inventory workbook could not be updated, " + "please review the documentation and try again");
			System.exit(1);
		}
	}
		
	/**
	 * Displays a message in a message dialog with a list of parts that were updated in the inventory workbook and the new quantities. 
	 * @param updateMessage The message that should contain all parts that were updated 
	 */
	public void showInventoryUpdateMessage(String updateMessage) {
		JTextArea message = new JTextArea("The inventory workbook was sucessfully updated!\nAll parts and their updated quantities are shown below:\n\n\n" + updateMessage + "\n\nPress ok to close the Burin");
		JScrollPane scrollPane = new JScrollPane(message);
		scrollPane.setBackground(SystemColor.inactiveCaption);
		message.setLineWrap(true);  
		message.setWrapStyleWord(true); 
		scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
		JOptionPane.showMessageDialog(new JFrame(),scrollPane, "Inventory Update", JOptionPane.INFORMATION_MESSAGE);
	}
	
	/**
	 * Write the workbook to the given file after updates have been made.
	 * @param file The name of the file 
	 * @throws IOException 
	 */
	public void writeInventoryWorkbook(String file) throws IOException{
		FileOutputStream f = new FileOutputStream(file);
		workbook.write(f);
		f.close();
	}
	
	
	/**
	 * Getter method for the part list
	 * @return The part list 
	 */
	public HashMap<Integer,Pair> getPartList(){return partList;}
	
	/**
	 * Setter for the part number starting row. Used for testing only
	 * @param num The starting row 
	 */
	public void setPartNumStartRow(int num) {partNumStartRow = num;}
	
	/**
	 * Displays an error message to the user with the given message 
	 * @param message The error message to display 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override 
	public void setSheet(int index) {
		sheet = workbook.getSheetAt(index);
	}
	
	/**
	 * Checks if the inventory workbook is valid. Displays an error message if the workbook is not valid.
	 */
	@Override
	public boolean isValid() {
		if(!checkHeader()) {
			errorMessage("Invalid inventory workbook detected. Make sure cell A1 contains the words \"inventory work book\". Please upload a valid inventory workbook.");
			return false;
		}
		partNumStartRow = checkPartNum();
		if (partNumStartRow == -1) {
			errorMessage("Invalid inventory workbook detected. Make sure there is a cell containing the word \"part#\". Please upload a valid inventory workbook.");
			return false;
		}
		return true;
	}
	
	/**
	 * Parse the work order sheet and populate the part list
	 * will only add parts that have a valid numerical part number and qty number. 
	 */
	@Override
	public void read() {
		
		partList = new HashMap<Integer, Pair>();
		
		for(int x = partNumStartRow+1; x <= sheet.getLastRowNum(); x++) {
			Row row = sheet.getRow(x);
			if(row == null) {continue;}
			if(row.getRowNum() <= partNumStartRow) {continue;}
			Cell partNumCell = row.getCell(0);
			Cell qtyCell = row.getCell(1);
			
			if(checkNumericCellValid(partNumCell) && checkNumericCellValid(qtyCell)) {
				Integer partNumValue = new Integer((int)partNumCell.getNumericCellValue());
				Integer qtyValue = new Integer((int)qtyCell.getNumericCellValue());
				Integer rowValue = new Integer(row.getRowNum());
				partList.put(partNumValue, new Pair(qtyValue, rowValue));
			}
		}
	}
}

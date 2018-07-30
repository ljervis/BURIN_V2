package rootPackage;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InventoryWorkbook implements WorkbookInterface {

	Workbook workbook;
	Sheet sheet;
	HashMap<Integer, Pair> partList;
	int partNumStartRow;
	String workbookFileName;
	String updateMessage;
	
	// Constants for validating the inventory work book
	final String IH = "inventory work book";
	final String IP = "part #";
		
	/*
	 * Constructor using OPCPackage
	 */
	public InventoryWorkbook(OPCPackage pgk) throws Exception {
		workbook = new XSSFWorkbook(pgk);
	}
	
	/*
	 * Constructor using workbook factory
	 */
	public InventoryWorkbook(String file) throws Exception {
		
		workbookFileName = file;
		File inp = new File(file);
		workbook = WorkbookFactory.create(inp);
		updateMessage = "";
		
	}
	
	/*
	 * Close the workbook
	 */
	public void closeWB() throws IOException {
		workbook.close();
	}
	
	/*
	 * Check to see if the words "inventory work book" is in cell A1
	 */
	public boolean checkHeader() {
		
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);
		if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(IH)) {return true;}
		return false;
	}
	
	/*
	 * Check to see if the word "part#" is in column A and return the row index or -1 if not present
	 * Assumption that checkHeader() returned true
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
	
	/*
	 * Return true if a cell is a valid string
	 */
	public boolean checkStringCellValid(Cell cell) {
		
		if(cell == null || cell.getCellTypeEnum() != CellType.STRING) {return false;}
		return true;
	}
	
	/*
	 * Return true if a cell holds a valid number
	 */
	public boolean checkNumericCellValid(Cell cell) {
		
		if(cell == null || cell.getCellTypeEnum() != CellType.NUMERIC) {return false;}
		return true;
	}
	
	/*
	 * Print out parts and quantities 
	 */
	public void printPartList() {
		int count = 0;
		for(Integer key : partList.keySet()) {
			count++;
			String part = key.toString();
			String qty = partList.get(key).toString();
			System.out.println(part + " " + qty + " " + count);
		}
	}
	
	/**
	 * Update the inventory workbook with the given values.
	 * Assume that there inv.size() > 0
	 * Assume that all part numbers in inv can be found in the inventory workbook, 
	 * Otherwise they would not have been populated in the data table
	 * 
	 * @param inv list of part numbers and the new quantity 
	 */
	public void updateInventoryWorkbook(ArrayList<Pair> invUpdate) {
		Iterator<Pair> iter = invUpdate.iterator();
		while(iter.hasNext()) {
			Pair invUpdatePair = iter.next();
			Pair partListPair = partList.get(invUpdatePair.first); // Get the part # entry in the inventory part list with the part # from the invUpdate
			Row row = sheet.getRow(partListPair.second); // Get this part #'s row in the inventory work book that was previously saved
			Cell cell = row.getCell(1); // Get the quantity cell for this part # 
			cell.setCellValue(invUpdatePair.second.intValue()); // Update the inventory workbook with the new quantitiy as reflected on the table  
			
			partListPair.first = invUpdatePair.second; // Update the part list with the new qty value
			
			updateMessage += invUpdatePair.first.toString() + " | " + invUpdatePair.second.toString() + "\n";
		}
		
		try {
			FileOutputStream f = new FileOutputStream("testWorkbook.xlsx");
			workbook.write(f);
			f.close();
			
			JTextArea message = new JTextArea("The inventory workbook was sucessfully updated!\nAll parts and their updated quantities are shown below:\n" + updateMessage);
			JScrollPane scrollPane = new JScrollPane(message);
			message.setLineWrap(true);  
			message.setWrapStyleWord(true); 
			scrollPane.setPreferredSize( new Dimension( 500, 500 ) );
//			updateMessage = "The inventory workbook was successfully updated!\nAll parts and their updated quantities are shown below:\n" + updateMessage;
			JOptionPane.showMessageDialog(new JFrame(),scrollPane, "Inventory Update", JOptionPane.INFORMATION_MESSAGE);
			System.exit(0);
		}catch(Exception e){
			JOptionPane.showMessageDialog(new JFrame(), "The inventory workbook could not be updated, "
					+ "please review the documentation and try again", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	/*
	 * Return the part:quantity hashmap
	 */
	public HashMap<Integer,Pair> getPartList(){return partList;}
	
	/*
	 * Set the part numbers header row
	 */
	public void setPartNumStartRow(int num) {partNumStartRow = num;}
	
	/*
	 * Display an error message to the user 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	
	@Override 
	public void setSheet(int index) {
		sheet = workbook.getSheetAt(index);
	}
	
	/* 
	 * Returns true if the work order has a valid format (i.e. header, part numbers), false otherwise
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
	
	/*
	 * Parse the work order sheet and populate the part list
	 * will only add parts that have a valid numerical part number and qty number
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

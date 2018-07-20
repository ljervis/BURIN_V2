package rootPackage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
	HashMap<Integer,Integer> partList;
	int partNumStartRow;
	
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
		
		File inp = new File(file);
		workbook = WorkbookFactory.create(inp);
		
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
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(IP)) {return row.getRowNum();}
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
	
	/*
	 * Return the part:quantity hashmap
	 */
	public HashMap<Integer,Integer> getPartList(){return partList;}
	
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
	 */
	@Override
	public void read() {
		
		partList = new HashMap<Integer, Integer>();
		
		for(int x = partNumStartRow+1; x <= sheet.getLastRowNum(); x++) {
			Row row = sheet.getRow(x);
			if(row.getRowNum() <= partNumStartRow) {continue;}
			Cell partNumCell = row.getCell(0);
			Cell qtyCell = row.getCell(1);
			
			if(checkNumericCellValid(partNumCell) && checkNumericCellValid(qtyCell)) {
				Integer partNumValue = new Integer((int)partNumCell.getNumericCellValue());
				Integer qtyValue = new Integer((int)qtyCell.getNumericCellValue());
				partList.put(partNumValue, qtyValue);
			}
		}
	}
}

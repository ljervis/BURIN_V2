package rootPackage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

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
public class WorkOrderWorkbook implements WorkbookInterface {
	
	/**
	 * Private fields used in this class
	 */
	private Workbook workbook;
	private Sheet sheet;
	private HashMap<Integer,Pair> partList;
	private int partStartRow;
	private int partEndRow;
	private int multiplier;
	private String workbookName;
	
	/**
	 * Final field used in this class
	 */
	final String WOH = "work order";
	final String WOS = "start";
	final String WOE = "end";
	final int mfgPartCol = 4;
	final int partDescriptionCol = 5;
	final int partSupplierCol = 7;
	
	/**
	 * Creates an object to control the life cycle and data contained in a
	 * work order workbook excel file. Initializes most private variables
	 * @param file	The file name of the inventory workbook 
	 * @param mult The number of work orders, used to calculate parts needed
	 * @throws Exception Creation of the workbook with the workbook factory can 
	 * throw IOException, InvalidFormatException, or EncryptedDocumentException
	 */
	public WorkOrderWorkbook(String file, int mult) throws Exception {
		
		InputStream inp = new FileInputStream(file);
		workbook = WorkbookFactory.create(inp);
		multiplier = mult;
		workbookName = file.substring(file.lastIndexOf("\\")+1, file.lastIndexOf("."));
		setSheet(0);
	}
	
	/**
	 * Checks to see if the work order file is valid. Reads the work order if valid and 
	 * displays an error message if invalid.
	 * @return true if the work order is valid, false otherwise 
	 */
	public boolean createWB() {
		if(!isValid()) {
			System.out.println("InvalidWB");
			errorMessage("Invalid work order detected. Please make sure the work "
					+ "order is closed, contains the words \"work order\" in cell A1, "
					+ "and contains cells with the words \"START\" and \"END\" above"
					+ " and below the part numbers");
			return false;
		}
		else {
			read();
			return true; 
		}
	}
	
	/**
	 * Displays an error message to the user with the given message 
	 * @param message The error message to display 
	 */
	public void errorMessage(String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, "Error", JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * Getter method for the work order workbook file name
	 * @return the name of the workbook with its file extension removed
	 */
	public String getWorkbookName() {
		return workbookName;
	}
	
	/**
	 * Getter for the number of work orders used in calculating the parts needed
	 * @return the multiplicity or the number of workbooks processed
	 */
	public int getMultiplicity() {
		return multiplier;
	}
	
	/**
	 * One of the work order validation methods. Checks to see if the words "work order" is in cell A1
	 * @return true if the work is present, false otherwise 
	 */
	public boolean checkHeader() {
		
		Row row = sheet.getRow(0);
		if(row != null) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOH)) {return true;}
		}
		return false;
	}
	
	/**
	 * One of the work order validation methods. Checks to see if the word "START" is in column A.
	 * Assumption that checkHeader() returned true
	 * @return The row number if the word is present, -1 otherwise 
	 */
	
	public int checkStart() {
		
		for(Row row : sheet) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOS)) {return row.getRowNum();}
		}
		return -1;
	}
	
	/**
	 * One of the work order validation methods. Checks to see if the word "END" is in column A.
	 * Assumption that isValid() returned true.
	 * @return The row number if the word is present, -1 otherwise.
	 */
	public int checkEnd() {
		
		for(Row row: sheet) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOE)) {return row.getRowNum();}
		}
		return -1;
	}
	
	/**
	 * Check whether the row containing the END row is after the START row
	 * Assumption that partStartRow and partEndRow have been initialized.
	 * @return True if END is above START, false otherwise
	 */
	public boolean checkRowsValid() {
		if(partEndRow > partStartRow) {return true;}
		return false;
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
		for(Integer key : partList.keySet()) {
			String part = key.toString();
			String qty = partList.get(key).first.toString();
			System.out.println(part + " " + qty);
		}
	}
	
	public HashMap<Integer, Pair> getPartList() {return partList;}
	
	/*
	 * Set the multiplier of this workbook
	 */
	public void setMultiplier(int mult) { multiplier = mult; }
	
	@Override
	public void setSheet(int index) {
		
		sheet = workbook.getSheetAt(index);
	}
	
	/*
	 * Set the part numbers header row
	 */
	public void setStartRow(int num) {partStartRow = num;}
	
	/*
	 * Set the part numbers ending row
	 */
	public void setEndRow(int num) {partEndRow = num;}
	
	/* 
	 * Returns true if the work order has a valid format (i.e. header, part numbers), false otherwise
	 */
	@Override
	public boolean isValid() {
		
		if(!checkHeader()) {return false;}
		partStartRow = checkStart();
		partEndRow = checkEnd();
		if (partStartRow == -1 || partEndRow == -1) {return false;}
		return true;
	}

	/**
	 * Parses the work order sheet and populates the part list with these values.
	 * Quantities are multiplied by the multiplier to simulate multiple work orders.
	 * Only rows from the work order with valid numeric part and quantity cells 
	 * will be added to the list
	 */
	@Override
	public void read() {
		
		partList = new HashMap<Integer, Pair>();
		
		for(int x = partStartRow+1; x < partEndRow; x++) {
			Row row = sheet.getRow(x);
			if(row != null) {
				Cell partNumCell = row.getCell(0);
				Cell qtyCell = row.getCell(1);
				
				if(checkNumericCellValid(partNumCell) && checkNumericCellValid(qtyCell)) {
					Integer partNumValue = new Integer((int)partNumCell.getNumericCellValue());
					Integer qtyValue = new Integer((int)qtyCell.getNumericCellValue());
					Integer rowValue = new Integer(row.getRowNum());
					partList.put(partNumValue, new Pair(qtyValue * multiplier, rowValue));
				}
			}
		}
	}
	
	/**
	 * If the given part is present in the part list, the row containing that part is used
	 * to retrieve the "MFG PART NUMBER" from the original work order workbook
	 * @param part The part used for lookup 
	 * @return The "MFG PART NUMBER" if present, an empty string otherwise
	 */
	public String getMFGPart(Integer part) {
		String mfgPart = "";
		Pair partInfo = partList.get(part);
		if(partInfo != null) {
			int rowNum = partInfo.second.intValue();
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(mfgPartCol);	// Uses mfgPartCol final variable as the column containing this field
			if(checkStringCellValid(cell)) {
				mfgPart = cell.getStringCellValue();
			}
		}
		return mfgPart;
	}
	
	/**
	 * If the given part is present in the part list, the row containing that part is used
	 * to retrieve the "DESCRIPTION" from the original work order workbook
	 * @param part The part used for lookup 
	 * @return The "DESCRIPTION" if present, an empty string otherwise
	 */
	public String getDescription(Integer part) {
		String description = "";
		Pair partInfo = partList.get(part);
		if(partInfo != null) {
			int rowNum = partInfo.second.intValue();
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(partDescriptionCol);	// Uses partDescriptionCol final variable as the column containing this field
			if(checkStringCellValid(cell)) {
				description = cell.getStringCellValue();
			}
		}
		return description;
	}
	
	/**
	 * If the given part is present in the part list, the row containing that part is used
	 * to retrieve the "SUPPLIER" from the original work order workbook.
	 * @param part The part used for lookup 
	 * @return The "SUPPLIER" if present, an empty string otherwise
	 */
	public String getSupplier(Integer part) {
		String supplier = "";
		Pair partInfo = partList.get(part);
		if(partInfo != null) {
			int rowNum = partInfo.second.intValue();
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(partSupplierCol);	// Uses partSupplierCol final variable as the column containing this field
			if(checkStringCellValid(cell)) {
				supplier = cell.getStringCellValue();
			}
		}
		return supplier;
	}
	
	/**
	 * Getter method for the Sheet
	 * @return The Sheet object
	 */
	public Sheet getSheet() {
		return sheet;
	}
	
	/**
	 * Getter method for the part number starting Row 
	 * @return The row number 
	 */
	public int getPartStartRow() {
		return partStartRow;
	}

}

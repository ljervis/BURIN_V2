package rootPackage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
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

public class WorkOrderWorkbook implements WorkbookInterface {
	
	Workbook workbook;
	Sheet sheet;
	HashMap<Integer,Pair> partList;
	int partStartRow;
	int partEndRow;
	int multiplier;
	String workbookName;
	
	// Constants for validating the work order
	final String WOH = "work order";
	final String WOS = "start";
	final String WOE = "end";
	final int mfgPartCol = 4;
	final int partDescriptionCol = 5;
	final int partSupplierCol = 7;
	
	/*
	 *  Open workbook with OPCPackage 
	 */
	public WorkOrderWorkbook(OPCPackage pgk) throws Exception {
		
		workbook = new XSSFWorkbook(pgk);
		multiplier = 1;
	}
	
	/*
	 *  Open workbook with workbook factory 
	 */
	public WorkOrderWorkbook(String file, int mult) throws Exception {
		
		InputStream inp = new FileInputStream(file);
		workbook = WorkbookFactory.create(inp);
		multiplier = mult;
		workbookName = file.substring(file.lastIndexOf("\\")+1, file.lastIndexOf("."));
		setSheet(0);
	}
	
	public boolean createWB() {
		if(!isValid()) {
			System.out.println("InvalidWB");
			JOptionPane.showMessageDialog(new JFrame(), "Invalid work order detected. Please make sure the work order is closed, contains the words \"work order\" in cell A1, and contains cells with the words \"START\" and \"END\" above and below the part numbers", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else {
			read();
			return true; 
		}
	}
	
	/**
	 * 
	 * @return the name of the workbook with its file extension removed
	 */
	public String getWorkbookName() {
		return workbookName;
	}
	
	/**
	 * 
	 * @return the multiplicity or the number of workbooks processed
	 */
	public int getMultiplicity() {
		return multiplier;
	}
	
	/*
	 *  Check to see if the words "work order" is in cell A1
	 */
	public boolean checkHeader() {
		
		Row row = sheet.getRow(0);
		if(row != null) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOH)) {return true;}
		}
		return false;
	}
	
	/* 
	 * Check to see if the word "part#" is in column A and return the row index or -1 if not present
	 * Assumption that checkHeader() returned true
	 */
	
	public int checkStart() {
		
		for(Row row : sheet) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOS)) {return row.getRowNum();}
		}
		return -1;
	}
	
	/*
	 * Check to see if the word "floor stock" is in column A and return the row index or -1 if not present
	 * Assumption that isValid() returned true
	 */
	public int checkEnd() {
		
		for(Row row: sheet) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOE)) {return row.getRowNum();}
		}
		return -1;
	}
	
	/*
	 * Return true if parts above floor stock, false otherwise
	 * Assumption that partStartRow and partEndRow have been initialized
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

	/*
	 * Parse the work order sheet and populate the part list
	 */
	@Override
	public void read() {
		
		partList = new HashMap<Integer, Pair>();
		
		for(int x = partStartRow+2; x < partEndRow; x++) {
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
	
	public String getMFGPart(Integer part) {
		String mfgPart = "";
		Pair partInfo = partList.get(part);
		if(partInfo != null) {
			int rowNum = partInfo.second.intValue();
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(mfgPartCol);
			if(checkStringCellValid(cell)) {
				mfgPart = cell.getStringCellValue();
			}
		}
		return mfgPart;
	}
	
	public String getDescription(Integer part) {
		String description = "";
		Pair partInfo = partList.get(part);
		if(partInfo != null) {
			int rowNum = partInfo.second.intValue();
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(partDescriptionCol);
			if(checkStringCellValid(cell)) {
				description = cell.getStringCellValue();
			}
		}
		return description;
	}
	
	
	public String getSupplier(Integer part) {
		String supplier = "";
		Pair partInfo = partList.get(part);
		if(partInfo != null) {
			int rowNum = partInfo.second.intValue();
			Row row = sheet.getRow(rowNum);
			Cell cell = row.getCell(partSupplierCol);
			if(checkStringCellValid(cell)) {
				supplier = cell.getStringCellValue();
			}
		}
		return supplier;
	}
	
	public Sheet getSheet() {
		return sheet;
	}
	
	public int getPartStartRow() {
		return partStartRow;
	}

}

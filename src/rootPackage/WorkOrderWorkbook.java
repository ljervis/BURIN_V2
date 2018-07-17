package rootPackage;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;

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
	HashMap<Integer,Integer> partList;
	int partNumStartRow;
	int floorStockStartRow;
	
	// Constants for validating the work order
	final String WOH = "work order";
	final String WOP = "part#";
	final String WOF = "floor stock";
	
	/*
	 *  Open workbook with OPCPackage 
	 */
	public WorkOrderWorkbook(OPCPackage pgk) throws Exception {
		
		workbook = new XSSFWorkbook(pgk);
	}
	
	/*
	 *  Open workbook with workbook factory 
	 */
	public WorkOrderWorkbook(String file) throws Exception {
		
		InputStream inp = new FileInputStream(file);
		workbook = WorkbookFactory.create(inp);
	}
	
	/*
	 *  Check to see if the words "work order" is in cell A1
	 */
	public boolean checkHeader() {
		
		Row row = sheet.getRow(0);
		Cell cell = row.getCell(0);
		if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOH)) {return true;}
		return false;
	}
	
	/* 
	 * Check to see if the word "part#" is in column A and return the row index or -1 if not present
	 * Assumption that checkHeader() returned true
	 */
	
	public int checkPartNum() {
		
		for(Row row : sheet) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOP)) {return row.getRowNum();}
		}
		return -1;
	}
	
	/*
	 * Check to see if the word "floor stock" is in column A and return the row index or -1 if not present
	 * Assumption that isValid() returned true
	 */
	public int checkFloorStock() {
		
		for(Row row: sheet) {
			Cell cell = row.getCell(0);
			if(checkStringCellValid(cell) && cell.getStringCellValue().trim().equalsIgnoreCase(WOF)) {return row.getRowNum();}
		}
		return -1;
	}
	
	/*
	 * Return true if parts above floor stock, false otherwise
	 * Assumption that partNumStartRow and floorStockStartRow have been initialized
	 */
	public boolean checkRowsValid() {
		if(floorStockStartRow > partNumStartRow) {return true;}
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
			String qty = partList.get(key).toString();
			System.out.println(part + " " + qty);
		}
	}
	
	public HashMap<Integer, Integer> getPartList() {return partList;}
	
	@Override
	public void setSheet(int index) {
		
		sheet = workbook.getSheetAt(index);
	}
	
	public void setPartNumStartRow(int num) {partNumStartRow = num;}
	
	public void setFloorStockStartRow(int num) {floorStockStartRow = num;}
	
	/* 
	 * Returns true if the work order has a valid format (i.e. header, part numbers), false otherwise
	 */
	@Override
	public boolean isValid() {
		
		if(!checkHeader()) {return false;}
		partNumStartRow = checkPartNum();
		if (partNumStartRow == -1) {return false;}
		return true;
	}

	/*
	 * Parse the work order sheet and populate the part list
	 */
	@Override
	public void read() {
		
		partList = new HashMap<Integer, Integer>();
		
		for(int x = partNumStartRow+1; x < floorStockStartRow; x++) {
			Row row = sheet.getRow(x);
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

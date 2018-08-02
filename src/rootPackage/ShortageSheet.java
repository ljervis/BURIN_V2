package rootPackage;

import java.util.*;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import views.DataTable;

/**
 * @author Luke <a href="mailto:lukejervis14@gmail.com">lukejervis14@gmail.com</a>
 *
 */
public class ShortageSheet implements Stock {

	/**
	 * Private fields used in this class
	 */
	private ArrayList<WorkOrderWorkbook> workOrderWBList;
	private DefaultTableModel tableModel;
	private Vector<Vector<Integer>> tableData;
	private Workbook workbook;
	private Sheet shortageSheet;
	private int rowCount;	// Counter to keep track of what row number is to be added next
	private String[] headerColumnNames;
	private InventoryWorkbook invWB; 
	
	/**
	 * Final fields to be used in this class
	 */
	private final String SHORT = "Quantity Short/Quantity Remaining";
	private final String MFG = "MFG Part Number";
	private final String DES = "Description";
	private final String SUP = "Supplier";
	
	/**
	 * Creates a shortage list sheet on the given excel workbook that will export the current shortages 
	 * as shown in the tables DataModel. Initializes most private fields. 
	 * @see DataTable 
	 * @param table Contains data from the current table in its table model
	 * @param wb The workbook in which the shortage list sheet will be created
	 * @param list A list of all work order workbook objects currently loaded onto the table
	 */
	public ShortageSheet(DataTable table, Workbook wb, ArrayList<WorkOrderWorkbook> list, InventoryWorkbook invWB) {
		
		tableModel = table.getTableModel();
		tableData = tableModel.getDataVector();
		workbook = wb; 
		workOrderWBList = list;
		rowCount = 0;
		headerColumnNames = new String[] {SHORT, MFG, DES};	// Supplier is not currently added to the shortage sheet
		this.invWB = invWB;
	}
	
	/**
	 * Creates the shortage list sheet in the workbook and dictates method calls to populate the list. 
	 */
	@Override
	public void createList() {
		
		shortageSheet = workbook.createSheet("Shortage List");
		createHeaderRow();
		createShortageList();
		autoSizeColumns();
	}
	
	/**
	 * Auto formats the width of the first ten columns of the shortage sheet.
	 * Should be called after the sheet has been completely populated. 
	 */
	public void autoSizeColumns() {
		for(int i = 0; i < 10; i++) {
			shortageSheet.autoSizeColumn(i); 
		}
	}
	
	/**
	 * Creates a CellStyle object customized with the parameter values.  
	 * @param size The font size
	 * @param color The foreground fill color
	 * @param bold whether the font should be in bold or not
	 * @return The custom CellStyle object that was created
	 */
	public CellStyle createCellStyle(int size, short color, boolean bold) {
		CellStyle headerRowStyle = workbook.createCellStyle();	
		headerRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerRowStyle.setFillForegroundColor(color);
		headerRowStyle.setAlignment(HorizontalAlignment.CENTER);
		
		Font font = workbook.createFont();
	    font.setFontHeightInPoints((short)size);
	    font.setBold(bold);
	    headerRowStyle.setFont(font);
	    
	    return headerRowStyle;
	}
	
	/**
	 * Creates a new cell to be added to the shortage list sheet.
	 * @param row The row where the cell will be created
	 * @param col The column where the cell will be created
	 * @param cellValue The value to be contained in the cell. Will accept String and Integer objects 
	 * @param cellStyle The style that the cell will use
	 * @return The newly created cell
	 */
	public Cell createCell(Row row, int col, Object cellValue, CellStyle cellStyle) {
		Cell cell = row.createCell(col);
		if(cellValue instanceof String) {
			cell.setCellValue((String)cellValue);
		}
		else if(cellValue instanceof Integer) {
			cell.setCellValue(((Integer)cellValue).doubleValue());
		}
		cell.setCellStyle(cellStyle);
		return cell;
	}
	
	/**
	 * Creates a header row for the shortage list sheet using the table model to populate some of the column names
	 */
	public void createHeaderRow() {
		Row headerRow = shortageSheet.createRow(rowCount);
		rowCount++;
		CellStyle headerRowStyle = createCellStyle(16, IndexedColors.GREY_25_PERCENT.getIndex(), true);
		addBorder(headerRowStyle);
		//	The header will use all but the last column header names from the table 
		for(int col = 0; col < tableModel.getColumnCount()-1; col++) {
			createCell(headerRow, col, tableModel.getColumnName(col), headerRowStyle);
		}
		
		for(int col = 0; col < headerColumnNames.length; col++) {
			int adjustedCol = col + tableModel.getColumnCount()-1;
			createCell(headerRow, adjustedCol, headerColumnNames[col], headerRowStyle);
		}
	}
	
	/**
	 * Fills out the shortage list sheet by scanning the table model and using rows that have a deficit in the "Remaining Qty" column. 
	 * Tries to fill out the final three columns of each row from the work orders that are currently loaded
	 */
	public void createShortageList() {
		
		CellStyle greyStyle = createCellStyle(12, IndexedColors.GREY_25_PERCENT.getIndex(), false);
		CellStyle deficitStyle = createCellStyle(12, IndexedColors.YELLOW.getIndex(), false);
		CellStyle borderStyle = createCellStyle(12, IndexedColors.WHITE.getIndex(), false);
		CellStyle minStyle = createCellStyle(12, IndexedColors.PINK1.getIndex(), false);
		addBorder(minStyle);
		addBorder(greyStyle);
		addBorder(deficitStyle);
		addBorder(borderStyle);
		
		for(Vector<Integer> v : tableData) {
			int qtyRemaining = v.lastElement().intValue();
			Integer partNumber = v.get(0);
			//	Only look at rows with a deficit in the "remaining Qty" column 
			if(qtyRemaining < 0) {
				Row dataRow = shortageSheet.createRow(rowCount);
				rowCount++;
				
				int cellCount = 0;
				for(Integer i : v) {
					if(cellCount == v.size()-2) {
						createCell(dataRow, cellCount, i, greyStyle);
					}
					else {
						Integer posative = i.intValue() >= 0 ? i : new Integer(i.intValue()*-1);
						createCell(dataRow, cellCount, posative, borderStyle);
					}
					cellCount++;
				}
				
				Integer partNum = v.get(0);	// the first element in the vector should be the part number 
				for(int col = 0; col < headerColumnNames.length - 1; col++) {
					int adjustedCol = col + cellCount;
					createCell(dataRow, adjustedCol, getDescriptor(headerColumnNames[col + 1], partNum), borderStyle);
				}
			}
			else if(invWB.checkBelowMin(partNumber, qtyRemaining)) {
				Row dataRow = shortageSheet.createRow(rowCount);
				rowCount++;
				
				int cellCount = 0;
				for(Integer i : v) {
					if(cellCount == v.size()-2) {
						createCell(dataRow, cellCount, i, greyStyle);
					}
					else {
						Integer posative = i.intValue() >= 0 ? i : new Integer(i.intValue()*-1);
						createCell(dataRow, cellCount, posative, minStyle);
					}
					cellCount++;
				}
				
				Integer partNum = v.get(0);	// the first element in the vector should be the part number 
				for(int col = 0; col < headerColumnNames.length - 1; col++) {
					int adjustedCol = col + cellCount;
					createCell(dataRow, adjustedCol, getDescriptor(headerColumnNames[col + 1], partNum), borderStyle);
				}
			}
		}
	}
	
	/**
	 * Searches the work orders for the first instance of a descriptor.
	 * 
	 * @param descriptorName The type of descriptor to look for 
	 * @param part The part number to find the correct row in the work orders
	 * @return The descriptor if found and an empty String if one is not found
	 */
	public String getDescriptor(String descriptorName, Integer part) {
		
		String text = "";
		Iterator<WorkOrderWorkbook> iter = workOrderWBList.iterator();
		switch(descriptorName) {
		case MFG : 
			while(text.equals("") && iter.hasNext()) { text = iter.next().getMFGPart(part); }
		case DES : 
			while(text.equals("") && iter.hasNext()) { text = iter.next().getDescription(part); }
		case SUP :
//			while(text.equals("") && iter.hasNext()) { text = iter.next().getSupplier(part); }
		}
		return text;
	}
	
	/**
	 * Adds a medium border to the CellStyle param 
	 * @param cs CellStyle 
	 */
	public void addBorder(CellStyle cs) {
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.MEDIUM);
	}
	
//	public void addWorkOrderNames(WorkOrderWorkbook wb) {
//	Row nameRow = shortageSheet.createRow(rowCount);
//	rowCount++;
//	Cell nameCell = nameRow.createCell(0);
//	CellStyle nameRowStyle = createHeaderRowStyle(14, IndexedColors.GREY_25_PERCENT.getIndex(), false);
//	nameCell.setCellStyle(nameRowStyle);
//	nameCell.setCellValue(wb.getMultiplicity() + " x " + wb.getWorkbookName());
//}
	
}

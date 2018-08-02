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
public class OrderStock implements Stock {
	
	/**
	 * Private fields used in this class
	 */
	private DefaultTableModel tableModel;
	private Vector<Vector<Integer>> tableData;
	private Workbook workbook;
	private Sheet pickList;
	private int rowCount;
	
	/**
	 * Final fields used in this class
	 */
	private final String QTY = "Quantity To Be Pulled";
	
	/**
	 * Creates a pick list sheet on the given excel workbook 
	 * that will export parts to be pulled from inventory 
	 * as shown in the tables DataModel. Initializes most 
	 * private variables 
	 * @param table Contains data from the current table in its table model
	 * @param wb The workbook in which the pick list sheet will be created
	 */
	public OrderStock(DataTable table, Workbook wb) {
		
		tableModel = table.getTableModel();
		workbook = wb;
		tableData = tableModel.getDataVector();
		rowCount = 0;
	}
	
	
	/**
	 * Creates the pick list sheet in the workbook and dictates method calls to populate the list. 
	 */
	@Override
	public void createList() {
		
		pickList = workbook.createSheet("Pick List");
		addHeader();
		createPickList();
		autoSizeColumns();
		
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
	 * Creates a header row for the pick list sheet using the table model to 
	 * populate most of the column names
	 */
	public void addHeader() {
	    
		Row headerRow = pickList.createRow(rowCount);
		rowCount++;
		CellStyle headerRowStyle = createCellStyle(18, IndexedColors.GREY_25_PERCENT.getIndex(), true);
		addBorder(headerRowStyle);
		pickList.createFreezePane(0, 1, 0, 1);
		int quantityRemainingRow = tableModel.getColumnCount()-1;	// New
		for(int col = 0; col < tableModel.getColumnCount()-1; col++) {
			createCell(headerRow, col, tableModel.getColumnName(col), headerRowStyle);
		}
		createCell(headerRow, tableModel.getColumnCount()-1, QTY, headerRowStyle);
		createCell(headerRow, tableModel.getColumnCount(), tableModel.getColumnName(quantityRemainingRow), headerRowStyle); // New
	}
	
	
	/**
	 * Fills out the pick list sheet by scanning the table model.
	 */
	public void createPickList() {
		
		CellStyle greyStyle = createCellStyle(11, IndexedColors.GREY_25_PERCENT.getIndex(), false);
		CellStyle deficitStyle = createCellStyle(11, IndexedColors.YELLOW.getIndex(), false);
		CellStyle borderStyle = createCellStyle(11, IndexedColors.WHITE.getIndex(), false);
		addBorder(greyStyle);
		addBorder(deficitStyle);
		addBorder(borderStyle);
		
		for(Vector<Integer> v : tableData) {
			Row dataRow = pickList.createRow(rowCount);
			rowCount++;
			
			int cellCount = 0;
			for(Integer i : v) {
				if(cellCount == v.size()-2) {
					createCell(dataRow, cellCount, i, greyStyle);
				}
				else if(cellCount == v.size()-1) {
					int cellVal = i.intValue() >= 0 ? i.intValue() : 0;	// This is the quantity remaining column on the table 
					int invQty = v.get(1);	// The inventory quantity should always be in column 1
					CellStyle style = i.intValue() < 0 ? deficitStyle : borderStyle;
					createCell(dataRow, cellCount, new Integer(invQty - cellVal), style);	// This is the quantity to be taken out of inventory
					cellCount++;
					createCell(dataRow, cellCount, cellVal, borderStyle); // This is the quantity remaining in inventory
				}
				else {
					createCell(dataRow, cellCount, i, borderStyle);
				}
				cellCount++;
			}
		}
	}
	
	/**
	 * Auto formats the width of the first ten columns of the shortage sheet.
	 * Should be called after the sheet has been completely populated. 
	 */
	public void autoSizeColumns() {
		for(int i = 0; i < tableModel.getColumnCount()+1; i++) {
			pickList.autoSizeColumn(i); 
		}
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
}

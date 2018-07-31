package rootPackage;

import java.util.*;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import views.DataTable;

public class InStock implements Stock {

	ArrayList<WorkOrderWorkbook> workOrderWBList;
	DefaultTableModel tableModel;
	Vector<Vector<Integer>> tableData;
	Workbook workbook;
	Sheet shortageSheet;
	int rowCount;
	
	
	public InStock(DataTable table, Workbook wb, ArrayList<WorkOrderWorkbook> list) {
		tableModel = table.getTableModel();
		tableData = tableModel.getDataVector();
		workbook = wb; 
		workOrderWBList = list;
		rowCount = 0;
	}
	
	@Override
	public void createList() {
		
		shortageSheet = workbook.createSheet("Shortage List");
		createHeaderRow();
		createShortageList();
		autoSizeColumns();
	}
	
	public void autoSizeColumns() {
		for(int i = 0; i < 10; i++) {
			shortageSheet.autoSizeColumn(i); 
		}
	}
	
//	public void addWorkOrderNames(WorkOrderWorkbook wb) {
//		Row nameRow = shortageSheet.createRow(rowCount);
//		rowCount++;
//		Cell nameCell = nameRow.createCell(0);
//		CellStyle nameRowStyle = createHeaderRowStyle(14, IndexedColors.GREY_25_PERCENT.getIndex(), false);
//		nameCell.setCellStyle(nameRowStyle);
//		nameCell.setCellValue(wb.getMultiplicity() + " x " + wb.getWorkbookName());
//	}
	
	public CellStyle createHeaderRowStyle(int size, short color, boolean bold) {
		CellStyle headerRowStyle = workbook.createCellStyle();
		headerRowStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		headerRowStyle.setFillForegroundColor(color);
		
		Font font = workbook.createFont();
	    font.setFontHeightInPoints((short)size);
	    font.setBold(bold);
	    headerRowStyle.setFont(font);
	    
	    return headerRowStyle;
	}
	
	public void createHeaderRow() {
		Row headerRow = shortageSheet.createRow(rowCount);
		rowCount++;
		CellStyle headerRowStyle = createHeaderRowStyle(16, IndexedColors.GREY_25_PERCENT.getIndex(), true);
		for(int col = 0; col < tableModel.getColumnCount()-1; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(tableModel.getColumnName(col));
			cell.setCellStyle(headerRowStyle);
		}
		int currCol = tableModel.getColumnCount()-1;
		Cell shortCell = headerRow.createCell(currCol);
		shortCell.setCellValue("Quantity Short");
		shortCell.setCellStyle(headerRowStyle);
		currCol++;
		Cell mfgPartCell = headerRow.createCell(currCol);
		mfgPartCell.setCellValue("MFG Part Number");
		mfgPartCell.setCellStyle(headerRowStyle);
		currCol++;
		Cell descriptionCell = headerRow.createCell(currCol);
		descriptionCell.setCellValue("Description");
		descriptionCell.setCellStyle(headerRowStyle);
		currCol++;
		Cell supplierCell = headerRow.createCell(currCol);
		supplierCell.setCellValue("Supplier");
		supplierCell.setCellStyle(headerRowStyle);
		currCol++;
	}
	
	/**
	 * 
	 */
	public void createShortageList() {
		
		CellStyle greyStyle = createHeaderRowStyle(11, IndexedColors.GREY_40_PERCENT.getIndex(), false);
		CellStyle deficitStyle = createHeaderRowStyle(11, IndexedColors.YELLOW.getIndex(), false);
		CellStyle borderStyle = createHeaderRowStyle(11, IndexedColors.WHITE.getIndex(), false);
		addBorder(greyStyle);
		addBorder(deficitStyle);
		addBorder(borderStyle);
		
		for(Vector<Integer> v : tableData) {
			int qtyRemaining = v.lastElement().intValue();
			if(qtyRemaining < 0) {
				Row dataRow = shortageSheet.createRow(rowCount);
				rowCount++;
				int cellCount = 0;
				for(Integer i : v) {
					Cell dataCell = dataRow.createCell(cellCount);
					dataCell.setCellStyle(borderStyle);
					if(cellCount == v.size()-2) {
						dataCell.setCellStyle(greyStyle);
					}
					if(cellCount == v.size()-1) {
						dataCell.setCellValue((double)(i.intValue() * (-1)));
						cellCount++;
						
					}
					else {
						cellCount++;
						dataCell.setCellValue((double)i.intValue());
					}
				}
				Cell mfgPartCell = dataRow.createCell(cellCount);
				cellCount++;
				mfgPartCell.setCellStyle(borderStyle);
				mfgPartCell.setCellValue("");
				Cell descriptionCell = dataRow.createCell(cellCount);
				cellCount++;
				descriptionCell.setCellStyle(borderStyle);
				descriptionCell.setCellValue("");
				Cell supplierCell = dataRow.createCell(cellCount);
				cellCount++;
				supplierCell .setCellStyle(borderStyle);
				supplierCell .setCellValue("");
				for(WorkOrderWorkbook w : workOrderWBList) {
					Integer part = v.get(0);
					String mfgText = w.getMFGPart(part);
					String descriptionText = w.getDescription(part);
					String supplierText = w.getSupplier(part);
					if(!mfgText.equals("")) { mfgPartCell.setCellValue(mfgText); }
					if(!descriptionText.equals("")) { descriptionCell.setCellValue(descriptionText); }
					if(!supplierText.equals("")) { supplierCell.setCellValue(supplierText); }
				}
			}
		}
	}
	
	public void addBorder(CellStyle cs) {
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.MEDIUM);
	}
	
}

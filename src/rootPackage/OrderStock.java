package rootPackage;

import java.awt.Color;
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

public class OrderStock implements Stock {

	DefaultTableModel dataModel;
	Vector<Vector<Integer>> tableData;
	Workbook workbook;
	Sheet pickList;
	int rowCount;
	
	public OrderStock(DataTable table, Workbook wb) {
		dataModel = table.getTableModel();
		workbook = wb;
		tableData = dataModel.getDataVector();
		rowCount = 0;
	}
	
	@Override
	public void createList() {
		pickList = workbook.createSheet("Pick List");
		addHeader();
		addData();
		autoSizeColumns();
		
	}
	
	public void addHeader() {
		Font font = workbook.createFont();
	    font.setFontHeightInPoints((short)18);
	    font.setBold(true);
	    
	    CellStyle style = workbook.createCellStyle();
	    style.setFont(font);

	    
		Row headerRow = pickList.createRow(0);
		rowCount++;
		pickList.createFreezePane(0, 1, 0, 1);
		for(int col = 0; col < dataModel.getColumnCount()-1; col++) {
			Cell cell = headerRow.createCell(col);
			cell.setCellValue(dataModel.getColumnName(col));
			cell.setCellStyle(style);
		}
		Cell cell = headerRow.createCell(dataModel.getColumnCount()-1);
		cell.setCellValue("Quantity To Be Pulled");
		cell.setCellStyle(style);
	}
	
	public void addData() {
		CellStyle greyStyle = workbook.createCellStyle();
		CellStyle deficitStyle = workbook.createCellStyle();
		CellStyle borderStyle = workbook.createCellStyle();
		greyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		deficitStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		greyStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
		deficitStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
		addBorder(greyStyle);
		addBorder(deficitStyle);
		addBorder(borderStyle);
		
		for(Vector<Integer> v : tableData) {
			Row dataRow = pickList.createRow(rowCount);
			rowCount++;
			int cellCount = 0;
			for(Integer i : v) {
				Cell dataCell = dataRow.createCell(cellCount);
				dataCell.setCellStyle(borderStyle);
				if(cellCount == v.size()-2) {
					dataCell.setCellStyle(greyStyle);
				}
				if(cellCount == v.size()-1) {
					if(i.intValue() < 0) {
						dataCell.setCellStyle(deficitStyle);
					}
					int cellVal = i.intValue() >= 0 ? i.intValue() : 0;
					int invQty = v.get(1);
					dataCell.setCellValue((double)(invQty - cellVal));
					cellCount++;
					
				}
				else {
					cellCount++;
					dataCell.setCellValue((double)i.intValue());
				}
					
			}
		}
	}
	
	public void autoSizeColumns() {
		for(int i = 0; i < dataModel.getColumnCount(); i++) {
			pickList.autoSizeColumn(i); 
		}
	}
	
	public void addBorder(CellStyle cs) {
		cs.setBorderBottom(BorderStyle.MEDIUM);
		cs.setBorderTop(BorderStyle.MEDIUM);
		cs.setBorderRight(BorderStyle.MEDIUM);
		cs.setBorderLeft(BorderStyle.MEDIUM);
	}
}

package rootPackage;

import java.awt.Color;
import java.util.*;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import views.DataTable;

public class OrderStock implements Stock {

	ArrayList<WorkOrderWorkbook> workOrderWBList;
	DefaultTableModel dataModel;
	Vector<Vector<Integer>> tableData;
	Workbook workbook;
	Sheet pickList;
	int rowCount;
	
	public OrderStock(ArrayList<WorkOrderWorkbook> list, DataTable table, Workbook wb) {
		dataModel = table.getTableModel();
		workOrderWBList = list;
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
		greyStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		greyStyle.setFillForegroundColor(IndexedColors.GREY_40_PERCENT.getIndex());
//		deficitStyle.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
		for(Vector<Integer> v : tableData) {
			Row dataRow = pickList.createRow(rowCount);
			rowCount++;
			int cellCount = 0;
			for(Integer i : v) {
				Cell dataCell = dataRow.createCell(cellCount);
				if(cellCount > 1 && cellCount < v.size()-1) {
					dataCell.setCellStyle(greyStyle);
				}
				if(cellCount == v.size()-1) {
					int cellVal = i.intValue() >= 0 ? i.intValue() : 0;
					int invQty = v.get(1);
					dataCell.setCellValue((double)(invQty - cellVal));
//					dataCell.setCellStyle(deficitStyle);
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
	
}

package rootPackage;

import java.util.*;

import javax.swing.table.DefaultTableModel;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
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
	
	
	public InStock(DataTable table, Workbook wb, ArrayList<WorkOrderWorkbook> list) {
		tableModel = table.getTableModel();
		tableData = tableModel.getDataVector();
		workbook = wb; 
		workOrderWBList = list;
	}
	
	
	@Override
	public void createList() {
		shortageSheet = workbook.createSheet("Shortage List");
		WorkOrderWorkbook testWorkbook = workOrderWBList.get(0); 
		for(int i = 0; i < testWorkbook.getPartStartRow(); i++) {
			copyRow(testWorkbook, i, i);
		}
		autoSizeColumns();
		
	}
	
	public void autoSizeColumns() {
		for(int i = 0; i < 20; i++) {
			shortageSheet.autoSizeColumn(i); 
		}
	}
	
	public void createHeader(WorkOrderWorkbook wb) {
		
		
	}
	
	public void createShortageLists() {
		
	}
	
	private Row copyRow(WorkOrderWorkbook sourceWB, int source, int destination) {
		Row destinationRow = shortageSheet.createRow(destination);
		Row sourceRow = sourceWB.getSheet().getRow(source);
		
		for (int i = 0; i < sourceRow.getLastCellNum(); i++) {
			Cell oldCell = sourceRow.getCell(i);
			Cell newCell = destinationRow.createCell(i);
			
			// If the old cell is null jump to next cell
	        if (oldCell == null) {
	            newCell = null;
	            continue;
	        }

	        // Copy style from old cell and apply to new cell
	        CellStyle newCellStyle = workbook.createCellStyle();
	        newCellStyle.cloneStyleFrom(oldCell.getCellStyle());
	        newCell.setCellStyle(newCellStyle);

	        // If there is a cell comment, copy
	        if (oldCell.getCellComment() != null) {
	            newCell.setCellComment(oldCell.getCellComment());
	        }

	        // If there is a cell hyperlink, copy
	        if (oldCell.getHyperlink() != null) {
	            newCell.setHyperlink(oldCell.getHyperlink());
	        }
	        
	        // Set the cell data type
	        newCell.setCellType(oldCell.getCellType());

	        // Set the cell data value
	        switch (oldCell.getCellType()) {
	            case Cell.CELL_TYPE_BLANK:
	                newCell.setCellValue(oldCell.getStringCellValue());
	                break;
	            case Cell.CELL_TYPE_BOOLEAN:
	                newCell.setCellValue(oldCell.getBooleanCellValue());
	                break;
	            case Cell.CELL_TYPE_ERROR:
	                newCell.setCellErrorValue(oldCell.getErrorCellValue());
	                break;
	            case Cell.CELL_TYPE_FORMULA:
	                newCell.setCellFormula(oldCell.getCellFormula());
	                break;
	            case Cell.CELL_TYPE_NUMERIC:
	                newCell.setCellValue(oldCell.getNumericCellValue());
	                break;
	            case Cell.CELL_TYPE_STRING:
	                newCell.setCellValue(oldCell.getRichStringCellValue());
	                break;
	        }
		}
		
		return destinationRow;
	}
	
}

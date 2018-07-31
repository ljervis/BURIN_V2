package rootPackage;

import java.util.*;

import javax.swing.table.DefaultTableModel;

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
	}
	
	
	@Override
	public void createList() {
		shortageSheet = workbook.createSheet("Shortage List");
		
	}
	
	public void createWorkOrderLists() {
		
	}
	
}

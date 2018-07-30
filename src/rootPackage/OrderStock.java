package rootPackage;

import java.util.*;

import javax.swing.table.DefaultTableModel;

import views.DataTable;

public class OrderStock implements Stock {

	ArrayList<WorkOrderWorkbook> workOrderWBList;
	DefaultTableModel dataModel;
	Vector<Vector<Integer>> tableData;
	
	public OrderStock(ArrayList<WorkOrderWorkbook> list, DataTable table) {
		dataModel = table.getTableModel();
		workOrderWBList = list;
		tableData = dataModel.getDataVector();
	}
	
	@Override
	public void createList() {}
	
}

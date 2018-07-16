package rootPackage;

import java.io.File;

import org.apache.poi.openxml4j.opc.OPCPackage;

public class Driver {
	
	public static void main(String args[]) {	
		
		try {
			
			//Create new workbooks
			WorkbookInterface workOrderWB = new WorkOrderWorkbook(".\\src\\Files\\160A110V.xlsx");
			WorkbookInterface inventoryWB = new InventoryWorkbook(".\\src\\Files\\InventoryWorkbook.xlsx");
			
			//Set the first sheet in the workbooks as active
			workOrderWB.setSheet(0);
			inventoryWB.setSheet(0);
			
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
//		try {
//			//Gives full control of the workbook life cycle
//			OPCPackage invPkg = OPCPackage.open(new File(".\\src\\Files\\INVENTORY WORK BOOK _2018.xlsx"));
//			OPCPackage workPkg = OPCPackage.open(new File(".\\src\\Files\\160A110V.xlsx"));
//			
//			// Create new workbooks
//			WorkbookInterface inventoryWB = new InventoryWorkbook(invPkg);
//			WorkbookInterface workOrderWB = new WorkOrderWorkbook(workPkg);
//			
//			
//			// Close workbooks
//			invPkg.close();
//			workPkg.close();
//			
//		} catch (Exception e) {
//			
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			
//		}
		
	}

}

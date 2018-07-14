package rootPackage;

import java.io.File;

import org.apache.poi.openxml4j.opc.OPCPackage;

public class Driver {
	
	public static void main(String args[]) {
		
		try {
			//Gives full control of the workbook life cycle
			OPCPackage invPkg = OPCPackage.open(new File(".\\src\\Files\\INVENTORY WORK BOOK _2018.xlsx"));
			OPCPackage workPkg = OPCPackage.open(new File(".\\src\\Files\\160A110V.xlsx"));
			
			// Create new workbooks
			Workbook inventoryWB = new InventoryWorkbook(invPkg);
			Workbook workOrderWB = new WorkOrderWorkbook(workPkg);
			
			
			// Close workbooks
			invPkg.close();
			workPkg.close();
			
		} catch (Exception e) {
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
	}

}

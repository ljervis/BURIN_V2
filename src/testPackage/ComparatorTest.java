package testPackage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rootPackage.Comparator;
import rootPackage.InventoryWorkbook;
import rootPackage.WorkOrderWorkbook;

class ComparatorTest {
	
	

	WorkOrderWorkbook workOrderWB;
	InventoryWorkbook inventoryWB;
	HashMap<Integer,Integer> invList;
	HashMap<Integer,Integer> workList;
	Comparator comp;

	@BeforeEach
	void setUp() throws Exception {
		workOrderWB = new WorkOrderWorkbook(".\\src\\Files\\160A110V.xlsx");
		inventoryWB = new InventoryWorkbook(".\\src\\Files\\InventoryWorkbook.xlsx");
		
		
		workOrderWB.setSheet(0);
		inventoryWB.setSheet(0);
		
		
		workOrderWB.setPartNumStartRow(workOrderWB.checkPartNum());
		workOrderWB.setFloorStockStartRow(workOrderWB.checkFloorStock());
		workOrderWB.read();
		
		inventoryWB.setPartNumStartRow(inventoryWB.checkPartNum());
		inventoryWB.read();
		
		invList = inventoryWB.getPartList();
		workList = workOrderWB.getPartList();
		
		comp = new Comparator(invList, workList);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void populateLists() {
		comp.compare(0);
		comp.print();
	}

}

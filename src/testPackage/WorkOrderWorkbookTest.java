package testPackage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rootPackage.WorkOrderWorkbook;
import rootPackage.WorkbookInterface;

class WorkOrderWorkbookTest {
	
	WorkOrderWorkbook validWB;
	WorkOrderWorkbook invalidWB;

	@BeforeEach
	void setUp() throws Exception {
		validWB = new WorkOrderWorkbook(".\\src\\Files\\160A11V.xlsx");
		invalidWB = new WorkOrderWorkbook(".\\src\\Files\\InvalidWorkbook.xlsx");
		
		
		validWB.setSheet(0);
		invalidWB.setSheet(0);
	}
	
	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void checkHeaderValid() {
		assertTrue(validWB.isValid());
	}
	
	@Test
	void checkHeaderInvalid() {
		assertFalse(invalidWB.isValid());
	}
	
	@Test
	void checkPartNumValid() {
		assertEquals(1, validWB.checkPartNum());
	}
	
	@Test
	void checkPartNumInvalid() {
		assertEquals(-1, invalidWB.checkPartNum());
	}
	
	@Test
	void checkFloorStockValid() {
		assertEquals(7, validWB.checkFloorStock());
	}
	
	@Test
	void checkFloorStockInvalid() {
		assertEquals(-1, invalidWB.checkFloorStock());
	}
	
	@Test
	void checkIsValid() {
		assertTrue(validWB.isValid());
		assertFalse(invalidWB.isValid());
	}
	
	@Test
	void partListPopulationValid() {
		validWB.partNumStartRow = validWB.checkPartNum();
		validWB.floorStockStartRow = validWB.checkFloorStock();
		validWB.read();
		validWB.printPartList();
	}
}
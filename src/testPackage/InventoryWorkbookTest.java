package testPackage;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import rootPackage.InventoryWorkbook;

class InventoryWorkbookTest {
	
	InventoryWorkbook validWB;
	InventoryWorkbook invalidWB;

	@BeforeEach
	void setUp() throws Exception {
		validWB = new InventoryWorkbook(".\\src\\Files\\InventoryWorkbookTest.xlsx");
		invalidWB = new InventoryWorkbook(".\\src\\Files\\InvalidWorkbook.xlsx");
		
		
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
	void partListPopulationValid() {
		validWB.partNumStartRow = validWB.checkPartNum();
		validWB.read();
		validWB.printPartList();
	}

}

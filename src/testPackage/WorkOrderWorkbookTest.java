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
		validWB = new WorkOrderWorkbook(".\\src\\Files\\160A110V.xlsx");
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
	void checkStartValid() {
		assertEquals(8, validWB.checkStart());
	}
	
	@Test
	void checkStartInvalid() {
		assertEquals(-1, invalidWB.checkStart());
	}
	
	@Test
	void checkEndValid() {
		assertEquals(20, validWB.checkEnd());
	}
	
	@Test
	void checkEndInvalid() {
		assertEquals(-1, invalidWB.checkEnd());
	}
	
	@Test
	void checkIsValid() {
		assertTrue(validWB.isValid());
		assertFalse(invalidWB.isValid());
	}
	
	@Test
	void partListPopulationValid() {
		System.out.println("PartList test");
		validWB.setStartRow(validWB.checkStart());
		validWB.setEndRow(validWB.checkEnd());
		validWB.read();
		validWB.printPartList();
	}
	
	@Test
	void multiplier() {
		System.out.println("multiplier test");
		validWB.setStartRow(validWB.checkStart());
		validWB.setEndRow(validWB.checkEnd());
		validWB.setMultiplier(2);
		validWB.read();
		validWB.printPartList(); 
	}
}

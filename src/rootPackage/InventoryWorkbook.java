package rootPackage;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class InventoryWorkbook implements WorkbookInterface {

	Workbook workbook;
	Sheet sheet;
	
	public InventoryWorkbook(OPCPackage pgk) throws Exception {
		workbook = new XSSFWorkbook(pgk);
	}
	
	public InventoryWorkbook(String file) throws Exception {
		
		InputStream inp = new FileInputStream(file);
		workbook = WorkbookFactory.create(inp);
	}
	
	@Override 
	public void setSheet(int index) {
		sheet = workbook.getSheetAt(index);
	}
	
	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void read() {
		// TODO Auto-generated method stub

	}
}

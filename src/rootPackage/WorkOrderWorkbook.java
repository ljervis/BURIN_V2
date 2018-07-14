package rootPackage;

import java.io.IOException;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class WorkOrderWorkbook implements Workbook {
	
	XSSFWorkbook workbook;
	
	public WorkOrderWorkbook(OPCPackage pgk) throws IOException {
		workbook = new XSSFWorkbook(pgk);
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

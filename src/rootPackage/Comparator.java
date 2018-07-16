package rootPackage;

import java.util.HashMap;

public class Comparator {
	
	HashMap<Integer,Integer> inventoryList;
	HashMap<Integer,Integer> workOrderList;
	HashMap<Integer,Integer> inStockList;
	HashMap<Integer,Integer> outOfStockList;
	HashMap<Integer,Integer> otherList;
	
	public Comparator(HashMap<Integer,Integer> inv, HashMap<Integer,Integer> work) {
		inventoryList = inv;
		workOrderList = work;
	}

	public void compare(int multiple) {
		
	}
	
}

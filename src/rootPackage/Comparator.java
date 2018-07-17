package rootPackage;

import java.util.ArrayList;
import java.util.HashMap;

public class Comparator {
	
	HashMap<Integer,Integer> inventoryList;
	
	ArrayList<HashMap<Integer,Integer>> workOrderList;
	
	public Comparator(HashMap<Integer,Integer> inv) {
		workOrderList = new ArrayList<HashMap<Integer,Integer>>();
		inventoryList = inv;
	}
	
	public void addWorkOrderList(String ID, HashMap<Integer,Integer> list) { workOrderList.add(list); }

	/*
	 * Calculate and display parts and stock values 
	 */
	public void updateDisplay() {
		
		
	}
	
}

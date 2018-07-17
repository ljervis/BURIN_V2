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
		inStockList = new HashMap<Integer,Integer>();
		outOfStockList = new HashMap<Integer,Integer>();
		otherList = new HashMap<Integer,Integer>();
	}

	public void compare(int multiple) {
		
		for(Integer part : workOrderList.keySet()) {
			if(inventoryList.get(part) == null) { otherList.put(part, workOrderList.get(part)); }
			else {
				int diff = inventoryList.get(part) - workOrderList.get(part);
				if(diff < 0) { outOfStockList.put(part, diff); }
				else { inStockList.put(part, diff); }
			}
		}
	}
	
	public void print() {
		
		System.out.println("In Stock");
		for (Integer part : inStockList.keySet()) {
			System.out.println(part + " " + inStockList.get(part));
		}
		System.out.println("Out Of Stock");
		for (Integer part : outOfStockList.keySet()) {
			System.out.println(part + " " + outOfStockList.get(part));
		}
		System.out.println("Other");
		for (Integer part : otherList.keySet()) {
			System.out.println(part + " " + otherList.get(part));
		}
	}
}

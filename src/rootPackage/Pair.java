package rootPackage;

/**
 * @author Luke <a href="mailto:lukejervis14@gmail.com">lukejervis14@gmail.com</a>
 *
 */
public class Pair {
	
	/**
	 * Public fields used in this class
	 */
	public Integer first;
	public Integer second;
	public Integer third;
	
	/**
	 * Creates a simple object to store and access two public Integers 
	 * @param f The first Integer 
	 * @param s The second Integer
	 */
	public Pair(Integer f, Integer s) {
		first = f;
		second = s;
	}
	
	/**
	 * Creates a simple object to store and access two public Integers 
	 * @param f The first Integer 
	 * @param s The second Integer
	 * @param t The third Integer
	 */
	public Pair(Integer f, Integer s, Integer t) {
		first = f;
		second = s;
		third = t;
	}
}

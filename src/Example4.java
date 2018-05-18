public class Example4 {
	public static Car m1(int j) {
		Car b = new Car();		
		if (j == j * 2 + 1) {
			b.setSpeed(j);
		}
		Car c = b;
		return c;
	}
}

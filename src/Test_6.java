public class Test_6 {
	public static Car m5( int n) {
		Car car = new Car();
		
		if (n > 0) {
			int q = -n;
			car.setSpeed(q);
		} else {
			car.setSpeed(n);
		}
		
		return car;
	}
}

public class Test_6 {
	public static Car m5(int a, int n) {
		Car car = new Car();
		if (n >= 0) {
			car.setSpeed(-n);
		} else {
			car.setSpeed(n);
		}
		
		return car;
	}
}

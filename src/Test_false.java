public class Test_false {
	public static Car m2(int a) {
		Car car = new Car();
		if (1 < 0) {
			car.setSpeed(42);
		}
		return car;
	}
}


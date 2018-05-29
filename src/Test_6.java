public class Test_6 {
	public static Car m5(int a, int n) {
		Car car = new Car();
		if (n >= 0) {
			a = -n;
		} else {
			a = n;
		}
		car.setSpeed(a);
		return car;
	}
}

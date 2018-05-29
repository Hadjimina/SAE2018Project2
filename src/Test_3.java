public class Test_3 {
	public static Car m3(int a, int b) {
		Car car = new Car();
		if (a * b < 5 && a * b > -5 && b != 0) {
			car.setSpeed(a + 1);
		}
		return car;
	}
}

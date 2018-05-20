public class Test_3 {
	public static void m3(int a, int b) {
		Car car = new Car();
		if (a * b < 5 && a * b > -5 && b != 0) {
			car.setSpeed(a + 1);
		}
	}
}

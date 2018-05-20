public class Test_2 {
	public static void m2(int a) {
		Car car = new Car();
		if (2*a < 9 && a > 0) {
			car.setSpeed(a);
			if (a > 5) {
				car.setSpeed(a + 4);
			}
		}
	}
}


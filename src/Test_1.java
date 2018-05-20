public class Test_1 {
	public static void m1(int j) {
		Car car = new Car();
		if (j > 2 && j < 6) {
			car.setSpeed(j - 2);
			j = 5;
		}
		else {
			car.setSpeed(j);
			j = 5;
		}
		j = j + 1;
	}
}

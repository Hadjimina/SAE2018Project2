// SHOULD NOT BE SAFE

public class Test_leftGreaterRight4 {
	public static Car m1(int j) {
		Car car = new Car();
		int k = 8;
		if (j > 5 && j < 10) {
			car.setSpeed(j);
		}
		return car;
	}
}

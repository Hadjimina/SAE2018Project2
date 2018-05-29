// Should be safe

public class Test_leftGreaterRight3 {
	public static Car m1(int j) {
		Car car = new Car();
		if (j > 5 && j < 10) {
			car.setSpeed(j);
		}
		return car;
	}
}

public class Test_multiplicationInputVars {
	public static Car m1(int a, int b) {
		Car car = new Car();
		int x = a * b;
		car.setSpeed(x);
		return car;
	}
}

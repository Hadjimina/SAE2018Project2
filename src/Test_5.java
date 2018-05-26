public class Test_5 {
	public static Car m5(int a, int n) {
		Car car = new Car();
		a = n + 1;
		if (n >= 0) {
			a++;
		} else {
			a--;
		}
		car.setSpeed(a - n);
		return car;
	}
}

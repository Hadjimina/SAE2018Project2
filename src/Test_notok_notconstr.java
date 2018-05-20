public class Test_notok_notconstr {
	public static void m1(int j) {
		Car car = new Car();
		int i = 1;
		int f = 1;
		int q = j * j;
		int v = j * f;
		car.setSpeed(q);
		car.setSpeed(v);
	}
}

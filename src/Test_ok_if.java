public class Test_ok_if {
	public static void m1(int j) {
		Car car = new Car();
		int i = 0;
		if (j > 2 && j < 6) {
			car.setSpeed(j - 2);			
		} else if (j > -2 && j < 6){
			car.setSpeed(j);
		}
		car.setSpeed(i);
	}
}

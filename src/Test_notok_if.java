public class Test_notok_if {
		public static Car m1(int j) {
			Car car = new Car();
			if (j > 2) {
				car.setSpeed(j - 2);			
			} else {
				car.setSpeed(j);
			}
			return car;
		}
}

public class Test_notok_while2 {
		public static void m1(int j) {
			Car car = new Car();
			int i = 0;
			while (i < 5) {
				i++;			
			} 
			car.setSpeed(i-2);
		}
}

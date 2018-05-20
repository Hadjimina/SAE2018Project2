public class Test_notok_while1 {
		public static void m1(int j) {
			Car car = new Car();
			int i = 0;
			int f = i;
			while (f < 5) {
				car.setSpeed(f*2);
				f++;
			} 
		}
}

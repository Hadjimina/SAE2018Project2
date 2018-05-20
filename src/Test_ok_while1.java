//Tests while stmt, should be ok
public class Test_ok_while1 {
		public static void m1(int j) {
			Car car = new Car();
			int i = 0;
			int f = i;
			while (f < 5) {
				car.setSpeed(f);			
			} 
		}
}

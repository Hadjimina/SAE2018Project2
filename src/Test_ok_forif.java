public class Test_ok_forif {
	public static void m1(int j) {
		Car car = new Car();
		int i = 8;

		for(int k = 1; k != i && j >= 0; k = k*2){
			car.setSpeed(k);
			if(j == k){
				car.setSpeed(j);
			}
		}
	}
}


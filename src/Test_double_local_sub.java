public class Test_double_local_sub {
	public static Car m2(int a, int b) {
		Car car = new Car();
		
		if( b > 0&&a < 10 && b < 5){
			car.setSpeed(a-b);		
		}
		return car;
	}
}


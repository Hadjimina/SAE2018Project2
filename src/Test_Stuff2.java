public class Test_Stuff2 {
	public static Car m1(int j) {
		Car car = new Car();
		
		car.setSpeed(3);
		if(j<0) {
			car.setSpeed(2);
		} else {
			car.setSpeed(1);
		}
		
		
		return car;
	
			
	}
}
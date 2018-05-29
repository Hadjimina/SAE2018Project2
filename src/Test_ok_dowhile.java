public class Test_ok_dowhile {
	public static Car m1(int j) {
		Car car = new Car();
		int i = 8;

		do{
			car.setSpeed(i);
			i *= -1;
			
		}while(i==8);
		return car;
	}
	
}

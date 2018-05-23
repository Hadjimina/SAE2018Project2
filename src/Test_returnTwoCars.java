//weldAt not safe
//weltBetween safe
public class Test_returnTwoCars {
	public static Car m1(int j) {
		Car car1 = new Car();
		Car car2 = new Car();
		
		car1.setSpeed(2);
		car2.setSpeed(-10);
		return car2;
	}
	
	
}

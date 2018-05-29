//Why NO SPEED SET
public class Test_globalVar {
	static int global = 53;
	
	public static Car m1(int j) {
		global++;
		Car car = new Car();
		car.setSpeed(global);
		return car;
	}
}

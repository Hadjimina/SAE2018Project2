//Why NO SPEED SET
public class Test_globalVar {
	static int global = 53;
	
	public static void m1(int j) {
		global++;
		Car car = new Car();
		car.setSpeed(global);
	}
}

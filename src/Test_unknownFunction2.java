//weldAt not safe
//weltBetween safe
public class Test_unknownFunction2 {
	public static void m1(int j) {
		Car car1 = new Car();
		f();
		car1.setSpeed(2);					
	}
	
	public static void f() {
		Car car2 = new Car();
		car2.setSpeed(-10);
	}
}

public class Test_return_soon {
	public static Car m2(int a) {
		Car c = new Car();
		c.setSpeed(-1);
		if(a > 0){
			return c;
		}

		c.setSpeed(a);

		return c;
	}
}


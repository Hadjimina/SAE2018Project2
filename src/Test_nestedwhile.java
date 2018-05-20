public class Test_nestedwhile {
	public static void m1(int j) {
		Car car = new Car();
		int i = 8;
		while(i>0)
		{
			while(j <= 0 && i > 0){
				while( j > -8 && i > 0){
					i--;
					car.setSpeed(j);
				}
			}
		}
		car.setSpeed(i);
	}
}

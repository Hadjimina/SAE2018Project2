//weldAt not safe
//weltBetween safe
public class Test_returnTesting {
	public static Car m1(int j) {
		Car car1 = new Car();
		Car car2 = new Car();
		if(j < 0)
		{
			car1.setSpeed(2);
			return car1;
		}else{
			car2.setSpeed(3);
			return car2;
		}
		
		
		
	}
	
	
}

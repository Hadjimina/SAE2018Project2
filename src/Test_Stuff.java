public class Test_Stuff {
		public static Car m1(int j) {
			Car car = new Car();
			Car car2 = new Car();
			
			car.setSpeed(6);
			car2.setSpeed(5);
			
			
			car2 = car;
			car2.setSpeed(3);
			return car2;
		
				
		}
	}

	/*
	public static Car m1(int j) {
		Car car = new Car();
		Car car2 = new Car();
		
		car.setSpeed(1);
		car2.setSpeed(2);
		
		if (j > 5) {
			car2 = car;
			return car2;
		}
		else 
		{
			return car;
		}	
	}
}
*/

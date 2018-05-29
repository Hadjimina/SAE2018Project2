public class Test_returnTwoCars2 {
	public static Car m1(int j) {
		Car car1 = new Car();
		Car car2 = new Car();
		Car car3 = new Car();
		
		
		if(j < 10 && j> 10){ 
			car1.setSpeed(j);
			return car1;
		}else if(j < 10  ){ 
			car2.setSpeed(j);
			return car2;
		}else{
			car3.setSpeed(j);
			return car3;
		}
		
		
		
	}
	
	
}

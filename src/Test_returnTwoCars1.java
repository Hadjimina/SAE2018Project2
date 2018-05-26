//weldAt not safe
//weltBetween safe
public class Test_returnTwoCars1 {
	public static Car m1(int j) {
		Car car1 = new Car();
		Car car2 = new Car();
		Car car3 = new Car();
		
		
		if(j < 10 && j> 10){ 
			car1.setSpeed(1);
			return car1;
		}else if(j < 10  ){ 
			car2.setSpeed(2);
			return car2;
		}else{
			car3.setSpeed(3);
			return car3;
		}
		
		
		
	}
	
	
}

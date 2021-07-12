package main;



import controller.ControllerDeliverable1;
import controller.ControllerDeliverable2;


public class MainControl {


	public static void main(String[] args) throws Exception  {
		
		
		String projName1 = "BOOKKEEPER";
		String projName2 = "ZOOKEEPER";
		String path = "./";
		var controllerDeliverable1 = new ControllerDeliverable1();
		var controllerDeliverable2 = new ControllerDeliverable2();

		
		/* ****************** BOOKKEEPER ***************** */
		
		
		controllerDeliverable1.run(projName1, path+projName1); //Deliverable1 --> creation of the dataset with the check oof the bugginess
		
		
		controllerDeliverable2.run(projName1, controllerDeliverable1.getCsvFileName()); //Deliverable2 --> analyze data with machine learning
		
		/* ****************** ZOOKEEPER ***************** */
		
		
		controllerDeliverable1.run(projName2, path+projName2); //Deliverable1 --> creation of the dataset with the check of the bugginess
		
		
		controllerDeliverable2.run(projName2, controllerDeliverable1.getCsvFileName()); //Deliverable2 --> analyze data with machine learning

		
	}
	
	

	
	
	
	
	
	

}

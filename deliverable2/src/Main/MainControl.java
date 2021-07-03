package Main;



import Controller.ControllerDeliverable1;
import Controller.ControllerDeliverable2;
import Utility.ProjectLogger;


public class MainControl {

	private static ProjectLogger logCTR;
	
	public static void main(String[] args) throws Exception  {
		
		
		String PROJNAME1 = "BOOKKEEPER";
		String PROJNAME2 = "ZOOKEEPER";
		String path = "./";
		logCTR = ProjectLogger.getSingletonInstance();
		var controllerDeliverable1 = new ControllerDeliverable1();
		var controllerDeliverable2 = new ControllerDeliverable2();

		
		/* ****************** BOOKKEEPER ***************** */
		
		
		controllerDeliverable1.run(PROJNAME1, path+PROJNAME1); //Deliverable1 --> creation of the dataset with the check oof the bugginess
		
		
		controllerDeliverable2.run(PROJNAME1, controllerDeliverable1.getCsvFileName()); //Deliverable2 --> analyze data with machine learning
		
		/* ****************** ZOOKEEPER ***************** */
		
		
		controllerDeliverable1.run(PROJNAME2, path+PROJNAME2); //Deliverable1 --> creation of the dataset with the check of the bugginess
		
		
		controllerDeliverable2.run(PROJNAME2, controllerDeliverable1.getCsvFileName()); //Deliverable2 --> analyze data with machine learning

		
	}
	
	

	
	
	
	
	
	

}

package Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Utility.ProjectLogger;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;

public class ControllerDeliverable2 {
	
	private Instances instances;
	//private List<Metrics> listMetrics;


	public ControllerDeliverable2() {}
	
	
	public void run(String projectName, String fileCsv) throws SecurityException, IOException {
		
		ProjectLogger.getSingletonInstance().saveMess("[*]Getting csv File: " +fileCsv+" and starting machine learning processing");
		
		//creation of he arff file taking data from csv file generated in the first part
		ArffSaver arffSav = getInstances(fileCsv, projectName);
		
		preProcess(arffSav);
		
		
		
	}


	private void preProcess(ArffSaver arffSav) {
		
		
		
	}


	private ArffSaver getInstances(String fileCsv, String projectName) throws SecurityException, IOException {
		CSVLoader csvLoader = new CSVLoader();
		try { 
			csvLoader.setSource( new File(fileCsv) ); 
		    this.instances = csvLoader.getDataSet();
		} 
	    catch (IOException e) { 
	    	ProjectLogger.getSingletonInstance().saveMess("[X] Error on loading csv file");
	    	System.exit(0);
	    }
		ArffSaver arffSav = new ArffSaver();
		arffSav.setInstances(this.instances);
		
		try {
	    	arffSav.setFile(new File(projectName + "Metrics.arff"));
	    	arffSav.writeBatch(); 
	    	ProjectLogger.getSingletonInstance().saveMess("arff file correctly creeated");
		} 
	    catch (IOException e) { 
	    	ProjectLogger.getSingletonInstance().saveMess("[X]Error on writing arff file");
	    }
		
		return arffSav;
	}
	

}

package Controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import Entity.Walk;
import weka.filters.Filter;

import Utility.ProjectLogger;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Normalize;

public class ControllerDeliverable2 {
	
	private Instances dataSet;
	//private List<Metrics> listMetrics;
	private List<Walk> walks;

	public ControllerDeliverable2() {}
	
	
	public void run(String projectName, String fileCsv) throws SecurityException, IOException {
		
		ProjectLogger.getSingletonInstance().saveMess("[*]Getting csv File: " +fileCsv+" and starting machine learning processing");
		
		//creation of he arff file taking data from csv file generated in the first part
		getInstances(fileCsv, projectName);
		
		preProcess();
		
		//get number of last walk of the Walk Forward
		int lastWalk = (int) this.dataSet.get(this.dataSet.size()-1).value(0);
		
		//instantiation of walks
		
		for (int i=0; i<lastWalk; i++) {
			walks.add(new Walk(i, this.dataSet));
		}
		
		
		
	}


	private void preProcess() throws SecurityException, IOException {
		
		//delete filename attribute because it is dangerous for the data analysis
		this.dataSet.deleteAttributeAt(2);
		
		//normalizing the dataset to make sure that each attribute has the same weight for the model train
		Normalize norm = new Normalize();
		
		try {
			
			//E' necessario impostare il classindex a 0 per evitare di normalizzare anche il numero di versione
			this.dataSet.setClassIndex(0);
			norm.setInputFormat(this.dataSet);
			
			this.dataSet = Filter.useFilter(this.dataSet, norm);
			
			this.dataSet.setClassIndex(this.dataSet.numAttributes() - 1);
			
			
		}
		catch (Exception e) {
			
			ProjectLogger.getSingletonInstance().saveMess("[X]Error in the dataset normalization");

			System.exit(0);
		}
		

		
	}


	private void getInstances(String fileCsv, String projectName) throws SecurityException, IOException {
		CSVLoader csvLoader = new CSVLoader();
		try { 
			csvLoader.setSource( new File(fileCsv) ); 
		    this.dataSet = csvLoader.getDataSet();
		} 
	    catch (IOException e) { 
	    	ProjectLogger.getSingletonInstance().saveMess("[X] Error on loading csv file");
	    	System.exit(0);
	    }
		ArffSaver arffSav = new ArffSaver();
		
		arffSav.setInstances(this.dataSet);
		
		try {
	    	arffSav.setFile(new File(projectName + "Metrics.arff"));
	    	arffSav.writeBatch(); 
	    	ProjectLogger.getSingletonInstance().saveMess("arff file correctly created");
		} 
	    catch (IOException e) { 
	    	ProjectLogger.getSingletonInstance().saveMess("[X]Error on writing arff file");
	    }
		
	}
	

}

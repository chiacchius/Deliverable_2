package Controller;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Entity.ModelMetrics;
import Entity.Walk;
import Handler.FilterHandler;
import Handler.SamplingHandler;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SpreadSubsample;
import Utility.CsvWriter;
import Utility.ProjectLogger;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.FilteredClassifier;
import weka.filters.supervised.instance.SMOTE;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVLoader;
import weka.filters.unsupervised.attribute.Normalize;

public class ControllerDeliverable2 {
	
	private Instances dataSet;
	private List<ModelMetrics> modelMetrics= new ArrayList<>();
	private List<Walk> walks = new ArrayList<>();
	private String projName;

	public ControllerDeliverable2() {}
	
	
	public void run(String projectName, String fileCsv) throws Exception {
		
		ProjectLogger.getSingletonInstance().saveMess("[*]Getting csv File: " +fileCsv+" and starting machine learning processing");
		this.projName = projectName;
		//creation of he arff file taking data from csv file generated in the first part
		getInstances(fileCsv, projectName);
		
		preProcess();
		
		//get number of last walk of the Walk Forward
		int lastWalk = (int) this.dataSet.get(this.dataSet.size()-1).value(0);
		System.out.println(lastWalk);
		
		//instantiation of walks
		
		for (int i=1; i<lastWalk; i++) {
			this.walks.add(new Walk(i, this.dataSet));
		}
		
		
		
		
		for (Walk walk: this.walks) {
			walkEvaluation(walk);
		}
		
		CsvWriter.writeSecondCsv(projectName, this.modelMetrics);
		
		
		
	}


	private void walkEvaluation(Walk walk) throws Exception {
		//classifier choice
		List<String> classifiers = new ArrayList<>();
		classifiers.add("Random Forest");
		classifiers.add("IBk");
		classifiers.add("Naive Bayes");
		
		AbstractClassifier classifier = null;
		
		
		for (int i =0; i<classifiers.size(); i++) {
			
			
			switch (classifiers.get(i)) {
				
				case "Random Forest": 
					classifier = new RandomForest();
					ProjectLogger.getSingletonInstance().saveMess("classifier choose : Random Forest");
				
				
				break;
				
				
				case "IBk": 
					classifier = new IBk();
					ProjectLogger.getSingletonInstance().saveMess("classifier choose : IBk");
				
				break;
				
				
				case "Naive Bayes": 
					classifier = new NaiveBayes();
					ProjectLogger.getSingletonInstance().saveMess("classifier choose : Naive Bayes");
				
				break;
				
				default: ProjectLogger.getSingletonInstance().saveMess("[X] Error in the classifier selection");
				
				System.exit(1);
				break;
			
			
			
			
			
			
			}
			
			chooseFeatureSelection(classifier, classifiers.get(i), walk);
			
			
		}
		
		
		
		
	}


	private void chooseFeatureSelection(AbstractClassifier classifier, String classifierName, Walk walk) throws Exception {
		
		List<String> features = new ArrayList<>();
		
		features.add("No selection");
		features.add("CFS with Best First");
		features.add("Wrapper with Best First");
		
		
		for (int i=0; i<features.size(); i++) {
			
			Filter filter = null;
			Instances filteredTrainSet = null;
			Instances filteredTestSet = null;
			
			
			
			switch(features.get(i)){
				
				case "No selection":
					
				break;
				
				
				case "CFS with Best First": 
					filter = FilterHandler.cfs(walk.getTrainSet());
					
				break;
				
				
				case "Wrapper with Best First": 
					filter = FilterHandler.wrapper(walk.getTrainSet());
					
				break;
				
				default:
					ProjectLogger.getSingletonInstance().saveMess("[X] Error in the feature selection");
					System.exit(1);
				break;
				
					
			
			}
			
			
			if (filter!=null) {
				
				try {
					filteredTrainSet = Filter.useFilter(walk.getTrainSet(), filter);
					filteredTestSet = Filter.useFilter(walk.getTestSet(), filter);				
				} catch (Exception e) {
					ProjectLogger.getSingletonInstance().saveMess("[X] Error in the application of the filters");
					System.exit(1);
				}
				
				filteredTrainSet.setClassIndex(filteredTrainSet.numAttributes() - 1);
				filteredTestSet.setClassIndex(filteredTestSet.numAttributes() - 1);
				
			}
			
			Instances trainingSet;
			Instances testingSet;
			
			
			if (filteredTrainSet==null) {
				ProjectLogger.getSingletonInstance().saveMess(features.get(i) + " filteredTrainSet null");
				trainingSet = walk.getTrainSet(); 
			}
			else {
				trainingSet = filteredTrainSet;
			}
			
			if (filteredTestSet==null) {
				ProjectLogger.getSingletonInstance().saveMess(features.get(i) + " filteredTestSet null");
				testingSet = walk.getTestSet(); 
			}
			else {
				testingSet =filteredTestSet;
			}
			
			chooseSamplingSelection(classifier, classifierName, features.get(i), walk, trainingSet, testingSet);
			

			
		}
		
	}


	private void chooseSamplingSelection(AbstractClassifier classifier, String classifierName, String featureName, Walk walk, Instances trainSet, Instances testSet) throws Exception {
		
		List<String> samplings = new ArrayList<>();
		
		samplings.add("No sampling");
		samplings.add("Oversampling");
		samplings.add("Undersampling");
		samplings.add("Smote");
		
		for (int i=0; i<samplings.size(); i++) {
			
			
			if (trainSet.numAttributes() <= 1 || (trainSet.numAttributes() <= 2  && trainSet.attribute(0).name().equals("Version ID"))) {
				
				break;
			}
			
			FilteredClassifier filteredClassifier = null;
			
			
			
			switch (samplings.get(i)) {
				
				case "No sampling": 
					
				break;
				
				case "Oversampling":
					Resample resample = SamplingHandler.oversampling(trainSet);
					
					filteredClassifier = new FilteredClassifier();
					filteredClassifier.setClassifier(classifier);
					filteredClassifier.setFilter(resample);
					
					try{trainSet = Filter.useFilter(trainSet, resample);}
					
					catch (Exception e) {
						
						ProjectLogger.getSingletonInstance().saveMess("[X]oversampling application error");
						
					}
					
					
				break;
				
				
				case "Undersampling":
					SpreadSubsample spreadSubsample = SamplingHandler.undersampling(trainSet);
					
					filteredClassifier = new FilteredClassifier();
					filteredClassifier.setClassifier(classifier);
					filteredClassifier.setFilter(spreadSubsample);
					
					try {trainSet = Filter.useFilter(trainSet, spreadSubsample);}
					
					catch (Exception e) {
						
						ProjectLogger.getSingletonInstance().saveMess("[X]undersampling aplication error");
						
					}
					
					
				break;
				
				
				case "Smote":
					
					SMOTE smote = SamplingHandler.smote(trainSet);
					
					filteredClassifier = new FilteredClassifier();
					filteredClassifier.setClassifier(classifier);
					filteredClassifier.setFilter(smote);
					
					try { 
						trainSet = Filter.useFilter(trainSet, smote);
					
					} 
					catch (Exception e) {
						
						ProjectLogger.getSingletonInstance().saveMess("[X]SMOTE application error");
						
					}
				
				
				break;
				
				default: ProjectLogger.getSingletonInstance().saveMess("[X] Error in the sampling selection");
			
				break;
			
			}
			
			
			Instances[] dataset = {trainSet, testSet};
			
			evaluate(classifier, filteredClassifier,
					classifierName, featureName, samplings.get(i), walk, dataset);
			
			
		}
		
		
		
	}


	private void evaluate(AbstractClassifier classifier, FilteredClassifier filteredClassifier, String classifierName,
			String featureName, String sampling, Walk walk, Instances[] dataset) throws SecurityException, IOException {

		Instances trainSet = dataset[0];
		Instances testSet = dataset[1];
		
		Evaluation evaluation = null; 
		try { evaluation = new Evaluation(testSet); }
		catch 
			(Exception e) { ProjectLogger.getSingletonInstance().saveMess("Error in initializing the Evaluator"); 
			System.exit(1);
		}
		
		int classIndex = trainSet.attribute(trainSet.numAttributes() - 1).indexOfValue("Yes");
		
		if(filteredClassifier != null)
		{
			
			try 
			{ 

				filteredClassifier.buildClassifier(trainSet); 
				evaluation.evaluateModel(filteredClassifier, testSet);
				
				
				
				
				
			}
			catch (Exception e) { 
				
				ProjectLogger.getSingletonInstance().saveMess("Error in the build of the filter classifier"); 
				System.exit(1);
				
			}
		}
		
		
		else 
		{
			
			try 
			{ 
				classifier.buildClassifier(trainSet); 
				
				evaluation.evaluateModel(classifier, testSet);
				
				
			}
			catch (Exception e) { 
				ProjectLogger.getSingletonInstance().saveMess("Failed to build unfiltered classifier"); 
		
				System.exit(1);
				
			}
		
		}
		
		if(!Double.isNaN(evaluation.precision(classIndex)) && !Double.isNaN(evaluation.recall(classIndex)) && !Double.isNaN(evaluation.areaUnderROC(classIndex)) &&!Double.isNaN(evaluation.kappa())) {
			
			String[] attributes = {classifierName, sampling, featureName};
			double[] percents = {walk.getPercentageTrain(), walk.getPercentageBugsTrain() , walk.getPercentageBugsTest() };
			int[] trueFalsePositiveNegative = {(int) evaluation.numTruePositives(classIndex), (int) evaluation.numTrueNegatives(classIndex), 
					(int) evaluation.numFalsePositives(classIndex), (int) evaluation.numFalseNegatives(classIndex)};
			double[] metrics = {evaluation.precision(classIndex), evaluation.recall(classIndex), evaluation.areaUnderROC(classIndex), evaluation.kappa()};
			
			this.modelMetrics.add(new ModelMetrics(this.projName, attributes, walk.getTrainId(), percents , trueFalsePositiveNegative, metrics));
		
		}
		else ProjectLogger.getSingletonInstance().saveMess("Invalid walk");
		
		
	}


	


	


	private void preProcess() throws SecurityException, IOException {
		
		//delete version name and filename attributes because they are dangerous for the data analysis
		
		this.dataSet.deleteAttributeAt(2);
		this.dataSet.deleteAttributeAt(1);
		
		
		
		
		//normalizing the dataset to make sure that each attribute has the same weight for the model train
		Normalize norm = new Normalize();
		
		try {
			
			//It is necessary to set the classindex to 0 to avoid normalizing the version number as well
			this.dataSet.setClassIndex(0);
			norm.setInputFormat(this.dataSet);
			
			this.dataSet = Filter.useFilter(this.dataSet, norm);
			
			this.dataSet.setClassIndex(this.dataSet.numAttributes() - 1);
			
			
		}
		catch (Exception e) {
			
			ProjectLogger.getSingletonInstance().saveMess("[X]Error in the dataset normalization");

			System.exit(1);
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
	    	System.exit(1);
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

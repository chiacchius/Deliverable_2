package entity;

import java.io.IOException;

import handler.SamplingHandler;
import utility.ProjectLogger;
import weka.core.Instances;

public class Walk {
	
	private int trainId;
	private Instances trainSet;
	private Instances testSet;
	private Double percentageTrain; //Indicates the percentage on which training is performed with respect to the whole dataset (# train_set / # dataset)
	
	private Double percentageBugsTrain;
	private Double percentageBugsTest;
	

	public Walk(int i, Instances dataSet) throws SecurityException, IOException {
		this.setTrainId(i);
		dataSetParser(i, dataSet); //parse dataSet to divide it into trainSet and testSet
		
		this.setPercentageBugsTrain(((double) SamplingHandler.getBugsNum(this.trainSet))/((double) trainSet.numInstances()));
		this.setPercentageBugsTest(((double) SamplingHandler.getBugsNum(this.testSet))/((double) testSet.numInstances()));
	}


	

	private void dataSetParser(int index, Instances dataset) throws SecurityException, IOException {
		
		
		int trainSetElementsNum=0;
		
		for (int i=0; i<dataset.size(); i++) {
			
			if (dataset.get(i).value(0)>index) {
				
				//the train set includes the realese files up to index 
				ProjectLogger.getSingletonInstance().saveMess("*********************************\nThe train set is up to the " + dataset.get(i-1).value(0) + "th release\n*********************************");
				trainSetElementsNum = i;
				
				this.trainSet = new Instances(dataset, 0, trainSetElementsNum);
				break;
			}
			
		}
	
		
		
		int testSetElementsNum = 0;
		
		
		for (int j=trainSetElementsNum; j<dataset.size(); j++) {
			//the test set includes all files of the realese after index
			if (dataset.get(j).value(0)>index+1) {
				break;
			}
			
			testSetElementsNum++;
			
		}
		
		this.testSet = new Instances(dataset, trainSetElementsNum, testSetElementsNum);
		
		
		this.setPercentageTrain((double) this.trainSet.size()/(double)dataset.size());

		
		
		
		
	}




	public Instances getTrainSet() {
		return this.trainSet;
	}




	public void setTrainSet(Instances trainSet) {
		this.trainSet = trainSet;
	}




	public Instances getTestSet() {
		return this.testSet;
	}




	public void setTestSet(Instances testSet) {
		this.testSet = testSet;
	}




	public Double getPercentageTrain() {
		return percentageTrain;
	}




	public void setPercentageTrain(Double percentageTrain) {
		this.percentageTrain = percentageTrain;
	}




	public Double getPercentageBugsTrain() {
		return percentageBugsTrain;
	}




	public void setPercentageBugsTrain(Double percentageBugsTrain) {
		this.percentageBugsTrain = percentageBugsTrain;
	}




	public Double getPercentageBugsTest() {
		return percentageBugsTest;
	}




	public void setPercentageBugsTest(Double percentageBugsTest) {
		this.percentageBugsTest = percentageBugsTest;
	}




	public int getTrainId() {
		return trainId;
	}




	public void setTrainId(int trainId) {
		this.trainId = trainId;
	}


	
	
	
}

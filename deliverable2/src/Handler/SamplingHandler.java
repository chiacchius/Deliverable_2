package Handler;

import java.io.IOException;

import Utility.ProjectLogger;
import weka.core.Instances;
import weka.filters.supervised.instance.Resample;
import weka.filters.supervised.instance.SMOTE;
import weka.filters.supervised.instance.SpreadSubsample;

public class SamplingHandler {

	public static Resample oversampling(Instances trainSet) throws SecurityException, IOException {
		Resample resample = null;
		
		try {
		
			resample = new Resample();
			resample.setInputFormat(trainSet);
			
			resample.setNoReplacement(false);
			resample.setBiasToUniformClass(1.0f);
			
			double percentMinority = getMinorityPercentage(getBugsNum(trainSet), trainSet.size());
			resample.setSampleSizePercent((100 - percentMinority)*2);
			
			
		} 
		catch (Exception e) {
		
			ProjectLogger.getSingletonInstance().saveMess("[X]Error instantiating oversample"); System.exit(1); 
			
		}
		
		return resample;
	}

	

	

	public static SpreadSubsample undersampling(Instances trainSet) throws SecurityException, IOException {
		SpreadSubsample spreadSubsample = null;
		
		try {
			
			spreadSubsample = new SpreadSubsample();
			spreadSubsample.setInputFormat(trainSet);
			
			
			String[] opts = new String[]{"-M", "1.0"};
			spreadSubsample.setOptions(opts);
			
		} 
		catch (Exception e) {
			
			ProjectLogger.getSingletonInstance().saveMess("[X]Error instantiating undersample"); System.exit(1); 
			
		}
		
		return spreadSubsample;
	}

	public static SMOTE smote(Instances trainSet) throws SecurityException, IOException {
		SMOTE smote = null;
		
		try {
		
			smote = new SMOTE();
			smote.setInputFormat(trainSet);
			
		} 
		
		catch (Exception e) {
			
			ProjectLogger.getSingletonInstance().saveMess("[X]Error instantiating smote"); System.exit(1); 
			
		}
		
		return smote;
	}

	
	
	public static int getBugsNum(Instances trainSet) {
		int bugs =0;
		int bugTrueIndex = trainSet.attribute(trainSet.numAttributes() - 1).indexOfValue("Yes");
		
		for(int i = 0; i!= trainSet.size(); i++) 
			if(trainSet.get(i).value(trainSet.classIndex()) == bugTrueIndex) 
				bugs++;
		
	
		return bugs;
	}
	
	private static double getMinorityPercentage(int bugs, int size) {


		int minority = bugs;
		if (size - bugs < bugs)
			minority = size-bugs;
		
		return (double) minority/size * 100;
		
	}


	
	
}

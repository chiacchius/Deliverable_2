package Handler;

import java.io.IOException;

import Utility.ProjectLogger;
import weka.attributeSelection.BestFirst;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.WrapperSubsetEval;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;

public class FilterHandler {

	public static Filter cfs(Instances trainSet) throws SecurityException, IOException {
		
		AttributeSelection filter = new AttributeSelection();
		
		CfsSubsetEval eval = new CfsSubsetEval();
		try 
		{ 
			eval.buildEvaluator(trainSet);
			
		    BestFirst search = new BestFirst();
		    
			filter.setEvaluator(eval);
			filter.setSearch(search);
			
			filter.setInputFormat(trainSet); 
		} 
		catch (Exception e) { 
			
			ProjectLogger.getSingletonInstance().saveMess("Error in instantiating CFS feature selection with Best First");
			
		}
		
		
		return filter;
		
	}

	public static Filter wrapper(Instances trainSet) throws SecurityException, IOException {
		AttributeSelection filter = new AttributeSelection();
		
		try {
			
			WrapperSubsetEval eval = new WrapperSubsetEval();
			
			String[] wrapperEvaluatorOpt = {"-B", "weka.classifiers.bayes.NaiveBayes","-F","5","-T","0.01","-R","1"};
			eval.setOptions(wrapperEvaluatorOpt);
			
			eval.buildEvaluator(trainSet);
		    
			BestFirst search = new BestFirst();
			String [] bfSearchOpt = {"-D", "2",  "-N", "5"};
			search.setOptions(bfSearchOpt);
			
			filter.setEvaluator(eval);
			filter.setSearch(search);
				
			filter.setInputFormat(trainSet); 
		} 
		catch (Exception e) { 
			
			ProjectLogger.getSingletonInstance().saveMess("Error in instantiating Wrapper feature selection with Best First");
			
		}
			
			
		return filter;
	}

}

package entity;

public class ModelMetrics {

	private String dataset;
	private String classifier;
	private String balancing;
	private String featureSelection;
	private String sensitive;
	private int trainingReleaseNumber;
	private double trainingPercent;
	private double defectTrainingPercent;
	private double defectTestPercent;
	private int tp; 
	private int tn;
	private int fp; 
	private int fn;
	private double precision;
	private double recall;
	private double rocArea;
	private double kappa;

	//To reduce the number of input parameters (<= 7)
	public ModelMetrics(String dataset, String[] classBalaFeatuSens, int trainingReleaseNumber, double[] trainPercDefTrainDefTest, int[] confusionMatrix, double[] precRecRocKap) {
		
		this.dataset = dataset;
		this.classifier = classBalaFeatuSens[0];
		this.balancing = classBalaFeatuSens[1];
		this.featureSelection = classBalaFeatuSens[2];
		this.sensitive = classBalaFeatuSens[3];
		this.trainingReleaseNumber = trainingReleaseNumber;
		this.trainingPercent = trainPercDefTrainDefTest[0];
		this.defectTrainingPercent = trainPercDefTrainDefTest[1];
		this.defectTestPercent = trainPercDefTrainDefTest[2];
		this.tp = confusionMatrix[0];
		this.tn = confusionMatrix[1];
		this.fp = confusionMatrix[2];
		this.fn = confusionMatrix[3];
		this.precision = precRecRocKap[0];
		this.recall = precRecRocKap[1];
		this.rocArea = precRecRocKap[2];
		this.kappa = precRecRocKap[3];
	}

	public String getDataset() {
		return dataset;
	}

	public String getClassifier() {
		return classifier;
	}

	public String getBalancing() {
		return balancing;
	}

	public String getFeatureSelection() {
		return featureSelection;
	}

	public String getSensitive() { return sensitive; }

	public int getTrainingReleaseNumber() {
		return trainingReleaseNumber;
	}

	public double getTrainingPercent() {
		return trainingPercent;
	}

	public double getDefectTrainingPercent() {
		return defectTrainingPercent;
	}

	public double getDefectTestPercent() {
		return defectTestPercent;
	}

	public int getTp() {
		return tp;
	}

	public int getTn() {
		return tn;
	}

	public int getFp() {
		return fp;
	}

	public int getFn() {
		return fn;
	}

	public double getPrecision() {
		return precision;
	}

	public double getRecall() {
		return recall;
	}

	public double getRocArea() {
		return rocArea;
	}

	public double getKappa() {
		return kappa;
	}



}

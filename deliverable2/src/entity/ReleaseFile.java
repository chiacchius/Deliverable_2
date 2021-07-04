package entity;

import java.util.ArrayList;
import java.util.List;

public class ReleaseFile {
	
	
	private String filePath; //file path
	private boolean bugginess=false;
	private Release release;
	private Changes change;
	private Integer numRev=0;
	private Integer loc=0;
	private Integer locAdded=0;
	private Integer maxLocAdded=0;
	private Integer locTouched=0;
	private Integer churn=0;
	private Integer locOperations=0;
	private Integer churnOperations=0;
	private Integer avgLocAdded = 0;
	private Integer avgChurn = 0;
	private Integer maxChurn = 0;
	private List<String> authors = new ArrayList<>();
	
	

	public ReleaseFile(Release release, String path) {
		this.release=release;
		this.filePath = path;
		
		
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
	public void setFilePath(String path) {
		this.filePath = path;
	}
	
	public void setBugginess(Boolean bugginess) {
		this.bugginess = bugginess;
	}
	
	public String getBugginess() {
		if (this.bugginess) return "Yes";
		return "No";
	}
	
	public void addAuthor(String author) {
		if (!this.authors.contains(author)) authors.add(author);
	}
	
	public List<String> getAuthors() {
		return this.authors;
	}
	
	public Changes getChange() {
		return this.change;

	}

	public void setChange(Changes change) {
		this.change = change;
	}

	public Integer getNumRev() {
		return numRev;
	}

	public void setNumRev(Integer numRev) {
		this.numRev = numRev;
	}

	public Release getRelease() {
		return release;
	}

	public void setRelease(Release release) {
		this.release = release;
	}

	public Integer getLoc() {
		return loc;
	}

	public void setLoc(Integer loc) {
		this.loc = loc;
	}

	

	public void addLocAdded(Integer locAdded) {
		if( locAdded > this.maxLocAdded ) setMaxLocAdded(locAdded);
		this.locAdded += locAdded;
		this.locOperations += 1; // increment for avg calc
		this.setAvgLocAdded();
	}

	private void setAvgLocAdded() {
		avgLocAdded = this.locAdded / this.locOperations;		
	}

	public Integer getMaxLocAdded() {
		return this.maxLocAdded;
	}
	
	public Integer getLocAdded() {
		return this.locAdded;
	}

	public void setMaxLocAdded(Integer maxLocAdded) {
		this.maxLocAdded = maxLocAdded;
	}
	
	public void addLocTouched( Integer locTouched ) {
		this.locTouched += locTouched;
	}

	public void setLocAdded(Integer locAdded) {
		this.locAdded = locAdded;
	}

	public Integer getChurn() {
		return churn;
	}

	public void addChurn(int churn) {
		if (churn>this.maxChurn) setMaxChurn(churn);
		this.churnOperations++;
		this.churn += churn;
		setAvgCharn(this.churn);
	}

	private void setMaxChurn(int churn) {
		this.maxChurn=churn;		
	}
	
	public Integer getMaxChurn() {
		return this.maxChurn;
	}
	
	public Integer getAvgChurn() {
		return this.avgChurn;
	}
	
	

	private void setAvgCharn(Integer churn) {
		this.avgChurn = churn/this.churnOperations;
	}

	public Integer getLocTouched() {
		return locTouched;
	}

	public void setLocTouched(Integer locTouched) {
		this.locTouched = locTouched;
	}

	public Integer getAvgLocAdded() {
		return this.avgLocAdded;
	}

	

	
	
	
}

package Entity;

public class ReleaseFile {
	
	
	private String filePath; //file path
	private Boolean bugginess;
	private Integer authors;
	private Release release;
	private Changes change;
	private Integer numRev;
	
	
	

	public ReleaseFile(Release release, String path) {
		this.release=release;
		this.filePath = path;
		bugginess = false;
		authors = 0;
		numRev=0;
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
	
	public Boolean getBugginess() {
		return this.bugginess;
	}
	
	public void addAuthor() {
		this.authors++;
	}
	
	public Integer getAuthors() {
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
	
	
	
}

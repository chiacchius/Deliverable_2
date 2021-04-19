package deliverable2;

public class ReleaseFile {
	
	
	private String filePath; //file path
	private Boolean bugginess;
	
	
	public ReleaseFile(String path) {
		this.filePath = path;
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
	public void setBugginess(Boolean bugginess) {
		this.bugginess = bugginess;
	}
	
	public Boolean getBugginess() {
		return this.bugginess;
	}
	
	
	
}

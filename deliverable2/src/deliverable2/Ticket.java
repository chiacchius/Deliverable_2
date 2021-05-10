package deliverable2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;
import org.json.JSONObject;


public class Ticket {
	private String key;
	private Integer id;
	private LocalDate creationDate;
	private LocalDate resolutionDate;
	private Release iv;
	private Release ov;
	private Release fv;
	private JSONObject jsonObj;
	private Boolean notProportion;
	
	private List<Release> av;
	private List<RevCommit> commits; //all commits related to the ticket
	private List<String> bugFilesPath;
	
	
	


	public Ticket(String key, Integer id, LocalDate crDate, LocalDate resDate) {
		this.key = key;
		this.id = id;
		this.creationDate = crDate;
		this.resolutionDate = resDate;
		this.av = new ArrayList<>();
		this.commits = new ArrayList<>();
		this.bugFilesPath = new ArrayList<>();
		this.notProportion=false;
	}
	
	
	public void printTicket() {
		System.out.print("Ticket: " + this.id.toString() + " key: " + this.key  +  " creation date: " + this.creationDate.toString()  + " resolution date: " + this.resolutionDate.toString() + "\n");
	}
	
	public void printVersions() {
		this.printIv();
		this.printFv();
		this.printOv();
		this.printAv();

	}
	
	private void printOv() {
		if (this.ov != null) {
			System.out.println("Ov: " +this.ov.getReleaseName());
		}	
	}


	private void printFv() {
		if (this.fv != null) {
			System.out.println("Fv: " + this.fv.getReleaseName());
		}
		else {
			System.out.println("Fv: null" );

		}
	}


	private void printIv() {
		if (this.iv != null) {
			System.out.println("Iv: " + this.iv.getReleaseName());
		}
		
	}
	
	private void printAv() {
		System.out.print("Av: ");

		for (int i = 0; i<av.size(); i++) {
			System.out.print(this.av.get(i).getReleaseName() + "\t");
		}
		System.out.print("\n");
		System.out.print(false);
	}


	public String getTicketKey() {
		return this.key;
	}
	
	public LocalDate getCreationDate() {
		return this.creationDate;
	}
	
	public LocalDate getResolutionDate() {
		return this.resolutionDate;
	}
	
	public void addJSONObject(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	
	public JSONObject getJSONObject() {
		return this.jsonObj;
	}
	
	public void setIv(Release release) {
		this.iv = release;
	}
	
	public void setOv(Release release) {
		this.ov = release;
	}
	
	public void setFv(Release release) {
		
		if (this.fv!=null) {
			
			if (release.getReleaseIndex() > this.fv.getReleaseIndex()) {
				this.fv = release;
			}
			
			
		}
		
		else {
			this.fv = release;
		}
		
		
	}
	
	public void setAv(Release release) {
		av.add(release);
	}
	
	public Release getFv() {
		return this.fv;
	}
	
	public Release getIv() {
		return this.iv;
	}
	
	public Release getOv() {
		return this.ov;
	}
	
	public List<Release> getAv() {
		return this.av;
	}
	
	
	public void addCommit(RevCommit commit) {
		this.commits.add(commit);
	}
	
	public List<RevCommit> getCommits() {
		return this.commits;
	}


	public void addBuggyFile(String rf) {
		if (!this.bugFilesPath.contains(rf))
			this.bugFilesPath.add(rf);
	}


	public List<String> getBugFiles() {
		return bugFilesPath;
	}


	public void setBugFiles(List<String> bugFiles) {
		this.bugFilesPath = bugFiles;
	}
	
	
	
	
	
	public Boolean getNotProportion() {
		return notProportion;
	}


	public void setNotProportion(Boolean notProportion) {
		this.notProportion = notProportion;
	}


	

	
	
	
	
}

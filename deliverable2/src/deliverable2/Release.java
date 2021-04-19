package deliverable2;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.revwalk.RevCommit;

public class Release {
	
	private Integer index;         //release index
	//private String releaseID;		
	private String releaseName;	  //release ID (eg. 4.0.0)
	private LocalDateTime date;   //when the version was released
	private List<RevCommit> commits = new ArrayList<>(); //all commits of the release
	private RevCommit lastCommit; //last commit of the release
	
	private List<ReleaseFile> releaseFiles = new ArrayList<>(); //all the java files of the release
	
	public Release(Integer index, String releaseName, LocalDateTime date) {
		this.index = index;
		//this.releaseID = releaseID;
		this.releaseName = releaseName;
		this.date = date;
		
	}
	

	
	public void printRelease() {
		System.out.print("\n\nRelease: " + this.index.toString() + "\t" + releaseName + "\t" + date.toString()+ "\n");
		System.out.print((Instant.ofEpochSecond((this.lastCommit).getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime()).toString() + "\n");
		
		int len = releaseFiles.size();
		
		/*System.out.print(this.lastCommit.toString());
		
		for (int i = 0; i < len ; i++) {
			System.out.print(this.releaseFiles.get(i).getFilePath()+"\n");
		}*/
		
		//this.printCommits();
	}
	
	public Integer getReleaseIndex() {
		return this.index;
	}
	
	/*public String getReleaseReleaseID() {
		return this.releaseID;
	}*/
	
	public String getReleaseName() {
		return this.releaseName;
	}
	
	public LocalDateTime getReleaseDate() {
		return this.date;
	}
	
	public List<ReleaseFile> getReleaseFiles(){
		return this.releaseFiles;
	}
	
	public RevCommit getLastCommit() {
		return this.lastCommit;
	}
	
	public void addFile(ReleaseFile file) {
		this.releaseFiles.add(file);
	}
	
	public void setLastCommit(RevCommit commit) {
		this.lastCommit = commit;
	}
	
	public void addCommit(RevCommit commit) {
		commits.add(commit);
	}
	
	public Boolean compare(Release release1, Release release2) {
		
		return release1.getReleaseName().equals(release2.getReleaseName());
		
	}
	
	public void printCommits() {
		for (int i=0; i<commits.size(); i++) {
			RevCommit commit = commits.get(i);
			System.out.print(commit.toString() + "\tdate: " + Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime().toString() + "\n\n");
		}
	}
	
	
	
	
	
}

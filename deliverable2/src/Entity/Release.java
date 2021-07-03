package Entity;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import java.util.List;

import Utility.ProjectLogger;
import org.eclipse.jgit.revwalk.RevCommit;

public class Release {
	
	private Integer index;         //release index	
	private String releaseName;	  //release ID (eg. 4.0.0)
	private LocalDateTime date;   //when the version was released
	private List<RevCommit> commits = new ArrayList<>(); //all commits of the release
	private RevCommit lastCommit; //last commit of the release
	private Integer numFiles;

	private List<ReleaseFile> releaseFiles = new ArrayList<>(); //all the java files of the release
	
	public Release(Integer index, String releaseName, LocalDateTime date) {
		this.index = index;
		this.releaseName = releaseName;
		this.date = date;
		
	}
	

	
	public void printRelease() throws IOException {
		ProjectLogger.getSingletonInstance().saveMess("\n\nRelease: " + this.index.toString() + "\t" + releaseName + "\t" + date.toString()+ "\n");
		ProjectLogger.getSingletonInstance().saveMess((Instant.ofEpochSecond((this.lastCommit).getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime()).toString() + "\n");
		ProjectLogger.getSingletonInstance().saveMess("commitsNumber = " + commits.size() + " numFiles = " + numFiles.toString()+ "\n");

	}
	
	public Integer getReleaseIndex() {
		return this.index;
	}
	
	
	public String getReleaseName() {
		return this.releaseName;
	}
	
	public LocalDateTime getReleaseDate() {
		return this.date;
	}
	
	public List<ReleaseFile> getReleaseFiles(){
		return this.releaseFiles;
	}
	
	public List<RevCommit> getCommits(){
		return this.commits;
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


	
	public Integer getNumFiles() {
		return this.numFiles;
	}



	public void setNumFiles(Integer numFiles) {
		this.numFiles = numFiles;
	}
	
	
	public Boolean containsFile(String path){
		boolean bool = false;
	    for( ReleaseFile rf : this.releaseFiles ) {
	      if(rf.getChange().getPaths().contains(path) ) {
	        bool = true;
	        return bool;
	      }
	    }
	    return bool;
	} 
	
	
	
	 public ReleaseFile getReleaseFileByName( String name ) {
		    for( ReleaseFile rf : this.releaseFiles ) {
		      if( rf.getFilePath().equals(name) || rf.getChange().getPaths().contains(name) ) {
		        return rf;
		      }
		    }
		    return null;
		 }
	
	
}

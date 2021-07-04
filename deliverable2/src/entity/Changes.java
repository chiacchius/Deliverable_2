package entity;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry.ChangeType;

public class Changes {
	
	private ChangeType change;
	private List<String> paths = new ArrayList<>();
	private String newPath;
	
	
	

	
	public Changes(String newPath) {
		
		this.newPath=newPath;
	}

	public ChangeType getChangeType() {
		return this.change;
	}

	public boolean checkPath(String newPath) {
		for(String path : this.paths) {
			
			if(path.equals(newPath)) {
				return false;
			}
		}
		
		return true;
	}
	
	public List<String> getPaths() {
		return this.paths;
	}

	public void addPath(String oldPath) {
		if (!this.paths.contains(oldPath))
			this.paths.add(oldPath);
	}

	public String getNewPath() {
		return newPath;
	}

	public void setNewPath(String newPath) {
		this.newPath = newPath;
	}
	
	
	public void setChangeType(ChangeType change) {
		this.change = change;
	}
	
	
}

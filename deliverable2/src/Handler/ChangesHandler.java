package Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import Entity.Changes;
import Entity.Release;
import Entity.ReleaseFile;

public class ChangesHandler {

	private ChangesHandler() {
		throw new IllegalStateException("Handler class");
	}
	
	public static List<Changes> getChanges(Repository repository, List<RevCommit> commits, List<ReleaseFile> files, List<Release> projReleases) throws IOException {
		
		List<Changes> changes = new ArrayList<>();
		RevWalk rw = new RevWalk(repository);
		
		
		
		
		for (RevCommit comm: commits) {
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);	
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			
			List<DiffEntry> entries=getEntryList(rw, df, comm);
			
			//create Changes
			for (DiffEntry entry: entries) {
				
				String oldPath = entry.getOldPath();
				String newPath = entry.getNewPath();
				
				if (oldPath.contains(".java") || newPath.contains(".java")) {
				
					if (entry.getChangeType().toString().equals("RENAME")) {
						createOrUpdateChanges(changes, oldPath, newPath);
						
						
					}

					
					
				}
				
			}
			
			
			
			
		}
		
		
		updateChanges(changes, files);
		updateReleaseFiles(changes, projReleases);
		

		return changes;
	}

	
	private static void updateReleaseFiles(List<Changes> changes, List<Release> projReleases) {

		for (int i=0; i<projReleases.size(); i++) {
			
			
			for (ReleaseFile file: projReleases.get(i).getReleaseFiles()) {
				for (Changes ch: changes) {
					
					if (ch.getPaths().contains(file.getFilePath())) {
						file.setChange(ch);
					}
					
					
					
				}
				
			}
			
		}
	
	
	}
	
	
	private static void updateChanges(List<Changes> changes, List<ReleaseFile> files) {
		//set newPath of changes with the last one
		for (int i=0;i<files.size();i++) {
			
			String fileName = files.get(i).getFilePath();
				
			for (int k=0;k<changes.size();k++) {
					
					for( int m=0;m<changes.get(k).getPaths().size();m++) {
						
						String renameFile = changes.get(k).getPaths().get(m);
						
						if (renameFile.equals(fileName) || fileName.contains(renameFile)) {
							changes.get(k).setNewPath(renameFile);
							
						}
					}
				}
			}
	}
	
	
private static void createOrUpdateChanges(List<Changes> changes, String oldPath, String newPath) {
		
		Boolean oldP = true;
		Boolean newP = true;
		
		for (Changes ch:changes) {
			
			//if ch contains oldPath
			if (!ch.checkPath(oldPath)) {
				oldP=false;
				if(ch.checkPath(newPath)) {

					ch.addPath(newPath);
					ch.setNewPath(newPath);
					newP=false;
				}
			}
			//if ch contains newPath
			if (!ch.checkPath(newPath)) {

				newP = false;
				if (ch.checkPath(oldPath)) {
					ch.addPath(oldPath);
					oldP = false;
				}
			}
			
			
		}
		
		
		//create a new change
		if (oldP && newP) {
			
			Changes change = new Changes(newPath);
			change.addPath(oldPath);
			change.addPath(newPath);
			changes.add(change);
		}
		
		
	}




	public static List<DiffEntry> getEntryList(RevWalk rw, DiffFormatter df, RevCommit commit) throws IOException {
		List<DiffEntry> diffEntries;
		RevCommit parent = null;
		
		if(commit.getParentCount() !=0) {
			parent =commit.getParent(0);
		}
			
		
		
		if(parent != null) {
			
			diffEntries = df.scan(parent.getTree(), commit.getTree());
			
		}
		else {
			
			ObjectReader reader = rw.getObjectReader();
			diffEntries =df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, reader, commit.getTree()));
		}
		
		return diffEntries;
	}


	
	

}

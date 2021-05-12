package Handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import Entity.Release;
import Entity.ReleaseFile;
import Entity.Ticket;
import Main.MainControl;


public class MetricsHandler {
	
	
	
	
	public static void checkBugginess(List<Release> projReleases, Ticket ticket, Repository repository, List<ReleaseFile> files) throws MissingObjectException, IncorrectObjectTypeException, IOException {
		//take the commit before the resolutionCommit of the bug
		for (String path: ticket.getBugFiles()) {
			
			for (Release rel: ticket.getAv()) {
				
					for (ReleaseFile rf: rel.getReleaseFiles()) {
						//if a file is called *path* or was called *path* set bugginess true		
						if (rf.getChange().getPaths().contains(path)) {
							
							rf.setBugginess(true);
						}
						
						
					}
					
					
			}
				
			
		}
	}

	public static void retrieveAllMetrics(Repository repository, List<RevCommit> commits, List<Ticket> projTickets, List<Release> projReleases) throws IOException {
		
		
		RevWalk rw = new RevWalk(repository);
		
		List<RevCommit> allCommits = new ArrayList<>();
		
		for (Release rel: projReleases) {
			
			
			for (ReleaseFile rf: rel.getReleaseFiles()) {
				
				calculateMetrics(rel, rf, repository);
				
			}
			
			
			
			
		}
		
	
	}

	
	
	
	
	private static void calculateMetrics(Release rel, ReleaseFile rf, Repository repository) throws IOException {

		int numRev = 0;
		
		
		List<RevCommit> commits;
		
		
		
		RevWalk rw = new RevWalk(repository);
		
		
		for (RevCommit comm: rel.getCommits()) {
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);	
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			
			List<DiffEntry> entries = MainControl.getEntryList(rw, df, comm);
			
			for (DiffEntry entry: entries) {
				
				
				if (rf.getChange().getPaths().contains(entry.getNewPath())) {
				}
			}
			
			
			
		}
		
	}


	
	
	

	
	
	
	

}

package deliverable2;

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


public class RetrieveMetrics {
	
	
	
	
	public static void checkBugginess(List<Release> projReleases, Ticket ticket, Repository repository, List<ReleaseFile> files) throws MissingObjectException, IncorrectObjectTypeException, IOException {
		//take the commit before the resolutionCommit of the bug
		
		for (String path: ticket.getBugFiles()) {
			
			for (Release rel: projReleases) {
				
				if (ticket.getAv().contains(rel)) {
					
					for (ReleaseFile rf: rel.getReleaseFiles()) {
						if (rf.getChange().getPaths().contains(path) || rf.getFilePath().equals(path)) {
							rf.setBugginess(true);
						}
					}
					
					
				}
				
			}
			
		}
	}

	public static void retrieveAllMetrics(Repository repository, List<RevCommit> commits, List<Ticket> projTickets, List<Release> projReleases) throws IOException {
		
		
		RevWalk rw = new RevWalk(repository);
		int num =0;
		for (RevCommit comm: commits) {
			DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);	
			df.setRepository(repository);
			df.setDiffComparator(RawTextComparator.DEFAULT);
			df.setDetectRenames(true);
			
			List<DiffEntry> entries=MainControl.getEntryList(rw, df, comm);
			
			for (DiffEntry entry: entries) {
				
				for (Release rel: projReleases) {
					
					//ReleaseFile rf = getFile(entry, rel);
					num+=getFile(entry, rel);
				}
			}
		}
		System.out.println("broo" + num);
	
	}

	
	
	
	
	private static Integer getFile(DiffEntry entry, Release rel) {
		
		int num=0;
			
		for (ReleaseFile rf: rel.getReleaseFiles()) {
				
			if (rf.getFilePath().equals(entry.getNewPath()) || rf.getFilePath().equals(entry.getOldPath())) {
				num++;
			}
							
		}
		
		
		
		return num;
	}

	private static void retrieveNumRevisions(List<ReleaseFile> files, List<RevCommit> commits, List<Ticket> projTickets, List<Release> projReleases) {
		
		
		
		
		
		
	}
	
	
	

	
	
	
	

}

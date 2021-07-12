package handler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import entity.Release;
import entity.ReleaseFile;
import entity.Ticket;


public class MetricsHandler {

	private MetricsHandler() {
		throw new IllegalStateException("Handler class");
	}
	
	
	public static void checkBugginess(Ticket ticket){
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

	public static void retrieveAllMetrics(Repository repository, List<Release> projReleases) throws IOException {
		
		RevCommit head = null;
		try( RevWalk revWalk = new RevWalk( repository ) ){
			revWalk.sort(RevSort.TOPO); // all commit child before parents
	        head = revWalk.parseCommit(repository.exactRef("HEAD").getObjectId()); //take the head of the commit
	        
	        while( head.getParentCount() != 0 ) {
	        
	        	RevCommit parent = 	revWalk.parseCommit(head.getParent(0));
	        	DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
				df.setRepository(repository);
				df.setDiffComparator(RawTextComparator.DEFAULT);
				df.setContext(0);
				df.setDetectRenames(true);
				List<DiffEntry> diffEntries = new ArrayList<>();
				diffEntries = df.scan(parent.getTree(), head.getTree());
				LocalDateTime ldt = Instant.ofEpochSecond( head.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
				Release rel = ReleaseHandler.findReleaseFromLdt( ldt, projReleases);
				
				if( rel == null ) {
					df.close();
					head = revWalk.parseCommit(head.getParent(0));
					continue;
				}
				
				
				for(DiffEntry diff : diffEntries){

					ReleaseFile rf=null;
					//control if '.java' file
					if ( (diff.getNewPath().contains(".java") || diff.getOldPath().contains(".java")) && findReleaseFileByPath(rel, diff.getNewPath(), diff.getOldPath())!=null) {
						
						
						rf = findReleaseFileByPath(rel, diff.getNewPath(), diff.getOldPath());
						

						

						int locAdded = 0;
						int locDeleted = 0;

						
						
						
						for (Edit edit : df.toFileHeader(diff).toEditList()) {
							locDeleted += edit.getEndA() - edit.getBeginA();
							locAdded += edit.getEndB() - edit.getBeginB();
							
					    }
						

						setMetrics(rf, locAdded, locDeleted, head);
						
						
						
					}
						
						
						
						
				}				
	        
				df.close();
				head = revWalk.parseCommit(head.getParent(0));
	        }
				
				
				
	        
        }
	    
		
		
	}

	private static void setMetrics(ReleaseFile rf, int locAdded, int locDeleted, RevCommit head) {
		int locTouched = locAdded + locDeleted;
		int churn = locAdded - locDeleted;

		if (rf!=null) {
			rf.addLocAdded(Integer.valueOf(locAdded));
			rf.addLocTouched(Integer.valueOf(locTouched));
			rf.addChurn(churn);
			rf.addAuthor(head.getAuthorIdent().getName());
			rf.setNumRev(rf.getNumRev() + 1);
		}
	}

	private static ReleaseFile findReleaseFileByPath(Release rel, String newPath, String oldPath) {
		ReleaseFile rf = null;
		if( Boolean.TRUE.equals(rel.containsFile(newPath ) ) ) {
			rf = rel.getReleaseFileByName(newPath);

		}else if( Boolean.TRUE.equals(rel.containsFile(oldPath))){
			rf = rel.getReleaseFileByName(oldPath);


		}
		return rf;
	}


	public static Integer locCalculator(Repository repository, TreeWalk treeWalk) throws IOException {
		ObjectLoader loader = repository.open(treeWalk.getObjectId(0));
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		
		loader.copyTo(output);
		
		
		String filecontent = output.toString();
		StringTokenizer token= new StringTokenizer(filecontent,"\n");
		
		int count=0;
		while(token.hasMoreTokens()) {
			
			count++;
			token.nextToken();
			
		}
		
		return count;
	}


	
	
	

	
	
	
	

}

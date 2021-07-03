package Handler;

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
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import Entity.Release;
import Entity.ReleaseFile;
import Entity.Ticket;


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
				ReleaseHandler releaseController = new ReleaseHandler();
				Release rel = ReleaseHandler.findReleaseFromLdt( ldt, projReleases);
				
				if( rel == null ) {
					df.close();
					head = revWalk.parseCommit(head.getParent(0));
					continue;
				}
				
				
				for(DiffEntry diff : diffEntries){
					
					//control if '.java' file
					if ( diff.getNewPath().contains(".java") || diff.getOldPath().contains(".java") ) {
						
						
						ReleaseFile rf = null;
						
						
						if( rel.containsFile(diff.getNewPath() )  ) {
							rf = rel.getReleaseFileByName(diff.getNewPath());
							
						}else if( rel.containsFile(diff.getOldPath() )){
							rf = rel.getReleaseFileByName(diff.getOldPath());
							
							
						}else {
							continue;
						}
						
						
						int locTouched = 0;
						int locAdded = 0;
						int locDeleted = 0;
						int churn = 0;
						
						
						
						for (Edit edit : df.toFileHeader(diff).toEditList()) {
							locDeleted += edit.getEndA() - edit.getBeginA();
							locAdded += edit.getEndB() - edit.getBeginB();
							
					    }
						
						
						locTouched = locAdded + locDeleted;
						churn = locAdded - locDeleted;
						rf.addLocAdded( Integer.valueOf(locAdded) );
						rf.addLocTouched( Integer.valueOf(locTouched) );
			            rf.addChurn( churn );
			            rf.addAuthor( head.getAuthorIdent().getName());
			            rf.setNumRev( rf.getNumRev() + 1);
						
						
						
						
					}
						
						
						
						
				}				
	        
				df.close();
				head = revWalk.parseCommit(head.getParent(0));
	        }
				
				
				
	        
        }
	    
		
		
	}
	
	

	
	
	
	
	

	public static Integer locCalculator(Repository repository, TreeWalk treeWalk) throws MissingObjectException, IOException {
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

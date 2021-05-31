package Controller;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import Entity.Changes;
import Entity.Release;
import Entity.ReleaseFile;
import Entity.Ticket;
import Handler.ChangesHandler;
import Handler.GitHubHandler;
import Handler.JiraHandler;
import Handler.MetricsHandler;
import Handler.ReleaseHandler;
import Utility.CsvWriter;
import Utility.ProjectLogger;

public class ControllerDeliverable1 {
	
	private String csvFile;
	
	
	
	public ControllerDeliverable1() {}







	public void run(String projectName, String path) throws GitAPIException, IOException, JSONException  {
		
		
		
		String PROJ_NAME = projectName;
		//String path;
		Repository repository;
	    
	    List<Release> projReleases;
	    List<ReleaseFile> files;
	    List<Ticket> projTickets;
	    List<RevCommit> commits;
	    List<Changes> changes;
		
	    ProjectLogger.getSingletonInstance().saveMess(" [*] Starting retrieve data for " + projectName);

		//path = "/Users/chiacchius/Desktop/" + PROJ_NAME;
		Git git= GitHubHandler.cloneProjectFromGitHub(path, PROJ_NAME);
		repository  = git.getRepository();
		
		
		
		
    	
    	//retrieve all releases of the project
    	projReleases = JiraHandler.getReleases(PROJ_NAME);
    	
     	
    	
    	//retrieve all the tickets
    	projTickets = JiraHandler.getTickets(PROJ_NAME, projReleases);
    	
    	
    	//retrieve all the file .java of a specific release
    	commits = GitHubHandler.getAllCommits(git);
    	files = GitHubHandler.findReleaseFiles(git, repository, projReleases, commits);
    	System.out.println("files: " + files.size());
    	
    	
    	//set what versions are Ov, Fv, Iv, Av
    	setOvReleases(projReleases, projTickets);
    	setFvReleases(projReleases,projTickets, commits);
    	setIvReleases(projReleases, projTickets);
    	setAvReleases(projReleases, projTickets);
    	
    	
    	/*for (Ticket ticket: projTickets) {
		
    		ticket.printTicket(); 
    		ticket.printVersions();
		}*/
    	
    	
    	
    	
    	
    	
    	
    	
    	changes = ChangesHandler.getChanges(repository, commits, files, projReleases);
    	MetricsHandler.retrieveAllMetrics(repository, commits, projTickets, projReleases);  //retrieve all metrics
    	
    	
    	setFilesToTicket(projTickets, changes, files, repository); //take all changed file for every ticket
    	
    
    	
    	for (Ticket ticket: projTickets) {
    		
        	MetricsHandler.checkBugginess(projReleases, ticket, repository, files); //check file buggy considering changed file for every ticket 
        	
        	
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	

    	this.csvFile = CsvWriter.writeFirstCsv(PROJ_NAME, projReleases);
    	
    	ProjectLogger.getSingletonInstance().saveMess(" [*] Exiting for " + projectName);


	}
	
	

	
	
	
	
	private static void setFilesToTicket(List<Ticket> projTickets, List<Changes> changes, List<ReleaseFile> files, Repository repository) throws IOException {
		
		for (Ticket ticket: projTickets) {
			
		
			try(RevWalk revWalk = new RevWalk( repository )){
				
				for (RevCommit comm: ticket.getCommits()) {
					revWalk.sort(RevSort.TOPO); // all commit child before parents
					RevCommit lastCommitBeforeResolution = revWalk.parseCommit(comm.getParent(0) );
					
					RevTree oldTree = lastCommitBeforeResolution.getTree();									
					RevTree newTree = comm.getTree();
					
					
					DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
					df.setRepository(repository);
					df.setDiffComparator(RawTextComparator.DEFAULT);
					df.setContext(0);
					df.setDetectRenames(true);
					List<DiffEntry> diffEntries = new ArrayList<>();
					diffEntries = df.scan(oldTree, newTree);
					for( DiffEntry entry : diffEntries ) {
						//for every ticket update buggy files list
						if ( entry.toString().contains(".java")) {
							//if a file was deleted a bug was resolved
							if (entry.getChangeType()==ChangeType.DELETE) {
								ticket.addBuggyFile(entry.getOldPath());
							}
							//if a file was modified a bug was resolved
							else if (entry.getChangeType()==ChangeType.MODIFY){
								ticket.addBuggyFile(entry.getNewPath());
							}  
							
						}
						  
					}
				}
				
				
			}
		
		
		
		}
		
		
	}






	public String getCsvFileName() {
		return this.csvFile;
	}
	






	







	







	







	







	



	private static void setOvReleases(List<Release> projReleases, List<Ticket> projTickets) {
		
		Integer ticketsNumber = projTickets.size();
		Integer releasesNumber = projReleases.size();
		
		
		for (int i = 0; i < ticketsNumber; i++) {
			
			
			Ticket ticket = projTickets.get(i);
			LocalDateTime ticketDate = ticket.getCreationDate().atStartOfDay();
			
			for (int j = 0; j < releasesNumber-1; j++) {
				
				Release release1 = projReleases.get(j);
				Release release2 = projReleases.get(j+1);
				
				if (j == 0 && ticketDate.isBefore(release1.getReleaseDate())) {
					ticket.setOv(release1);
				}
				
				else if (ticketDate.isAfter(release1.getReleaseDate()) && ticketDate.isBefore(release2.getReleaseDate())) {
					
					ticket.setOv(release2);
					
				}
				
				else if (ticketDate.isEqual(release1.getReleaseDate())) {
					ticket.setOv(release1);

				}
				
				else if (ticketDate.isEqual(release2.getReleaseDate())) {
					ticket.setOv(release2);
				
			    }
				
			
			}
		}

		
	}
	
	
	
	
	
	
	private static void setAvReleases(List<Release> projReleases, List<Ticket> projTickets) {
		
		for (Ticket ticket: projTickets) {
			
			//System.out.println(ticket.getTicketKey());
			Integer iv = ticket.getIv().getReleaseIndex();
			Integer fv = ticket.getFv().getReleaseIndex();
			
			
			for (int i = iv; i < fv; i++) {
				
				Release release = ReleaseHandler.findRelease(i, projReleases);
				ticket.setAv(release);
				
				
			}
			
			//ticket.printTicket();
			//ticket.printVersions();
			
			
			
			
			
		}
		
		
	}
	
	
	
	
	

	





































	private static void checkIfFvExists(List<Ticket> projTickets) {
		
		List<Ticket> index = new ArrayList<>();
			
		for (int i = 0; i < projTickets.size(); i++) {
			if (projTickets.get(i).getFv() == null) {
				index.add(projTickets.get(i));
			}
		}
		
		
		projTickets.removeAll(index);
		
			
		
	}


















	private static void setIvReleases(List<Release> projReleases, List<Ticket> projTickets) throws JSONException {
		
		Double prop;
		Integer onePercent;
		
		for (Ticket ticket : projTickets) {
			
			Release release = findIV(ticket, projReleases);
			
			
			if (release != null) {
				Boolean isCorrect = checkIfIvIsCorrect(release, ticket);
				if (isCorrect) {
					ticket.setIv(release);
					ticket.setNotProportion(true);
				}
				
				//System.out.println(ticket.getTicketKey());
			}
			
			
			
			//ticket.printTicket();
			//ticket.printVersions();
		}
		
		onePercent = projTickets.size() /100;
		
		//prop = proportion(projTickets);
		//Collections.reverse(projTickets);
		
		for (Ticket ticketWithoutIv : projTickets) {
			
			if (ticketWithoutIv.getIv()==null) {
				
				
				
				prop = proportion(ticketWithoutIv, projTickets, onePercent);
				System.out.println(ticketWithoutIv.getTicketKey() + "   ok");
				
				Math.ceil(prop);
				
				Double fv = (double) ticketWithoutIv.getFv().getReleaseIndex();
				Double ov = (double) ticketWithoutIv.getOv().getReleaseIndex();
				
				Double id = fv - (fv - ov) * prop;
				
				id = Math.floor(id);

				if (id.intValue() <= 0 ) {
					id = (double) 1;
					

				}
				if (id.intValue() > ticketWithoutIv.getOv().getReleaseIndex()) {
					//System.out.println(ticketWithoutIv.getTicketKey()+ "eh");
					

					id = (double) ticketWithoutIv.getOv().getReleaseIndex();
					
					//System.out.println(id.intValue());
				}
				Release release = ReleaseHandler.findRelease(id.intValue(), projReleases);
				System.out.println(release.getReleaseName());
				
				ticketWithoutIv.setIv(release);
				//System.out.println(ticketWithoutIv.getTicketKey());
				//System.out.println(id.intValue());
				//System.out.println(release.getReleaseIndex());

				
			}
			
			//ticketWithoutIv.printTicket();
			//ticketWithoutIv.printVersions();
			
			
		}
		
		Collections.reverse(projTickets);
		
		
	}









	private static Boolean checkIfIvIsCorrect(Release release, Ticket ticket) {
		
		if (release.getReleaseIndex() > ticket.getOv().getReleaseIndex()) {
			
			return false;
		
		}
		
		return true;
	}


















	


















	private static Double proportion(Ticket tick, List<Ticket> projTickets, Integer onePercent) {
		
		List<Double> propList = new ArrayList<>();
		Integer index = projTickets.indexOf(tick);
		
		/*if (index-onePercent-1<0) {
			return (double) 0;
		}*/
		
		for (int i = index-1; i >= 0; i--) {
			
			Ticket ticket = projTickets.get(i);
			
			if (projTickets.get(i).getNotProportion() && projTickets.get(i).getIv()!=null && !ticket.getFv().getReleaseIndex().equals(ticket.getOv().getReleaseIndex()) ) {
				
				double num = Math.ceil(ticket.getFv().getReleaseIndex().doubleValue() - ticket.getIv().getReleaseIndex().floatValue());
				double den = Math.floor(ticket.getFv().getReleaseIndex().doubleValue() - ticket.getOv().getReleaseIndex().floatValue());
				
				propList.add(num/den);

			
			}
			
			else if (projTickets.get(i).getNotProportion() && ticket.getFv().getReleaseIndex().equals(ticket.getOv().getReleaseIndex())){
				double num = Math.ceil(ticket.getFv().getReleaseIndex().doubleValue() - ticket.getIv().getReleaseIndex().floatValue());
				double den = (double) 1;
				propList.add(Math.ceil(num/den));
			}
			
			if (propList.size()==onePercent) break;
			
		}
		
		double tot = 0;
		for (Double p: propList) {
			tot += p;
		}
		System.out.println(tick.getTicketKey()+    "   "+ tot/propList.size());
		return (tot/propList.size());
	
	}


















	private static Release findIV(Ticket ticket, List<Release> projReleases) throws JSONException {
		
			
		JSONObject json = ticket.getJSONObject();
		
			if (json.getJSONObject("fields").getJSONArray("versions").length() > 0) {
				
				JSONArray avs = json.getJSONObject("fields").getJSONArray("versions");
				JSONObject jrelease = avs.getJSONObject(0);
				String releaseName = jrelease.get("name").toString();
				
				for (Release release : projReleases) {
					if (releaseName.equals(release.getReleaseName())) {
						return release;
					}
				}
				
				
		
				
				
			}
			return null;
			
		
	}


















	



private static void setFvReleases(List<Release> projReleases, List<Ticket> projTickets, List<RevCommit> commits) {
		

		int j;
		Integer commitsNum;

		
		
		commitsNum = commits.size();
		
		for (Ticket ticket : projTickets) {
			
			
			for(j = 0; j < commitsNum; j++) {
				
				RevCommit commit = commits.get(j);
				
				if (commit.getFullMessage().contains(ticket.getTicketKey() + ":")) {
					
					
					ticket.addCommit(commit);
					Release fv = ReleaseHandler.findReleaseFromLdt(Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime(), projReleases);
					if (fv!=null) {
						ticket.setFv(fv);
					}
					

					//System.err.print("\nKey: " + ticket.getTicketKey() + "\n" + commit.getFullMessage() +"\n\n");
					//System.out.println("ticket: " + ticket.getTicketKey() + "------>commit: " + commit.getFullMessage());
					
				}
				
			}
			
		}
		checkIfFvExists(projTickets);
		
		
		
	}
	
	
	

}

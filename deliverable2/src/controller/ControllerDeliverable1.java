package controller;


import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;

import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import entity.Release;
import entity.ReleaseFile;
import entity.Ticket;
import handler.ChangesHandler;
import handler.GitHubHandler;
import handler.JiraHandler;
import handler.MetricsHandler;
import handler.ReleaseHandler;
import utility.CsvWriter;
import utility.ProjectLogger;

public class ControllerDeliverable1 {
	
	private String csvFile;
	
	
	public ControllerDeliverable1() throws IOException {
		ProjectLogger.getSingletonInstance().saveMess("deliverable1 start");
	}







	public void run(String projectName, String path) throws GitAPIException, IOException, JSONException  {
		
		
		
		String projName = projectName;
		Repository repository;
	    
	    List<Release> projReleases;
	    List<ReleaseFile> files;
	    List<Ticket> projTickets;
	    List<RevCommit> commits;

	    ProjectLogger.getSingletonInstance().saveMess(" [*] Starting retrieve data for " + projectName);

		Git git= GitHubHandler.cloneProjectFromGitHub(path, projName);
		repository  = git.getRepository();
		
		
		
		
    	
    	//retrieve all releases of the project
    	projReleases = JiraHandler.getReleases(projName);
    	
     	
    	
    	//retrieve all the tickets
    	projTickets = JiraHandler.getTickets(projName);
    	
    	
    	//retrieve all the file .java of a specific release
    	commits = GitHubHandler.getAllCommits(git);
    	files = GitHubHandler.findReleaseFiles(repository, projReleases, commits);
    	ProjectLogger.getSingletonInstance().saveMess("files: " + files.size());
    	
    	
    	//set what versions are Ov, Fv, Iv, Av
    	setOvReleases(projReleases, projTickets);
    	setFvReleases(projReleases,projTickets, commits);
    	setIvReleases(projReleases, projTickets);
    	setAvReleases(projReleases, projTickets);

    	
    	
    	
    	ChangesHandler.getChanges(repository, commits, files, projReleases);
    	MetricsHandler.retrieveAllMetrics(repository, projReleases);  //retrieve all metrics
    	
    	
    	setFilesToTicket(projTickets, repository); //take all changed file for every ticket
    	
    
    	
    	for (Ticket ticket: projTickets) {
    		
        	MetricsHandler.checkBugginess(ticket); //check file buggy considering changed file for every ticket
        	
        	
    	}
    	
    	
    	
    	
    	
    	
    	
    	
    	

    	this.csvFile = CsvWriter.writeFirstCsv(projName, projReleases);
    	
    	ProjectLogger.getSingletonInstance().saveMess(" [*] Exiting for " + projectName);


	}
	
	

	
	
	
	
	private static void setFilesToTicket(List<Ticket> projTickets, Repository repository) throws IOException {
		
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
						if ( entry.toString().contains(".java") && entry.getChangeType()==ChangeType.DELETE) {
							//if a file was deleted a bug was resolved
							ticket.addBuggyFile(entry.getOldPath());
						}
							//if a file was modified a bug was resolved
						else if (entry.toString().contains(".java") && entry.getChangeType()==ChangeType.MODIFY){
								ticket.addBuggyFile(entry.getNewPath());
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
			

			Integer iv = ticket.getIv().getReleaseIndex();
			Integer fv = ticket.getFv().getReleaseIndex();
			
			
			for (int i = iv; i < fv; i++) {
				
				Release release = ReleaseHandler.findRelease(i, projReleases);
				ticket.setAv(release);
				
				
			}

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
				boolean isCorrect = checkIfIvIsCorrect(release, ticket);
				if (isCorrect) {
					ticket.setIv(release);
					ticket.setNotProportion(true);
				}
				
			}

		}
		
		onePercent = projTickets.size() /100;
		

		
		for (Ticket ticketWithoutIv : projTickets) {
			
			if (ticketWithoutIv.getIv()==null) {
				
				
				
				prop = proportion(ticketWithoutIv, projTickets, onePercent);

				Math.ceil(prop);
				
				Double fv = (double) ticketWithoutIv.getFv().getReleaseIndex();
				Double ov = (double) ticketWithoutIv.getOv().getReleaseIndex();
				
				Double id = fv - (fv - ov) * prop;
				
				id = Math.floor(id);

				if (id.intValue() <= 0 ) {
					id = (double) 1;
					

				}
				if (id.intValue() > ticketWithoutIv.getOv().getReleaseIndex()) {


					id = (double) ticketWithoutIv.getOv().getReleaseIndex();
					
				}
				Release release = ReleaseHandler.findRelease(id.intValue(), projReleases);

				ticketWithoutIv.setIv(release);


				
			}

			
			
		}
		
		Collections.reverse(projTickets);
		
		
	}









	private static Boolean checkIfIvIsCorrect(Release release, Ticket ticket) {

		boolean bool = true;
		if (release.getReleaseIndex() > ticket.getOv().getReleaseIndex()) {
			
			bool=false;
		
		}
		
		return bool;
	}



	private static Double proportion(Ticket tick, List<Ticket> projTickets, Integer onePercent) {
		
		List<Double> propList = new ArrayList<>();
		Integer index = projTickets.indexOf(tick);
		

		
		for (int i = index-1; i >= 0; i--) {
			
			Ticket ticket = projTickets.get(i);
			
			if (Boolean.TRUE.equals(projTickets.get(i).getNotProportion()) && projTickets.get(i).getIv()!=null && Boolean.FALSE.equals(ticket.getFv().getReleaseIndex().equals(ticket.getOv().getReleaseIndex())) ) {
				
				double num = Math.ceil(ticket.getFv().getReleaseIndex().doubleValue() - ticket.getIv().getReleaseIndex().floatValue());
				double den = Math.floor(ticket.getFv().getReleaseIndex().doubleValue() - ticket.getOv().getReleaseIndex().floatValue());
				
				propList.add(num/den);

			
			}
			
			else if (Boolean.TRUE.equals(projTickets.get(i).getNotProportion()) && Boolean.TRUE.equals(ticket.getFv().getReleaseIndex().equals(ticket.getOv().getReleaseIndex()))){
				double num = Math.ceil(ticket.getFv().getReleaseIndex().doubleValue() - ticket.getIv().getReleaseIndex().floatValue());
				double den = 1;
				propList.add(Math.ceil(num/den));
			}
			
			if (propList.size()==onePercent) break;
			
		}
		
		double tot = 0;
		for (Double p: propList) {
			tot += p;
		}
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
					


					
				}
				
			}
			
		}
		checkIfFvExists(projTickets);
		
		
		
	}
	
	
	

}

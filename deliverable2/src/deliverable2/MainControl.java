package deliverable2;

import java.io.File;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainControl {

	
	
	public static void main(String[] args) throws InvalidRemoteException, GitAPIException, IOException, JSONException  {
		
		
		
		String PROJ_NAME = "ZOOKEEPER";
		String path;
		String url;
		Repository repository;
	    Map<LocalDateTime, String> releases;
	    
	    List<Release> projReleases = new ArrayList<>();
	    List<Ticket> projTickets = new ArrayList<>();
	    List<RevCommit> commits;
		
		int j;
		path = "/Users/chiacchius/Desktop/" + PROJ_NAME;
		url = "https://github.com/apache/" + PROJ_NAME;
		Git git= GetGitInfo.cloneProjectFromGitHub(path, PROJ_NAME);
		repository  = git.getRepository();
		
		
		
		
    	
    	//retrieve all releases of the project
    	projReleases = GetJiraInfo.getReleases(PROJ_NAME);
    	
    	
    	//consider only the first half
    	int projReleasesLen = projReleases.size();
    	
    	
    	//retrieve all the tickets
    	projTickets = GetJiraInfo.getTickets(PROJ_NAME, projReleases);
    	
    	
    	//retrieve all the file .java of a specific release
    	commits = GetGitInfo.getAllCommits(git);
    	GetGitInfo.findReleaseFiles(git, repository, projReleases, commits);
    	
    	//set what versions are Ov, Fv, Iv, Av
    	setOvReleases(projReleases, projTickets);
    	setFvReleases(projReleases,projTickets, commits);
    	setIvReleases(projReleases, projTickets);
    	setAvReleases(projReleases, projTickets);
    	
    	//find bugginess of files
    	
    	
    	
    	
    	/*for (j=0;j<projReleasesLen ;j++) {
       	 
    	 	projReleases.get(j).printRelease();	
    	 	
    	 
        }*/
    	
    	


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
	
	
	private static void setFvReleases(List<Release> projReleases, List<Ticket> projTickets, List<RevCommit> commits) {
		

		int j;
		Integer commitsNum;

		
		
		commitsNum = commits.size();
		
		for (Ticket ticket : projTickets) {
			
			
			for(j = 0; j < commitsNum; j++) {
				
				RevCommit commit = commits.get(j);
				
				if (commit.getFullMessage().contains(ticket.getTicketKey() + ":")) {
			
					Release fv = findFv(Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime(), projReleases);
					ticket.setFv(fv);
					//System.err.print("\nKey: " + ticket.getTicketKey() + "\n" + commit.getFullMessage() +"\n\n");
					
				}
				
			}
			
		}
		checkIfFvExists(projTickets);
		
		
		
	}
	
	
	
	private static void setAvReleases(List<Release> projReleases, List<Ticket> projTickets) {
		
		for (Ticket ticket: projTickets) {
			
			System.out.println(ticket.getTicketKey());
			Integer iv = ticket.getIv().getReleaseIndex();
			Integer fv = ticket.getFv().getReleaseIndex();
			
			
			for (int i = iv; i < fv; i++) {
				
				Release release = findRelease(i, projReleases);
				ticket.setAv(release);
				
				
			}
			
			ticket.printTicket();
			ticket.printVersions();
			
			
			
			
			
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
		
		Float prop;
		
		for (Ticket ticket : projTickets) {
			
			Release release = findIV(ticket, projReleases);
			
			
			if (release != null) {
				Boolean isCorrect = checkIfIvIsCorrect(release, ticket);
				if (isCorrect) {
					ticket.setIv(release);
				}
				
				//System.out.println(ticket.getTicketKey());
			}
			
			
			
			//ticket.printTicket();
			//ticket.printVersions();
		}
		
		prop = proportion(projTickets);
		
		for (Ticket ticketWithoutIv : projTickets) {
			
			if (ticketWithoutIv.getIv()==null) {
				
				int fv = ticketWithoutIv.getFv().getReleaseIndex();
				int ov = ticketWithoutIv.getOv().getReleaseIndex();
				
				Float id = fv - (fv - ov) * prop;
				
				//System.out.println(id.intValue());

				if (id.intValue() <= 0 ) {
					id = (float) 1;
					

				}
				if (id.intValue() > ticketWithoutIv.getFv().getReleaseIndex()) {
					//System.out.println(ticketWithoutIv.getTicketKey()+ "eh");
					

					id = (float) ticketWithoutIv.getFv().getReleaseIndex();
					
					//System.out.println(id.intValue());
				}
				Release release = findRelease(id.intValue(), projReleases);
				

				ticketWithoutIv.setIv(release);
				//System.out.println(ticketWithoutIv.getTicketKey());
				//System.out.println(id.intValue());
				//System.out.println(release.getReleaseIndex());

				
			}
			
			//ticketWithoutIv.printTicket();
			//ticketWithoutIv.printVersions();
			
			
		}
		
		
	}









	private static Boolean checkIfIvIsCorrect(Release release, Ticket ticket) {
		
		if (release.getReleaseIndex() > ticket.getOv().getReleaseIndex()) {
			
			return false;
		
		}
		
		return true;
	}


















	private static Release findRelease(int id, List<Release> projReleases) {
		for (int i = 0; i < projReleases.size(); i++) {
		
			
			if (projReleases.get(i).getReleaseIndex() == id) {
			
				return projReleases.get(i);
				
			}
			
			
		}
		return null;
	}


















	private static Float proportion(List<Ticket> projTickets) {
		
		List<Float> propList = new ArrayList<>();
		
		for (Ticket ticket: projTickets) {
			
			if (ticket.getIv()!=null && !ticket.getFv().getReleaseIndex().equals(ticket.getOv().getReleaseIndex()) ) {
				
				float num =(ticket.getFv().getReleaseIndex().floatValue() - ticket.getIv().getReleaseIndex().floatValue());
				float den = ticket.getFv().getReleaseIndex().floatValue() - ticket.getOv().getReleaseIndex().floatValue();
				propList.add(num/den);

				
				
				
			}
			
			
		}
		
		float tot = 0;
		for (Float p: propList) {
			tot += p;
		}
		System.out.println(tot/propList.size());
		return tot/propList.size();
	
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
			//System.out.println(ticket.getTicketKey());
			return null;
			
		
	}


















	private static Release findFv(LocalDateTime commitDate, List<Release> projReleases) {
		
		for (int i = 0; i < projReleases.size()-1; i++ ) {
			
			Release release1 = projReleases.get(i);
			Release release2 = projReleases.get(i+1);

			
			if (i ==0 && commitDate.isBefore(release1.getReleaseDate())) {

				return release1;

			}
			
			else if (commitDate.isAfter(release1.getReleaseDate()) && commitDate.isBefore(release2.getReleaseDate())) {
				
				//System.out.print(release2.getReleaseName().toString() + "\n");

				return release2;

				
			}
			
			else if (commitDate.isEqual(release1.getReleaseDate())) {

				return release1;

			}
			
			else if (commitDate.isEqual(release2.getReleaseDate())) {

				return release2;


			}
			
			else if (i == projReleases.size()-1 && commitDate.isAfter(release2.getReleaseDate())) {
				return new Release(projReleases.size()+1 ,null, null);
			}
			
			
			
		}
		return null;
		
	}



	
	

}

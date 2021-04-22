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

	private static String PROJ_NAME = "BOOKKEEPER";
	static String path;
	static String url;
	static Repository repository;
    static Map<LocalDateTime, String> releases;
    
    private static List<Release> projReleases = new ArrayList<>();
    private static List<Ticket> projTickets = new ArrayList<>();
    private static List<RevCommit> commits = new ArrayList<>();
	
	public static void main(String[] args) throws InvalidRemoteException, GitAPIException, IOException, JSONException  {
		
		int j;
		path = "/Users/chiacchius/Desktop/" + PROJ_NAME;
		url = "https://github.com/apache/" + PROJ_NAME;
		Git git= GetGitInfo.cloneProjectFromGitHub(path, PROJ_NAME);
		Repository repository = git.getRepository();
		
		
		
		
    	
    	//retrieve all releases of the project
    	projReleases = GetJiraInfo.getReleases(PROJ_NAME);
    	
    	
    	//consider only the first half
    	int projReleasesLen = projReleases.size();
    	
    	
    	//retrieve all the tickets
    	projTickets = GetJiraInfo.getTickets(PROJ_NAME, projReleases);
    	
    	
    	//retrieve all the file .java of a specific release
    	commits = GetGitInfo.getAllCommits(git);
    	GetGitInfo.findReleaseFiles(git, repository, projReleases, commits);
    	
    	//set what versions are Ov, Fv, Iv 
    	setOvReleases(projReleases, projTickets);
    	setFvReleases(projReleases,projTickets, commits);
    	setIvReleases(projReleases, projTickets);
    	
    	
    	for (j=0;j<projReleasesLen ;j++) {
       	 
    	 	projReleases.get(j).printRelease();	
    	 	
    	 
        }
    	
    	
    	
    	
    	
		
		
	

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
					LocalDateTime commitDate = Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
					Release fv = findFv(commitDate, projReleases);
					ticket.setFv(fv);
					//System.err.print("\nKey: " + ticket.getTicketKey() + "\n" + commit.getFullMessage() +"\n\n");
					
				}
				
			}
			
		}
		checkIfFvExists(projTickets);
		
		
		
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
			JSONObject json = ticket.getJSONObject();
			//Release iv = findIV(json);
			Release release = findIV(json, projReleases);
			ticket.setIv(release);
			ticket.printTicket();
			ticket.printVersions();
		}
		
		prop = proportion(projTickets);
		
		
	}









	private static Float proportion(List<Ticket> projTickets) {
		
		List<Float> propList = new ArrayList<>();
		
		for (Ticket ticket: projTickets) {
			
			if (ticket.getIv()!=null && ticket.getFv().getReleaseIndex() != ticket.getOv().getReleaseIndex()) {
				
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


















	private static Release findIV(JSONObject json, List<Release> projReleases) throws JSONException {
		
			
		
		
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

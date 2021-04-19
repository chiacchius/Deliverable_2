package deliverable2;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
import org.json.JSONException;


public class MainControl {

	private static String PROJ_NAME = "BOOKKEEPER";
	static String path;
	static String url;
	static Repository repository;
    static Map<LocalDateTime, String> releases;
    
    private static List<Release> projReleases = new ArrayList<>();
    private static List<Ticket> projTickets = new ArrayList<>();
    //private static List<RevCommit> commits = new ArrayList<>();
	
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
    	projReleasesLen = projReleasesLen/2;
    	
    	//retrieve all the tickets
    	projTickets = GetJiraInfo.getTickets(PROJ_NAME);
    	
    	
    	//retrieve all the file .java of a specific release
    	GetGitInfo.findReleaseFiles(git, repository, projReleases);
    	
    	//set what versions are Ov, Fv, Iv 
    	setOvReleases(projReleases, projTickets);
    	setFvReleases(projReleases, projTickets);  
    	
    	
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
				
				if (j ==0 && ticketDate.isBefore(release1.getReleaseDate())) {
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
	
	
	
	private static void setFvReleases(List<Release> projReleases, List<Ticket> projTickets) {
		
		Integer ticketsNumber = projTickets.size();
		Integer releasesNumber = projReleases.size();
		
		for (int i = 0; i < ticketsNumber; i++) {
			
			
			Ticket ticket = projTickets.get(i);
			LocalDateTime ticketDate = ticket.getResolutionDate().atStartOfDay();
			
			for (int j = 0; j < releasesNumber-1; j++) {
				
				Release release1 = projReleases.get(j);
				Release release2 = projReleases.get(j+1);
				
				
				if (j ==0 && ticketDate.isBefore(release1.getReleaseDate())) {
					ticket.setFv(release1);
				}
				
				else if (ticketDate.isAfter(release1.getReleaseDate()) && ticketDate.isBefore(release2.getReleaseDate())) {
					
					//System.out.print(release2.getReleaseName().toString() + "\n");
					ticket.setFv(release2);
					
				}
				
				else if (ticketDate.isEqual(release1.getReleaseDate())) {
					ticket.setFv(release1);

				}
				
				else if (ticketDate.isEqual(release2.getReleaseDate())) {
					ticket.setFv(release2);

				}
				
			}
			ticket.printTicket();
			ticket.printVersions();
		}
		
		
	}
	
	
	
	
	
	
	
	
	
	
	

}

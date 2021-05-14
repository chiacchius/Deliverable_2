package Controller;

import java.time.LocalDateTime;
import java.util.List;

import Entity.Release;

public class ReleaseController {

	
	
	public static Release findRelease(int id, List<Release> projReleases) {
		for (int i = 0; i < projReleases.size(); i++) {
		
			
			if (projReleases.get(i).getReleaseIndex() == id) {
			
				return projReleases.get(i);
				
			}
			
			
		}
		return null;
	}
	
	
	
	
	
public static Release findReleaseFromLdt(LocalDateTime commitDate, List<Release> projReleases) {
		
		for (int i = 0; i < projReleases.size()-1; i++ ) {
			
			Release release1 = projReleases.get(i);
			Release release2 = projReleases.get(i+1);

			
			if (i ==0 && commitDate.isBefore(release1.getReleaseDate())) {

				return release1;

			}
			
			else if (commitDate.isAfter(release1.getReleaseDate()) && commitDate.isBefore(release2.getReleaseDate())) {
				

				return release2;

				
			}
			
			else if (commitDate.isEqual(release1.getReleaseDate())) {

				return release1;

			}
			
			else if (commitDate.isEqual(release2.getReleaseDate())) {

				return release2;


			}
			
			else if (i == projReleases.size()-1 && commitDate.isAfter(release2.getReleaseDate())) {
				return null;
			}
			
			
			
		}
		return null;
		
	}
	
	
	
	
	
	
}

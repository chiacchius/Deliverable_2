package Handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.json.JSONException;
import org.json.JSONObject;

import Entity.Release;
import Entity.Ticket;

import org.json.JSONArray;

public class JiraHandler {
	
	
	
	private JiraHandler() {
		throw new IllegalStateException("Handler class");
	}
	
	public static List<Release> getReleases(String projectName) throws IOException, JSONException{
		
		
		HashMap<LocalDateTime, String> releaseNames;
	    HashMap<LocalDateTime, String> releaseID;
	    ArrayList<LocalDateTime> releases;

		
		List<Release> projReleases = new ArrayList<>();
		
		   //Fills the arraylist with releases dates and orders them
		   //Ignores releases with missing dates
		   releases = new ArrayList<>();
		         Integer i;
		         Integer j;
		         String url = "https://issues.apache.org/jira/rest/api/2/project/" + projectName;
		         JSONObject json = readJsonFromUrl(url);
		         JSONArray versions = json.getJSONArray("versions");
		         releaseNames = new HashMap<>();
		         releaseID = new HashMap<> ();
		         for (i = 0; i < versions.length(); i++ ) {
		            String name = "";
		            String id = "";
		            if(versions.getJSONObject(i).has("releaseDate")) {
		               if (versions.getJSONObject(i).has("name"))
		                  name = versions.getJSONObject(i).get("name").toString();
		               if (versions.getJSONObject(i).has("id"))
		                  id = versions.getJSONObject(i).get("id").toString();
		               addRelease(releases, releaseID, releaseNames, versions.getJSONObject(i).get("releaseDate").toString(),
		                          name,id);
		            }
		         }
		         // order releases by date
		         Collections.sort(releases, new Comparator<LocalDateTime>(){
		            //@Override
		            public int compare(LocalDateTime o1, LocalDateTime o2) {
		                return o1.compareTo(o2);
		            }
		         });
		         if (releases.size() < 6)
		            return Collections.emptyList();
		         
		         for (j=0;j<releases.size();j++) {
		        	 
		        	 projReleases.add(new Release(j+1,releaseNames.get((releases).get(j)),releases.get(j)));
		        	 
		        	 
		         }
		         
		    
		         
		         return projReleases;
		
	}
	
	
	
	
	public static List<Ticket> getTickets(String projectName, List<Release> projReleases) throws IOException, JSONException{
		
		List<Ticket> tickets = new ArrayList<>();
		
		 Integer j = 0, i = 0, total = 1;
	      //Get JSON API for closed bugs w/ AV in the project
	      do {
	         //Only gets a max of 1000 at a time, so must do this multiple times if bugs >1000
	         j = i + 1000;
	         String url = "https://issues.apache.org/jira/rest/api/2/search?jql=project=%22"
	                + projectName + "%22AND%22issueType%22=%22Bug%22AND(%22status%22=%22closed%22OR"
	                + "%22status%22=%22resolved%22)AND%22resolution%22=%22fixed%22&fields=key,fixversions,resolutiondate,versions,created&startAt="
	                + i.toString() + "&maxResults=" + j.toString();
	         JSONObject json = readJsonFromUrl(url);
	         JSONArray issues = json.getJSONArray("issues");
	         total = json.getInt("total");
	         for (; i < total && i < j; i++) {
	            //Iterate through each bug
	        	 
	            String key = issues.getJSONObject(i%1000).get("key").toString();
	            
	            Integer id = issues.getJSONObject(i%1000).getInt("id");
	            CharSequence dateString = ((CharSequence) issues.getJSONObject(i%1000).getJSONObject("fields").get("created")).subSequence(0,10);
	            LocalDate crDate = LocalDate.parse(dateString);
	            dateString = ((CharSequence) issues.getJSONObject(i%1000).getJSONObject("fields").get("resolutiondate")).subSequence(0,10);
	            LocalDate resDate = LocalDate.parse(dateString);
	            
	      
	           	Ticket ticket = new Ticket(key, id, crDate, resDate);
	           	ticket.addJSONObject(issues.getJSONObject(i%1000));
	           	tickets.add(ticket);
	          

	         }  
	      } while (i < total);
		
		
		
		
		return tickets;
	}
	
	
	
	
	
	
	public static JSONArray readJsonArrayFromUrl(String url) throws IOException, JSONException {
	      InputStream is = new URL(url).openStream();
	      try(
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	      ) {
	    	  return new JSONArray(readAll(rd));
	       } 
	       finally {
	         is.close();
	       }
	   }
	
	
	public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
	      InputStream is = new URL(url).openStream();
	      try(
	    	BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
	      ){
	    	  
	         return new JSONObject(readAll(rd));  
	          
	       } finally {
	         is.close();
	       }
	   }
	
	private static String readAll(Reader rd) throws IOException {
	      StringBuilder sb = new StringBuilder();
	      int cp;
	      while ((cp = rd.read()) != -1) {
	         sb.append((char) cp);
	      }
	      return sb.toString();
	   }
	
	
	
	public static void addRelease(List<LocalDateTime> releases, Map<LocalDateTime, String> releaseID, Map<LocalDateTime, String> releaseNames, String strDate, String name, String id) {
	      LocalDate date = LocalDate.parse(strDate);
	      LocalDateTime dateTime = date.atStartOfDay();
	      if (!releases.contains(dateTime))
	         releases.add(dateTime);
	      releaseNames.put(dateTime, name);
	      releaseID.put(dateTime, id);
	   }
	
	
	
	
	
	
	
	
	
}

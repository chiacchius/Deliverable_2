package deliverable2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;

import org.json.JSONException;
import org.json.JSONObject;


import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.revwalk.DepthWalk.Commit;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.json.JSONArray;


public class GetGitInfo {
	
	
	
	
	private GetGitInfo() throws InvalidRemoteException, TransportException, GitAPIException {}
	
	
	public static Git cloneProjectFromGitHub(String path, String projectName) throws InvalidRemoteException, org.eclipse.jgit.api.errors.TransportException, GitAPIException, IOException {
		//get the project from github and copy it locally
		
		Git git;
		
		try {
			git = Git.cloneRepository()
				       .setURI("https://github.com/apache/" + projectName)
				       .setDirectory(new File(path)) 
				       .call();
		} catch (JGitInternalException e) {
			//e.printStackTrace();
			git = Git.open(new File(path));
			
		}
		
		return git;
		
		
	}
	
	public static void findReleaseFiles(Git git, Repository repository, List<Release> projReleases) throws NoHeadException, GitAPIException, IOException {
			
		List<RevCommit> commits = new ArrayList<>();
		
    	//get all commits of the project
		commits = getAllCommits(git);
		
		//set last release commit
    	associateLastCommit(commits, projReleases);
    	
    	//get all files of a release
    	getFiles(projReleases, repository);
    	
    	
    	
    	
		
	}


	public static List<RevCommit> getAllCommits(Git git) throws NoHeadException, GitAPIException, IOException{
		
		List<RevCommit> commits = new ArrayList<>();
		Iterable<RevCommit> log = git.log().all().call();

        for (RevCommit commit : log) {
                commits.add(commit);
        }
		return commits;
        
        //System.out.print(commits.toString() + "\n");
		
	}
	
	public static void associateLastCommit(List<RevCommit> commits, List<Release> projReleases) {
		
		Integer commitsNumber = commits.size();
		Integer releaseNumber = projReleases.size();
		Release release;
		RevCommit commit;
		LocalDateTime commitDate;
		
		int start = 0;

		Collections.reverse(commits);
		
		for (int i = 0; i < releaseNumber ; i++) {
			
			release = projReleases.get(i);
			
			
		    for (int j = start; j < commitsNumber; j++) {
					
					commit = commits.get(j);
					
					commitDate = Instant.ofEpochSecond(commit.getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime();
					

					if (commitDate.isAfter(release.getReleaseDate())) {
						release.setLastCommit(commits.get(j-1));
						start = j;
						break;
					}
					
					release.addCommit(commit);
					//System.out.print(commit.toString());
				}
				
			}
			

			
		
		
		
	}
	
	
	
	public static void getFiles(List<Release> releases, Repository repository) throws MissingObjectException, IncorrectObjectTypeException, CorruptObjectException, IOException {
		
		int i;
		for(i=0 ; i < releases.size(); i++) {
			
			Release release = releases.get(i);
			
			RevCommit lastCommit = release.getLastCommit();
			
			
			 RevTree tree = lastCommit.getTree();
	         TreeWalk treeWalk = new TreeWalk(repository);
	         treeWalk.addTree(tree);
	         treeWalk.setRecursive(true);
	         while (treeWalk.next()) {
	            	
	            if (treeWalk.getPathString().contains(".java")) {
	            	
	            	
	            	release.addFile(new ReleaseFile(treeWalk.getPathString()));
	            		            		
	           		
	         
	           	}
	            	

	         }
		
		}
		
		
	}
		
	
	
	
	
	
	
	
	
	
	
}

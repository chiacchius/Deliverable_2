package Handler;

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
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;

import org.json.JSONException;
import org.json.JSONObject;

import Entity.Changes;
import Entity.Release;
import Entity.ReleaseFile;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.RawTextComparator;
import org.eclipse.jgit.errors.CorruptObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.TransportException;
import org.eclipse.jgit.revwalk.DepthWalk.Commit;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import org.json.JSONArray;


public class GitHubHandler {
	
	
	
	
	private GitHubHandler() throws InvalidRemoteException, TransportException, GitAPIException {}
	
	
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
	
	public static List<ReleaseFile> findReleaseFiles(Git git, Repository repository, List<Release> projReleases, List <RevCommit> commits) throws NoHeadException, GitAPIException, IOException {
			
		
		
		//set last release commit
    	associateLastCommit(commits, projReleases);
    	
    	//get all files of a release
    	return getFiles(projReleases, repository);
    	
    	
    	
    	
		
	}


	public static List<RevCommit> getAllCommits(Git git) throws NoHeadException, GitAPIException, IOException{
		
		List<RevCommit> commits = new ArrayList<>();
		Iterable<RevCommit> log = git.log().all().call();

        for (RevCommit commit : log) {
        		
                commits.add(commit);
        }
        System.out.println(commits.size());
		return commits;
        
        //System.out.print(commits.toString() + "\n");
		
	}
	
	public static List<RevCommit> associateLastCommit(List<RevCommit> commits, List<Release> projReleases) {
		
		Integer commitsNumber = commits.size();
		Integer releaseNumber = projReleases.size();
		Release release;
		RevCommit commit;
		LocalDateTime commitDate;
		
		int start = 0;

		Collections.reverse(commits);
		Release lastRelease = projReleases.get(projReleases.size()-1);
		
		for(int k = 0; k < commitsNumber; k++) {
			
			if (Instant.ofEpochSecond(commits.get(k).getCommitTime()).atZone(ZoneId.of("UTC")).toLocalDateTime().isAfter(lastRelease.getReleaseDate())) {
				commitsNumber = k+1;
				
			}
			
		}
		
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
			}
		    

				
		}
			
		return commits;
			
		
		
		
	}
	
	
	
	public static List<ReleaseFile> getFiles(List<Release> releases, Repository repository) throws IOException {
		
		int i;
		List<ReleaseFile> files = new ArrayList<>();
		for(Release release: releases) {
			
			//Release release = releases.get(i);
			int numFiles=0;
			
			try (RevWalk revWalk = new RevWalk(repository)) {
                RevCommit commit = release.getLastCommit();
                
                // and using commit's tree find the path
                RevTree tree = commit.getTree();
                try (TreeWalk treeWalk = new TreeWalk(repository)) {
                    treeWalk.addTree(tree);
                    treeWalk.setRecursive(true);
                   
                    while( treeWalk.next() ) {
                    	if( treeWalk.getPathString().contains(".java") ) {
                    		//retrieve some data                		
                    		numFiles++;
                    		//build java class entity
                    		//init
                    		ReleaseFile rf = new ReleaseFile(release, treeWalk.getPathString());
                    		Changes change = new Changes(treeWalk.getPathString());
                    		change.addPath(treeWalk.getPathString());
                    		rf.setChange(change);
                    		//rf.setLOC(Integer.valueOf( FileParser.countLines( treeWalk, this.repository )) );                      	
                    		release.addFile(rf);
                    		files.add(rf);
                    	}
                    }
                    
                }
                
			}
			release.setNumFiles(numFiles);
			
			
			
			
	         
		
		}
		return files;
		
		
		
		
		
	}


	

	
	
	

	
	
	
	
	
	
	
	
	
}

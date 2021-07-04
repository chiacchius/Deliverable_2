package handler;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import java.util.List;
import java.util.Collections;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;


import org.eclipse.jgit.lib.Repository;


import entity.Changes;
import entity.Release;
import entity.ReleaseFile;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;

import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;

import org.eclipse.jgit.treewalk.TreeWalk;


public class GitHubHandler {
	
	
	
	
	private GitHubHandler() {
			throw new IllegalStateException("Handler class");
	}
	
	
	public static Git cloneProjectFromGitHub(String path, String projectName) throws GitAPIException, IOException {
		//get the project from github and copy it locally
		
		Git git;
		
		try {
			git = Git.cloneRepository()
				       .setURI("https://github.com/apache/" + projectName)
				       .setDirectory(new File(path)) 
				       .call();
		} catch (JGitInternalException e) {
			git = Git.open(new File(path));
			
		}
		
		return git;
		
		
	}
	
	public static List<ReleaseFile> findReleaseFiles(Repository repository, List<Release> projReleases, List <RevCommit> commits) throws IOException {
			
		
		
		//set last release commit
    	associateLastCommit(commits, projReleases);
    	
    	//get all files of a release
    	return getFiles(projReleases, repository);
    	
    	
    	
    	
		
	}


	public static List<RevCommit> getAllCommits(Git git) throws GitAPIException, IOException{
		
		List<RevCommit> commits = new ArrayList<>();
		Iterable<RevCommit> log = git.log().all().call();

        for (RevCommit commit : log) {
        		
                commits.add(commit);
        }
		return commits;
        

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
		
		List<ReleaseFile> files = new ArrayList<>();
		for(Release release: releases) {
			
			int numFiles=0;
			

			RevCommit commit = release.getLastCommit();
                
			// and using commit's tree find the path
			RevTree tree = commit.getTree();
			try (TreeWalk treeWalk = new TreeWalk(repository)) {
				treeWalk.addTree(tree);
				treeWalk.setRecursive(true);
                   
				while( treeWalk.next() ) {
					if( treeWalk.getPathString().contains(".java") ) {
						numFiles++;
                    		
						ReleaseFile rf = new ReleaseFile(release, treeWalk.getPathString()); //build ReleaseFile entity
						Changes change = new Changes(treeWalk.getPathString());
						change.addPath(treeWalk.getPathString());
						rf.setChange(change);
						release.addFile(rf);
						Integer loc = MetricsHandler.locCalculator(repository, treeWalk);
						rf.setLoc(loc);
						files.add(rf); //add it to files list
					}
				}
                    
			}
                

			release.setNumFiles(numFiles);
			

		}
		return files;
		
		
		
		
		
	}


	

	
	
	

	
	
	
	
	
	
	
	
	
}

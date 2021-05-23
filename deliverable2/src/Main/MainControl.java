package Main;

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

import Controller.ControllerDeliverable1;
import Controller.ControllerDeliverable2;
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


public class MainControl {

	private static ProjectLogger logCTR;
	
	public static void main(String[] args) throws GitAPIException, IOException, JSONException  {
		
		
		String PROJNAME1 = "BOOKKEEPER";
		String PROJNAME2 = "ZOOKEEPER";
		String path = "./";
		logCTR = ProjectLogger.getSingletonInstance();
		ControllerDeliverable1 controllerDeliverable1 = new ControllerDeliverable1();
		ControllerDeliverable2 controllerDeliverable2 = new ControllerDeliverable2();

		
		/* ****************** BOOKKEEPER ***************** */
		
		
		controllerDeliverable1.run(PROJNAME1, path+PROJNAME1); //Deliverable1 --> creation of the dataset with the check oof the bugginess
		
		
		controllerDeliverable2.run(PROJNAME1, controllerDeliverable1.getCsvFileName()); //Deliverable2 --> analyze data with machine learning
		
		/* ****************** ZOOKEEPER ***************** */
		
		
		controllerDeliverable1.run(PROJNAME2, path+PROJNAME2); //Deliverable1 --> creation of the dataset with the check of the bugginess
		
		
		controllerDeliverable2.run(PROJNAME2, controllerDeliverable1.getCsvFileName()); //Deliverable2 --> analyze data with machine learning

		
	}
	
	

	
	
	
	
	
	

}

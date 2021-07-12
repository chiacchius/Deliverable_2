package utility;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class ProjectLogger {



	private static final String PATH = "./logFile.log";
	
	private static ProjectLogger instance = null;
	private Logger logger = Logger.getLogger( "myLogger" );
	private FileHandler fh;
	
	private ProjectLogger() throws SecurityException, IOException {
		this.fh = new FileHandler( PATH );
		this.logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);
	}
	
	public static ProjectLogger getSingletonInstance() throws SecurityException, IOException {
		if( instance == null )
			instance = new ProjectLogger();
		return instance;
	}
	
	public void saveMess( String message ) {
		this.logger.info(message);
	}
}

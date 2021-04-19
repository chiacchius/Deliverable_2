package deliverable2;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvWriter {
	
	
	public static void writeCsv(String projectName, ArrayList<LocalDateTime> releases, HashMap<LocalDateTime, String> releaseID, HashMap<LocalDateTime, String> releaseNames) throws IOException{
		   
		FileWriter fileWriter = null;
		 try {
	            fileWriter = null;
	            String outname = projectName + "VersionInfo.csv";
					    //Name of CSV for output
					    fileWriter = new FileWriter(outname);
	            fileWriter.append("Index,Version ID,Version Name,Date");
	            fileWriter.append("\n");
	            int numVersions = releases.size();
	            for ( int i = 0; i < releases.size(); i++) {
	               Integer index = i + 1;
	               fileWriter.append(index.toString());
	               fileWriter.append(",");
	               fileWriter.append(releaseID.get(releases.get(i)));
	               fileWriter.append(",");
	               fileWriter.append(releaseNames.get(releases.get(i)));
	               fileWriter.append(",");
	               fileWriter.append(releases.get(i).toString());
	               fileWriter.append("\n");
	            }

	         } catch (Exception e) {
	            System.out.println("Error in csv writer");
	            e.printStackTrace();
	         } finally {
	            try {
	               fileWriter.flush();
	               fileWriter.close();
	            } catch (IOException e) {
	               System.out.println("Error while flushing/closing fileWriter !!!");
	               e.printStackTrace();
	            }
	         }
	}
}

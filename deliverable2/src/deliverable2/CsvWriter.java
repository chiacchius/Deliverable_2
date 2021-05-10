package deliverable2;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CsvWriter {
	
	
	public static void writeCsv(String projectName, List<Release> releases) throws IOException{
		   
		FileWriter fileWriter = null;
		 try {
	            fileWriter = null;
	            String outname = projectName + "VersionInfo.csv";
					    //Name of CSV for output
					    fileWriter = new FileWriter(outname);
	            fileWriter.append("Index,Version ID,Version Name,Date,File,Bugginess");
	            fileWriter.append("\n");
	            for ( int i = 0; i < releases.size()/2; i++) {
		           	Release release = releases.get(i);
		            for (int j=0; j<release.getNumFiles(); j++) {
		               
		               Integer index = i + 1;
		               fileWriter.append(index.toString());
		               fileWriter.append(",");
		               fileWriter.append(release.getReleaseIndex().toString());
		               fileWriter.append(",");
		               fileWriter.append(release.getReleaseName());
		               fileWriter.append(",");
		               fileWriter.append(release.getReleaseDate().toString());
		               fileWriter.append(",");
		               fileWriter.append(release.getReleaseFiles().get(j).getFilePath());
		               fileWriter.append(",");
		               fileWriter.append(release.getReleaseFiles().get(j).getBugginess().toString());
		               fileWriter.append("\n");
		            }
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

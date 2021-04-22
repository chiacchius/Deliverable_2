package deliverable2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.json.JSONObject;


public class Ticket {
	private String key;
	private Integer id;
	private LocalDate creationDate;
	private LocalDate resolutionDate;
	private Release iv;
	private Release ov;
	private Release fv;
	private JSONObject jsonObj;
	private List<Release> av;
	
	
	
	public Ticket(String key, Integer id, LocalDate crDate, LocalDate resDate) {
		this.key = key;
		this.id = id;
		this.creationDate = crDate;
		this.resolutionDate = resDate;
	}
	
	
	public void printTicket() {
		System.out.print("Ticket: " + this.id.toString() + " key: " + this.key  +  " creation date: " + this.creationDate.toString()  + " resolution date: " + this.resolutionDate.toString() + "\n");
	}
	
	public void printVersions() {
		this.printIv();
		this.printFv();
		this.printOv();

	}
	
	private void printOv() {
		if (this.ov != null) {
			System.out.println("Ov: " +this.ov.getReleaseName());
		}	
	}


	private void printFv() {
		if (this.fv != null) {
			System.out.println("Fv: " + this.fv.getReleaseName());
		}
		else {
			System.out.println("Fv: null" );

		}
	}


	private void printIv() {
		if (this.iv != null) {
			System.out.println("Iv: " + this.iv.getReleaseName());
		}
		
	}


	public String getTicketKey() {
		return this.key;
	}
	
	public LocalDate getCreationDate() {
		return this.creationDate;
	}
	
	public LocalDate getResolutionDate() {
		return this.resolutionDate;
	}
	
	public void addJSONObject(JSONObject jsonObj) {
		this.jsonObj = jsonObj;
	}
	
	public JSONObject getJSONObject() {
		return this.jsonObj;
	}
	
	public void setIv(Release release) {
		this.iv = release;
	}
	
	public void setOv(Release release) {
		this.ov = release;
	}
	
	public void setFv(Release release) {
		
		if (this.fv!=null) {
			
			if (release.getReleaseIndex() > this.fv.getReleaseIndex()) {
				this.fv = release;
			}
			
			
		}
		
		else {
			this.fv = release;
		}
		
		
	}
	
	public void setAv(Release release) {
		av.add(release);
	}
	
	public Release getFv() {
		return this.fv;
	}
	
	public Release getIv() {
		return this.iv;
	}
	
	public Release getOv() {
		return this.ov;
	}
	
	
}

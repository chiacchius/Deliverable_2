package deliverable2;

import java.time.LocalDate;
import java.util.List;


public class Ticket {
	private String key;
	private Integer id;
	private LocalDate creationDate;
	private LocalDate resolutionDate;
	private Release iv;
	private Release ov;
	private Release fv;
	private List<Release> av;
	
	
	
	public Ticket(String key, Integer id, LocalDate crDate, LocalDate resDate) {
		this.key = key;
		this.id = id;
		this.creationDate = crDate;
		this.resolutionDate = resDate;
	}
	
	
	public void printTicket() {
		System.out.print("\nTicket: " + this.id.toString() + " key: " + this.key  +  " creation date: " + this.creationDate.toString()  + " resolution date: " + this.resolutionDate.toString() + "\n");
	}
	
	public void printVersions() {
		System.out.print("Ov: " + this.ov.getReleaseName() + "  Fv: " + this.fv.getReleaseName() + "\n\n");
	}
	
	public LocalDate getCreationDate() {
		return this.creationDate;
	}
	
	public LocalDate getResolutionDate() {
		return this.resolutionDate;
	}
	
	
	public void setIv(Release release) {
		this.iv = release;
	}
	
	public void setOv(Release release) {
		this.ov = release;
	}
	
	public void setFv(Release release) {
		this.fv = release;
	}
	
	public void setAv(Release release) {
		av.add(release);
	}
	
	
}

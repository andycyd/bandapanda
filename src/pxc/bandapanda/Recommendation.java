package pxc.bandapanda;

public class Recommendation {
	
	private int source_id;
	private int resource_id;
	private String type;
	private String date;
	private int read;
	private String source_name;
	private String name;
	
	
	Recommendation(int s, int r, String t, String d, int read, String sname, String name){
		source_id = s;
		resource_id = r;
		type = t;
		date = d;
		this.read = read;
		this.source_name = sname;
		this.name = name;
	}
	public int getSource_id() {
		return source_id;
	}
	public void setSource_id(int source_id) {
		this.source_id = source_id;
	}
	public int getResource_id() {
		return resource_id;
	}
	public void setResource_id(int resource_id) {
		this.resource_id = resource_id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public int getRead() {
		return read;
	}
	public void setRead(int read) {
		this.read = read;
	}
	public String getSource_name() {
		return source_name;
	}
	public void setSource_name(String source_name) {
		this.source_name = source_name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}

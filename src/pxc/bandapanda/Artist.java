package pxc.bandapanda;

public class Artist {
	
	private int ID;
	private String name;
	private String cover;
	private int imageDrawablePointer;
	
	
	public Artist(){
		ID = -1;
	}
	public Artist(int id, String n, String c){
		ID = id;
		name = n;
		cover = c;
		imageDrawablePointer = -1;
	}
	
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCover() {
		return cover;
	}
	public void setCover(String cover) {
		this.cover = cover;
	}

	public int getImageDrawablePointer() {
		return imageDrawablePointer;
	}

	public void setImageDrawablePointer(int imageDrawablePointer) {
		this.imageDrawablePointer = imageDrawablePointer;
	}
	

}

package pxc.bandapanda;


public class Song {
	
	private int ID;
	private String title;
	private int IDalbum;
	private String album;
	private int IDgroup;
	private String group;
	private String cover;
	private String url;
	private int coverDrawablePointer;
	
	
	
	public Song(int id, String tit,int ida, String alb, int idg, String gr, String cov, int covP, String u){
		ID = id;
		title = tit;
		IDalbum = ida;
		album = alb;
		IDgroup = idg;
		group = gr;
		cover = cov;
		url = u;
		setCoverDrawablePointer(covP);
	}

	public int getID() {
		return ID;
	}



	public void setID(int iD) {
		ID = iD;
	}



	public String getTitle() {
		return title;
	}



	public void setTitle(String title) {
		this.title = title;
	}



	public String getAlbum() {
		return album;
	}



	public void setAlbum(String album) {
		this.album = album;
	}



	public String getGroup() {
		return group;
	}



	public void setGroup(String group) {
		this.group = group;
	}



	public String getCover() {
		return cover;
	}



	public void setCover(String cover) {
		this.cover = cover;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}

	public int getIDalbum() {
		return IDalbum;
	}

	public void setIDalbum(int iDalbum) {
		IDalbum = iDalbum;
	}

	public int getIDgroup() {
		return IDgroup;
	}

	public void setIDgroup(int iDgroup) {
		IDgroup = iDgroup;
	}

	public int getCoverDrawablePointer() {
		return coverDrawablePointer;
	}

	public void setCoverDrawablePointer(int coverDrawablePointer) {
		this.coverDrawablePointer = coverDrawablePointer;
	}

}

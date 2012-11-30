package pxc.bandapanda;

import java.util.Vector;

public class Artist {
	
	private int ID;
	private String name;
	private String cover;
	private int imageDrawablePointer;
	private Vector<Album> albums;
	private String info;
	private int year;
	
	
	public Artist(){
		ID = -1;
		albums = new Vector<Album>();
	}
	public Artist(int id, String n, String c){
		ID = id;
		name = n;
		cover = c;
		imageDrawablePointer = -1;
		albums = new Vector<Album>();
	}
	
	public void addAlbum(Album a){
		albums.add(a);
	}
	
	public Album getAlbum(int i){
		return albums.get(i);
	}
	
	public int getNumAlbums(){
		return albums.size();
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
	public Vector<Album> getAlbums() {
		return albums;
	}
	public void setAlbums(Vector<Album> albums) {
		this.albums = albums;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	

}

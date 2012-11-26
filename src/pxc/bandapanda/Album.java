package pxc.bandapanda;

import java.util.Vector;

public class Album {
	
	private int ID;
	private String name;
	private int IDart;
	private String cover;
	private String nameArt;
	private Vector<Song> songs;
	private int coverDrawablePointer;
	
	public Album(){
		coverDrawablePointer = -1;
		ID = -1;
		IDart = -1;
		songs = new Vector<Song>();
	}
	
	public Album(int id, String n, int idart, String nameart, String c){
		ID = id;
		name = n;
		IDart = idart;
		nameArt = nameart;
		songs = new Vector<Song>();
		coverDrawablePointer = -1;
		cover = c;
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
	public Vector<Song> getSongs() {
		return songs;
	}
	public void setSongs(Vector<Song> songs) {
		this.songs = songs;
	}
	
	public void addSong(Song s){
		songs.add(s);
	}
	
	public Song getSong(int i){
		return songs.get(i);
	}
	public int getCoverDrawablePointer() {
		return coverDrawablePointer;
	}
	public void setCoverDrawablePointer(int coverDrawablePointer) {
		this.coverDrawablePointer = coverDrawablePointer;
	}
	public int getIDart() {
		return IDart;
	}
	public void setIDart(int iDart) {
		IDart = iDart;
	}
	public String getNameArt() {
		return nameArt;
	}
	public void setNameArt(String nameArt) {
		this.nameArt = nameArt;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}
	

}

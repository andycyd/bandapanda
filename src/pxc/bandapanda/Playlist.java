package pxc.bandapanda;

import java.util.Vector;

public class Playlist {

	private Vector<Song> playlist;
	private int numSongs;
	private String name;
	private int ID;
	
	public Playlist(){
		name = "";
		ID = -1;
		numSongs = 0;
		playlist = new Vector<Song>();
	}
	
	
	public Playlist(String name, int id){
		playlist = new Vector<Song>();
		numSongs = 0;
		this.name = name;
		this.ID = id;
	}
	
	public void resetPlaylist(){
		playlist = new Vector<Song>();
		numSongs = 0;
	}
	
	public int getNumSongs() {
		return numSongs;
	}
	
	public void setNumSongs(int numSongs) {
		this.numSongs = numSongs;
	}
	
	public Song getSong(int i){
		return playlist.get(i);
	}
	
	public void addSong(Song s){
		playlist.add(s);
		++numSongs;
	}
	
	public void addSongAt(int pos, Song s){
		playlist.add(pos, s);
		++numSongs;
	}
	
	public void removeSong(int i){
		playlist.remove(i);
		--numSongs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}
	
	
}

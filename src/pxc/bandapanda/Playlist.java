package pxc.bandapanda;

import java.util.Vector;

public class Playlist {

	private Vector<Song> playlist;
	private int numSongs;
	
	
	public Playlist(){
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
	
	public void removeSong(int i){
		playlist.remove(i);
		--numSongs;
	}
	
	
}

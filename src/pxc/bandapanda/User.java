package pxc.bandapanda;

import java.util.Vector;

public class User {

	
	    
		private int ID;
		private String token;
		private String user;
		private static Vector<Playlist> playlists;
		private static User instance;
		
		static{
			instance = new User();
		}
		
		public void addSongToPlaylist(int id, Song s){
			playlists.get(id).addSong(s);
		}
		
		public void addPlaylist(String name, int id){
			Playlist p = new Playlist(name,id);
			playlists.add(p);
		}
		
		public void addPlaylist(Playlist p){
			playlists.add(p);
		}
		
		public String getNamePlaylist(int i){
			return playlists.get(i).getName();
		}
		
		public int getNumberPlaylists(){
			return playlists.size();
		}
		
		public int getIdPlaylist(int i){
			return playlists.get(i).getID();
		}
		private User(){
			playlists = new Vector<Playlist>();
		}
		
		public static User getInstance(){
			return User.instance;
		}
		
		public int getId(){
	    	return ID;
	    }
		
		public String getToken(){
			return token;
		}
		
		public String getUser(){
			return user;
		}
		
		public void setId(int id2){
			this.ID = id2;
		}
		
		public void setToken(String token2){
			this.token = token2;
		}
		
		public void setUser(String user2){
			this.user = user2;
		}

		public Vector<Playlist> getPlaylists() {
			return playlists;
		}

		public void setPlaylists(Vector<Playlist> playlists) {
			this.playlists = playlists;
		}
		
	}



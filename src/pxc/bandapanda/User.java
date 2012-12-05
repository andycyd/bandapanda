package pxc.bandapanda;

import java.util.Vector;

import org.json.JSONException;
import org.json.JSONObject;

import pusherclient.ChannelListener;
import pusherclient.Pusher;
import pusherclient.PusherListener;
import pusherclient.Pusher.Channel;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class User {

	
	    
		private int ID;
		private String token;
		private String user;
		private static Vector<Playlist> playlists;
		private static User instance;
		private static Context ct;
		private static NotificationManager nm;
		private boolean conected;
		
		static{
			instance = new User();
		}
		
		public void connectToPusher(){
			PusherConnection pc = new PusherConnection(ct, nm);
			pc.execute();
			conected = true;
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
			ID = -1;
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
		
		public Context getCt() {
			return ct;
		}

		public void setCt(Context ct) {
			this.ct = ct;
		}

		public NotificationManager getNm() {
			return nm;
		}

		public void setNm(NotificationManager nm) {
			this.nm = nm;
		}

		public boolean isConected() {
			return conected;
		}

		public void setConected(boolean conected) {
			this.conected = conected;
		}

		public class PusherConnection extends AsyncTask <Void, Void, String> {

	    	Context context;
	    	NotificationManager mNotificationManager;
	    	
		    
		    public PusherConnection(Context ct, NotificationManager nm){
		    	context = ct;
		    	mNotificationManager = nm;
		    }
		    
		    @Override
		    protected void onPreExecute(){

	        }
		    
		    
	    	@Override
	    	protected String doInBackground(Void... params) {
	    		final Pusher pusher = new Pusher("37cc28f59fd3d3e4f801");   
	            PusherListener eventListener = new PusherListener() {  
	                Channel channel;

	                public void onConnect(String socketId) {
	                    System.out.println("Pusher connected. Socket Id is: " + socketId);
	                    channel = pusher.subscribe("bandapanda_1");
	                    System.out.println("Subscribed to channel: " + channel);
	                    channel.send("client-test", new JSONObject());

	                    channel.bind("price-updated", new ChannelListener() {
	                        public void onMessage(String message) {
	                            System.out.println("Received bound channel message: " + message);
	                        }
	                    });
	                }

	                public void onMessage(String message) {
	                    System.out.println("Received message from Pusher: " + message);
	                    
	                    String ns = Context.NOTIFICATION_SERVICE;
	                    int icon = R.drawable.ic_launcher;
	                    CharSequence tickerText = "BandaPanda Notification";
	                    long when = System.currentTimeMillis();
	                     
	                    Notification notification = new Notification(icon, tickerText, when);
	                    
	                    CharSequence contentTitle = "My notification";
	    				JSONObject result;
	        			CharSequence contentText = null;
						try {
							result = new JSONObject(message);
							if(!result.getString("event").equals("connection_established") && !result.getString("event").equals("pusher:error")){
							
								if(result.getString("event").equals("song_recommendation")){
									contentTitle = "Song recommended:";
									contentText = "asdfasdfwqer";
								}
								if(result.getString("event").equals("album_recommendation")){
									contentTitle = "Album recommended:";
									contentText = "asdfasdfwqer";
								}

								Intent notificationIntent = new Intent(context, Search.class);
								PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
								notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
								final int HELLO_ID = 1;
		                    
								mNotificationManager.notify(HELLO_ID, notification);
							}
							//contentText = "Hello World!"+result.getString("data");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                    
	                    
	                }

	                public void onDisconnect() {
	                    System.out.println("Pusher disconnected.");
	                }
	            };

	            pusher.setPusherListener(eventListener);
	            pusher.connect();  
	    		return "null";
	    	
	    	}
	    	
	    	@SuppressWarnings("deprecation")
			protected void onPostExecute(String results) {
	    		
	    	}
	    } 
		
}



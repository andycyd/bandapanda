package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class RecommendationsView extends FragmentActivity{

	
	Vector<Recommendation> recommendations;
	int finished;
	public static Context context;
	Vector<Drawable> drawable;
	Song song;
	JSONArray ids;
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recommendations);
        recommendations = new Vector<Recommendation>();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
        context = this;
        finished = 0;
        LongRunningGetRecommendations lr = new LongRunningGetRecommendations();
        lr.execute();
        while(finished != 1);
        LinearLayout layout = (LinearLayout)findViewById(R.id.recomsLayout);
        for(int i = 0; i < recommendations.size(); ++i){
        	final LinearLayout layoutint = new LinearLayout(context);
        	layoutint.setOrientation(1);
            TextView t = new TextView(this);
            t.setText(recommendations.get(i).getType()+": "+recommendations.get(i).getName());
            if(recommendations.get(i).getType().equals("song")) {
            	t.setTextColor(Color.RED);
            }
            if(recommendations.get(i).getType().equals("playlist")) {
            	t.setTextColor(Color.YELLOW);
            }
            if(recommendations.get(i).getType().equals("album")) {
            	t.setTextColor(Color.CYAN);
            }
            if(recommendations.get(i).getType().equals("artist")) {
            	t.setTextColor(Color.GREEN);
            }
            TextView t1 = new TextView(this);
            t1.setText("By "+recommendations.get(i).getSource_name()+" on "+recommendations.get(i).getDate());
            TextView t2 = new TextView(this);
            t2.setText("________________________");
            t.setTextSize(25);
            t1.setTextSize(15);
            t2.setTextSize(10);
            layoutint.addView(t);
            layoutint.addView(t1);
            layoutint.addView(t2);
            Space s = new Space(this);
            layoutint.addView(s);
            if(recommendations.get(i).getRead() == 0) layoutint.setBackgroundColor(Color.GRAY);
            final int id = i;
            layoutint.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	layoutint.setBackgroundColor(Color.TRANSPARENT);
                	if(recommendations.get(id).getType().equals("song")){
                		crearMenuSong(id);
                	}
                	else if(recommendations.get(id).getType().equals("artist")){
                		crearMenuArtist(id);
                	}
                	else if(recommendations.get(id).getType().equals("album")){
                		crearMenuAlbum(id);
                	}
                	else{
                		crearMenuPlaylist(id);
                		
                	}
    			}
    		});
    		
            layout.addView(layoutint);
        }
        
    }
    
    private void crearMenuSong(final int index){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(recommendations.get(index).getName());
    	CharSequence[] item = {"Play", "Add to a Playlist"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					CurrentPL current = CurrentPL.getInstance();
					current.resetPlaylist();
					finished = 0;
					LongRunningGetOneSong lrgos = new LongRunningGetOneSong(recommendations.get(index).getResource_id());
					lrgos.execute();
					while(finished!=1);
			    	current.addSong(song);
					Intent i = new Intent(context, MusicPlayer.class);
                	startActivity(i);
				}
				if(which == 2){
					AlertDialog.Builder b1 = new AlertDialog.Builder(context);
			    	b1.setTitle(recommendations.get(index).getName());
			    	CharSequence[] item = new CharSequence[User.getInstance().getNumberPlaylists()];
			    	for(int i = 0; i < User.getInstance().getNumberPlaylists(); ++i){
			    		item[i]= User.getInstance().getNamePlaylist(i);
			    	}
			    	b1.setItems(item, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which2) {
							finished = 0;
							LongRunningPostInsertSongPlaylist lrpisp = new LongRunningPostInsertSongPlaylist(User.getInstance().getIdPlaylist(which2), recommendations.get(index).getResource_id());
							lrpisp.execute();
							while(finished != 1);
						}
					});
			    	b1.show();
				}
				
			}
		});
    	b.show();
    }
    
    private void crearMenuAlbum(final int index){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(recommendations.get(index).getName());
    	CharSequence[] item = {"Watch album"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					Intent i = new Intent(context, AlbumView.class);
					Bundle b = new Bundle();
					b.putInt("id", recommendations.get(index).getResource_id());
					i.putExtras(b);
					startActivity(i);
				}
				
			}
		});
    	b.show();
    }
    
    private void crearMenuArtist(final int index){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(recommendations.get(index).getName());
    	CharSequence[] item = {"Watch artist"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
                	Intent i = new Intent(context, ArtistView.class);
                	Bundle b = new Bundle();
                	b.putInt("id", recommendations.get(index).getResource_id());
                	i.putExtras(b);
                	startActivity(i);
				}
				
			}
		});
    	b.show();
    }
    
    private void crearMenuPlaylist(final int index){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(recommendations.get(index).getName());
    	CharSequence[] item = {"Play", "Add to my Playlists"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					finished = 0;
					LongRunningGetOnePlaylist lrgos = new LongRunningGetOnePlaylist(recommendations.get(index).getResource_id());
					lrgos.execute();
					while(finished!=1);

			    	finished = 0;

					drawable = new Vector<Drawable>();
					LongRunningGetImages lrgi = new LongRunningGetImages();
			    	lrgi.execute();
					while(finished != 1);
					int numSongs = CurrentPL.getInstance().getNumSongs();
				    for(int i = 0; i < numSongs; ++i){
				    	CurrentPL.getInstance().getSong(i).setDcover((Drawable) drawable.get(CurrentPL.getInstance().getSong(i).getCoverDrawablePointer()));
				    }
			    	Intent i = new Intent(context, MusicPlayer.class);
                	startActivity(i);
				}
				
				if(which == 1){
					finished = 0;
					LongRunningGetOnePlaylist lrgos = new LongRunningGetOnePlaylist(recommendations.get(index).getResource_id());
					lrgos.execute();
					while(finished!=1);
					ids = new JSONArray();
					for(int i = 0; i < CurrentPL.getInstance().getNumSongs(); ++i){
						ids.put(CurrentPL.getInstance().getSong(i).getID());
					}
					finished = 0;
					LongRunningPostPlaylist lrgos2 = new LongRunningPostPlaylist(recommendations.get(index).getName());
					lrgos2.execute();
					while(finished!=1);


					AlertDialog alert = new AlertDialog.Builder(RecommendationsView.this).create();
					alert.setTitle("Success");
					alert.setMessage("Playlist added to your playlists.");
					alert.setButton("Close",new DialogInterface.OnClickListener() {
						
						public void onClick(final DialogInterface dialog, final int which) {
						}
					});
					alert.show();
					
				}
				
			}
		});
    	b.show();
    }

    
public class LongRunningGetRecommendations extends AsyncTask <Void, Void, String> {

    	
	    ProgressDialog pd;
	    
	    @Override
	    protected void onPreExecute(){
		    pd = new ProgressDialog(context);
	       	pd.setMessage("Searching...");
	       	pd.setCancelable(false);
	       	pd.setIndeterminate(true);
	       	pd.show();
        }
	    
	    
    	@Override
    	protected String doInBackground(Void... params) {
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpContext localContext = new BasicHttpContext();
    		String t = getString(R.string.api_url)+"/users/"+User.getInstance().getId()+"/recommendations.json";
    		HttpGet httpget = new HttpGet(t);
    		httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
    			HttpResponse response = httpClient.execute(httpget, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String res = String.valueOf(stl.getStatusCode());
    			if(res.equals("200") || res.equals("206")){
    				String src = EntityUtils.toString(ent);
    				JSONArray result = new JSONArray(src);
    				for (int i = 0; i < result.length(); ++i) {
    				    JSONObject rec = result.getJSONObject(i);
    				    int sourID = Integer.parseInt(rec.getString("source_id"));
    					String type = rec.getString("type");
    					String source_name = rec.getString("source_username");
    					String name = rec.getString("resource_name");
    					int resID = Integer.parseInt(rec.getString("resource_id"));
    					String date = rec.getString("date");
    					int read = Integer.parseInt(rec.getString("read"));
    		    		Recommendation r = new Recommendation(sourID,resID,type,date,read,source_name,name);
    		    		recommendations.add(r);
    				}
    			}
				finished = 1;
    			return String.valueOf(stl.getStatusCode());
    		} catch (Exception e) {
    			System.out.println("Error"+e.getLocalizedMessage());
    			return e.getLocalizedMessage();
    		}
    	}
    	
    	
		protected void onPostExecute(String results) {
    		if(results.equals("200") || results.equals("206")){
			}
			else{
				crearAlert("Connection error", "No connection with the server");
			}
			pd.dismiss();
    	}
    	
    	@SuppressWarnings("deprecation")
		private void crearAlert(String t, String s){
    		AlertDialog alert = new AlertDialog.Builder(RecommendationsView.this).create();
			alert.setTitle(t);
			alert.setMessage(s);
			alert.setButton("Close",new DialogInterface.OnClickListener() {
				
				public void onClick(final DialogInterface dialog, final int which) {
				}
			});
			alert.show();
    	}
    }

	public class LongRunningGetOneSong extends AsyncTask <Void, Void, String> {
	
		ProgressDialog pd;
		int id;
	
		
		public LongRunningGetOneSong(int i){
			id = i;
		}
		
	    public Object fetch(String address) throws MalformedURLException, IOException{
			URL url = new URL(address);
			Object content = url.getContent();
			return content;
	    	
	    }
	    
	
	    private Drawable ImageOperations(Context ctx, String url) {
	        try {
	            InputStream is = (InputStream) this.fetch(url);
	            Drawable d = Drawable.createFromStream(is, "src");
	            return d;
	        } catch (MalformedURLException e) {
	            return null;
	        } catch (IOException e) {
	            return null;
	        }
	    }
	    
	   @Override
	   protected void onPreExecute(){
		    pd = new ProgressDialog(context);
	      	pd.setMessage("Searching...");
	      	pd.setCancelable(false);
	      	pd.setIndeterminate(true);
	      	pd.show();
	   }
	   
	   @Override 
	   protected void onPostExecute(final String s){
		   pd.dismiss();
	   }
		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext localContext = new BasicHttpContext();
			String t = getString(R.string.api_url)+"/songs/"+id+".json?";
			HttpGet httpget = new HttpGet(t);
			httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
			try {
				HttpResponse response = httpClient.execute(httpget, localContext);
				StatusLine stl = response.getStatusLine();
				HttpEntity ent = response.getEntity();
				String res = String.valueOf(stl.getStatusCode());
				if(res.equals("200")){
					String src = EntityUtils.toString(ent);
					JSONObject rec = new JSONObject(src);
					int ID = Integer.parseInt(rec.getString("song_id"));
					String title = rec.getString("song_title");
					int IDalbum = Integer.parseInt(rec.getString("album_id"));
					String album = rec.getString("album_title");
					int IDgroup = Integer.parseInt(rec.getString("artist_id"));
					String group = rec.getString("artist_name");
					String url = getString(R.string.resources_url)+rec.getString("audio_url");
					String cover = getString(R.string.resources_url)+rec.getString("cover_url");
					Song s = new Song(ID, title, IDalbum, album, IDgroup, group, cover, -1,  url);
					song = new Song(ID, title, IDalbum, album, IDgroup, group, cover, -1,  url);
					song.setDcover(ImageOperations(context,song.getCover()));
				}
				finished = 1;
			} catch (Exception ex) {
				return null;
			}
	       
			return null;
		}
	}
	
	
	public class LongRunningPostInsertSongPlaylist extends AsyncTask <Void, Void, String> {
	
		
	    int playlist;
	    int song;
	    
	    public LongRunningPostInsertSongPlaylist(int pl, int s){
	    	playlist = pl;
	    	song = s;
	    }
	    
	    @Override
	    protected void onPreExecute(){
	
	    }
	    
	    
		@Override
		protected String doInBackground(Void... params) {
			HttpClient httpClient = new DefaultHttpClient();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("song_id",Integer.toString(song)));
			HttpContext localContext = new BasicHttpContext();
			HttpPost httppost = new HttpPost(getString(R.string.api_url)+"/playlists/"+Integer.toString(playlist)+".json");
			httppost.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
			try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
			try {
				HttpResponse response = httpClient.execute(httppost, localContext);
				StatusLine stl = response.getStatusLine();
				finished = 1;
				return String.valueOf(stl.getStatusCode());
			} catch (Exception e) {
				System.out.println("Error"+e.getLocalizedMessage());
				return e.getLocalizedMessage();
			}
		}
		
		@SuppressWarnings("deprecation")
		protected void onPostExecute(String results) {
			if(results.equals("200")){
				
			}
			else{
	
				AlertDialog alert = new AlertDialog.Builder(context).create();
				alert.setTitle("Connection Error");
				alert.setMessage("No connection with the server");
				alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
				alert.show();
			}
		}
	} 
	
public class LongRunningGetOnePlaylist extends AsyncTask <Void, Void, String> {

    	
	    ProgressDialog pd;
	    int idPlaylist;
	    
	    public LongRunningGetOnePlaylist(int a){
	    	idPlaylist = a;
    	}
	    
	    @Override
	    protected void onPreExecute(){

		    pd = new ProgressDialog(context);

	       	pd.setMessage("Loading playlist...");
	       	pd.setCancelable(false);
	       	pd.setIndeterminate(true);
	       	pd.show();
        }
	    
	    
    	@Override
    	protected String doInBackground(Void... params) {
    		CurrentPL.getInstance().resetPlaylist();
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpContext localContext = new BasicHttpContext();
    		HttpGet httpget = new HttpGet(getString(R.string.api_url)+"/playlists/"+idPlaylist+".json?lim=200");
    		httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
    			HttpResponse response = httpClient.execute(httpget, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String src = EntityUtils.toString(ent);
    			JSONObject result = new JSONObject(src);
    			JSONArray playl = result.getJSONArray("songs");
    			for (int i = 0; i < playl.length(); ++i) {
    				JSONObject rec = playl.getJSONObject(i);
				    int ID = Integer.parseInt(rec.getString("song_id"));
					String title = rec.getString("song_title");
					int IDalbum = Integer.parseInt(rec.getString("album_id"));
					String album = rec.getString("album_title");
					int IDgroup = Integer.parseInt(rec.getString("artist_id"));
					String group = rec.getString("artist_name");
					String url = getString(R.string.resources_url)+rec.getString("audio_url");
					String cover = getString(R.string.resources_url)+rec.getString("cover_url");
					Song s = new Song(ID, title, IDalbum, album, IDgroup, group, cover, -1,  url);
					CurrentPL.getInstance().addSong(s);
    			}
				finished = 1;
    			return String.valueOf(stl.getStatusCode());
    		} catch (Exception e) {
    			System.out.println("Error"+e.getLocalizedMessage());
    			return e.getLocalizedMessage();
    		}
    	}
    	
    	@SuppressWarnings("deprecation")
		protected void onPostExecute(String results) {
    		if(results.equals("200")){
				pd.dismiss();
			}
			else if(results.equals("401")){

    			AlertDialog alert = new AlertDialog.Builder(RecommendationsView.this).create();
    			alert.setTitle("Login error");
    			alert.setMessage("User/Password incorrect.");
    			alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
    			pd.dismiss();
    			alert.show();
			}
			else{

    			AlertDialog alert = new AlertDialog.Builder(RecommendationsView.this).create();
    			alert.setTitle("Connection error");
    			alert.setMessage("No connection with the server");
    			alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
    			pd.dismiss();
    			alert.show();
			}
    	}
    }

public class LongRunningPostPlaylist extends AsyncTask <Void, Void, String> {

	
    ProgressDialog pd;
    String playlist;
    
    public LongRunningPostPlaylist(String t){
    	playlist = t;
    	
    }
    
    @Override
    protected void onPreExecute(){

	    pd = new ProgressDialog(context);

       	pd.setMessage("Logging in...");
       	pd.setCancelable(false);
       	pd.setIndeterminate(true);
       	pd.show();
    }
    
    
	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		nameValuePairs.add(new BasicNameValuePair("name",playlist));
		nameValuePairs.add(new BasicNameValuePair("songs",ids.toString()));
		HttpContext localContext = new BasicHttpContext();
		HttpPost httppost = new HttpPost(getString(R.string.api_url)+"/users/"+User.getInstance().getId()+"/playlists.json");
		httppost.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		try {
			HttpResponse response = httpClient.execute(httppost, localContext);
			StatusLine stl = response.getStatusLine();
			HttpEntity ent = response.getEntity();
			String res = String.valueOf(stl.getStatusCode());
			if(res.equals("201")  ){
				String src = EntityUtils.toString(ent);
				JSONObject result = new JSONObject(src);
    			Playlist p = new Playlist(playlist, Integer.parseInt(result.getString("id")));
    	     	User.getInstance().addPlaylist(p);
    	     	
			}
			finished = 1;
			return String.valueOf(stl.getStatusCode());
		} catch (Exception e) {
			System.out.println("Error"+e.getLocalizedMessage());
			return e.getLocalizedMessage();
		}
	}
	
	@SuppressWarnings("deprecation")
	protected void onPostExecute(String results) {
		if(results.equals("201") || results.equals("500")){
			
			pd.dismiss();
		}
		else{

			AlertDialog alert = new AlertDialog.Builder(RecommendationsView.this).create();
			alert.setTitle("Connection Error");
			alert.setMessage("No connection with the server");
			alert.setButton("Close",new DialogInterface.OnClickListener() {
				
				public void onClick(final DialogInterface dialog, final int which) {
				}
			});
			pd.dismiss();
			alert.show();
		}
	}
}    

public class LongRunningGetImages extends AsyncTask <Void, Void, String> {

	ProgressDialog pd;

	
	public LongRunningGetImages(){
	}
	
    public Object fetch(String address) throws MalformedURLException, IOException{
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
    	
    }
    

    private Drawable ImageOperations(Context ctx, String url) {
        try {
            InputStream is = (InputStream) this.fetch(url);
            Drawable d = Drawable.createFromStream(is, "src");
            return d;
        } catch (MalformedURLException e) {
            return null;
        } catch (IOException e) {
            return null;
        }
    }
    
   @Override
   protected void onPreExecute(){
	    pd = new ProgressDialog(context);
      	pd.setMessage("Loading...");
      	pd.setCancelable(false);
      	pd.setIndeterminate(true);
      	pd.show();
   }
   
   @Override 
   protected void onPostExecute(final String s){
	   pd.dismiss();
   }
	@Override
	protected String doInBackground(Void... params) {
		try {
			String currentUrl;
        	for(int i = 0; i < CurrentPL.getInstance().getNumSongs(); ++i){

        		currentUrl = CurrentPL.getInstance().getSong(i).getCover();
					drawable.add(ImageOperations(context,currentUrl));
					CurrentPL.getInstance().getSong(i).setCoverDrawablePointer(drawable.size()-1);
        	}
        	finished = 1;
		} catch (Exception ex) {
			return null;
		}
       
		return null;
	}

}

	
	
	
}

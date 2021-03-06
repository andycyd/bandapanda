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
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class MenuPlaylist extends FragmentActivity {
   public static Context context;

   Vector<Playlist> vectorPlaylists;
   int finished;
   Vector<String> urlDrawables;

   Vector<OtherUsers> resSearchUsers;
   Vector<Drawable> drawable;
   int numSongs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        //PusherConnection pc = new PusherConnection();

        context = this;
        if(!User.getInstance().isConected()){
	        User.getInstance().setCt(context);
	        String ns = Context.NOTIFICATION_SERVICE;
	        User.getInstance().setNm((NotificationManager) getSystemService(ns));
	        User.getInstance().connectToPusher();
        }
        //pc.execute();
        vectorPlaylists = User.getInstance().getPlaylists();
        if(vectorPlaylists.size() == 0){
        	LongRunningGetPlaylists lrgi = new LongRunningGetPlaylists();
        	finished = 0;
        	lrgi.execute();
        	while(finished != 1); 
        }

        refreshPlaylists();
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_playlists, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_new:
        	createNewPlaylist();
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    void createNewPlaylist(){
    	final EditText input = new EditText(context);
    	new AlertDialog.Builder(this)
    	    .setTitle("Create Playlist")
    	    .setMessage("Select the name for the playlist")
    	    .setView(input)
    	    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	            createPlaylist(input.getText().toString());
    	         	refreshPlaylists();
    	         }
    	    })
    	    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	                // Do nothing.
    	         }
    	    }).show();
    }
    
    public void createPlaylist(String name){
    	LongRunningPostPlaylist lp = new LongRunningPostPlaylist(name);
    	finished = 0;
    	lp.execute();
    	while(finished != 1);
    	refreshPlaylists();
    }
  
    
	private void refreshPlaylists(){
        String[] playlists = new String[vectorPlaylists.size()];        
        ListView list = (ListView) findViewById(R.id.lisOfPlaylists);
        for(int i = 0; i < vectorPlaylists.size(); ++i){
        	playlists[i] = vectorPlaylists.get(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		  android.R.layout.simple_list_item_1, android.R.id.text1, playlists);

        list.setAdapter(adapter);
        list.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
                     int position, long id) {
        		 crearMenu(position);     		 
             }
         });
    }
    
    
    
    public void searchSongs(View view){
    	Intent i = new Intent(this, Search.class );
    	startActivity(i);       
    }
    
    
    private void crearMenu(final int position){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(User.getInstance().getPlaylists().get(position).getName());
    	CharSequence[] item = {"Play","Delete","Watch songs","Share"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			@SuppressWarnings("deprecation")
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					CurrentPL current = CurrentPL.getInstance();
					current.resetPlaylist();
					urlDrawables = new Vector<String>();
					drawable = new Vector<Drawable>();
					numSongs = User.getInstance().getPlaylists().get(position).getNumSongs();
					if(numSongs == 0){
					    LongRunningGetOnePlaylist lrgop = new LongRunningGetOnePlaylist(position);
					    finished = 0;
					    lrgop.execute();
						while(finished != 1);
					}
					numSongs = User.getInstance().getPlaylists().get(position).getNumSongs();
				    if(numSongs == 0){
				    	AlertDialog alert = new AlertDialog.Builder(context).create();
						alert.setTitle("Can't play");
						alert.setMessage("No songs for this playlist");
						alert.setButton("Close",new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, final int which) {
							}
						});
						alert.show();
				    	return;
				    }
				    LongRunningGetImages lrgi = new LongRunningGetImages(position);
			    	finished = 0;
			    	lrgi.execute();
					while(finished != 1);
					numSongs = User.getInstance().getPlaylists().get(position).getNumSongs();
				    for(int i = 0; i < numSongs; ++i){
				    	User.getInstance().getPlaylists().get(position).getSong(i).setDcover((Drawable) drawable.get(User.getInstance().getPlaylists().get(position).getSong(i).getCoverDrawablePointer()));
				    	current.addSong(User.getInstance().getPlaylists().get(position).getSong(i));
				    }
					Intent i = new Intent(context, MusicPlayer.class);
	             	startActivity(i);
				}
				if(which == 1){
					LongRunningDeletePlaylist ld = new LongRunningDeletePlaylist(User.getInstance().getPlaylists().get(position).getID());
					finished = 0;
					ld.execute();
					while(finished != 1);

		        	vectorPlaylists.remove(position);
			    	refreshPlaylists();
				}
				if(which == 2){
					urlDrawables = new Vector<String>();
					drawable = new Vector<Drawable>();
					numSongs = User.getInstance().getPlaylists().get(position).getNumSongs();
					LongRunningGetOnePlaylist lrgop = new LongRunningGetOnePlaylist(position);
					finished = 0;
					lrgop.execute();
					while(finished != 1);
					numSongs = User.getInstance().getPlaylists().get(position).getNumSongs();
				    if(numSongs == 0){
				    	AlertDialog alert = new AlertDialog.Builder(context).create();
						alert.setTitle("Can't play");
						alert.setMessage("No songs for this playlist");
						alert.setButton("Close",new DialogInterface.OnClickListener() {
							public void onClick(final DialogInterface dialog, final int which) {
							}
						});
						alert.show();
				    	return;
				    }
				    LongRunningGetImages lrgi = new LongRunningGetImages(position);
			    	finished = 0;
			    	lrgi.execute();
					while(finished != 1);
				    for(int i = 0; i < numSongs; ++i){
				    	User.getInstance().getPlaylists().get(position).getSong(i).setDcover((Drawable) drawable.get(User.getInstance().getPlaylists().get(position).getSong(i).getCoverDrawablePointer()));
				    }
					Intent i = new Intent(context, PlaylistView.class);
					Bundle b = new Bundle();
                	b.putInt("number", position);
                	i.putExtras(b);
	             	startActivity(i);
				}
				if(which == 3){
					recommendPlaylist(User.getInstance().getPlaylists().get(position).getID(), User.getInstance().getPlaylists().get(position).getName());
					
				}
			}
		});
    	b.show();
    }
    
    private void recommendPlaylist(final int id, final String name){
    	final EditText input = new EditText(context);
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle("Recommend "+name);
    	b.setMessage("Search user to recommend");
    	b.setView(input);
    	b.setPositiveButton("Search", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	        	AlertDialog.Builder b1 = new AlertDialog.Builder(context);
 			    	b1.setTitle("Recommend "+name);
 			    	resSearchUsers = new Vector<OtherUsers>();
 			    	finished = 0;
 			    	LongRunningGetSearchUsers lrgsu = new LongRunningGetSearchUsers(input.getText().toString());
 			    	lrgsu.execute();
					while(finished != 1);
 			    	CharSequence[] item = new CharSequence[resSearchUsers.size()];
 			    	for(int i = 0; i < resSearchUsers.size(); ++i){
 			    		item[i]= resSearchUsers.get(i).getName();
 			    	}
 			    	b1.setItems(item, new DialogInterface.OnClickListener() {
 						public void onClick(DialogInterface dialog, int which) {
 							finished = 0;
 							LongRunningPostRecommend lrpr = new LongRunningPostRecommend(resSearchUsers.get(which).getID(), id, "playlist");
 							lrpr.execute();
 							while(finished != 1);
 						}
 					});
 			    	b1.show();
 				}
    	    });
    	b.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
    	         public void onClick(DialogInterface dialog, int whichButton) {
    	         }
    	    });
    	b.show();
    }
    
    
    public void goRecommendations(View view){
    	Intent i = new Intent(this, RecommendationsView.class );
    	startActivity(i);  
    }
    
    
 
    public class LongRunningGetPlaylists extends AsyncTask <Void, Void, String> {

    	
	    ProgressDialog pd;
	    
	    @Override
	    protected void onPreExecute(){

		    pd = new ProgressDialog(context);

	       	pd.setMessage("Searching Playlists...");
	       	pd.setCancelable(false);
	       	pd.setIndeterminate(true);
	       	pd.show();
        }
	    
	    
    	@Override
    	protected String doInBackground(Void... params) {
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpContext localContext = new BasicHttpContext();
    		HttpGet httpget = new HttpGet(getString(R.string.api_url)+"/users/"+User.getInstance().getId()+"/playlists.json?lim=100");
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
    				    int ID = Integer.parseInt(rec.getString("playlist_id"));
    					String name = rec.getString("playlist_name");
    					Playlist pl = new Playlist(name,ID);
    					vectorPlaylists.add(pl);
    				}
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
    		if(results.equals("200") || results.equals("206")){
				pd.dismiss();
			}
			else{

    			AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
    			alert.setTitle("Error");
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

    public class LongRunningGetOnePlaylist extends AsyncTask <Void, Void, String> {

    	
	    ProgressDialog pd;
	    int idPlaylist;
	    int position;
	    
	    public LongRunningGetOnePlaylist(int a){
	    	position = a;
	    	idPlaylist = User.getInstance().getPlaylists().get(position).getID();
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
    		User.getInstance().getPlaylists().get(position).resetPlaylist();
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
					User.getInstance().getPlaylists().get(position).addSong(s);
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

    			AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
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

    			AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
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
    
    public class LongRunningGetImages extends AsyncTask <Void, Void, String> {

    	ProgressDialog pd;
    	int position;

    	
    	public LongRunningGetImages(int a){
    		position = a;
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
	        	for(int i = 0; i < numSongs; ++i){
	        		currentUrl = User.getInstance().getPlaylists().get(position).getSong(i).getCover();
					if(!urlDrawables.contains(currentUrl)){
						urlDrawables.add(currentUrl);
						drawable.add(ImageOperations(context,currentUrl));
						Song aux = User.getInstance().getPlaylists().get(position).getSong(i);
						User.getInstance().getPlaylists().get(position).removeSong(i);
						aux.setCoverDrawablePointer(drawable.size()-1);
						User.getInstance().getPlaylists().get(position).addSongAt(i, aux);
					}
					else{
						int trob = 0;
						int a = 0;
						while(trob == 0){
							if(urlDrawables.get(a).equals(currentUrl)){
								Song aux = User.getInstance().getPlaylists().get(position).getSong(i);
								User.getInstance().getPlaylists().get(position).removeSong(i);
								aux.setCoverDrawablePointer(drawable.size()-1);
								User.getInstance().getPlaylists().get(position).addSongAt(i, aux);
								trob = 1;
							}
							else a++;
						}
					}
	        	}
	        	finished = 1;
			} catch (Exception ex) {
				return null;
			}
           
			return null;
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

			//JSONArray array = new JSONArray();
    		//nameValuePairs.add(new BasicNameValuePair("songs",array.toString()));
    		HttpContext localContext = new BasicHttpContext();
    		HttpPost httppost = new HttpPost(getString(R.string.api_url)+"/users/"+User.getInstance().getId()+"/playlists.json");
    		httppost.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs, HTTP.UTF_8));
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
    		try {
    			HttpResponse response = httpClient.execute(httppost, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String res = String.valueOf(stl.getStatusCode());
    			
    			if(res.equals("201")){
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

    			AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
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
    


public class LongRunningDeletePlaylist extends AsyncTask <Void, Void, String> {

	
    int playlist;
    
    public LongRunningDeletePlaylist(int p){
    	playlist = p;
    }
    
    @Override
    protected void onPreExecute(){

    }
    
    
	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		
		HttpDelete httpdelete = new HttpDelete(getString(R.string.api_url)+"/playlists/"+Integer.toString(playlist)+".json");
		httpdelete.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
		try {
			HttpResponse response = httpClient.execute(httpdelete, localContext);
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
			AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
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

public class LongRunningGetSearchUsers extends AsyncTask <Void, Void, String> {
	ProgressDialog pd;
	String search;
    
	public LongRunningGetSearchUsers(String s){
		search = s;
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
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		String t = getString(R.string.api_url)+"/users/search.json?q="+search+"&order=ASC&lim=400";
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
				    int id = Integer.parseInt(rec.getString("user_id"));
					String name = rec.getString("user_username");
					OtherUsers u = new OtherUsers(name, id);
		    		resSearchUsers.add(u);
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
		else if(results.equals("401")){
			crearAlert("Error", "Unautorized");
		}
		else{
			crearAlert("Connection error", "No connection with the server");
		}
		pd.dismiss();
	}
	
	@SuppressWarnings("deprecation")
	private void crearAlert(String t, String s){
		AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
		alert.setTitle(t);
		alert.setMessage(s);
		alert.setButton("Close",new DialogInterface.OnClickListener() {
			
			public void onClick(final DialogInterface dialog, final int which) {
			}
		});
		alert.show();
	}
}


public class LongRunningPostRecommend extends AsyncTask <Void, Void, String> {

	
    int user;
    int resource;
    
    public LongRunningPostRecommend(int idu, int idr, String tp){
    	user = idu;
    	resource = idr;
    }
    
    @Override
    protected void onPreExecute(){

    }
    
    
	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httppost = new HttpPost(getString(R.string.api_url)+"/users/"+user+"/recommendations.json?type=playlist&resource_id="+resource);
		httppost.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
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
		if(results.equals("201")){
			
		}
		else{

			AlertDialog alert = new AlertDialog.Builder(MenuPlaylist.this).create();
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


    
}

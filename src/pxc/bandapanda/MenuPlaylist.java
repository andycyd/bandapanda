package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
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

import pxc.bandapanda.Search.LongRunningGetImages;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MenuPlaylist extends FragmentActivity {
   public static Context context;

   Vector<Playlist> vectorPlaylists;
   int finished;
   Vector<String> urlDrawables;
   Vector<Drawable> drawable;
   int numSongs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        context = this;
        for(int i = 0; i < 20; ++i){
        	Playlist p = new Playlist("Hola "+i,i);
            User.getInstance().addPlaylist(p);
        }
        vectorPlaylists = User.getInstance().getPlaylists();
        /* 
         * 
         * DESCOMENTAR ESTO PARA LA BUSQUEDA DE LAS PLAYLISTS!!!
         * 
         * 
         * 
        LongRunningGetImages lrgi = new LongRunningGetPlaylists();
    	finished = 0;
    	lrgi.execute();
    	System.out.println("Acaba covers");
		while(finished != 1); 
		System.out.println("ponemos en su sitio");
		
		*/
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
    	// FALTA LLAMAR A CREAR PLAYLIST DE LA API
    	Playlist p = new Playlist(name, 2);
     	User.getInstance().addPlaylist(p);
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

        	 @SuppressWarnings("deprecation")
			public void onItemClick(AdapterView<?> parent, View view,
                     int position, long id) {
        		CurrentPL current = CurrentPL.getInstance();
				current.resetPlaylist();
				urlDrawables = new Vector<String>();
				drawable = new Vector<Drawable>();
				// DESCOMENTAR CUANDO TENGAMOS LA LLAMADA GET PLAYLIST
			    /*LongRunningGetOnePlaylist lrgop = new LongRunningGetOnePlaylist(position);
			    finished = 0;
			    lrgop.execute();
				while(finished != 1);*/
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
			    	current.addSong(User.getInstance().getPlaylists().get(position).getSong(i));
			    }
				Intent i = new Intent(context, MusicPlayer.class);
             	startActivity(i);
        		 
                 
             }
         });
    }
    
    
    
    public void searchSongs(View view){
    	Intent i = new Intent(this, Search.class );
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
    		HttpGet httpget = new HttpGet("http://polar-thicket-1771.herokuapp.com/users/"+User.getInstance().getId()+"playlists/");
    		httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
    			HttpResponse response = httpClient.execute(httpget, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String res = String.valueOf(stl.getStatusCode());
    			if(res.equals("200")){
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
    			System.out.println(String.valueOf(stl.getStatusCode()));
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
    			alert.setTitle("Login error");
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
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpContext localContext = new BasicHttpContext();
    		HttpGet httpget = new HttpGet("http://polar-thicket-1771.herokuapp.com/playlists/"+idPlaylist);
    		httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
    			HttpResponse response = httpClient.execute(httpget, localContext);
    			StatusLine stl = response.getStatusLine();
    			// OJO! quizas falta comprovar que response status sea 200
    			/*
    			 * 
    			 * 
    			 * FALTA TRATAR QUE NOS DEVUELVA UNA PARTE DE LAS CANCIONES, AHORA SOLO DEVUELVE UNA PARTE DE LAS CANCIONES
    			 * 
    			 * 
    			 */
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
    
}

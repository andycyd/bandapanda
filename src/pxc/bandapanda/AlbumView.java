package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import pxc.bandapanda.MenuPlaylist.LongRunningGetImages;

import android.app.AlertDialog;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AlbumView extends FragmentActivity {
	int IDAlbum;
	Album albumInf;
	Context context;
	int finished;
	Drawable cover;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		IDAlbum = getIntent().getExtras().getInt("id");
		albumInf = new Album();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_album);
        context = this;
        LongRunningGetAlbum lrga = new LongRunningGetAlbum();
        finished = 0;
        lrga.execute();
        while(finished!=1);
        ImageView iv = (ImageView) findViewById(R.id.albumCoverImage);
        iv.setImageDrawable(cover);
        TextView name = new TextView(context);
        TextView artist = new TextView(context);
        TextView genre = new TextView(context);
        TextView year = new TextView(context);
        name.setText(albumInf.getName());
        artist.setText(albumInf.getNameArt());
        year.setText(String.valueOf(albumInf.getYear()));
        genre.setText(albumInf.getGenre());
        LinearLayout l = (LinearLayout) findViewById(R.id.layoutInfo);
        l.addView(name);
        l.addView(artist);
        l.addView(year);
        l.addView(genre);
        ListView lv = (ListView) findViewById(R.id.songsAlbumList);
        String[] songs = new String[albumInf.getNumSongs()];        
        for(int i = 0; i < albumInf.getNumSongs(); ++i){
        	songs[i] = String.valueOf(albumInf.getSong(i).getTrack()) + " - " + albumInf.getSong(i).getTitle();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		  android.R.layout.simple_list_item_1, android.R.id.text1, songs);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

       	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       		 	crearMenu(albumInf.getSong(position));
            }
        });
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_play_current_playlist_album:
        	Intent in = new Intent(context, MusicPlayer.class);
        	startActivity(in);
            return true;
        case R.id.menu_play_all_songs_album:
            CurrentPL current = CurrentPL.getInstance();
			current.resetPlaylist();
			if(albumInf.getNumSongs() == 0){
				AlertDialog alert = new AlertDialog.Builder(context).create();
				alert.setTitle("Can't play");
				alert.setMessage("No songs to be played");
				alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
				alert.show();
				return true;
			}
			for(int i = 0; i <albumInf.getNumSongs(); ++i){
				albumInf.getSong(i).setDcover(cover);
		    	current.addSong(albumInf.getSong(i));
			}
			Intent in2 = new Intent(context, MusicPlayer.class);
        	startActivity(in2);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	
	 private void crearMenu(final Song s){
	    	AlertDialog.Builder b = new AlertDialog.Builder(context);
	    	b.setTitle(s.getTitle());
	    	CharSequence[] item = {"Play","Add to playing now", "Add to favorites", "Add to a Playlist", "Share"};
	    	b.setItems(item, new DialogInterface.OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if(which == 0){
						CurrentPL current = CurrentPL.getInstance();
						current.resetPlaylist();
				    	s.setDcover(cover);
				    	current.addSong(s);
						Intent i = new Intent(context, MusicPlayer.class);
	                	startActivity(i);
					}
					if(which == 1){
						CurrentPL current = CurrentPL.getInstance();
				    	s.setDcover(cover);
				    	current.addSong(s);
					}
					if(which == 2){
						
					}
					if(which == 3){
						AlertDialog.Builder b1 = new AlertDialog.Builder(context);
				    	b1.setTitle(s.getTitle());
				    	CharSequence[] item = new CharSequence[User.getInstance().getNumberPlaylists()];
				    	for(int i = 0; i < User.getInstance().getNumberPlaylists(); ++i){
				    		item[i]= User.getInstance().getNamePlaylist(i);
				    	}
				    	b1.setItems(item, new DialogInterface.OnClickListener() {
							
							public void onClick(DialogInterface dialog, int which) {
								// HAY QUE MODIFICAR PARA AÑADIRLA EN LA API, NO EN LOCAL
								User.getInstance().addSongToPlaylist(which, s);
							}
						});
				    	b1.show();
					}
					if(which == 4){
						
					}
					
				}
			});
	    	b.show();
	    }
	
	
	

    public class LongRunningGetAlbum extends AsyncTask <Void, Void, String> {

    	
	    ProgressDialog pd;
	    
	    @Override
	    protected void onPreExecute(){

		    pd = new ProgressDialog(context);

	       	pd.setMessage("Searching...");
	       	pd.setCancelable(false);
	       	pd.setIndeterminate(true);
	       	pd.show();
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
    	protected String doInBackground(Void... params) {
    		HttpClient httpClient = new DefaultHttpClient();
    		HttpContext localContext = new BasicHttpContext();
    		String t = getString(R.string.api_url)+"/albums/"+IDAlbum+".json";
    		HttpGet httpget = new HttpGet(t);
    		httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
    			HttpResponse response = httpClient.execute(httpget, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String res = String.valueOf(stl.getStatusCode());
    			if(res.equals("200")){
    				String src = EntityUtils.toString(ent);
    				//System.out.println(src);
    				// {album_title, cover_url, artist_id, artist_name, album_genre, album_year, album_songs: [song_id, song_track, song_title, audio_url]* }
    				JSONObject result = new JSONObject(src);
    				albumInf.setName(result.getString("album_title"));
    				albumInf.setCover(getString(R.string.resources_url)+result.getString("cover_url"));
    				cover = ImageOperations(context, albumInf.getCover());
    				albumInf.setNameArt(result.getString("artist_name"));
    				albumInf.setIDart(Integer.parseInt(result.getString("artist_id")));
    				albumInf.setGenre(result.getString("album_genre"));
    				System.out.println("album year: "+result.getString("album_year"));
    				albumInf.setYear(Integer.parseInt(result.getString("album_year")));
    				JSONArray songs = result.getJSONArray("album_songs");
    				for (int i = 0; i < songs.length(); ++i) {
    				    JSONObject rec = songs.getJSONObject(i);
    				    int ID = Integer.parseInt(rec.getString("song_id"));
    				    System.out.println("Prueba 4");
    					String title = rec.getString("song_title");
    					int track = Integer.parseInt(rec.getString("song_track"));
    					String url = getString(R.string.resources_url)+rec.getString("audio_url");
    					Song s = new Song(ID, title, albumInf.getID(), albumInf.getName(), albumInf.getIDart(), albumInf.getNameArt(), albumInf.getCover(), -1,  url);
    					System.out.println("Prueba 3");
    					s.setTrack(track);
    					albumInf.addSong(s);
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
			System.out.println(results);
    		if(results.equals("200")){
			}
			else{
				crearAlert("Connection error", "No connection with the server");
			}
			pd.dismiss();
    	}
    	
    	@SuppressWarnings("deprecation")
		private void crearAlert(String t, String s){
    		AlertDialog alert = new AlertDialog.Builder(AlbumView.this).create();
			alert.setTitle(t);
			alert.setMessage(s);
			alert.setButton("Close",new DialogInterface.OnClickListener() {
				
				public void onClick(final DialogInterface dialog, final int which) {
				}
			});
			alert.show();
    	}
    }
	
}

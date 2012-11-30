package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ArtistView extends FragmentActivity {
	int IDArtist;
	Artist artistInf;
	Context context;
	int finished;
	Drawable image;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		IDArtist = getIntent().getExtras().getInt("id");
		artistInf = new Artist();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_artist);
        context = this;
        LongRunningGetArtist lrga = new LongRunningGetArtist();
        finished = 0;
        lrga.execute();
        while(finished!=1);
        ImageView iv = (ImageView) findViewById(R.id.artistImage);
        iv.setImageDrawable(image);
        TextView name = new TextView(context);
        TextView info = new TextView(context);
        TextView year = new TextView(context);
        name.setText(artistInf.getName());
        year.setText(String.valueOf(artistInf.getYear()));
        info.setText(artistInf.getInfo());
        LinearLayout l = (LinearLayout) findViewById(R.id.layoutInfoArtist);
        l.addView(name);
        l.addView(year);
        l.addView(info);
        ListView lv = (ListView) findViewById(R.id.albumsArtistList);
        String[] albums = new String[artistInf.getNumAlbums()];        
        for(int i = 0; i < artistInf.getNumAlbums(); ++i){
        	albums[i] = String.valueOf(artistInf.getAlbum(i).getYear())+" - "+artistInf.getAlbum(i).getName();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
        		  android.R.layout.simple_list_item_1, android.R.id.text1, albums);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new OnItemClickListener() {

       	 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
       		Intent i = new Intent(context, AlbumView.class);
        	Bundle b = new Bundle();
        	b.putInt("id", artistInf.getAlbum(position).getID());
        	i.putExtras(b);
        	startActivity(i);
       		 	//crearMenu(artistInf.getAlbum(position));
            }
        });
	}
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_artist, menu);
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_play_current_playlist_artist:
        	Intent in = new Intent(context, MusicPlayer.class);
        	startActivity(in);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
	
	/*
	 private void crearMenu(final Album s){
	    	AlertDialog.Builder b = new AlertDialog.Builder(context);
	    	b.setTitle(s.getName());
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
	*/
	
	

    public class LongRunningGetArtist extends AsyncTask <Void, Void, String> {

    	
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
    		String t = "http://polar-thicket-1771.herokuapp.com/artists/"+IDArtist+".json";
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
    				//{artist_name, artist_image, artist_info, artist_year, artist_albums: [album_id, album_title, album_cover]* }
    				JSONObject result = new JSONObject(src);

					System.out.println("Hola sonia 3");
    				artistInf.setName(result.getString("artist_name"));
    				artistInf.setCover(getString(R.string.resources_url)+result.getString("artist_image"));
    				artistInf.setInfo(result.getString("artist_info"));
    				artistInf.setYear(Integer.parseInt(result.getString("artist_year")));
    				image = ImageOperations(context, artistInf.getCover());
    				JSONArray songs = result.getJSONArray("artist_albums");
    				for (int i = 0; i < songs.length(); ++i) {
    					System.out.println("Hola sonia");
    				    JSONObject rec = songs.getJSONObject(i);
    				    int ID = Integer.parseInt(rec.getString("album_id"));
    					String name = rec.getString("album_title");
    					String url = getString(R.string.resources_url)+rec.getString("album_cover");
    					Album s = new Album(ID, name, artistInf.getID(), artistInf.getName(), url);
    					System.out.println("Hola sonia 2");
    					artistInf.addAlbum(s);
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
    		AlertDialog alert = new AlertDialog.Builder(ArtistView.this).create();
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

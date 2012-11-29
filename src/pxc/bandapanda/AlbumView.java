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
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AlbumView extends FragmentActivity {
	int IDAlbum;
	Album albumInf;
	Context context;
	int finished;
	Drawable cover;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		IDAlbum = 0;
		albumInf = new Album();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_album);
        context = this;
        LongRunningGetAlbum lrga = new LongRunningGetAlbum();
        finished = 0;
        lrga.execute();
        while(finished!=1);
        ImageView i = (ImageView) findViewById(R.id.albumCoverImage);
        i.setImageDrawable(cover);
        TextView name = new TextView(context);
        TextView artist = new TextView(context);
        TextView genre = new TextView(context);
        TextView year = new TextView(context);
        name.setText(albumInf.getName());
        artist.setText(albumInf.getNameArt());
        year.setText(albumInf.getYear());
        genre.setText(albumInf.getGenre());
        
        
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_album, menu);
        return true;
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
    		String t = "http://polar-thicket-1771.herokuapp.com/albums/"+IDAlbum;
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
    				albumInf.setName(result.getString("artist_name"));
    				albumInf.setIDart(Integer.parseInt(result.getString("artist_id")));
    				albumInf.setGenre(result.getString("album_genre"));
    				albumInf.setYear(Integer.parseInt(result.getString("album_year")));
    				JSONArray songs = result.getJSONArray("album_songs");
    				for (int i = 0; i < result.length(); ++i) {
    				    JSONObject rec = songs.getJSONObject(i);
    				    int ID = Integer.parseInt(rec.getString("song_id"));
    					String title = rec.getString("song_title");
    					int track = Integer.parseInt(rec.getString("song_track"));
    					String url = getString(R.string.resources_url)+rec.getString("audio_url");
    					Song s = new Song(ID, title, albumInf.getID(), albumInf.getName(), albumInf.getIDart(), albumInf.getNameArt(), albumInf.getCover(), -1,  url);
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

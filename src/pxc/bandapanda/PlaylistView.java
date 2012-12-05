package pxc.bandapanda;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import pxc.bandapanda.MenuPlaylist.LongRunningGetPlaylists;
import pxc.bandapanda.Search.LongRunningGetCovers;
import pxc.bandapanda.Search.LongRunningGetSearchSongs;
import pxc.bandapanda.Search.LongRunningPostInsertSongPlaylist;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class PlaylistView extends FragmentActivity {
	
	int finished;
	Context context;
	int playlistNumber;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		playlistNumber = getIntent().getExtras().getInt("number");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_playlist);
        context = this;
        ((TextView) findViewById(R.id.playlistName)).setText(User.getInstance().getPlaylists().get(playlistNumber).getName());
        fillPlaylist();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_view_playlist, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_play_watching_playlist:
        	Playlist current = CurrentPL.getInstance();
        	current = new Playlist();
        	current = User.getInstance().getPlaylists().get(playlistNumber);
        	Intent in2 = new Intent(context, MusicPlayer.class);
        	startActivity(in2);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void fillPlaylist(){
    	ScrollView scroll = (ScrollView) findViewById(R.id.songsInPlaylist);
		LinearLayout finallayout = new LinearLayout(context);
    	finallayout.setOrientation(1);
    	Playlist pl = User.getInstance().getPlaylists().get(playlistNumber);
    	int numSongs = pl.getNumSongs();
    	for(int i = 0; i < numSongs; ++i){
    		final LinearLayout layout1 = new LinearLayout(context);
    		LinearLayout vertical1 = new LinearLayout(context);
    		vertical1.setOrientation(1);
    		final TextView artist = new TextView(context);
    		artist.setText(pl.getSong(i).getTitle());
    		artist.setTextSize(25);
    		vertical1.addView(artist);
    		final TextView album = new TextView(context);
    		album.setText(pl.getSong(i).getGroup());
    		album.setTextSize(18);
    		vertical1.addView(album);
    		
    		layout1.setOrientation(0);
    		final ImageView image = new ImageView(context);
            Drawable d = pl.getSong(i).getDcover();
            Bitmap bd = ((BitmapDrawable) d).getBitmap();
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            double resize = 0.25;
            int y = (int) (size.y*resize);
            int x = (int) (size.x*resize); 
            if(x > y) x= y;
            Bitmap bitmapOrig = Bitmap.createScaledBitmap(bd, x, x, false);
    		image.setImageDrawable(new BitmapDrawable(bitmapOrig));
    		layout1.addView(image);
    		layout1.addView(vertical1);
    		final int id = i;
    		
    		layout1.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	crearMenu(id);
    			}
    		});
    		
    		finallayout.addView(layout1);
    	}
    	scroll.removeAllViews();
        scroll.addView(finallayout);
    }
    
    private void crearMenu(final int index){
    	final Playlist pl = User.getInstance().getPlaylists().get(playlistNumber);
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(pl.getSong(index).getTitle());
    	CharSequence[] item = {"Play","Add to playing now", "Add to favorites", "Add to a Playlist", "Share","Deletle from playlist"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					CurrentPL current = CurrentPL.getInstance();
					current.resetPlaylist();
			    	current.addSong(pl.getSong(index));
					Intent i = new Intent(context, MusicPlayer.class);
                	startActivity(i);
				}
				if(which == 1){
					CurrentPL current = CurrentPL.getInstance();
					current.addSong(pl.getSong(index));
				}
				if(which == 2){
					
				}
				if(which == 3){
					AlertDialog.Builder b1 = new AlertDialog.Builder(context);
			    	b1.setTitle(pl.getSong(index).getTitle());
			    	CharSequence[] item = new CharSequence[User.getInstance().getNumberPlaylists()];
			    	for(int i = 0; i < User.getInstance().getNumberPlaylists(); ++i){
			    		item[i]= User.getInstance().getNamePlaylist(i);
			    	}
			    	b1.setItems(item, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which2) {
							finished = 0;
							LongRunningPostInsertSongPlaylist lrpisp = new LongRunningPostInsertSongPlaylist(User.getInstance().getIdPlaylist(which2), pl.getSong(index).getID());
							lrpisp.execute();
							while(finished != 1);
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
    			String res = String.valueOf(stl.getStatusCode());
    			System.out.println(res);
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

    			AlertDialog alert = new AlertDialog.Builder(PlaylistView.this).create();
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

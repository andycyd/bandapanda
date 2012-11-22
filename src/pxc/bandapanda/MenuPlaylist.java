package pxc.bandapanda;

import java.io.UnsupportedEncodingException;
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

import pxc.bandapanda.Search.LongRunningGetImages;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MenuPlaylist extends FragmentActivity {
   public static Context context;

   Vector<Playlist> vectorPlaylists;
   int finished;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        context = this;
        vectorPlaylists = new Vector<Playlist>();
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
        LinearLayout ll = (LinearLayout) findViewById(R.id.playlistlayout);
        for(int i = 0; i < vectorPlaylists.size(); ++i){
            TextView t = new TextView(this);
            t.setText(vectorPlaylists.get(i).getName());
            ll.addView(t);
        }
        
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }
    
    
    public void searchSongs(View view){
    	Intent i = new Intent(this, Search.class );
    	startActivity(i);       
    }
    /*
     * 
     * 
     * * 
         * DESCOMENTAR ESTO PARA LA BUSQUEDA DE LAS PLAYLISTS!!!
         * 
         * 
         * 
         * 
         * 
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
    }    */



    
    
}

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
import org.apache.http.params.HttpParams;
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
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Search extends FragmentActivity {
    private PagerAdapter pageAdapter;
    private static Context context;
    ViewPager mViewPager;
    int numSongs;
    int width;
    Vector<String> urlDrawables;
    Vector<Drawable> drawable;
    Vector<Song> resSearchSongs;
    int currentPage;
    int finished;
    int windowWidth;
    int offse;
	String lastSearch;
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Display disp = getWindowManager().getDefaultDisplay();
    	Point point = new Point();
    	disp.getSize(point);
    	width = point.x;
        super.onCreate(savedInstanceState);
        offse = 0;
    	lastSearch = "ldfijghsdflkgvd";
        context = this;
        drawable = new Vector<Drawable>();
        urlDrawables = new Vector<String>();
        resSearchSongs = new Vector<Song>();
        setContentView(R.layout.activity_search);
		pageAdapter = new CustomPageAdapter(context);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(pageAdapter);
		currentPage = -1;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		windowWidth = metrics.widthPixels;
		TextView search = (TextView) findViewById(R.id.searchText);
		search.setOnClickListener( new OnClickListener(){
			public void onClick(View v){
				Button b = (Button) findViewById(R.id.buttonSearch);
				b.setText("Search");
				lastSearch = "sdkfjhnsdñflksd";
				offse = 0;
			}
		});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        
        //Intent i = new Intent(context, MusicPlayer.class);
    	//startActivity(i);
        return true;
    }

    
    @SuppressWarnings("deprecation")
	private void searchSongs(){
    	resSearchSongs = new Vector<Song>();
		LongRunningGetSearch lrgs = new LongRunningGetSearch();
		finished = 0;
		lrgs.execute();
		while(finished != 1);
		numSongs = resSearchSongs.size();
    	LongRunningGetImages lrgi = new LongRunningGetImages();
    	finished = 0;
    	lrgi.execute();
		while(finished != 1);
    	ScrollView scroll = (ScrollView) mViewPager.getChildAt(0);
		LinearLayout finallayout = new LinearLayout(context);
    	finallayout.setOrientation(1);
    	int i;
    	for(i = 0; i < numSongs; ++i){
    		final LinearLayout layout1 = new LinearLayout(context);
    		LinearLayout vertical1 = new LinearLayout(context);
    		vertical1.setOrientation(1);
    		final TextView artist = new TextView(context);
    		artist.setText(resSearchSongs.get(i).getTitle());
    		artist.setTextSize(25);
    		vertical1.addView(artist);
    		final TextView album = new TextView(context);
    		album.setText(resSearchSongs.get(i).getGroup());
    		album.setTextSize(18);
    		vertical1.addView(album);
    		final TextView songid = new TextView(context);
    		songid.setText(Integer.toString(i));
    		songid.setTextSize(0);
    		vertical1.addView(songid);
    		
    		layout1.setOrientation(0);
    		final ImageView image = new ImageView(context);
    		if(resSearchSongs.get(i).getCoverDrawablePointer() == -1) image.setImageDrawable(getResources().getDrawable(R.drawable.nonfound));
    		else {

                Drawable d = (Drawable) drawable.get(resSearchSongs.get(i).getCoverDrawablePointer());
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
    		}
    		layout1.addView(image);
    		layout1.addView(vertical1);
    		final int id = i;
    		
    		layout1.setOnLongClickListener(new OnLongClickListener() {
    			public boolean onLongClick(View v){
					return false;
    			}
    			
    		});
    		
    		layout1.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	LinearLayout vert = (LinearLayout) layout1.getChildAt(1);
                	int index = Integer.parseInt(((TextView) vert.getChildAt(2)).getText().toString());
                	crearMenu(index);
                	//Intent i = new Intent(context, MusicPlayer.class);
                	//startActivity(i);
    			}
    		});
    		
    		finallayout.addView(layout1);
    	}
    	scroll.removeAllViews();
        scroll.addView(finallayout);
        mViewPager.removeViewAt(0);
        mViewPager.addView(scroll, 0);
    }
    
	
    
    private void searchAlbums(){
    	
    }
    
    private void searchArtists(){
    	
    }
    
    public void searchClick(View view){    	
    	hideInputMethod();
    	currentPage = mViewPager.getCurrentItem();
		switch(currentPage){
		case 0: searchSongs(); break;
		case 2: searchAlbums(); break;
		case 1: searchArtists(); break;
		}
		Button b = (Button) findViewById(R.id.buttonSearch);
		if(!lastSearch.equals("sdkfjhnsdñflksd")) b.setText("More");
    }
    
    
    private void crearMenu(final int index){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(resSearchSongs.get(index).getTitle());
    	CharSequence[] item = {"Play"," Add to current playlist", "Add to favorites", "Add to Playlist", "Share"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					CurrentPL current = CurrentPL.getInstance();
					current.resetPlaylist();
			    	resSearchSongs.get(index).setDcover((Drawable) drawable.get(resSearchSongs.get(index).getCoverDrawablePointer()));
			    	current.addSong(resSearchSongs.get(index));
					Intent i = new Intent(context, MusicPlayer.class);
                	startActivity(i);
				}
				if(which == 1){
					CurrentPL current = CurrentPL.getInstance();
			    	resSearchSongs.get(index).setDcover((Drawable) drawable.get(resSearchSongs.get(index).getCoverDrawablePointer()));
			    	current.addSong(resSearchSongs.get(index));
					Intent i = new Intent(context, MusicPlayer.class);
                	startActivity(i);
				}
				if(which == 2){
					
				}
				if(which == 3){
					AlertDialog.Builder b1 = new AlertDialog.Builder(context);
			    	b1.setTitle(resSearchSongs.get(index).getTitle());
			    	CharSequence[] item = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "2", "3", "4", "5"};
			    	b1.setItems(item, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which) {
							if(which == 0){
							}
							if(which == 1){
							}
							if(which == 2){
							}
							if(which == 3){
				
							}
							
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
    
    
    public class CustomPageAdapter extends PagerAdapter{
    	
    	private final Context context;
    	//private int finished;

    	
    	
    	public CustomPageAdapter(Context context){
    		super();
    		finished = 0;
    		this.context = context;
    		drawable = new Vector<Drawable>();
    	}
    	
    	@Override
    	public void destroyItem(View collection, int position, Object view){
    		((ViewPager) collection).removeView((ScrollView) view);
    	}
    	

        @Override
        public int getCount() {
            return 3;
        }
        
        @Override  
        public void finishUpdate(View arg0) {  
              
        } 

        @Override  
        public boolean isViewFromObject(View view, Object object) {  
             return view==((ScrollView)object);  
        }  
      
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
              
        }  
      
        @Override  
        public Parcelable saveState() {  
            return null;  
        }  
      
        @Override  
        public void startUpdate(View arg0) {   
              
        }
        
        @Override
        public synchronized Object instantiateItem(View collection,int position){
        	ScrollView scroll = new ScrollView(context);
            ((ViewPager) collection).addView(scroll,position);
            return scroll;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return getString(R.string.title_section1).toUpperCase();
                case 1: return getString(R.string.title_section2).toUpperCase();
                case 2: return getString(R.string.title_section3).toUpperCase();
            }
            return null;
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
			try {
				String currentUrl;
	        	for(int i = 0; i < numSongs; ++i){
	        		currentUrl = resSearchSongs.get(i).getCover();
					if(!urlDrawables.contains(currentUrl)){
						urlDrawables.add(currentUrl);
						drawable.add(ImageOperations(context,currentUrl));
						Song aux = resSearchSongs.get(i);
						resSearchSongs.remove(i);
						aux.setCoverDrawablePointer(drawable.size()-1);
						resSearchSongs.add(i, aux);
					}
					else{
						int trob = 0;
						int a = 0;
						while(trob == 0){
							if(urlDrawables.get(a).equals(currentUrl)){
								Song aux = resSearchSongs.get(i);
								resSearchSongs.remove(i);
								aux.setCoverDrawablePointer(a);
								resSearchSongs.add(i, aux);
								trob = 1;
							}
							else a++;
						}
					}
	        	}
	        	System.out.println("tenemos todas las covers");
	        	finished = 1;
			} catch (Exception ex) {
				return null;
			}
           
			return null;
		}

    }
    
    public class LongRunningGetSearch extends AsyncTask <Void, Void, String> {

    	
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
    		TextView search = (TextView)findViewById(R.id.searchText);
    		HttpClient httpClient = new DefaultHttpClient();
    		String requestToSearch = search.getText().toString();
    		if(lastSearch.equals(requestToSearch)) offse+=10;
    		else offse = 0;
    		lastSearch = requestToSearch;
    		HttpContext localContext = new BasicHttpContext();
    		String t = "http://polar-thicket-1771.herokuapp.com/songs/search.json?q="+requestToSearch+"&order=ASC&offset="+String.valueOf(offse);
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
    				JSONArray result = new JSONArray(src);
    				for (int i = 0; i < result.length(); ++i) {
    				    JSONObject rec = result.getJSONObject(i);
    				    int ID = Integer.parseInt(rec.getString("song_id"));
    					String title = rec.getString("song_title");
    					int IDalbum = Integer.parseInt(rec.getString("album_id"));
    					String album = rec.getString("album_title");
    					int IDgroup = Integer.parseInt(rec.getString("artist_id"));
    					String group = rec.getString("artist_name");
    					String url = getString(R.string.resources_url)+rec.getString("audio_url");
    					String cover = getString(R.string.resources_url)+rec.getString("cover_url");
    					Song s = new Song(ID, title, IDalbum, album, IDgroup, group, cover, -1,  url);
    		    		resSearchSongs.add(s);
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
			}
    		else if(results.equals("400")){
				crearAlert("Error", "Wrong parameters");
			}
			else if(results.equals("416")){
				if(offse == 0) crearAlert("Error", "No songs match with the search");
				else {
					crearAlert("Error", "No more songs avaliable");
					Button b = (Button) findViewById(R.id.buttonSearch);
					b.setText("Search");
				}
				lastSearch = "sdkfjhnsdñflksd";
				offse = 0;
				
			}
			else{
				crearAlert("Connection error", "No connection with the server");
				lastSearch = "sdkfjhnsdñflksd";
				offse = 0;
			}
			pd.dismiss();
    	}
    	
    	@SuppressWarnings("deprecation")
		private void crearAlert(String t, String s){
    		AlertDialog alert = new AlertDialog.Builder(Search.this).create();
			alert.setTitle(t);
			alert.setMessage(s);
			alert.setButton("Close",new DialogInterface.OnClickListener() {
				
				public void onClick(final DialogInterface dialog, final int which) {
				}
			});
			alert.show();
    	}
    }
    
    private void hideInputMethod(){  
    	TextView srch = (TextView) findViewById(R.id.searchText);
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
        imm.hideSoftInputFromWindow(srch.getWindowToken(), 0);  
    } 

}




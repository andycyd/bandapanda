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
    

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Display disp = getWindowManager().getDefaultDisplay();
    	Point point = new Point();
    	disp.getSize(point);
    	width = point.x;
        super.onCreate(savedInstanceState);
        numSongs = 3;
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    
    @SuppressWarnings("deprecation")
	private void searchSongs(){
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
    		songid.setText(String.valueOf(resSearchSongs.get(i).getID()));
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
    				System.out.println("Apretado: "+id);
					return false;
    			}
    			
    		});
    		
    		layout1.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	LinearLayout vert = (LinearLayout) layout1.getChildAt(1);
                	System.out.println(((TextView) vert.getChildAt(2)).getText());
                	CurrentPL current = CurrentPL.getInstance();
                	resSearchSongs.get(0).setDcover((Drawable) drawable.get(resSearchSongs.get(0).getCoverDrawablePointer()));
                	resSearchSongs.get(1).setDcover((Drawable) drawable.get(resSearchSongs.get(1).getCoverDrawablePointer()));
                	current.addSong(resSearchSongs.get(0));
                	current.addSong(resSearchSongs.get(1));
                	Intent i = new Intent(context, MusicPlayer.class);
                	startActivity(i);
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
    
    public void testClick(View view){    	

    	currentPage = mViewPager.getCurrentItem();
		switch(currentPage){
		case 0: 
			numSongs++;
			LongRunningGetSearch lrgs = new LongRunningGetSearch();
			lrgs.execute();
	    	resSearchSongs = new Vector<Song>();
	    	for(int i = 0; i < numSongs; ++i){
	    		Song s = new Song(i, "Song ".concat(String.valueOf(i+1)), 0, "albm ".concat(String.valueOf(i+1)), 0, "group ".concat(String.valueOf(i+1)), "http://galeon.com/miscosasvarias/cover".concat(String.valueOf((i%4)+1).concat(".jpg")), -1,  "aaa");
	    		resSearchSongs.add(s);
	    	}
	    	LongRunningGetImages lrgi = new LongRunningGetImages();
	    	lrgi.execute();
			while(finished != 1); 
			searchSongs(); break;
		case 2: searchAlbums(); break;
		case 1: searchArtists(); break;
		}
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
    		HttpContext localContext = new BasicHttpContext();
    		HttpGet httpget = new HttpGet("http://polar-thicket-1771.herokuapp.com/songs/search.json?q="+requestToSearch+"&order=ASC&limit=10&offset=0");
    		httpget.setHeader("X-AUTH-TOKEN", User.getInstance().getToken());
    		try {
    			HttpResponse response = httpClient.execute(httpget, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String res = String.valueOf(stl.getStatusCode());
    			if(res.equals("200")){
    				String src = EntityUtils.toString(ent);
    				System.out.println(src);
    				JSONArray result = new JSONArray(src);
    				for (int i = 0; i < result.length(); ++i) {
    				    JSONObject rec = result.getJSONObject(i);
    				    int ID = Integer.parseInt(rec.getString("song_id"));
    					String title = rec.getString("song_title");
    					int IDalbum = Integer.parseInt(rec.getString("album_id"));
    					String album = rec.getString("album_title");
    					int IDgroup = Integer.parseInt(rec.getString("artist_id"));
    					String group = rec.getString("artist_name");
    					String cover = rec.getString("audio_url");
    					String url = rec.getString("cover_url");
    					System.out.println("Song: "+title);
    					System.out.println("Group: "+group);
    					System.out.println("album: "+album);
    				}
    			}
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
				finished = 1;
			}
			else /*if(results.equals("401"))*/{

    			AlertDialog alert = new AlertDialog.Builder(context).create();
    			alert.setTitle("Searching error");
    			alert.setMessage("yoNoSaber...");
    			alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
    			alert.show();
			}
			/*else{

    			AlertDialog alert = new AlertDialog.Builder(context).create();
    			alert.setTitle("Login error");
    			alert.setMessage("No connection with the server");
    			alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
    			alert.show();
			}*/
    	}
    }


}




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
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class Search extends FragmentActivity {
    private PagerAdapter pageAdapter;
    private static Context context;
    ViewPager mViewPager;
    int numSongs;
    int numArtists;
    int numAlbums;
    int width;
    Vector<String> urlDrawables;
    Vector<Drawable> drawable;
    Vector<Song> resSearchSongs;
    Vector<Artist> resSearchArtists;
    Vector<Album> resSearchAlbums;
    Vector<OtherUsers> resSearchUsers;
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
        resSearchArtists = new Vector<Artist>();
        resSearchAlbums = new Vector<Album>();
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

		mViewPager.setOnPageChangeListener(new OnPageChangeListener(){
			public void onPageScrollStateChanged(int arg0) {

			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
				
			}

			public void onPageSelected(int arg0) {
				Button b = (Button) findViewById(R.id.buttonSearch);
				b.setText("Search");
				lastSearch = "sdkfjhnsdñflksd";
				offse = 0;
				drawable = new Vector<Drawable>();
		        urlDrawables = new Vector<String>();
		        resSearchSongs = new Vector<Song>();
		        resSearchArtists = new Vector<Artist>();
		        resSearchAlbums = new Vector<Album>();
			}
    		
    	});
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }
    
    @SuppressWarnings("deprecation")
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.menu_play:
        	Intent in = new Intent(context, MusicPlayer.class);
        	startActivity(in);
            return true;
        case R.id.menu_play_all:
            CurrentPL current = CurrentPL.getInstance();
			current.resetPlaylist();
			if(resSearchSongs.size() == 0){
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
			for(int i = 0; i < resSearchSongs.size(); ++i){
				resSearchSongs.get(i).setDcover((Drawable) drawable.get(resSearchSongs.get(i).getCoverDrawablePointer()));
		    	current.addSong(resSearchSongs.get(i));
			}
			Intent in2 = new Intent(context, MusicPlayer.class);
        	startActivity(in2);
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    
    @SuppressWarnings("deprecation")
	private void searchSongs(){
    	resSearchSongs = new Vector<Song>();
    	LongRunningGetSearchSongs lrgs = new LongRunningGetSearchSongs();
		finished = 0;
		lrgs.execute();
		while(finished != 1);
		numSongs = resSearchSongs.size();
		LongRunningGetCovers lrgc = new LongRunningGetCovers();
    	finished = 0;
    	lrgc.execute();
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
    		
    		layout1.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	crearMenu(id);
    			}
    		});
    		
    		finallayout.addView(layout1);
    	}
    	scroll.removeAllViews();
        scroll.addView(finallayout);
        mViewPager.removeViewAt(0);
        mViewPager.addView(scroll, 0);
        ScrollView scrollEmpty = new ScrollView(context);
        mViewPager.removeViewAt(1);
        mViewPager.addView(scrollEmpty, 1);
    }
    
	
    
    private void searchAlbums(){
    	resSearchAlbums = new Vector<Album>();
    	LongRunningGetSearchAlbum lrgsa = new LongRunningGetSearchAlbum();
		finished = 0;
		lrgsa.execute();
		while(finished != 1);
		numAlbums = resSearchAlbums.size();
		LongRunningGetCoverAlbums lrgi = new LongRunningGetCoverAlbums();
    	finished = 0;
    	lrgi.execute();
		while(finished != 1);
    	ScrollView scroll = (ScrollView) mViewPager.getChildAt(1);
		LinearLayout finallayout = new LinearLayout(context);
    	finallayout.setOrientation(1);
    	int i;
    	for(i = 0; i < numAlbums; ++i){
    		final LinearLayout layout1 = new LinearLayout(context);
    		LinearLayout vertical1 = new LinearLayout(context);
    		vertical1.setOrientation(1);
    		final TextView album = new TextView(context);
    		album.setText(resSearchAlbums.get(i).getName());
    		album.setTextSize(30);
    		vertical1.addView(album);
    		
    		layout1.setOrientation(0);
    		final ImageView image = new ImageView(context);
    		if(resSearchAlbums.get(i).getCoverDrawablePointer() == -1) image.setImageDrawable(getResources().getDrawable(R.drawable.nonfound));
    		else {

                Drawable d = (Drawable) drawable.get(resSearchAlbums.get(i).getCoverDrawablePointer());
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
    		
    		layout1.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	crearMenuAlbum(id);
    			}
    		});
    		
    		finallayout.addView(layout1);
    	}
    	scroll.removeAllViews();
        scroll.addView(finallayout);
        mViewPager.removeViewAt(1);
        mViewPager.addView(scroll, 1);
        ScrollView scrollEmpty = new ScrollView(context);
        mViewPager.removeViewAt(0);
        mViewPager.addView(scrollEmpty, 0);
    }
    
    private void searchArtists(){
    	resSearchArtists = new Vector<Artist>();
    	LongRunningGetSearchArtist lrgsa = new LongRunningGetSearchArtist();
		finished = 0;
		lrgsa.execute();
		while(finished != 1);
		numArtists = resSearchArtists.size();
		LongRunningGetImages lrgi = new LongRunningGetImages();
    	finished = 0;
    	lrgi.execute();
		while(finished != 1);
    	ScrollView scroll = (ScrollView) mViewPager.getChildAt(1);
		LinearLayout finallayout = new LinearLayout(context);
    	finallayout.setOrientation(1);
    	int i;
    	for(i = 0; i < numArtists; ++i){
    		final LinearLayout layout1 = new LinearLayout(context);
    		LinearLayout vertical1 = new LinearLayout(context);
    		vertical1.setOrientation(1);
    		final TextView artist = new TextView(context);
    		artist.setText(resSearchArtists.get(i).getName());
    		artist.setTextSize(30);
    		vertical1.addView(artist);
    		
    		layout1.setOrientation(0);
    		final ImageView image = new ImageView(context);
    		if(resSearchArtists.get(i).getImageDrawablePointer() == -1) image.setImageDrawable(getResources().getDrawable(R.drawable.nonfound));
    		else {

                Drawable d = (Drawable) drawable.get(resSearchArtists.get(i).getImageDrawablePointer());
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
    		
    		layout1.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	crearMenuArtist(id);
    			}
    		});
    		
    		finallayout.addView(layout1);
    	}
    	scroll.removeAllViews();
        scroll.addView(finallayout);
        mViewPager.removeViewAt(1);
        mViewPager.addView(scroll, 1);
        ScrollView scrollEmpty = new ScrollView(context);
        mViewPager.removeViewAt(0);
        mViewPager.addView(scrollEmpty, 0);
        //mViewPager.removeViewAt(2);
        //mViewPager.addView(scrollEmpty, 2);
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
    	CharSequence[] item = {"Play","Add to playing now", "Add to a Playlist", "Share"};
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
				}
				if(which == 2){
					AlertDialog.Builder b1 = new AlertDialog.Builder(context);
			    	b1.setTitle(resSearchSongs.get(index).getTitle());
			    	CharSequence[] item = new CharSequence[User.getInstance().getNumberPlaylists()];
			    	for(int i = 0; i < User.getInstance().getNumberPlaylists(); ++i){
			    		item[i]= User.getInstance().getNamePlaylist(i);
			    	}
			    	b1.setItems(item, new DialogInterface.OnClickListener() {
						
						public void onClick(DialogInterface dialog, int which2) {
							finished = 0;
							LongRunningPostInsertSongPlaylist lrpisp = new LongRunningPostInsertSongPlaylist(User.getInstance().getIdPlaylist(which2), resSearchSongs.get(index).getID());
							lrpisp.execute();
							while(finished != 1);
						}
					});
			    	b1.show();
				}
				if(which == 3){
					recommend(resSearchSongs.get(index).getID(), "song", resSearchSongs.get(index).getTitle());
				}
				
			}
		});
    	b.show();
    }
    
    private void crearMenuAlbum(final int index){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(resSearchAlbums.get(index).getName());
    	CharSequence[] item = {"Watch album","Share"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
					Intent i = new Intent(context, AlbumView.class);
					Bundle b = new Bundle();
					b.putInt("id", resSearchAlbums.get(index).getID());
					i.putExtras(b);
					startActivity(i);
				}
				if(which == 1){
					recommend(resSearchAlbums.get(index).getID(), "album", resSearchAlbums.get(index).getName());
				}
				
			}
		});
    	b.show();
    }
    
    private void crearMenuArtist(final int id){
    	AlertDialog.Builder b = new AlertDialog.Builder(context);
    	b.setTitle(resSearchArtists.get(id).getName());
    	CharSequence[] item = {"Watch artist","Share"};
    	b.setItems(item, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if(which == 0){
                	Intent i = new Intent(context, ArtistView.class);
                	Bundle b = new Bundle();
                	b.putInt("id", resSearchArtists.get(id).getID());
                	i.putExtras(b);
                	startActivity(i);
				}
				if(which == 1){
					recommend(resSearchArtists.get(id).getID(), "artist", resSearchArtists.get(id).getName());
				}
				
			}
		});
    	b.show();
    }
    
    private void recommend(final int id, final String what, final String name){
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
 							LongRunningPostRecommend lrpr = new LongRunningPostRecommend(resSearchUsers.get(which).getID(), id, what);
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
    
    public class LongRunningGetCovers extends AsyncTask <Void, Void, String> {

    	ProgressDialog pd;

    	
    	public LongRunningGetCovers(){

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
    
    public class LongRunningGetCoverAlbums extends AsyncTask <Void, Void, String> {

    	ProgressDialog pd;

    	
    	public LongRunningGetCoverAlbums(){

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
	        	for(int i = 0; i < numAlbums; ++i){
	        		currentUrl = resSearchAlbums.get(i).getCover();
					if(!urlDrawables.contains(currentUrl)){
						urlDrawables.add(currentUrl);
						drawable.add(ImageOperations(context,currentUrl));
						Album aux = resSearchAlbums.get(i);
						resSearchAlbums.remove(i);
						aux.setCoverDrawablePointer(drawable.size()-1);
						resSearchAlbums.add(i, aux);
					}
					else{
						int trob = 0;
						int a = 0;
						while(trob == 0){
							if(urlDrawables.get(a).equals(currentUrl)){
								Album aux = resSearchAlbums.get(i);
								resSearchAlbums.remove(i);
								aux.setCoverDrawablePointer(a);
								resSearchAlbums.add(i, aux);
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
	        	for(int i = 0; i < numArtists; ++i){
	        		currentUrl = resSearchArtists.get(i).getCover();
					if(!urlDrawables.contains(currentUrl)){
						urlDrawables.add(currentUrl);
						drawable.add(ImageOperations(context,currentUrl));
						Artist aux = resSearchArtists.get(i);
						resSearchArtists.remove(i);
						aux.setImageDrawablePointer(drawable.size()-1);
						resSearchArtists.add(i, aux);
					}
					else{
						int trob = 0;
						int a = 0;
						while(trob == 0){
							if(urlDrawables.get(a).equals(currentUrl)){
								Artist aux = resSearchArtists.get(i);
								resSearchArtists.remove(i);
								aux.setImageDrawablePointer(a);
								resSearchArtists.add(i, aux);
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
    
    public class LongRunningGetSearchSongs extends AsyncTask <Void, Void, String> {

    	
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
    		if(lastSearch.equals(requestToSearch)) offse+=15;
    		else offse = 0;
    		lastSearch = requestToSearch;
    		HttpContext localContext = new BasicHttpContext();
    		String t = getString(R.string.api_url)+"/songs/search.json?q="+requestToSearch+"&order=ASC&offset="+String.valueOf(offse);
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
        
    
public class LongRunningGetSearchArtist extends AsyncTask <Void, Void, String> {

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
    		if(lastSearch.equals(requestToSearch)) offse+=15;
    		else offse = 0;
    		lastSearch = requestToSearch;
    		HttpContext localContext = new BasicHttpContext();
    		String t = getString(R.string.api_url)+"/artists/search.json?q="+requestToSearch+"&order=ASC&offset="+String.valueOf(offse);
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
    				    int ID = Integer.parseInt(rec.getString("artist_id"));
    					String name = rec.getString("artist_name");
    					String cover = getString(R.string.resources_url)+rec.getString("artist_img_url");
    					Artist a = new Artist(ID, name, cover);
    		    		resSearchArtists.add(a);
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
    		else if(results.equals("400")){
				crearAlert("Error", "Wrong parameters");
			}
			else if(results.equals("416")){
				if(offse == 0) crearAlert("Error", "No artists match with the search");
				else {
					crearAlert("Error", "No more artists avaliable");
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

public class LongRunningGetSearchAlbum extends AsyncTask <Void, Void, String> {

	
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
		if(lastSearch.equals(requestToSearch)) offse+=15;
		else offse = 0;
		lastSearch = requestToSearch;
		HttpContext localContext = new BasicHttpContext();
		String t = getString(R.string.api_url)+"/albums/search.json?q="+requestToSearch+"&order=ASC&offset="+String.valueOf(offse);
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
				    int ID = Integer.parseInt(rec.getString("album_id"));
					String name = rec.getString("album_title");
					int IDart = Integer.parseInt(rec.getString("artist_id"));
					String nameart = rec.getString("artist_name");
					String cover = getString(R.string.resources_url)+rec.getString("cover_url");
					Album al = new Album(ID, name, IDart, nameart, cover);
		    		resSearchAlbums.add(al);
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
		else if(results.equals("400")){
			crearAlert("Error", "Wrong parameters");
		}
		else if(results.equals("416")){
			if(offse == 0) crearAlert("Error", "No albums match with the search");
			else {
				crearAlert("Error", "No more albums avaliable");
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
	
				AlertDialog alert = new AlertDialog.Builder(Search.this).create();
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

public class LongRunningPostRecommend extends AsyncTask <Void, Void, String> {

	
    int user;
    int resource;
    String type;
    
    public LongRunningPostRecommend(int idu, int idr, String tp){
    	user = idu;
    	resource = idr;
    	type = tp;
    }
    
    @Override
    protected void onPreExecute(){

    }
    
    
	@Override
	protected String doInBackground(Void... params) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpContext localContext = new BasicHttpContext();
		HttpPost httppost = new HttpPost(getString(R.string.api_url)+"/users/"+user+"/recommendations.json?type="+type+"&resource_id="+resource);
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

			AlertDialog alert = new AlertDialog.Builder(Search.this).create();
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

private void hideInputMethod(){  
	TextView srch = (TextView) findViewById(R.id.searchText);
    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);  
    imm.hideSoftInputFromWindow(srch.getWindowToken(), 0);  
}

}






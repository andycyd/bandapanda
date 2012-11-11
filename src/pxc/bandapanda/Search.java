package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import android.R.bool;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class Search extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private PagerAdapter pageAdapter;
    private static Context context;
    ViewPager mViewPager;
    int numSongs;
    int width;
    Vector<String> urlDrawables;
    Vector<Drawable> drawable;
    Vector<Song> resSearch;
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
        resSearch = new Vector<Song>();
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

    
    public void testClick(View view){
    	numSongs++;
    	
    	//falta llamada api
    	resSearch = new Vector<Song>();
    	for(int i = 0; i < numSongs; ++i){
    		Song s = new Song(i, "Song ".concat(String.valueOf(i+1)), 0, "albm ".concat(String.valueOf(i+1)), 0, "group ".concat(String.valueOf(i+1)), "http://galeon.com/miscosasvarias/cover".concat(String.valueOf((i%4)+1).concat(".jpg")), -1,  "aaa");
    		resSearch.add(s);
    	}
    	currentPage = mViewPager.getCurrentItem();
    	//drawable = new Vector<Drawable>();
    	//urlDrawables = new Vector<String>();
    	LongRunningGetIO lrgio = new LongRunningGetIO();
		lrgio.execute();
		while(finished != 1); 
    	
    	if(currentPage == 0){
    		ScrollView scroll = (ScrollView) mViewPager.getChildAt(0);
    		LinearLayout finallayout = new LinearLayout(context);
        	finallayout.setOrientation(1);
        	for(int i = 0; i < numSongs; ++i){
        		final LinearLayout layout1 = new LinearLayout(context);
        		LinearLayout vertical1 = new LinearLayout(context);
        		vertical1.setOrientation(1);
        		final TextView artist = new TextView(context);
        		artist.setText(resSearch.get(i).getTitle());
        		artist.setTextSize(25);
        		vertical1.addView(artist);
        		final TextView album = new TextView(context);
        		album.setText(resSearch.get(i).getGroup());
        		album.setTextSize(18);
        		vertical1.addView(album);
        		final TextView songid = new TextView(context);
        		songid.setText(String.valueOf(resSearch.get(i).getID()));
        		songid.setTextSize(0);
        		vertical1.addView(songid);
        		
        		layout1.setOrientation(0);
        		final ImageView image = new ImageView(context);
        		if(resSearch.get(i).getCoverDrawablePointer() == -1) image.setImageDrawable(getResources().getDrawable(R.drawable.nonfound));
        		else image.setImageDrawable((Drawable) drawable.get(resSearch.get(i).getCoverDrawablePointer()));
        		float scale = (float) (windowWidth*0.26)/128;
        		image.setScaleX(scale);
        		image.setScaleY(scale);
        		image.setMinimumWidth((int)scale);
        		image.setMinimumHeight((int)scale);
        		layout1.addView(image);
        		layout1.addView(vertical1);
        		
        		layout1.setOnClickListener(new OnClickListener() { 
                    public void onClick(View v){
                    	LinearLayout vert = (LinearLayout) layout1.getChildAt(1);
                    	System.out.println(((TextView) vert.getChildAt(2)).getText());
        			}
        		});
        		
        		finallayout.addView(layout1);
        	}
        	scroll.removeAllViews();
            scroll.addView(finallayout);
            mViewPager.removeViewAt(0);
            mViewPager.addView(scroll, 0);
            //ScrollView scroll1 = new ScrollView(context);
            //ScrollView scroll2 = new ScrollView(context);
            //mViewPager.addView(scroll1, 0);
            //mViewPager.addView(scroll2, 2);
    	}
    	/*try{
    		pageAdapter = new CustomPageAdapter(context);
    		mViewPager = (ViewPager) findViewById(R.id.pager);
    		mViewPager.setAdapter(pageAdapter);
    		ScrollView scroll = (ScrollView) mViewPager.getChildAt(1);
    		LinearLayout finallayout = (LinearLayout) scroll.getChildAt(0);
    		LinearLayout layout1 = (LinearLayout) finallayout.getChildAt(0);
    		ImageView image = (ImageView) layout1.getChildAt(0);
    		image.setImageDrawable((Drawable) drawable2.get(0));
    		layout1.addView(image, 0);
    		finallayout.addView(layout1, 0);
    		scroll.addView(finallayout, 0);
    		mViewPager.addView(scroll, 1);
    		
    		System.out.println("numCanciones : ");
    		System.out.println(finallayout.getChildCount());
    		
    	}catch(Exception e){
    		e.printStackTrace();
    	}*/
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
            // TODO Auto-generated method stub  
              
        } 

        @Override  
        public boolean isViewFromObject(View view, Object object) {  
             return view==((ScrollView)object);  
        }  
      
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {  
            // TODO Auto-generated method stub  
              
        }  
      
        @Override  
        public Parcelable saveState() {  
            // TODO Auto-generated method stub  
            return null;  
        }  
      
        @Override  
        public void startUpdate(View arg0) {  
            // TODO Auto-generated method stub  
              
        }
        
        @Override
        public synchronized Object instantiateItem(View collection,int position){
        	ScrollView scroll = new ScrollView(context);
            ((ViewPager) collection).addView(scroll,position);
            return scroll;
        	/*LinearLayout finallayout = new LinearLayout(context);
        	finished = 0;
        	finallayout.setOrientation(1);
    		LongRunningGetIO lrgio = new LongRunningGetIO();
    		lrgio.execute();


            
    		//while(!lrgio.isCancelled()){}
    		while(finished != 1); 
    		//pd.dismiss();
        	for(int i = 0; i < numSongs; ++i){
        		LinearLayout layout1 = new LinearLayout(context);
        		LinearLayout vertical1 = new LinearLayout(context);
        		vertical1.setOrientation(1);
        		final TextView artist = new TextView(context);
        		artist.setText("Artista 1");
        		artist.setTextSize(25);
        		vertical1.addView(artist);
        		final TextView album = new TextView(context);
        		album.setText("Album 1");
        		album.setTextSize(18);
        		vertical1.addView(album);
        		
        		layout1.setOrientation(0);
        		final ImageView image = new ImageView(context);
        		image.setImageDrawable((Drawable) drawable.get(i));
        		
        		layout1.addView(image);
        		layout1.addView(vertical1);
            
            
        		
        		finallayout.addView(layout1);
        	}
            ScrollView scroll = new ScrollView(context);
            scroll.addView(finallayout);
            ((ViewPager) collection).addView(scroll,0);
            return scroll;*/
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
    
    public class LongRunningGetIO extends AsyncTask <Void, Void, String> {

    	ProgressDialog pd;

    	
    	public LongRunningGetIO(){

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
            System.out.println("Empieza dialog");
    	    pd = new ProgressDialog(context);
          	pd.setMessage("Searching...");
          	pd.setCancelable(false);
          	pd.setIndeterminate(true);
          	pd.show();
       }
       
       @Override 
       protected void onPostExecute(final String s){
    	   System.out.println("Acaba");
    	   pd.dismiss();
       }
    	@Override
		protected String doInBackground(Void... params) {
			try {
				String currentUrl;
	        	for(int i = 0; i < numSongs; ++i){
	        		currentUrl = resSearch.get(i).getCover();
					if(!urlDrawables.contains(currentUrl)){
						//System.out.println("Current Url:");
		        		//System.out.println(currentUrl);
						urlDrawables.add(currentUrl);
						drawable.add(ImageOperations(context,currentUrl));
						Song aux = resSearch.get(i);
						resSearch.remove(i);
						aux.setCoverDrawablePointer(drawable.size()-1);
						resSearch.add(i, aux);
					}
					else{
						int trob = 0;
						int a = 0;
						while(trob == 0){
							if(urlDrawables.get(a).equals(currentUrl)){
								Song aux = resSearch.get(i);
								resSearch.remove(i);
								aux.setCoverDrawablePointer(a);
								resSearch.add(i, aux);
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




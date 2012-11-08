package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

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
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	Display disp = getWindowManager().getDefaultDisplay();
    	Point point = new Point();
    	disp.getSize(point);
    	width = point.x;
        super.onCreate(savedInstanceState);
        numSongs = 6;
        context = this;
        setContentView(R.layout.activity_search);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    
    public void testClick(View view){
    	numSongs++;
    	try{
    		pageAdapter = new CustomPageAdapter(context,numSongs);
    		mViewPager = (ViewPager) findViewById(R.id.pager);
    		mViewPager.setAdapter(pageAdapter);
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    
    public class CustomPageAdapter extends PagerAdapter{
    	
    	private final Context context;
    	private int numSongs;

    	Drawable drawable;
    	
    	public CustomPageAdapter(Context context, int n){
    		super();
    		this.numSongs = n;
    		this.context = context;
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
        	System.out.println("LLAMANDO");
        	LinearLayout finallayout = new LinearLayout(context);
        	finallayout.setOrientation(1);
        	for(int i = 0; i <= numSongs+1; ++i){
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
        		LongRunningGetIO lrgio =    new LongRunningGetIO();
        		lrgio.execute();
        		
        		while(!lrgio.isCancelled()){
        		} 
        		final ImageView image = new ImageView(context);
        		image.setImageDrawable(drawable);
        		int iwidth = image.getWidth();
        		float sc = (width/iwidth)*(1/5);
        		image.setScaleX(sc);
        		image.setScaleY(sc);
        		
        		layout1.addView(image);
        		layout1.addView(vertical1);
            
            
        		
        		finallayout.addView(layout1);
        	}
            ScrollView scroll = new ScrollView(context);
            scroll.addView(finallayout);
            ((ViewPager) collection).addView(scroll,0);
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
        
        
        public class LongRunningGetIO extends AsyncTask <Void, Void, String> {

        	

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
    			try {
                    drawable = ImageOperations(context,"http://racketmag.com/wp-content/uploads/2008/10/appeal-reason-rise-against-cd-cover-art.thumbnail.jpg");
                    this.cancel(true);
    			} catch (Exception ex) {
    				return null;
    			}
               
				return null;
			}
    
        }
    	
    }
    
}

package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import pxc.bandapanda.BandaPanda.LongRunningGetIO;
import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SearchSongs extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;
   public static Context context;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    int numSongs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        numSongs = 1;
        setContentView(R.layout.activity_search);
        // Create the adapter that will return a fragment for each of the three primary sections
        // of the app.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        context = this;
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }

    
    public void testClick(View view){
    	numSongs++;
    }
    
    


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
        	DummySectionFragment fragment = new DummySectionFragment();
            fragment.changePage(numSongs);
            Bundle args = new Bundle();
            args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, i + 1);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return 3;
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

    /**
     * A dummy fragment representing a section of the app, but that simply displays dummy text.
     */
    public static class DummySectionFragment extends Fragment {
    	
    	Drawable drawable;
    	int numS;
    	Activity A;
    	
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public synchronized View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
        	A = getActivity();
        	LinearLayout finallayout = new LinearLayout(context);
        	finallayout.setOrientation(1);
        	return finallayout;
        }
        
        public synchronized View changePage(int n){
        	numS = n;
        	LinearLayout finallayout = new LinearLayout(context);
        	finallayout.setOrientation(1);
        	for(int i = 0; i <= numS+1; ++i){
        		LinearLayout layout1 = new LinearLayout(context);
        		LinearLayout vertical1 = new LinearLayout(context);
        		vertical1.setOrientation(1);
        		final TextView artist = new TextView(A);
        		artist.setText("Artista 1");
        		artist.setTextSize(25);
        		vertical1.addView(artist);
        		final TextView album = new TextView(A);
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
        		//image.setScaleX(20);
        		//image.setScaleY(20);
        		
        		layout1.addView(image);
        		layout1.addView(vertical1);
            
            
        		
        		finallayout.addView(layout1);
        	}
            
            
            return finallayout;
        	
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

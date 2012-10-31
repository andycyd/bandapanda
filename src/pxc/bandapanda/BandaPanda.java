package pxc.bandapanda;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
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
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import pxc.bandapanda.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class BandaPanda extends FragmentActivity {
	/**
     * The {@link android.support.v4.view.PagerAdapter} that will provide fragments for each of the
     * sections. We use a {@link android.support.v4.app.FragmentPagerAdapter} derivative, which will
     * keep every loaded fragment in memory. If this becomes too memory intensive, it may be best
     * to switch to a {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wellcome);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);
        return true;
    }
    
    public void clickLogin(View view){
    	
    	switch (view.getId()){
    		case R.id.loginbutton:
        		new LongRunningGetIO().execute();
    			break;
        	case R.id.searchView1:
        		System.out.println("Hola"); break;
        	
    		}
    	
    }
    
    public void goRegister(){
    	
       setContentView(R.layout.activity_search);
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
		//Set up the ViewPager with the sections adapter.
    	mViewPager = (ViewPager) findViewById(R.id.pager);
    	mViewPager.setAdapter(mSectionsPagerAdapter);
    }
    
    public void search(){
    	System.out.println("Hola");
    }
    
    
    public void redirectRegister(View view){
    	Intent httpIntent = new Intent(Intent.ACTION_VIEW);
    	httpIntent.setData(Uri.parse("http://polar-thicket-1771.herokuapp.com/users/sign_up"));
    	startActivity(httpIntent);       
    }
    
    
    public class SectionsPagerAdapter extends FragmentPagerAdapter {


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new DummySectionFragment();
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
    public static class DummySectionFragment extends Fragment {
        public DummySectionFragment() {
        }

        public static final String ARG_SECTION_NUMBER = "section_number";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            TextView textView = new TextView(getActivity());
            textView.setGravity(Gravity.CENTER);
            Bundle args = getArguments();
            textView.setText(Integer.toString(args.getInt(ARG_SECTION_NUMBER)));
            return textView;
        }
    }
    
public class LongRunningGetIO extends AsyncTask <Void, Void, String> {
    	
    	protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
    		InputStream in = entity.getContent();
    		StringBuffer out = new StringBuffer();
    		int n = 1;
    		while (n>0) {
    			byte[] b = new byte[4096];
    			n =  in.read(b);
    			if (n>0) out.append(new String(b, 0, n));
    		}
    		return out.toString();
    	}

    	@Override
    	protected String doInBackground(Void... params) {
    		TextView user = (TextView)findViewById(R.id.editText1);
        	TextView pass = (TextView)findViewById(R.id.textpasswd);
    		HttpClient httpClient = new DefaultHttpClient();
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
    		nameValuePairs.add(new BasicNameValuePair("user[email]","test@example.com"));
    		nameValuePairs.add(new BasicNameValuePair("user[password]","testing"));
    		//nameValuePairs.add(new BasicNameValuePair("user[email]",user.getText().toString()));
    		//nameValuePairs.add(new BasicNameValuePair("user[password]",pass.getText().toString()));
    		
    		HttpContext localContext = new BasicHttpContext();
    		HttpPost httppost = new HttpPost("http://polar-thicket-1771.herokuapp.com/users/sign_in.json");
    		try {
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			} catch (UnsupportedEncodingException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
    		try {
    			HttpResponse response = httpClient.execute(httppost, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			if(ent != null){
    				String src = EntityUtils.toString(ent);
    				JSONObject result = new JSONObject(src);
        			User.getInstance().setId(Integer.parseInt(result.getString("user_id")));
        			User.getInstance().setToken(result.getString("auth_token"));
        			User.getInstance().setUser(user.getText().toString());
    			}
    			return String.valueOf(stl.getStatusCode());
    		} catch (Exception e) {
    			System.out.println("Error"+e.getLocalizedMessage());
    			return e.getLocalizedMessage();
    		}
    	}
    	
    	@SuppressWarnings("deprecation")
		protected void onPostExecute(String results) {
    		if(results.equals("200")){
    			System.out.println("Ok");
				goRegister();
			}
			else{

    			AlertDialog alert = new AlertDialog.Builder(BandaPanda.this).create();
    			alert.setTitle("Login error");
    			alert.setMessage("User/Password incorrect.");
    			alert.setButton("Close",new DialogInterface.OnClickListener() {
					
					public void onClick(final DialogInterface dialog, final int which) {
					}
				});
    			alert.show();
			}
    	}
    }    

}

    

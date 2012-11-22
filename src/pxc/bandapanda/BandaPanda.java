package pxc.bandapanda;

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
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

public class BandaPanda extends FragmentActivity {

	
	Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wellcome);
        context = this;
        
    }

    
    public void clickLogin(View view){
    	switch (view.getId()){
    		case R.id.loginbutton:
        		new LongRunningGetIO().execute();
    			break;        	
    		}
    	
    }
   
    
    public void goSearch(){
    	Intent i = new Intent(this, MenuPlaylist.class);
    	startActivity(i);
    }
    
    
    
    public void redirectRegister(View view){
    	Intent httpIntent = new Intent(Intent.ACTION_VIEW);
    	httpIntent.setData(Uri.parse("http://polar-thicket-1771.herokuapp.com/users/sign_up"));
    	startActivity(httpIntent);       
    }
    
    
public class LongRunningGetIO extends AsyncTask <Void, Void, String> {

	
	    ProgressDialog pd;
	    
	    @Override
	    protected void onPreExecute(){

		    pd = new ProgressDialog(context);

	       	pd.setMessage("Logging in...");
	       	pd.setCancelable(false);
	       	pd.setIndeterminate(true);
	       	pd.show();
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
				e1.printStackTrace();
			}
    		try {
    			HttpResponse response = httpClient.execute(httppost, localContext);
    			StatusLine stl = response.getStatusLine();
    			HttpEntity ent = response.getEntity();
    			String res = String.valueOf(stl.getStatusCode());
    			if(res.equals("200")){
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
				goSearch();
				pd.dismiss();
			}
			else if(results.equals("401")){

    			AlertDialog alert = new AlertDialog.Builder(BandaPanda.this).create();
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

    			AlertDialog alert = new AlertDialog.Builder(BandaPanda.this).create();
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
    }    

}

    

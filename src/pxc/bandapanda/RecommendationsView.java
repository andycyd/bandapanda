package pxc.bandapanda;

import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Space;
import android.widget.TextView;

public class RecommendationsView extends FragmentActivity{

	
	Vector<Recommendation> recommendations;
	int finished;
	   public static Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_recommendations);
        recommendations = new Vector<Recommendation>();

		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(1);
        context = this;
        /*finished = 0;
         * 
         * 
         * 
         * 
         * 
         * 
        LongRunningGetRecommendations lr = new LongRunningGetRecommendations();
        lr.execute();
        while(finished != 1);*/
        for(int i = 0; i < 15; ++ i){
        	Recommendation r = new Recommendation(i, i, "AAA", "BBB", i, "CCC", "DDD");
        	recommendations.add(r);
        }
        LinearLayout layout = (LinearLayout)findViewById(R.id.recomsLayout);
        for(int i = 0; i < recommendations.size(); ++i){
        	LinearLayout layoutint = new LinearLayout(context);
        	layoutint.setOrientation(1);
            TextView t = new TextView(this);
            t.setText(recommendations.get(i).getType()+": "+recommendations.get(i).getName());
            TextView t1 = new TextView(this);
            t1.setText("By "+recommendations.get(i).getSource_name()+" on "+recommendations.get(i).getDate());
            t.setTextSize(30);
            t1.setTextSize(20);
            layoutint.addView(t);
            layoutint.addView(t1);
            Space s = new Space(this);
            layoutint.addView(s);
            final int id = i;
            layoutint.setOnClickListener(new OnClickListener() { 
                public void onClick(View v){
                	if(recommendations.get(id).getType().equals("song")){
                	}
    			}
    		});
    		
            layout.addView(layoutint);
        }
        
    }
    
public class LongRunningGetRecommendations extends AsyncTask <Void, Void, String> {

    	
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
    		HttpContext localContext = new BasicHttpContext();
    		String t = getString(R.string.api_url)+"/users/"+User.getInstance().getId()+"/recommendations.json";
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
    				    int sourID = Integer.parseInt(rec.getString("source_id"));
    					String type = rec.getString("type");
    					String source_name = rec.getString("source_name");
    					String name = rec.getString("name");
    					int resID = Integer.parseInt(rec.getString("resource_id"));
    					String date = rec.getString("date");
    					int read = Integer.parseInt(rec.getString("read"));
    		    		Recommendation r = new Recommendation(sourID,resID,type,date,read,source_name,name);
    		    		recommendations.add(r);
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
			else{
				crearAlert("Connection error", "No connection with the server");
			}
			pd.dismiss();
    	}
    	
    	@SuppressWarnings("deprecation")
		private void crearAlert(String t, String s){
    		AlertDialog alert = new AlertDialog.Builder(RecommendationsView.this).create();
			alert.setTitle(t);
			alert.setMessage(s);
			alert.setButton("Close",new DialogInterface.OnClickListener() {
				
				public void onClick(final DialogInterface dialog, final int which) {
				}
			});
			alert.show();
    	}
    }

}

package pxc.bandapanda;

import java.util.ArrayList;
import java.util.HashMap;
 
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
 
public class PlayActivity extends ListActivity {
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playlist);
 
        ArrayList<HashMap<String, String>> songsListData = new ArrayList<HashMap<String, String>>();

        // get all songs from sdcard
        CurrentPL playlist = CurrentPL.getInstance();
 
        // looping through playlist
        for (int i = 0; i < playlist.getNumSongs(); i++) {
            // creating new HashMap
            HashMap<String, String> song = new HashMap<String, String>();
            song.put("songTitle", playlist.getSong(i).getTitle());
            song.put("songPath", playlist.getSong(i).getUrl());
 
            // adding HashList to ArrayList
            songsListData.add(song);
        }
 
        // Adding menuItems to ListView
        ListAdapter adapter = new SimpleAdapter(this, songsListData,
                R.layout.playlist_item, new String[] { "songTitle" }, new int[] {
                        R.id.songTitle });
 
        setListAdapter(adapter);
 
        // selecting single ListView item
        ListView lv = getListView();
        // listening to single listitem click
        lv.setOnItemClickListener(new OnItemClickListener() {
 
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                // getting listitem index
                int songIndex = position;
 
                // Starting new intent
                Intent in = new Intent(getApplicationContext(),
                        MusicPlayer.class);
                // Sending songIndex to PlayerActivity
                in.putExtra("songIndex", songIndex);
                setResult(100, in);
                // Closing PlayListView
                finish();
            }
        });
    }
}
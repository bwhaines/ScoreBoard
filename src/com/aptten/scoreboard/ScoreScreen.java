package com.aptten.scoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;

public class ScoreScreen extends Activity {
	
	// Instance Variables
	ListView listview;
	PlayerAdapter adapter;
	ArrayList<Player> list;
	
	// Preferences
	SharedPreferences prefs;
	int startingScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// standard onCreate calls
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_screen);
		
		// get shared preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);
		
		// create and link ListView UI object to this
		listview = (ListView) findViewById(R.id.list);
		
		// load preferences
		startingScore = Integer.parseInt(prefs.getString("startScore", "0"));
		if(prefs.getBoolean("firstRun", true)) {
			prefs.edit().putBoolean("firstRun", false).apply();
			hintsDialog();
		}
		
		// creates list used to populate ListView
		list = new ArrayList<Player>();
		
		// create list of generic names to initially populate ListView
		list = createPlaceholderObjects();
		
		// creates and populates ListView
		makeListView();
	}
	
	private void makeListView() {
        if(prefs.getBoolean("leftyMode", false)){
            adapter = new PlayerAdapter(ScoreScreen.this, R.layout.score_list_item_left, list, prefs);
        } else {
            adapter = new PlayerAdapter(ScoreScreen.this, R.layout.score_list_item, list, prefs);
        }
		listview.setAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		makeListView();
		startingScore = Integer.parseInt(prefs.getString("startScore", "0"));
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.score_screen, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_add:
	        	String name = "Player " + (list.size() + 1);
	        	list.add(new Player(name, startingScore));
	        	adapter.notifyDataSetChanged();
	            return true;
	        case R.id.action_remove:
	        	if(list.size() > 0)
	        		list.remove(list.size() - 1);
	        	adapter.notifyDataSetChanged();
	            return true;
	        case R.id.action_settings:
	        	Intent intent = new Intent(ScoreScreen.this, PrefsActivity.class);
                startActivity(intent);
	        	return true;
	        case R.id.hints_settings:
	        	hintsDialog();
	        	return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this)
			.setMessage("Do you want to exit? (This will erase all scores!)")
			.setCancelable(false)
			.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						ScoreScreen.this.finish();
					}
				})
           .setNegativeButton("No", null)
           .show();
	}
	
	public ArrayList<Player> createPlaceholderObjects() {
		ArrayList<Player> list = new ArrayList<Player>();
		
		for(int i = 0; i < 4; i++) {
			String name = "Player " + (i+1);
			Player player = new Player(name, startingScore);
			list.add(player);
		}

		return list;
	}

	private void hintsDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title)
        	   .setMessage(R.string.dialog_hints)
               .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                   public void onClick(DialogInterface dialog, int id) {
                       // Dialog disappears
                   }
               })
        	   .show();
	}
	
}

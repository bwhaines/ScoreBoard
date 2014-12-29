package com.aptten.scoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreScreen extends Activity implements TimerFragment.TimerDialogListener {
	
	// Instance Variables
    private ListView listview;
    private TextView timerText;
    private View horizRule;
    private LinearLayout timerLayout;
    private PlayerAdapter adapter;
    private ArrayList<Player> list;
    private CountDownTimer timer;
    private long timeRemaining;
	
	// Preferences
	private SharedPreferences prefs;
	private int startingScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// standard onCreate calls
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_screen);

        // display logo in action bar
        if(getActionBar()!= null) {
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setLogo(R.drawable.logo);
            getActionBar().setDisplayUseLogoEnabled(true);
            getActionBar().setTitle(R.string.empty_string);
        }
		
		// get shared preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// create and link ListView UI object to this
		listview = (ListView) findViewById(R.id.list);

        // link timer layout to object
        timerLayout = (LinearLayout) findViewById(R.id.timer_layout);
        horizRule = findViewById(R.id.horizontal_rule);

        // link timer display to textView
        timerText = (TextView) findViewById(R.id.timer_display);
        timerText.setText("0:00:00");
        timerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer == null) {
                    createTimer();
                }
            }
        });
		
		// load preferences
		startingScore = Integer.parseInt(prefs.getString("startScore", "0"));
		if(prefs.getBoolean("firstRun", true)) {
			prefs.edit().putBoolean("firstRun", false).apply();
			hintsDialog();
		}

		// creates list used to populate ListView
		list = new ArrayList<>();
		
		// create list of generic names to initially populate ListView
		list = createPlaceholderObjects();
		
		// creates and populates ListView
		makeListView();

        // set up listeners for the timer buttons
        ImageButton playTimer = (ImageButton) findViewById(R.id.play_button);
        playTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeRemaining > 0) onReturnTimer(timeRemaining);
            }
        });

        ImageButton pauseTimer = (ImageButton) findViewById(R.id.pause_button);
        pauseTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer != null) timer.cancel();
            }
        });

        ImageButton stopTimer = (ImageButton) findViewById(R.id.stop_button);
        stopTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer != null) {
                    timer.cancel();
                    timer = null;
                    timerText.setText("0:00:00");
                    timeRemaining = 0;
                }
            }
        });

	}

    // determine which layout to use (left-aligned or normal) and set up adapter
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

        // get starting score preference
		startingScore = Integer.parseInt(prefs.getString("startScore", "0"));

        // make timer invisible if preference dictates
        if(!prefs.getBoolean("dispTimer",true)) {
            timerLayout.setVisibility(LinearLayout.GONE);
            horizRule.setVisibility(View.GONE);
        } else {
            timerLayout.setVisibility(LinearLayout.VISIBLE);
            horizRule.setVisibility(View.VISIBLE);
        }

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

    // create the initial 4 players shown when app begins
	public ArrayList<Player> createPlaceholderObjects() {
		ArrayList<Player> list = new ArrayList<>();
		
		for(int i = 0; i < 4; i++) {
			String name = "Player " + (i+1);
			Player player = new Player(name, startingScore);
			list.add(player);
		}

		return list;
	}

    // create and display the hints dialog
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

    // create and display the "New Timer" dialog
    private void createTimer() {
        TimerFragment timerFragment = new TimerFragment();
        timerFragment.show(getFragmentManager(), "timer_dialog");
    }

    public void onReturnTimer(long millis) {

        // display time
        displayTime(millis);

        // create the timer object and start countdown
        timer = new CountDownTimer(millis, 1000) {
            public void onTick(long millis) {
                displayTime(millis);
                timeRemaining = millis;
            }
            public void onFinish() {
                // Clear timer object and text
                timerText.setText("0:00:00");
                timer = null;
                timeRemaining = 0;

                // Create a vibrator object to go off when timer reaches 0
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                // Vibrate device for one second
                v.vibrate(1000);

                // Clear timer object and text
                timer = null;
                timeRemaining = 0;
                timerText.setText("0:00:00");
            }
        }.start();
    }

    private void displayTime(long millis) {
        String time = String.format("%d:%02d:%02d", millis/3600000, millis/60000%60, millis/1000%60);
        timerText.setText(time);
    }
	
}
package com.aptten.scoreboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
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
    private LinearLayout timerLayout, timerTitleLayout;
    private PlayerAdapter adapter;
    private ArrayList<Player> list;
    private CountDownTimer timer;
    private long timeRemaining;
	
	// Preferences
	private SharedPreferences prefs;
	private int startingScore;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// Standard onCreate calls
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_score_screen);

        // Display logo in action bar
        if(getActionBar()!= null) {
            getActionBar().setDisplayShowHomeEnabled(true);
            getActionBar().setLogo(R.drawable.header);
            getActionBar().setDisplayUseLogoEnabled(true);
            getActionBar().setTitle(R.string.empty_string);
        }
		
		// get shared preferences
		prefs = PreferenceManager.getDefaultSharedPreferences(this);

		// Create and link ListView UI object to this
		listview = (ListView) findViewById(R.id.list);

        // Link timer layouts to objects
        timerLayout = (LinearLayout) findViewById(R.id.timer_layout);
        timerTitleLayout = (LinearLayout) findViewById(R.id.timer_title_layout);

        // Create Typeface object to set custom font
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Quicksand-Regular.otf");

        // Link timer display to textView, set font
        timerText = (TextView) findViewById(R.id.timer_display);
        timerText.setTypeface(font);
        timerText.setText("0:00:00");
        timerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer == null) {
                    createTimer();
                }
            }
        });

        // Link timer title to TextView, set font
        TextView timerTitle = (TextView) findViewById(R.id.timer_title);
        timerTitle.setTypeface(font);
		
		// Load preferences
		startingScore = Integer.parseInt(prefs.getString("startScore", "0"));
		if(prefs.getBoolean("firstRun", true)) {
			prefs.edit().putBoolean("firstRun", false).apply();
			hintsDialog();
		}

		// Creates list used to populate ListView
		list = new ArrayList<>();
		
		// Create list of generic names to initially populate ListView
		list = createPlaceholderObjects();
		
		// Creates and populates ListView
		makeListView();

        // Set up listeners for the timer buttons
        ImageButton playTimer = (ImageButton) findViewById(R.id.play_button);
        playTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timeRemaining > 0 && timer == null) onReturnTimer(timeRemaining);
            }
        });

        ImageButton pauseTimer = (ImageButton) findViewById(R.id.pause_button);
        pauseTimer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(timer != null){
                    timer.cancel();
                    timer = null;
                }
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

    // Determine which layout to use (left-aligned or normal) and set up adapter
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

        // Met starting score preference
		startingScore = Integer.parseInt(prefs.getString("startScore", "0"));

        // Make timer invisible if preference dictates
        if(!prefs.getBoolean("dispTimer",true)) {
            timerLayout.setVisibility(LinearLayout.GONE);
            timerTitleLayout.setVisibility(LinearLayout.GONE);
        } else {
            timerLayout.setVisibility(LinearLayout.VISIBLE);
            timerTitleLayout.setVisibility(LinearLayout.VISIBLE);
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
            case R.id.action_clear_scores:
                for(Player p : list) p.setScore(0);
                makeListView();
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

    // Create the initial 4 players shown when app begins
	public ArrayList<Player> createPlaceholderObjects() {
		ArrayList<Player> list = new ArrayList<>();
		
		for(int i = 0; i < 4; i++) {
			String name = "Player " + (i+1);
			Player player = new Player(name, startingScore);
			list.add(player);
		}

		return list;
	}

    // Create and display the hints dialog
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

    // Create and display winner dialog
    private void endDialog(String name) {
        WinnerFragment winnerFrag = new WinnerFragment();

        // Pass winner's name to fragment
        Bundle args = new Bundle();
        args.putString("winner", name);
        winnerFrag.setArguments(args);

        winnerFrag.show(getFragmentManager(), "winner_dialog");
    }

    // Create and display the "New Timer" dialog
    private void createTimer() {
        TimerFragment timerFragment = new TimerFragment();
        timerFragment.show(getFragmentManager(), "timer_dialog");
    }

    public void onReturnTimer(long millis) {

        // Display time
        displayTime(millis);

        // Create the timer object and start countdown
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

                // Display dialog with message winner
                endDialog(getWinner(list).getName());
            }
        }.start();
    }

    // Format remaining time appropriately and display it
    private void displayTime(long millis) {
        String time = String.format("%d:%02d:%02d", millis/3600000, millis/60000%60, millis/1000%60);
        timerText.setText(time);
    }

    //  Determine the player with the highest score, ties go to player higher on list
    private Player getWinner(ArrayList<Player> playerList) {
        if(playerList.isEmpty()) return null;

        Player winner = playerList.get(0);
        for(Player p : playerList) {
            if(p.getScore() > winner.getScore()) winner = p;
        }
        return winner;
    }

}
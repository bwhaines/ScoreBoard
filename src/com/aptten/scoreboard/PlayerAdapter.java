package com.aptten.scoreboard;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ViewSwitcher;

import java.util.List;

/** PlayerAdapter creates a UI in the list for each Player object
 *
 */
public class PlayerAdapter extends ArrayAdapter<Player> {
	int increment;
	int resource;
	List<Player> list;
	SharedPreferences prefs;

    // Constructor
	public PlayerAdapter(Context context, int NewResource, List<Player> newList, SharedPreferences prefs) {
		super(context, NewResource, newList);
		resource = NewResource;
		list = newList;
		this.prefs = prefs;
		
		try {
			increment = Integer.parseInt(prefs.getString("increment", null));
		}
		catch(NumberFormatException e) {
			increment = 1;
		}
	}
	
	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RelativeLayout playerView;
        
        // Get the current Player object
        final Player player = getItem(position);
         
        // Inflates the view
        if(convertView==null) {
            playerView = new RelativeLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi;
            vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resource, playerView, true);
        } else {
            playerView = (RelativeLayout) convertView;
        }

        // Set up an input method manager to control the keyboard
        final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        
        // Connects TextView in layout to object here
        final TextView nameField = (TextView) playerView.findViewById(R.id.name_textview);
        final TextView scoreField = (TextView) playerView.findViewById(R.id.score_textview);
        final ViewSwitcher nameSwitcher = (ViewSwitcher) playerView.findViewById(R.id.name_switcher);
        final ViewSwitcher scoreSwitcher = (ViewSwitcher) playerView.findViewById(R.id.score_switcher);
        final EditText nameEdit = (EditText) playerView.findViewById(R.id.name_edittext);
        final EditText scoreEdit = (EditText) playerView.findViewById(R.id.score_edittext);
        final Button plus = (Button) playerView.findViewById(R.id.up_button);
        final Button minus = (Button) playerView.findViewById(R.id.down_button);

        // Replaces default TextView text with correct data
        nameField.setText(player.getName());
        scoreField.setText(Integer.toString(player.getScore()));

        // Set fonts for textviews
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "fonts/Quicksand-Regular.otf");
        nameField.setTypeface(font);
        scoreField.setTypeface(font);

        // Creates onClickListener objects for plus and minus buttons
        plus.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
            	player.increment(increment);
            	scoreField.setText(Integer.toString(player.getScore()));
        	}
        });
        minus.setOnClickListener(new View.OnClickListener() {
        	@Override
        	public void onClick(View v) {
            	player.decrement(increment);
            	scoreField.setText(Integer.toString(player.getScore()));
        	}
        });

        // Sets listeners to change views to fields on long press
        nameField.setOnLongClickListener(new View.OnLongClickListener() {
        	@Override
        	public boolean onLongClick(View v) {
        	    nameSwitcher.showNext();
        	    nameEdit.setText("");
        	    nameEdit.setFocusableInTouchMode(true);
        	    nameEdit.requestFocus();
        	    return false;
        	}
        });
        scoreField.setOnLongClickListener(new View.OnLongClickListener() {
        	@Override
        	public boolean onLongClick(View v) {
        		scoreSwitcher.showNext();
        		nameEdit.setText("");
        		scoreEdit.setFocusableInTouchMode(true);
        	    scoreEdit.requestFocus();
        	    return false;
        	}
        });

        // Sets listeners to change fields back into views on pressing return key
        nameEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				String newName = nameEdit.getText().toString();
				nameSwitcher.showPrevious();
                if(!newName.equals("")) {
        	        nameField.setText(newName);
        	        player.setName(newName);
                }
                imm.hideSoftInputFromWindow(nameEdit.getWindowToken(), 0);
        	    return false;
			}
        });
        scoreEdit.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				int newScore = Integer.parseInt(scoreEdit.getText().toString());
				scoreSwitcher.showPrevious();
        	    scoreField.setText(""+newScore);
        	    player.setScore(newScore);
                imm.hideSoftInputFromWindow(scoreEdit.getWindowToken(), 0);
        	    return false;
			}
        });

        return playerView;
    }

}

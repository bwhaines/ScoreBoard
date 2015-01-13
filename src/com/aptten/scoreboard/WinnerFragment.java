package com.aptten.scoreboard;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class WinnerFragment extends DialogFragment {

    TextView winnerMessage;
    Button shareButton;

    public WinnerFragment() {
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Change theme to match app
        setStyle(DialogFragment.STYLE_NORMAL, R.style.ScoreboardDialogTheme);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Put UI objects into the view
        View v = inflater.inflate(R.layout.fragment_winner_dialog, container, false);

        // Get name of winner from arguments Bundle
        Bundle args = getArguments();
        String winnerName = args.getString("winner");

        // Create message for display and sharing
        final String message = winnerName + " " + getString(R.string.winner_dialog);

        // Set text and font for winner message
        Typeface font = Typeface.createFromAsset(getActivity().getAssets(), "fonts/Quicksand-Regular.otf");
        winnerMessage = (TextView) v.findViewById(R.id.winner_message);
        winnerMessage.setText(message);
        winnerMessage.setTypeface(font);

        // Set up button action listeners
        shareButton = (Button) v.findViewById(R.id.share_button);
        shareButton.setTypeface(font);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Create an image of current players and scores
                Bitmap image = makeShareableImage();

                // Store image in Android device MediaStore, get URI to image
                String uriString = MediaStore.Images.Media.insertImage(
                        getActivity().getContentResolver(), image, "ScoreBoard", "Image to share");

                try {
                    // Create a URI object
                    Uri uri = Uri.parse(uriString);

                    // Create the intent to share the image
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/bmp");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, uri);

                    // Display the share menu
                    startActivity(Intent.createChooser(shareIntent, "Share"));

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        return v;
    }

    // Creates an image of the ListView containing players and scores
    private Bitmap makeShareableImage() {
        Bitmap image = null;
        try {
            // Create the image of the ListView
            ListView listView = (ListView) getActivity().findViewById(R.id.list);
            listView.setDrawingCacheEnabled(true);
            image = listView.getDrawingCache();
        } catch(Exception e) {
            e.printStackTrace();
        }
        return image;
    }
}
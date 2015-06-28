package com.myprescience.ui.mix_play;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.dto.UserData;
import com.myprescience.util.Indicator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Vector;

import static com.myprescience.util.Server.MIX_PLAY_API;
import static com.myprescience.util.Server.SELECT_GENRE;
import static com.myprescience.util.Server.SERVER_ADDRESS;
import static com.myprescience.util.Server.WITH_MODE;
import static com.myprescience.util.Server.WITH_USER;
import static com.myprescience.util.Server.getStringFromUrl;


public class SelectGenreActivity extends ActionBarActivity {

    private UserData userDTO;
    private int MODE;
    private Indicator mIndicator;
    private String[] playlist = new String[200];
    private String[] playlist_id = new String[200];

    public static Activity sRecommendActivity;
    private ImageButton rightButton;
    private ProgressBar progressBar;
    private TextView textView;
    private int selectCount;
    private Vector<CardView> genreCards = new Vector<>();
    private Vector<TextView> genreCardsTexts = new Vector<>();
    public ArrayList<String> selectGenre = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mix_select_genre);

        userDTO = new UserData(this);
        mIndicator = new Indicator(this);
        MODE = getIntent().getExtras().getInt("mode");

        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.invalidate();
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Genre = TextUtils.join(",", selectGenre);
                new getPlayList().execute(SERVER_ADDRESS + MIX_PLAY_API + SELECT_GENRE + Genre
                        + WITH_MODE + MODE + WITH_USER + userDTO.getId());
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(getString(R.string.play_select_genre2));
        genreCards.add((CardView) findViewById(R.id.pop_card));
        genreCards.add((CardView) findViewById(R.id.electronic_card));
        genreCards.add((CardView) findViewById(R.id.hiphop_card));
        genreCards.add((CardView) findViewById(R.id.rock_card));
        genreCards.add((CardView) findViewById(R.id.rnb_card));
        genreCards.add((CardView) findViewById(R.id.club_card));
        genreCards.add((CardView) findViewById(R.id.country_card));
        genreCards.add((CardView) findViewById(R.id.jazz_card));
        genreCardsTexts.add((TextView) findViewById(R.id.pop_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.electronic_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.hiphop_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.rock_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.rnb_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.club_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.country_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.jazz_card_text_view));

        for(CardView card : genreCards){
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String genre = (String) view.getContentDescription();

                    if(selectGenre.indexOf(genre) == -1) {
                        selectGenre.add(genre);
                        genreCardsTexts.get(genreCards.indexOf(view)).setBackgroundColor(0xff009688);
                    }
                    else {
                        selectGenre.remove(genre);
                        genreCardsTexts.get(genreCards.indexOf(view)).setBackgroundColor(0xAA000000);
                    }

                    selectCount = selectGenre.size();
                    progressBar.setProgress( (selectCount >= 3)? 100 : selectCount*33 );
                    progressBar.invalidate();

                    if(selectGenre.size() > 3){
                        textView.setText(getString(R.string.play_select_genre2));
                        rightButton.setVisibility(ImageButton.INVISIBLE);
                    }
                    else{
                        textView.setText(selectGenre.size() + " " + getString(R.string.play_select_genre1));
                        rightButton.setVisibility(ImageButton.VISIBLE);
                    }
                    textView.invalidate();
                    rightButton.invalidate();
                    Log.e("selectGenre", selectGenre.toString());
                }
            });
        }

    }

    public void startMixPlay() {
        Intent intent = new Intent(SelectGenreActivity.this, PlayerActivity.class);
        intent.putExtra("playlist", playlist);
        intent.putExtra("playlist_id", playlist_id);
        startActivity(intent);
    }

    class getPlayList extends AsyncTask<String, String, String> {

        public getPlayList(){
        }

        @Override
        protected String doInBackground(String... url) {
            return getStringFromUrl(url[0]);
        }

        @Override
        protected void onPostExecute(String songJSON) {
            super.onPostExecute(songJSON);

            try {
                JSONParser jsonParser = new JSONParser();
                JSONArray playlistJSON = (JSONArray) jsonParser.parse(songJSON);

                for(int i = 0; i < playlistJSON.size(); i ++) {
                    JSONObject song = (JSONObject) playlistJSON.get(i);
                    String id = (String) song.get("id");
                    playlist_id[i] = id;

                    String title = (String) song.get("title");
                    String artist = (String) song.get("artist");
                    playlist[i] = title + " " + artist;
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (mIndicator.isShowing())
                mIndicator.hide();
            startMixPlay();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if ( !mIndicator.isShowing())
                mIndicator.show();
        }
    }
}

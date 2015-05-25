package com.myprescience.ui.main;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.ui.song.SongListActivity;

import java.util.ArrayList;
import java.util.Vector;

import static com.myprescience.util.Server.FIRST_MODE;


public class SelectGenreActivity2 extends ActionBarActivity {
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
        setContentView(R.layout.activity_select_genre_2);

        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.invalidate();
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRecommendActivity = SelectGenreActivity2.this;
                Intent intent = new Intent(SelectGenreActivity2.this, SongListActivity.class);
                intent.putExtra("mode", FIRST_MODE);
                intent.putExtra("selectGenre", selectGenre);
                startActivity(intent);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.toolbar_title);
        genreCards.add((CardView) findViewById(R.id.pop_card));
        genreCards.add((CardView) findViewById(R.id.electronic_card));
        genreCards.add((CardView) findViewById(R.id.hiphop_card));
        genreCards.add((CardView) findViewById(R.id.rock_card));
        genreCards.add((CardView) findViewById(R.id.rnb_card));
        genreCards.add((CardView) findViewById(R.id.club_card));
        genreCards.add((CardView) findViewById(R.id.country_card));
        genreCardsTexts.add((TextView) findViewById(R.id.pop_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.electronic_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.hiphop_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.rock_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.rnb_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.club_card_text_view));
        genreCardsTexts.add((TextView) findViewById(R.id.country_card_text_view));


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

                    if(selectGenre.size() < 3){
                        textView.setText(R.string.select_genre);
                        rightButton.setVisibility(ImageButton.INVISIBLE);
                    }
                    else{
                        textView.setText(R.string.select_genre_3);
                        rightButton.setVisibility(ImageButton.VISIBLE);
                    }
                    textView.invalidate();
                    rightButton.invalidate();
                    Log.e("selectGenre", selectGenre.toString());
                }
            });
        }

    }
}

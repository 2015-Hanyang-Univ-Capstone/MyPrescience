package com.myprescience.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.ui.song.MySongListActivity;
import com.myprescience.util.Indicator;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyPageActivity extends ActionBarActivity {

    private GridView gridView;
    private int userId;
    private boolean mLockListView = false;

    private LinearLayout linearLayout, songTextView;
    private ImageView facebook_profile;
    private TextView nameTextView, ArtistTextView, AlbumTextView, OptionTextView;

    Indicator mIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);
        setActionBar(R.string.title_section4);

        mIndicator = new Indicator(this);

//        facebook_profile = (ImageView) findViewById(R.id.profileImageView);
//        facebook_profile.setImageDrawable(getFACEBOOK_PROFILE_BITMAP());

//        nameTextView = (TextView) findViewById(R.id.nameTextView);
//        nameTextView.setText(getUSER_NAME());

        songTextView = (LinearLayout) findViewById(R.id.mysongButton);
//        ArtistTextView = (TextView) findViewById(R.id.myartistTextView);
//        AlbumTextView = (TextView) findViewById(R.id.myalbumTextView);
//        OptionTextView = (TextView) findViewById(R.id.myoptionTextView);

        songTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyPageActivity.this, MySongListActivity.class);
                startActivity(intent);
            }
        });

    }

    private void setActionBar(int title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");

        // 뒤로가기 버튼
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView TitleTextView = (TextView) findViewById(R.id.toolbar_title);
        TitleTextView.setText(title);
        Typeface face = Typeface.createFromAsset(getAssets(),
                "Steinerlight.ttf");
        TitleTextView.setTypeface(face);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.search_menu, menu);
        return true;
    }

    // 뒤로가기 버튼
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    };

}
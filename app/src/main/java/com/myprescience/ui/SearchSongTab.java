package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.util.RangeSeekBar;

import java.util.ArrayList;

@SuppressLint("ValidFragment")
public class SearchSongTab extends Fragment {

    Context mContext;
    ViewGroup mStep1TableLayout, mStep2TableLayout, mStep3TableLayout;
    TextView mStep2TextView, mStep3TextView;
    Button mSearchButton;

    ArrayList<String> songs, genres, properties;


    public SearchSongTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_activity_search, null);
        initSetting(view);

        mSearchButton = (Button) view.findViewById(R.id.searchButton);
        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("SONGS", songs.toString());
            }
        });

        return view;
    }

    private void initSetting(View view) {

        mStep1TableLayout = (ViewGroup) view.findViewById(R.id.step1TableLayout);
        mStep2TableLayout = (ViewGroup) view.findViewById(R.id.step2TableLayout);
        mStep3TableLayout = (ViewGroup) view.findViewById(R.id.step3TableLayout);

        mStep2TextView = (TextView) view.findViewById(R.id.step2TextView);
        mStep3TextView = (TextView) view.findViewById(R.id.step3TextView);

        songs = new ArrayList<String>();
        genres = new ArrayList<String>();
        properties = new ArrayList<String>();

        final Animation fadeInAnimation = AnimationUtils.loadAnimation(mContext, R.anim.abc_fade_in);

        for(int i = 0; i < mStep1TableLayout.getChildCount(); i ++) {
            ViewGroup TableRow = (ViewGroup) mStep1TableLayout.getChildAt(i);
            for (int j = 0; j < TableRow.getChildCount(); j++) {
                TableRow.getChildAt(j).setSelected(false);
                TableRow.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            v.setBackgroundResource(R.drawable.rectangle_not_active);
                            v.setSelected(false);
                            songs.remove((String) v.getTag());
                        } else {
                            v.setBackgroundResource(R.drawable.rectangle_active);
                            v.setSelected(true);
                            songs.add((String) v.getTag());
                        }

                        if(mStep2TableLayout.getVisibility() == View.GONE) {
                            mStep2TableLayout.startAnimation(fadeInAnimation);
                            mStep2TextView.startAnimation(fadeInAnimation);
                            mStep2TableLayout.setVisibility(View.VISIBLE);
                            mStep2TextView.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }

        for(int i = 0; i < mStep2TableLayout.getChildCount(); i ++) {
            ViewGroup TableRow = (ViewGroup) mStep2TableLayout.getChildAt(i);
            for (int j = 0; j < TableRow.getChildCount(); j++) {
                TableRow.getChildAt(j).setSelected(false);
                TableRow.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            v.setBackgroundResource(R.drawable.rectangle_not_active);
                            v.setSelected(false);
                        } else {
                            v.setBackgroundResource(R.drawable.rectangle_active);
                            v.setSelected(true);
                        }

                        if(mStep3TableLayout.getVisibility() == View.GONE) {
                            mStep3TableLayout.setVisibility(View.VISIBLE);
                            mStep3TextView.setVisibility(View.VISIBLE);
                            mStep3TableLayout.startAnimation(fadeInAnimation);
                            mStep3TextView.startAnimation(fadeInAnimation);
                        }
                    }
                });
            }
        }

        for(int i = 0; i < mStep3TableLayout.getChildCount(); i ++) {
            ViewGroup TableRow = (ViewGroup) mStep3TableLayout.getChildAt(i);
            for(int j = 0; j < TableRow.getChildCount(); j++) {
                TableRow.getChildAt(j).setSelected(false);
                TableRow.getChildAt(j).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (v.isSelected()) {
                            v.setBackgroundResource(R.drawable.rectangle_not_active);
                            v.setSelected(false);
                        } else {
                            v.setBackgroundResource(R.drawable.rectangle_active);
                            v.setSelected(true);
                        }
                    }
                });
            }
        }
    }
}
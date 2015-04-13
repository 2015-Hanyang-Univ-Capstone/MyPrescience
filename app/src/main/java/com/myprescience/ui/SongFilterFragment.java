package com.myprescience.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.ListFragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.myprescience.R;

import static com.myprescience.util.Server.BILLBOARDHOT_MODE;
import static com.myprescience.util.Server.KPOP_MODE;
import static com.myprescience.util.Server.POP_MODE;
import static com.myprescience.util.Server.RANDOM_MODE;
import static com.myprescience.util.Server.MODE;

/**
 * Created by paveld on 4/17/14.
 */
public class SongFilterFragment extends Fragment {

    private Button mRandomSongFilterButton, mKpopSongFilterButton, mPopSongFilterButton, mBillboardHot100SongFilterButton, mClickedButton;
    private OnFilterSelectedListener mCallback;
    private int Clicked = RANDOM_MODE;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View filter_view = inflater.inflate(R.layout.song_fliter_fragment_layout, container, false);

        mRandomSongFilterButton = (Button) filter_view.findViewById(R.id.random_SongFilter);
        mKpopSongFilterButton = (Button) filter_view.findViewById(R.id.kpop_SongFilter);
        mPopSongFilterButton = (Button) filter_view.findViewById(R.id.pop_SongFilter);
        mBillboardHot100SongFilterButton = (Button) filter_view.findViewById(R.id.billboardHot100_SongFilter);

        mClickedButton = mRandomSongFilterButton;

        switch(MODE) {
            case 0 : activeButton(mRandomSongFilterButton);
                     break;
            case 2 : activeButton(mKpopSongFilterButton);
                     break;
            case 3 : activeButton(mPopSongFilterButton);
                     break;
            case 4 : activeButton(mBillboardHot100SongFilterButton);
                     break;
        }


        mRandomSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != RANDOM_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = RANDOM_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mKpopSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != KPOP_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = KPOP_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mPopSongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != POP_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = POP_MODE;
                    activeButton((Button) v);
                }
            }
        });

        mBillboardHot100SongFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Clicked != BILLBOARDHOT_MODE) {
                    nonAtiveButton(mClickedButton);
                    Clicked = BILLBOARDHOT_MODE;
                    activeButton((Button) v);
                }
            }
        });

        return filter_view;
    }

    public void activeButton(Button clickedButton) {
        clickedButton.setPadding(25, 25, 25, 25);
        clickedButton.setBackgroundResource(R.drawable.active_background);
        clickedButton.setTextColor(Color.WHITE);
        mClickedButton = clickedButton;
        mCallback.onFilterSelected(Clicked);
    }

    public void nonAtiveButton(Button clickedButton) {
        clickedButton.setBackgroundResource(R.drawable.background);
        clickedButton.setTextColor(getResources().getColor(R.color.color_base_light_blue));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFilterSelectedListener {
        public void onFilterSelected(int position);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            mCallback = (OnFilterSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()+ " must implement OnHeadlineSelectedListener");
        }
    }
}
